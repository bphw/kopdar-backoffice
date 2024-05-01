(ns kopdarbackoffice.handlers.stores
  (:require [inertia.middleware :as inertia]
            [kopdarbackoffice.models.stores :as db]
            [kopdarbackoffice.models.products :as product-db]
            [kopdarbackoffice.shared.pagination :as pagination]
            [ring.util.response :as rr]
            [struct.core :as st]))

(def store-schema
  [[:nama st/required st/string]
   [:telp st/required st/string]
   [:emai st/required st/email]])

(defn index
  [db]
  (fn [{:keys [params uri query-string] :as request}]
    (let [filters (select-keys params [:search :trashed])
          page (get-in request [:parameters :query :page] 1)
          offset (* (dec page) 10)
          count (:aggregate (first (db/retrieve-and-filter-stores db filters)))
          stores (db/retrieve-and-filter-stores db filters offset)
          stores-formated (map #(assoc % :product {:nama (:product %)}) stores)
          props {:stores {:data stores-formated
                            :current_page page
                            :links (pagination/pagination-links uri query-string page count 10)}
                 :filters filters}]
      (inertia/render "Stores/Index" props))))

(defn stores-form [db]
  (fn [_]
    (let [props {:products (product-db/list-products db)}]
      (inertia/render "Stores/Create" props))))

(defn store-store! [db]
  (fn [{:keys [body-params] :as req}]
    (if-let [errors (first (st/validate body-params store-schema))]
      (-> (rr/redirect "/stores/create")
          (assoc :flash {:error errors}))
      (let [crby (-> req :identity :kode)
            stores-created? (db/insert-store! db (assoc body-params :crby crby))]
        (when stores-created?
          (-> (rr/redirect "/stores")
              (assoc :flash {:success "Stores created."})))))))

(defn edit-store! [db]
  (fn [{:keys [path-params]}]
    (let [props {:store (db/get-store-by-id db (:store-id path-params))
                 :products (product-db/list-products db)}]
      (inertia/render "Stores/Edit" props))))

(defn update-store! [db]
  (fn [{:keys [body-params] :as req}]
    (let [id (-> req :path-params :store-id)
          url (str (-> req :uri) "/edit")
          chby (-> req :session :identity :kode)]
      (if-let [errors (first (st/validate body-params store-schema))]
        (-> (rr/redirect url :see-other)
            (assoc :flash {:error errors}))
        (let [store-updated? (db/update-store! db body-params id)]
          (when store-updated?
            (-> (rr/redirect url :see-other)
                (assoc :flash {:success "Store updated."}))))))))

(defn delete-store! [db]
  (fn [req]
    (let [id (-> req :path-params :store-id)
          back (get (:headers req) "referer")
          store-deleted? (db/soft-delete-store! db id)]
      (when store-deleted?
        (-> (rr/redirect back :see-other)
            (assoc :flash {:success "Store deleted."}))))))

(defn restore-store! [db]
  (fn [req]
    (let [id (-> req :path-params :store-id)
          back (get (:headers req) "referer")
          store-restored? (db/restore-deleted-store! db id)]
      (when store-restored?
        (-> (rr/redirect back :see-other)
            (assoc :flash {:success "Store restored."}))))))
