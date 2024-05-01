(ns kopdarbackoffice.models.trx-statuses
  (:require [honey.sql :as h]
            [honey.sql.helpers :refer [where]]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

(defn list-trx-statuses [db]
  (let [query (h/format {:select [:tbid :kode :nama]
                         :from [:kdstblmastkat1]
                         :order-by [:nama]})]
    (jdbc/execute! db query)))

(defn retrieve-and-filter-trx-statuses
  ([db filters]
   (retrieve-and-filter-trx-statuses db filters nil))
  ([db {:keys [search trashed]} offset]
   (let [query (h/format
                (cond-> {:select (if offset [:*] [[:%count.* :aggregate]])
                         :from [:kdstblmasttranstat]
                         :order-by [:nama]}
                  offset (merge {:limit 10
                                 :offset offset})
                  search (where [:like :nama (str "%" search "%")])
                  true (where (case trashed
                                "with" nil
                                "only" [:<> :chdt nil]
                                [:= :chdt nil]))))]
     (jdbc/execute! db query))))

(defn get-trx-status-by-id
  [db id]
  (sql/get-by-id db :kdstblmasttranstat id :tbid {}))

(defn insert-trx-status!
  [db trx]
  (let [query (h/format {:insert-into :kdstblmasttranstat
                         :values [(merge trx {:crdt :current_timestamp
                                              :crtm :current_timestamp})]})]
    (jdbc/execute-one! db query)))

(defn update-trx-status!
  [db trx id]
  (sql/update! db :kdstblmasttranstat trx {:tbid id}))

(defn soft-delete-trx-status!
  [db id]
  (let [query (h/format {:update :kdstblmasttranstat
                         :set {:chdt :current_timestamp
                               :chtm :current_timestamp}
                         :where [:= :tbid id]})]
    (jdbc/execute-one! db query)))

(defn restore-deleted-trx-status!
  [db id]
  (let [query (h/format {:update :kdstblmasttranstat
                         :set {:chdt :current_timestamp}
                         :where [:= :tbid id]})]
    (jdbc/execute-one! db query)))
