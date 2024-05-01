(ns kopdarbackoffice.handlers.chats
  (:require [inertia.middleware :as inertia]
            [kopdarbackoffice.models.chats :as db]
            [kopdarbackoffice.shared.pagination :as pagination]
            [ring.util.response :as rr]
            [struct.core :as st]))

(def chat-schema
  [[:trno st/required st/string]
   [:trit st/required st/string]
   [:tokotbid st/required st/number]
   [:usertbid st/required st/number]
   [:custtbid st/required st/number]
   [:sendtbid st/required st/number]
   [:recptbid st/required st/string]
   [:subj st/string]
   [:stat st/string]
   [:dsc1 st/string]])

(defn index
  [db]
  (fn [{:keys [params uri query-string] :as request}]
    (let [filters (select-keys params [:search :trashed])
          page (get-in request [:parameters :query :page] 1)
          offset (* (dec page) 10)
          count (:aggregate (first (db/retrieve-and-filter-chats db filters)))
          chats (db/retrieve-and-filter-chats db filters offset)
          props {:chats {:data chats
                            :current_page page
                            :links (pagination/pagination-links uri query-string page count 10)}
                 :filters filters}]
      (inertia/render "Chats/Index" props))))

(defn chats-form [db]
  (fn [_]
      (inertia/render "Chats/Create")))

(defn store-chat! [db]
  (fn [{:keys [body-params] :as req}]
    (if-let [errors (first (st/validate body-params chat-schema))]
      (-> (rr/redirect "/chats/create")
          (assoc :flash {:error errors}))
      (let [crby (-> req :identity :kode)
            chats-created? (db/insert-chat! db (assoc body-params :crby crby))]
        (when chats-created?
          (-> (rr/redirect "/chats")
              (assoc :flash {:success "Chat created."})))))))

(defn edit-chat! [db]
  (fn [{:keys [path-params]}]
    (let [props {:delivery (db/get-chat-by-id db (:chat-id path-params))}]
      (inertia/render "Chats/Edit" props))))

(defn update-chat! [db]
  (fn [{:keys [body-params] :as req}]
    (let [id (-> req :path-params :chat-id)
          url (str (-> req :uri) "/edit")]
      (if-let [errors (first (st/validate body-params chat-schema))]
        (-> (rr/redirect url :see-other)
            (assoc :flash {:error errors}))
        (let [chat-updated? (db/update-chat! db body-params id)]
          (when chat-updated?
            (-> (rr/redirect url :see-other)
                (assoc :flash {:success "Chat updated."}))))))))

(defn delete-chat! [db]
  (fn [req]
    (let [id (-> req :path-params :chat-id)
          back (get (:headers req) "referer")
          chat-deleted? (db/soft-delete-chat! db id)]
      (when chat-deleted?
        (-> (rr/redirect back :see-other)
            (assoc :flash {:success "Chat deleted."}))))))

(defn restore-chat! [db]
  (fn [req]
    (let [id (-> req :path-params :chat-id)
          back (get (:headers req) "referer")
          chat-restored? (db/restore-deleted-chat! db id)]
      (when chat-restored?
        (-> (rr/redirect back :see-other)
            (assoc :flash {:success "Chat restored."}))))))
