(ns kopdarbackoffice.models.trx-headers
  (:require [honey.sql :as h]
            [honey.sql.helpers :refer [where]]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

(defn list-trx-headers [db]
  (let [query (h/format {:select [:tbid :trno :trit :tokotbid :custtbid :almttbid :kirimtbid :mbyrtbid :quanitem :quantota :disctota :potgtota :gamttota :namttota :hkir :stat]
                         :from [:kdstbltjuahead]
                         :order-by [:nama]})]
    (jdbc/execute! db query)))

(defn retrieve-and-filter-trx-headers
  ([db filters]
   (retrieve-and-filter-trx-headers db filters nil))
  ([db {:keys [search trashed]} offset]
   (let [query (h/format
                (cond-> {:select (if offset [:*] [[:%count.* :aggregate]])
                         :from [:kdstbltjuahead]
                         :order-by [:trno]}
                  offset (merge {:limit 10
                                 :offset offset})
                  search (where [:like :trno (str "%" search "%")])
                  true (where (case trashed
                                "with" nil
                                "only" [:<> :chdt nil]
                                [:= :chdt nil]))))]
     (jdbc/execute! db query))))

(defn get-trx-header-by-id
  [db id]
  (sql/get-by-id db :kdstbltjuahead id :tbid {}))

(defn insert-trx-header!
  [db trx]
  (let [query (h/format {:insert-into :kdstbltjuahead
                         :values [(merge trx {:crdt :current_timestamp
                                              :crtm :current_timestamp})]})]
    (jdbc/execute-one! db query)))

(defn update-trx-header!
  [db trx id]
  (sql/update! db :kdstbltjuahead trx {:tbid id}))

(defn soft-delete-trx-header!
  [db id]
  (let [query (h/format {:update :kdstbltjuahead
                         :set {:chdt :current_timestamp
                               :chtm :current_timestamp}
                         :where [:= :tbid id]})]
    (jdbc/execute-one! db query)))

(defn restore-deleted-trx-header!
  [db id]
  (let [query (h/format {:update :kdstbltjuahead
                         :set {:chdt :current_timestamp}
                         :where [:= :tbid id]})]
    (jdbc/execute-one! db query)))
