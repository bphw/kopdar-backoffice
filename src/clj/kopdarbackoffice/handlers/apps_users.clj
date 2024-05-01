(ns kopdarbackoffice.handlers.apps-users
  (:require [crypto.password.bcrypt :as password]
            [inertia.middleware :as inertia]
            [kopdarbackoffice.models.apps-users :as db]
            [ring.util.response :as rr]
            [struct.core :as st]))

(def user-schema
  [[:kode st/required st/string]
   [:nama st/required st/string]
   [:emai st/required st/email]
   [:telp st/string]
   [:almttbid st/string]
   [:pswd st/required st/string]
   [:imag st/string]])

(defn validate-unique-user
  [db params]
  (let [{:keys [emai]} params
        validation (first (st/validate params user-schema))]
    (if (db/get-apps-user-by-email db emai)
      (assoc validation :email "Email sudah ada yang punya.")
      validation)))

(defn get-users [db]
  (fn [{:keys [params]}]
    (let [filters {:search (:search params)
                   :role (:role params)
                   :trashed (:trashed params)}
          props {:users (db/retrieve-and-filter-apps-users db filters)
                 :filters filters}]
      (inertia/render "AppsUsers/Index" props))))

(defn user-form [_]
  (inertia/render "AppsUsers/Create"))

(defn store-user! [db]
  (fn [{:keys [body-params] :as req}]
    (if-let [errors (validate-unique-user db body-params)]
      (-> (rr/redirect "/users/create")
          (assoc :flash {:error errors}))
      (let [crby (-> req :identity :kode)
            user body-params
            encrypted-user (update user :password password/encrypt)
            user-created? (db/insert-apps-user! db (assoc encrypted-user :crby crby))]
        (when user-created?
          (-> (rr/redirect "/users")
              (assoc :flash {:success "User berhasil di create."})))))))

(defn edit-user! [db]
  (fn [{:keys [path-params]}]
    (let [props {:user (db/get-apps-user-by-id db (:user-id path-params))}]
      (inertia/render "AppsUsers/Edit" props))))

(defn update-user! [db]
  (fn [{:keys [body-params] :as req}]
    (let [id (-> req :path-params :user-id)
          url (str (-> req :uri) "/edit")]
      (if-let [errors (first (st/validate body-params user-schema))]
        (-> (rr/redirect url :see-other)
            (assoc :flash {:error errors}))
        (let [user-form (select-keys (:body-params req) [:kode :nama :almttbid :emai :telp :pswd :imag])
              user-updated? (db/update-apps-user! db user-form id)]
          (when user-updated?
            (-> (rr/redirect url :see-other)
                (assoc :flash {:success "User berhasil di update."}))))))))

(defn delete-user! [db]
  (fn [req]
    (let [id (-> req :path-params :user-id)
          back (get (:headers req) "referer")
          user-deleted? (db/soft-delete-apps-user! db id)]
      (when user-deleted?
        (-> (rr/redirect back :see-other)
            (assoc :flash {:success "User berhasil di delete."}))))))

(defn restore-user! [db]
  (fn [req]
    (let [id (-> req :path-params :user-id)
          back (get (:headers req) "referer")
          user-restored? (db/restore-deleted-apps-user! db id)]
      (when user-restored?
        (-> (rr/redirect back :see-other)
            (assoc :flash {:success "User berhasil dikembalikan."}))))))
