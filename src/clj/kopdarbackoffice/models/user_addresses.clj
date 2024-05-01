(ns kopdarbackoffice.models.user-addresses
  (:require [honey.sql :as h]
            [honey.sql.helpers :refer [where]]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

(defn list-user-addresses [db]
  (let [query (h/format {:select [:tbid :kode :nama]
                         :from [:kdstblmastalmtuser]
                         :order-by [:nama]})]
    (jdbc/execute! db query)))

(defn retrieve-and-filter-user-addresses
  ([db filters]
   (retrieve-and-filter-user-addresses db filters nil))
  ([db {:keys [search trashed]} offset]
   (let [query (h/format
                (cond-> {:select (if offset [:*] [[:%count.* :aggregate]])
                         :from [:kdstblalmtuser]
                         :order-by [:usertbid]}
                  offset (merge {:limit 10
                                 :offset offset})
                  search (where [:like :nama (str "%" search "%")])
                  true (where (case trashed
                                "with" nil
                                "only" [:<> :chdt nil]
                                [:= :chdt nil]))))]
     (jdbc/execute! db query))))

(defn get-user-address-by-id
  [db id]
  (sql/get-by-id db :kdstblalmtuser id :tbid {}))

(defn insert-user-address!
  [db addr]
  (let [query (h/format {:insert-into :kdstblalmtuser
                         :values [(merge addr {:crdt :current_timestamp
                                              :crtm :current_timestamp})]})]
    (jdbc/execute-one! db query)))

(defn update-user-address!
  [db addr id]
  (sql/update! db :kdstblalmtuser addr {:tbid id}))

(defn soft-delete-user-address!
  [db id]
  (let [query (h/format {:update :kdstblalmtuser
                         :set {:chdt :current_timestamp
                               :chtm :current_timestamp}
                         :where [:= :tbid id]})]
    (jdbc/execute-one! db query)))

(defn restore-deleted-user-address!
  [db id]
  (let [query (h/format {:update :kdstblalmtuser
                         :set {:chdt :current_timestamp}
                         :where [:= :tbid id]})]
    (jdbc/execute-one! db query)))
