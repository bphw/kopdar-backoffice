(ns kopdarbackoffice.models.trx-details
  (:require [honey.sql :as h]
            [honey.sql.helpers :refer [where]]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

(defn list-trx-details [db]
  (let [query (h/format {:select [:tbid :trno :trit :prdktbid :prdkkode :prdknama :prdkhjua :item :quan :disc :potg :pajk :gamt :namt]
                         :from [:kdstbltjuadeta]
                         :order-by [:nama]})]
    (jdbc/execute! db query)))

(defn retrieve-and-filter-trx-details
  ([db filters]
   (retrieve-and-filter-trx-details db filters nil))
  ([db {:keys [search trashed]} offset]
   (let [query (h/format
                (cond-> {:select (if offset [:*] [[:%count.* :aggregate]])
                         :from [:kdstbltjuadeta]
                         :order-by [:trno]}
                  offset (merge {:limit 10
                                 :offset offset})
                  search (where [:like :trno (str "%" search "%")])
                  true (where (case trashed
                                "with" nil
                                "only" [:<> :chdt nil]
                                [:= :chdt nil]))))]
     (jdbc/execute! db query))))

(defn get-trx-detail-by-id
  [db id]
  (sql/get-by-id db :kdstbltjuadeta id :tbid {}))

(defn insert-trx-detail!
  [db trx]
  (let [query (h/format {:insert-into :kdstbltjuadeta
                         :values [(merge trx {:crdt :current_timestamp
                                              :crtm :current_timestamp})]})]
    (jdbc/execute-one! db query)))

(defn update-trx-detail!
  [db trx id]
  (sql/update! db :kdstbltjuadeta trx {:tbid id}))

(defn soft-delete-trx-detail!
  [db id]
  (let [query (h/format {:update :kdstbltjuadeta
                         :set {:chdt :current_timestamp
                               :chtm :current_timestamp}
                         :where [:= :tbid id]})]
    (jdbc/execute-one! db query)))

(defn restore-deleted-trx-detail!
  [db id]
  (let [query (h/format {:update :kdstbltjuadeta
                         :set {:chdt :current_timestamp}
                         :where [:= :tbid id]})]
    (jdbc/execute-one! db query)))
