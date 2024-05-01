(ns kopdarbackoffice.handlers.trx-details
  (:require [inertia.middleware :as inertia]
            [kopdarbackoffice.models.trx-details :as db]
            [kopdarbackoffice.shared.pagination :as pagination]
            [ring.util.response :as rr]
            [struct.core :as st]))

(def trx-details-schema
  [[:trno st/required st/string]
   [:trit st/required st/string]
   [:prdktbid st/string]
   [:prdkkode st/string]
   [:prdknama st/string]
   [:prdkhjua st/number]
   [:item st/string]
   [:quan st/number]
   [:disc st/number]
   [:potg st/number]
   [:pajk st/number]
   [:gamt st/number]
   [:namt st/number]]
   )

(defn index
  [db]
  (fn [{:keys [params uri query-string] :as request}]
    (let [filters (select-keys params [:search :trashed])
          page (get-in request [:parameters :query :page] 1)
          offset (* (dec page) 10)
          count (:aggregate (first (db/retrieve-and-filter-trx-details db filters)))
          trx-details (db/retrieve-and-filter-trx-details db filters offset)
          props {:trx-details {:data trx-details
                            :current_page page
                            :links (pagination/pagination-links uri query-string page count 10)}
                 :filters filters}]
      (inertia/render "TrxDetails/Index" props))))

(defn trx-details-form [db]
  (fn [_]
      (inertia/render "TrxDetails/Create")))

(defn store-trx-detail! [db]
  (fn [{:keys [body-params] :as req}]
    (if-let [errors (first (st/validate body-params trx-details-schema))]
      (-> (rr/redirect "/trx-detail/create")
          (assoc :flash {:error errors}))
      (let [crby (-> req :identity :kode)
            trx-details-created? (db/insert-trx-detail! db (assoc body-params :crby crby))]
        (when trx-details-created?
          (-> (rr/redirect "/trx-details")
              (assoc :flash {:success "Trx Detail created."})))))))

(defn edit-trx-detail! [db]
  (fn [{:keys [path-params]}]
    (let [props {:trx-detail (db/get-trx-detail-by-id db (:trx-detail-id path-params))}]
      (inertia/render "TrxDetails/Edit" props))))

(defn update-trx-detail! [db]
  (fn [{:keys [body-params] :as req}]
    (let [id (-> req :path-params :trx-detail-id)
          url (str (-> req :uri) "/edit")]
      (if-let [errors (first (st/validate body-params trx-details-schema))]
        (-> (rr/redirect url :see-other)
            (assoc :flash {:error errors}))
        (let [trx-detail-updated? (db/update-trx-detail! db body-params id)]
          (when trx-detail-updated?
            (-> (rr/redirect url :see-other)
                (assoc :flash {:success "Trx updated."}))))))))

(defn delete-trx-detail! [db]
  (fn [req]
    (let [id (-> req :path-params :trx-detail-id)
          back (get (:headers req) "referer")
          trx-detail-deleted? (db/soft-delete-trx-detail! db id)]
      (when trx-detail-deleted?
        (-> (rr/redirect back :see-other)
            (assoc :flash {:success "Trx detail deleted."}))))))

(defn restore-trx-detail! [db]
  (fn [req]
    (let [id (-> req :path-params :trx-detail-id)
          back (get (:headers req) "referer")
          trx-detail-restored? (db/restore-deleted-trx-detail! db id)]
      (when trx-detail-restored?
        (-> (rr/redirect back :see-other)
            (assoc :flash {:success "Trx detail restored."}))))))
