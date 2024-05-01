(ns kopdarbackoffice.handlers.deliveries
  (:require [inertia.middleware :as inertia]
            [kopdarbackoffice.models.deliveries :as db]
            [kopdarbackoffice.shared.pagination :as pagination]
            [ring.util.response :as rr]
            [struct.core :as st]))

(def delivery-schema
  [[:kode st/required st/string]
   [:nama st/required st/string]
   [:grp1 st/required st/string]
   [:buom st/number]
   [:puom st/number]
   [:huom st/number]
   [:bjrk st/string]
   [:pjrk st/number]
   [:hjrk st/number]
   [:hkir st/number]])

(defn index
  [db]
  (fn [{:keys [params uri query-string] :as request}]
    (let [filters (select-keys params [:search :trashed])
          page (get-in request [:parameters :query :page] 1)
          offset (* (dec page) 10)
          count (:aggregate (first (db/retrieve-and-filter-deliveries db filters)))
          deliveries (db/retrieve-and-filter-deliveries db filters offset)
          props {:deliveries {:data deliveries
                            :current_page page
                            :links (pagination/pagination-links uri query-string page count 10)}
                 :filters filters}]
      (inertia/render "Deliveries/Index" props))))

(defn deliveries-form [db]
  (fn [_]
      (inertia/render "Deliveries/Create")))

(defn store-delivery! [db]
  (fn [{:keys [body-params] :as req}]
    (if-let [errors (first (st/validate body-params delivery-schema))]
      (-> (rr/redirect "/deliveries/create")
          (assoc :flash {:error errors}))
      (let [crby (-> req :identity :kode)
            deliveries-created? (db/insert-delivery! db (assoc body-params :crby crby))]
        (when deliveries-created?
          (-> (rr/redirect "/deliveries")
              (assoc :flash {:success "Deliveries created."})))))))

(defn edit-delivery! [db]
  (fn [{:keys [path-params]}]
    (let [props {:delivery (db/get-delivery-by-id db (:delivery-id path-params))}]
      (inertia/render "Deliveries/Edit" props))))

(defn update-delivery! [db]
  (fn [{:keys [body-params] :as req}]
    (let [id (-> req :path-params :delivery-id)
          url (str (-> req :uri) "/edit")]
      (if-let [errors (first (st/validate body-params delivery-schema))]
        (-> (rr/redirect url :see-other)
            (assoc :flash {:error errors}))
        (let [delivery-updated? (db/update-delivery! db body-params id)]
          (when delivery-updated?
            (-> (rr/redirect url :see-other)
                (assoc :flash {:success "Delivery updated."}))))))))

(defn delete-delivery! [db]
  (fn [req]
    (let [id (-> req :path-params :delivery-id)
          back (get (:headers req) "referer")
          delivery-deleted? (db/soft-delete-delivery! db id)]
      (when delivery-deleted?
        (-> (rr/redirect back :see-other)
            (assoc :flash {:success "Delivery deleted."}))))))

(defn restore-delivery! [db]
  (fn [req]
    (let [id (-> req :path-params :delivery-id)
          back (get (:headers req) "referer")
          delivery-restored? (db/restore-deleted-delivery! db id)]
      (when delivery-restored?
        (-> (rr/redirect back :see-other)
            (assoc :flash {:success "Delivery restored."}))))))
