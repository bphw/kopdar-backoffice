(ns kopdarbackoffice.models.rate-stores
  (:require [honey.sql :as h]
            [honey.sql.helpers :refer [where]]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

(defn list-rate-stores [db]
  (let [query (h/format {:select [:tbid :kode :nama]
                         :from [:kdstblrtokhead]
                         :order-by [:nama]})]
    (jdbc/execute! db query)))

(defn retrieve-and-filter-rate-stores
  ([db filters]
   (retrieve-and-filter-rate-stores db filters nil))
  ([db {:keys [search trashed]} offset]
   (let [query (h/format
                (cond-> {:select (if offset [:*] [[:%count.* :aggregate]])
                         :from [:kdstblmastkat1]
                         :order-by [:trno]}
                  offset (merge {:limit 10
                                 :offset offset})
                  search (where [:like :trno (str "%" search "%")])
                  true (where (case trashed
                                "with" nil
                                "only" [:<> :chdt nil]
                                [:= :chdt nil]))))]
     (jdbc/execute! db query))))

(defn get-rate-store-by-id
  [db id]
  (sql/get-by-id db :kdstblrtokhead id :tbid {}))

(defn insert-rate-store!
  [db rate]
  (let [query (h/format {:insert-into :kdstblrtokhead
                         :values [(merge rate {:crdt :current_timestamp
                                              :crtm :current_timestamp})]})]
    (jdbc/execute-one! db query)))

(defn update-rate-store!
  [db rate id]
  (sql/update! db :kdstblrtokhead rate {:tbid id}))

(defn soft-delete-rate-store!
  [db id]
  (let [query (h/format {:update :kdstblrtokhead
                         :set {:chdt :current_timestamp
                               :chtm :current_timestamp}
                         :where [:= :tbid id]})]
    (jdbc/execute-one! db query)))

(defn restore-deleted-rate-store!
  [db id]
  (let [query (h/format {:update :kdstblrtokhead
                         :set {:chdt :current_timestamp}
                         :where [:= :tbid id]})]
    (jdbc/execute-one! db query)))
