(ns kopdarbackoffice.handlers.trx-statuses
  (:require [inertia.middleware :as inertia]
            [kopdarbackoffice.models.trx-statuses :as db]
            [kopdarbackoffice.shared.pagination :as pagination]
            [ring.util.response :as rr]
            [struct.core :as st]))

(def status-schema
  [[:nama st/required st/string]
   [:kode st/required st/string]])

(defn index
  [db]
  (fn [{:keys [params query-string uri] :as request}]
    (let [filters (select-keys params [:search :trashed])
          page (get-in request [:parameters :query :page] 1)
          offset (* (dec page) 10)
          count (:aggregate (first (db/retrieve-and-filter-trx-statuses db filters)))
          statuses (db/retrieve-and-filter-trx-statuses db filters offset)
          props {:products {:data statuses
                            :current_page page
                            :links (pagination/pagination-links uri query-string page count 10)}
                 :filters filters}]
      (inertia/render "TrxStatuses/Index" props))))

(defn trx-statuses-form [_]
  (inertia/render "TrxStatuses/Create"))

(defn store-trx-status!
  [db]
  (fn [{:keys [body-params] :as req}]
    (if-let [errors (first (st/validate body-params status-schema))]
      (-> (rr/redirect "/trx-statuses/create")
          (assoc :flash {:error errors}))
      (let [crby (-> req :identity :kode)
            status-created? (db/insert-trx-status! db (assoc body-params :crby crby))]
        (when status-created?
          (-> (rr/redirect "/trx-statuses")
              (assoc :flash {:success "Status created."})))))))

(defn edit-trx-status!
  [db]
  (fn [{:keys [path-params]}]
    (let [props {:trx-status (db/get-trx-status-by-id db (:trx-status-id path-params))}]
      (inertia/render "TrxStatus/Edit" props))))

(defn update-trx-status!
  [db]
  (fn [{:keys [body-params] :as req}]
    (let [id (-> req :path-params :trx-status-id)
          url (str (-> req :uri) "/edit")]
      (if-let [errors (first (st/validate body-params status-schema))]
        (-> (rr/redirect url :see-other)
            (assoc :flash {:error errors}))
        (let [status-updated? (db/update-trx-status! db body-params id)]
             (when status-updated?
               (-> (rr/redirect url :see-other)
                   (assoc :flash {:success "Status updated."}))))))))

(defn delete-trx-status!
  [db]
  (fn [req]
    (let [id (-> req :path-params :trx-status-id)
          back (get (:headers req) "referer")
          status-deleted? (db/soft-delete-trx-status! db id)]
      (when status-deleted?
        (-> (rr/redirect back :see-other)
            (assoc :flash {:success "Status deleted."}))))))

(defn restore-trx-status!
  [db]
  (fn [req]
    (let [id (-> req :path-params :trx-status-id)
          back (get (:headers req) "referer")
          status-restored? (db/restore-deleted-trx-status! db id)]
      (when status-restored?
        (-> (rr/redirect back :see-other)
            (assoc :flash {:success "Status restored."}))))))
