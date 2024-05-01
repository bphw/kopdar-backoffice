(ns kopdarbackoffice.handlers.trx-headers
  (:require [inertia.middleware :as inertia]
            [kopdarbackoffice.models.trx-headers :as db]
            [kopdarbackoffice.shared.pagination :as pagination]
            [ring.util.response :as rr]
            [struct.core :as st]))

(def header-schema
  [[:trno st/required st/string]
   [:tokotbid st/required st/integer]
   [:custtbid st/required st/integer]
   [:almttbid st/required st/integer]
   [:krimtbid st/required st/integer]
   [:mbyrtbid st/required st/integer]
   [:quanitem st/required st/integer]
   [:quantota st/required st/integer]
   [:disctota st/required st/integer]
   [:potgtota st/required st/integer]
   [:gamttota st/required st/integer]
   [:namttota st/required st/integer]
   [:hkir st/required st/integer]
   [:status st/required st/string]])

(defn index
  [db]
  (fn [{:keys [params uri query-string] :as request}]
    (let [filters (select-keys params [:search :trashed])
          page (get-in request [:parameters :query :page] 1)
          offset (* (dec page) 10)
          count (:aggregate (first (db/retrieve-and-filter-trx-headers db filters)))
          hdrs (db/retrieve-and-filter-trx-headers db filters offset)
          props {:trx-headers {:data hdrs
                            :current_page page
                            :links (pagination/pagination-links uri query-string page count 10)}
                 :filters filters}]
      (inertia/render "TrxHeaders/Index" props))))

(defn trx-headers-form [db]
  (fn [_]
      (inertia/render "TrxHeaders/Create")))

(defn store-trx-header! [db]
  (fn [{:keys [body-params] :as req}]
    (if-let [errors (first (st/validate body-params header-schema))]
      (-> (rr/redirect "/trx-headers/create")
          (assoc :flash {:error errors}))
      (let [crby (-> req :identity :kode)
            trx-headers-created? (db/insert-trx-header! db (assoc body-params :crby crby))]
        (when trx-headers-created?
          (-> (rr/redirect "/trx-headers")
              (assoc :flash {:success "Trx Headers created."})))))))

(defn edit-trx-header! [db]
  (fn [{:keys [path-params]}]
    (let [props {:header (db/get-trx-header-by-id db (:trx-header-id path-params))}]
      (inertia/render "TrxHeaders/Edit" props))))

(defn update-trx-header! [db]
  (fn [{:keys [body-params] :as req}]
    (let [id (-> req :path-params :trx-header-id)
          url (str (-> req :uri) "/edit")]
      (if-let [errors (first (st/validate body-params header-schema))]
        (-> (rr/redirect url :see-other)
            (assoc :flash {:error errors}))
        (let [trx-header-updated? (db/update-trx-header! db body-params id)]
          (when trx-header-updated?
            (-> (rr/redirect url :see-other)
                (assoc :flash {:success "Trx Header updated."}))))))))

(defn delete-trx-header! [db]
  (fn [req]
    (let [id (-> req :path-params :trx-header-id)
          back (get (:headers req) "referer")
          trx-header-deleted? (db/soft-delete-trx-header! db id)]
      (when trx-header-deleted?
        (-> (rr/redirect back :see-other)
            (assoc :flash {:success "Trx header deleted."}))))))

(defn restore-trx-header! [db]
  (fn [req]
    (let [id (-> req :path-params :trx-header-id)
          back (get (:headers req) "referer")
          
          trx-header-restored? (db/restore-deleted-trx-header! db id)]
      (when trx-header-restored?
        (-> (rr/redirect back :see-other)
            (assoc :flash {:success "Trx header restored."}))))))
