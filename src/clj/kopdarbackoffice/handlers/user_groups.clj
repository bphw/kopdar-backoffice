(ns kopdarbackoffice.handlers.user-groups
  (:require [inertia.middleware :as inertia]
            [kopdarbackoffice.models.user-groups :as db]
            [kopdarbackoffice.shared.pagination :as pagination]
            [ring.util.response :as rr]
            [struct.core :as st]))

(def group-schema
  [[:kode st/required st/string]
   [:nama st/required st/string]])

(defn index
  [db]
  (fn [{:keys [params uri query-string] :as request}]
    (let [filters (select-keys params [:search :trashed])
          page (get-in request [:parameters :query :page] 1)
          offset (* (dec page) 10)
          count (:aggregate (first (db/retrieve-and-filter-user-groups db filters)))
          groups (db/retrieve-and-filter-user-groups db filters offset)
          props {:user-groups {:data groups
                            :current_page page
                            :links (pagination/pagination-links uri query-string page count 10)}
                 :filters filters}]
      (inertia/render "UserGroups/Index" props))))

(defn user-groups-form [db]
  (fn [_]
      (inertia/render "UserGroups/Create")))

(defn store-user-group! [db]
  (fn [{:keys [body-params] :as req}]
    (if-let [errors (first (st/validate body-params group-schema))]
      (-> (rr/redirect "/user-groups/create")
          (assoc :flash {:error errors}))
      (let [crby (-> req :identity :kode)
            groups-created? (db/insert-user-group! db (assoc body-params :crby crby))]
        (when groups-created?
          (-> (rr/redirect "/user-groups")
              (assoc :flash {:success "Groups created."})))))))

(defn edit-user-group! [db]
  (fn [{:keys [path-params]}]
    (let [props {:delivery (db/get-user-group-by-id db (:user-group-id path-params))}]
      (inertia/render "UserGroups/Edit" props))))

(defn update-user-group! [db]
  (fn [{:keys [body-params] :as req}]
    (let [id (-> req :path-params :user-group-id)
          url (str (-> req :uri) "/edit")]
      (if-let [errors (first (st/validate body-params group-schema))]
        (-> (rr/redirect url :see-other)
            (assoc :flash {:error errors}))
        (let [group-updated? (db/update-user-group! db body-params id)]
          (when group-updated?
            (-> (rr/redirect url :see-other)
                (assoc :flash {:success "Group updated."}))))))))

(defn delete-user-group! [db]
  (fn [req]
    (let [id (-> req :path-params :user-group-id)
          back (get (:headers req) "referer")
          group-deleted? (db/soft-delete-user-group! db id)]
      (when group-deleted?
        (-> (rr/redirect back :see-other)
            (assoc :flash {:success "Group deleted."}))))))

(defn restore-user-group! [db]
  (fn [req]
    (let [id (-> req :path-params :user-group-id)
          back (get (:headers req) "referer")
          group-restored? (db/restore-deleted-user-group! db id)]
      (when group-restored?
        (-> (rr/redirect back :see-other)
            (assoc :flash {:success "Group restored."}))))))
