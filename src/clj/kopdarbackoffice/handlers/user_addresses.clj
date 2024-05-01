(ns kopdarbackoffice.handlers.user-addresses
  (:require [inertia.middleware :as inertia]
            [kopdarbackoffice.models.user-addresses :as db]
            [kopdarbackoffice.shared.pagination :as pagination]
            [ring.util.response :as rr]
            [struct.core :as st]))

(def address-schema
  [[:nama st/required st/string]
   [:kode st/required st/string]
   [:tokotbid st/required st/number]
   [:usertbid st/required st/number]
   [:kat1tbid st/required st/number]
   [:kat2tbid st/required st/number]
   [:kat3tbid st/required st/number]
   [:jln1 st/required st/string]
   [:jln2 st/required st/string]
   [:kelu st/required st/string]
   [:kota st/required st/string]
   [:prop st/required st/string]
   [:ctry st/required st/string]
   [:kpos st/required st/string]
   ])

(defn index
  [db]
  (fn [{:keys [params query-string uri] :as request}]
    (let [filters (select-keys params [:search :trashed])
          page (get-in request [:parameters :query :page] 1)
          offset (* (dec page) 10)
          count (:aggregate (first (db/retrieve-and-filter-user-addresses db filters)))
          addresses (db/retrieve-and-filter-user-addresses db filters offset)
          props {:user-addresses {:data addresses
                            :current_page page
                            :links (pagination/pagination-links uri query-string page count 10)}
                 :filters filters}]
      (inertia/render "UserAddresses/Index" props))))

(defn user-address-form [_]
  (inertia/render "UserAddresses/Create"))

(defn store-user-address!
  [db]
  (fn [{:keys [body-params] :as req}]
    (if-let [errors (first (st/validate body-params address-schema))]
      (-> (rr/redirect "/user-addresses/create")
          (assoc :flash {:error errors}))
      (let [crby (-> req :identity :kode)
            address-created? (db/insert-user-address! db (assoc body-params :crby crby))]
        (when address-created?
          (-> (rr/redirect "/user-addresses")
              (assoc :flash {:success "Address created."})))))))

(defn edit-user-address!
  [db]
  (fn [{:keys [path-params]}]
    (let [props {:user-address (db/get-user-address-by-id db (:user-address-id path-params))}]
      (inertia/render "UserAddresses/Edit" props))))

(defn update-user-address!
  [db]
  (fn [{:keys [body-params] :as req}]
    (let [id (-> req :path-params :user-address-id)
          url (str (-> req :uri) "/edit")]
      (if-let [errors (first (st/validate body-params address-schema))]
        (-> (rr/redirect url :see-other)
            (assoc :flash {:error errors}))
        (let [address-updated? (db/update-user-address! db body-params id)]
             (when address-updated?
               (-> (rr/redirect url :see-other)
                   (assoc :flash {:success "Address updated."}))))))))

(defn delete-user-address!
  [db]
  (fn [req]
    (let [id (-> req :path-params :user-address-id)
          back (get (:headers req) "referer")
          user-address-deleted? (db/soft-delete-user-address! db id)]
      (when user-address-deleted?
        (-> (rr/redirect back :see-other)
            (assoc :flash {:success "Address deleted."}))))))

(defn restore-user-address!
  [db]
  (fn [req]
    (let [id (-> req :path-params :user-address-id)
          back (get (:headers req) "referer")
          user-address-restored? (db/restore-deleted-user-address! db id)]
      (when user-address-restored?
        (-> (rr/redirect back :see-other)
            (assoc :flash {:success "Product restored."}))))))
