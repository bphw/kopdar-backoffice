(ns kopdarbackoffice.handlers.payment-methods
  (:require [inertia.middleware :as inertia]
            [kopdarbackoffice.models.payment-methods :as db]
            [kopdarbackoffice.shared.pagination :as pagination]
            [ring.util.response :as rr]
            [struct.core :as st]))

(def payment-method-schema
  [[:kode st/required st/string]
   [:nama st/required st/string]
   [:imag st/required st/string]])

(defn index
  [db]
  (fn [{:keys [params uri query-string] :as request}]
    (let [filters (select-keys params [:search :trashed])
          page (get-in request [:parameters :query :page] 1)
          offset (* (dec page) 10)
          count (:aggregate (first (db/retrieve-and-filter-payment-methods db filters)))
          payment-methods (db/retrieve-and-filter-payment-methods db filters offset)
          props {:payment-methods {:data payment-methods
                 :current_page page
                 :links (pagination/pagination-links uri query-string page count 10)}
                 :filters filters}]
      (inertia/render "PaymentMethods/Index" props))))

(defn payment-methods-form [db]
  (fn [_]
    (inertia/render "PaymentMethods/Create")))

(defn payment-method-store! [db]
  (fn [{:keys [body-params] :as req}]
    (if-let [errors (first (st/validate body-params payment-method-schema))]
      (-> (rr/redirect "/payment-methods/create")
          (assoc :flash {:error errors}))
      (let [crby (-> req :identity :kode)
            payment-methods-created? (db/insert-payment-method! db (assoc body-params :crby crby))]
        (when payment-methods-created?
          (-> (rr/redirect "/payment-methods")
              (assoc :flash {:success "Payment Methods created."})))))))

(defn edit-payment-method! [db]
  (fn [{:keys [path-params]}]
    (let [props {:payment-method (db/get-payment-method-by-id db (:payment-method-id path-params))}]
      (inertia/render "PaymentMethods/Edit" props))))

(defn update-payment-method! [db]
  (fn [{:keys [body-params] :as req}]
    (let [id (-> req :path-params :payment-method-id)
          url (str (-> req :uri) "/edit")]
      (if-let [errors (first (st/validate body-params payment-method-schema))]
        (-> (rr/redirect url :see-other)
            (assoc :flash {:error errors}))
        (let [payment-method-updated? (db/update-payment-method! db body-params id)]
          (when payment-method-updated?
            (-> (rr/redirect url :see-other)
                (assoc :flash {:success "Payment Method updated."}))))))))

(defn delete-payment-method! [db]
  (fn [req]
    (let [id (-> req :path-params :payment-method-id)
          back (get (:headers req) "referer")
          payment-method-deleted? (db/soft-delete-payment-method! db id)]
      (when payment-method-deleted?
        (-> (rr/redirect back :see-other)
            (assoc :flash {:success "Payment Method deleted."}))))))

(defn restore-payment-method! [db]
  (fn [req]
    (let [id (-> req :path-params :payment-method-id)
          back (get (:headers req) "referer")
          payment-method-restored? (db/restore-deleted-payment-method! db id)]
      (when payment-method-restored?
        (-> (rr/redirect back :see-other)
            (assoc :flash {:success "Payment Method restored."}))))))
