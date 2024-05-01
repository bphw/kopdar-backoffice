(ns kopdarbackoffice.models.chats
  (:require [honey.sql :as h]
            [honey.sql.helpers :refer [where]]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

(defn list-chats [db]
  (let [query (h/format {:select [:tbid :trno :trit :tokotbid :usertbid :custtbid :sendtbid :recptbid :dsc1]
                         :from [:kdstblchathead]
                         :order-by [:nama]})]
    (jdbc/execute! db query)))

(defn retrieve-and-filter-chats
  ([db filters]
   (retrieve-and-filter-chats db filters nil))
  ([db {:keys [search trashed]} offset]
   (let [query (h/format
                (cond-> {:select (if offset [:*] [[:%count.* :aggregate]])
                         :from [:kdstblchathead]
                         :order-by [:trno]}
                  offset (merge {:limit 10
                                 :offset offset})
                  search (where [:like :trno (str "%" search "%")])
                  true (where (case trashed
                                "with" nil
                                "only" [:<> :chdt nil]
                                [:= :chdt nil]))))]
     (jdbc/execute! db query))))

(defn get-chat-by-id
  [db id]
  (sql/get-by-id db :kdstblchathead id :tbid {}))

(defn insert-chat!
  [db cat]
  (let [query (h/format {:insert-into :kdstblchathead
                         :values [(merge cat {:crdt :current_timestamp
                                              :crtm :current_timestamp})]})]
    (jdbc/execute-one! db query)))

(defn update-chat!
  [db cat id]
  (sql/update! db :kdstblchathead cat {:tbid id}))

(defn soft-delete-chat!
  [db id]
  (let [query (h/format {:update :kdstblchathead
                         :set {:chdt :current_timestamp
                               :chtm :current_timestamp}
                         :where [:= :tbid id]})]
    (jdbc/execute-one! db query)))

(defn restore-deleted-chat!
  [db id]
  (let [query (h/format {:update :kdstblchathead
                         :set {:chdt :current_timestamp}
                         :where [:= :tbid id]})]
    (jdbc/execute-one! db query)))
