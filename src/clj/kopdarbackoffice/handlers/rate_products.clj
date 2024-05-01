(ns kopdarbackoffice.handlers.rate-products
  (:require [inertia.middleware :as inertia]
            [kopdarbackoffice.models.rate-products :as db]
            [kopdarbackoffice.shared.pagination :as pagination]
            [ring.util.response :as rr]
            [struct.core :as st]))

(def rate-schema
  [[:trno st/required st/string]
   [:trit st/required st/string]
   [:ratg st/required st/number]])

(defn index
  [db]
  (fn [{:keys [params uri query-string] :as request}]
    (let [filters (select-keys params [:search :trashed])
          page (get-in request [:parameters :query :page] 1)
          offset (* (dec page) 10)
          count (:aggregate (first (db/retrieve-and-filter-rate-products db filters)))
          rate-products (db/retrieve-and-filter-rate-products db filters offset)
          props {:rate-products {:data rate-products
                            :current_page page
                            :links (pagination/pagination-links uri query-string page count 10)}
                 :filters filters}]
      (inertia/render "RateProducts/Index" props))))

(defn rate-products-form [db]
  (fn [_]
      (inertia/render "RateProducts/Create")))

(defn store-rate-product! [db]
  (fn [{:keys [body-params] :as req}]
    (if-let [errors (first (st/validate body-params rate-schema))]
      (-> (rr/redirect "/rate-products/create")
          (assoc :flash {:error errors}))
      (let [crby (-> req :identity :kode)
            rate-products-created? (db/insert-rate-product! db (assoc body-params :crby crby))]
        (when rate-products-created?
          (-> (rr/redirect "/rate-products")
              (assoc :flash {:success "Rate created."})))))))

(defn edit-rate-product! [db]
  (fn [{:keys [path-params]}]
    (let [props {:rate-product (db/get-rate-product-by-id db (:rate-product-id path-params))}]
      (inertia/render "RateProducts/Edit" props))))

(defn update-rate-product! [db]
  (fn [{:keys [body-params] :as req}]
    (let [id (-> req :path-params :rate-product-id)
          url (str (-> req :uri) "/edit")]
      (if-let [errors (first (st/validate body-params rate-schema))]
        (-> (rr/redirect url :see-other)
            (assoc :flash {:error errors}))
        (let [rate-updated? (db/update-rate-product! db body-params id)]
          (when rate-updated?
            (-> (rr/redirect url :see-other)
                (assoc :flash {:success "Rate updated."}))))))))

(defn delete-rate-product! [db]
  (fn [req]
    (let [id (-> req :path-params :rate-product-id)
          back (get (:headers req) "referer")
          rate-deleted? (db/soft-delete-rate-product! db id)]
      (when rate-deleted?
        (-> (rr/redirect back :see-other)
            (assoc :flash {:success "Rate deleted."}))))))

(defn restore-rate-product! [db]
  (fn [req]
    (let [id (-> req :path-params :rate-product-id)
          back (get (:headers req) "referer")
          rate-restored? (db/restore-deleted-rate-product! db id)]
      (when rate-restored?
        (-> (rr/redirect back :see-other)
            (assoc :flash {:success "Rate restored."}))))))
