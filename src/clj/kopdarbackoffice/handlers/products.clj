(ns kopdarbackoffice.handlers.products
  (:require [inertia.middleware :as inertia]
            [kopdarbackoffice.models.products :as db]
            [kopdarbackoffice.shared.pagination :as pagination]
            [ring.util.response :as rr]
            [struct.core :as st]))

(def product-schema
  [[:nama st/required st/string]
   [:kode st/required st/string]
   [:hjua st/required st/number-str]])

(defn index
  [db]
  (fn [{:keys [params query-string uri] :as request}]
    (let [filters (select-keys params [:search :trashed])
          page (get-in request [:parameters :query :page] 1)
          offset (* (dec page) 10)
          count (:aggregate (first (db/retrieve-and-filter-products db filters)))
          products (db/retrieve-and-filter-products db filters offset)
          props {:products {:data products
                            :current_page page
                            :links (pagination/pagination-links uri query-string page count 10)}
                 :filters filters}]
      (inertia/render "Products/Index" props))))

(defn product-form [_]
  (inertia/render "Products/Create"))

(defn store-product!
  [db]
  (fn [{:keys [body-params] :as req}]
    (if-let [errors (first (st/validate body-params product-schema))]
      (-> (rr/redirect "/products/create")
          (assoc :flash {:error errors}))
      (let [crby (-> req :identity :kode)
            product-created? (db/insert-product! db (assoc body-params :crby crby))]
        (when product-created?
          (-> (rr/redirect "/products")
              (assoc :flash {:success "Product created."})))))))

(defn edit-product!
  [db]
  (fn [{:keys [path-params]}]
    (let [props {:product (db/get-product-by-id db (:product-id path-params))}]
      (inertia/render "Product/Edit" props))))

(defn update-product!
  [db]
  (fn [{:keys [body-params] :as req}]
    (let [id (-> req :path-params :product-id)
          url (str (-> req :uri) "/edit")]
      (if-let [errors (first (st/validate body-params product-schema))]
        (-> (rr/redirect url :see-other)
            (assoc :flash {:error errors}))
        (let [product-updated? (db/update-product! db body-params id)]
             (when product-updated?
               (-> (rr/redirect url :see-other)
                   (assoc :flash {:success "Product updated."}))))))))

(defn delete-product!
  [db]
  (fn [req]
    (let [id (-> req :path-params :product-id)
          back (get (:headers req) "referer")
          product-deleted? (db/soft-delete-product! db id)]
      (when product-deleted?
        (-> (rr/redirect back :see-other)
            (assoc :flash {:success "Product deleted."}))))))

(defn restore-product!
  [db]
  (fn [req]
    (let [id (-> req :path-params :product-id)
          back (get (:headers req) "referer")
          product-restored? (db/restore-deleted-product! db id)]
      (when product-restored?
        (-> (rr/redirect back :see-other)
            (assoc :flash {:success "Product restored."}))))))
