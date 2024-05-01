(ns kopdarbackoffice.handlers.rate-stores
  (:require [inertia.middleware :as inertia]
            [kopdarbackoffice.models.rate-stores :as db]
            [kopdarbackoffice.shared.pagination :as pagination]
            [ring.util.response :as rr]
            [struct.core :as st]))

(def rate-schema
  [[:trno st/required st/string]
   [:ratg st/required st/number]])

(defn index
  [db]
  (fn [{:keys [params uri query-string] :as request}]
    (let [filters (select-keys params [:search :trashed])
          page (get-in request [:parameters :query :page] 1)
          offset (* (dec page) 10)
          count (:aggregate (first (db/retrieve-and-filter-rate-stores db filters)))
          rates (db/retrieve-and-filter-rate-stores db filters offset)
          props {:rate-stores {:data rates
                            :current_page page
                            :links (pagination/pagination-links uri query-string page count 10)}
                 :filters filters}]
      (inertia/render "RateStores/Index" props))))

(defn rate-form [db]
  (fn [_]
      (inertia/render "RateStores/Create")))

(defn store-rate! [db]
  (fn [{:keys [body-params] :as req}]
    (if-let [errors (first (st/validate body-params rate-schema))]
      (-> (rr/redirect "/rate-stores/create")
          (assoc :flash {:error errors}))
      (let [crby (-> req :identity :kode)
            rates-created? (db/insert-rate-store! db (assoc body-params :crby crby))]
        (when rates-created?
          (-> (rr/redirect "/rates")
              (assoc :flash {:success "Rate created."})))))))

(defn edit-rate! [db]
  (fn [{:keys [path-params]}]
    (let [props {:rate-store (db/get-rate-store-by-id db (:delivery-id path-params))}]
      (inertia/render "RateStores/Edit" props))))

(defn update-rate! [db]
  (fn [{:keys [body-params] :as req}]
    (let [id (-> req :path-params :rate-store-id)
          url (str (-> req :uri) "/edit")]
      (if-let [errors (first (st/validate body-params rate-schema))]
        (-> (rr/redirect url :see-other)
            (assoc :flash {:error errors}))
        (let [rate-updated? (db/update-rate-store! db body-params id)]
          (when rate-updated?
            (-> (rr/redirect url :see-other)
                (assoc :flash {:success "Rate updated."}))))))))

(defn delete-rate! [db]
  (fn [req]
    (let [id (-> req :path-params :rate-store-id)
          back (get (:headers req) "referer")
          rate-deleted? (db/soft-delete-rate-store! db id)]
      (when rate-deleted?
        (-> (rr/redirect back :see-other)
            (assoc :flash {:success "Rate deleted."}))))))

(defn restore-rate! [db]
  (fn [req]
    (let [id (-> req :path-params :rate-store-id)
          back (get (:headers req) "referer")
          rate-restored? (db/restore-deleted-rate-store! db id)]
      (when rate-restored?
        (-> (rr/redirect back :see-other)
            (assoc :flash {:success "Delivery restored."}))))))
