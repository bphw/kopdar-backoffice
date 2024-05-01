(ns kopdarbackoffice.models.rate-products
  (:require [honey.sql :as h]
            [honey.sql.helpers :refer [where]]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

(defn list-rate-products [db]
  (let [query (h/format {:select [:tbid :kode :nama]
                         :from [:kdstblrprdkdeta]
                         :order-by [:trno]})]
    (jdbc/execute! db query)))

(defn retrieve-and-filter-rate-products
  ([db filters]
   (retrieve-and-filter-rate-products db filters nil))
  ([db {:keys [search trashed]} offset]
   (let [query (h/format
                (cond-> {:select (if offset [:*] [[:%count.* :aggregate]])
                         :from [:kdstblrprdkdeta]
                         :order-by [:trno]}
                  offset (merge {:limit 10
                                 :offset offset})
                  search (where [:like :trno (str "%" search "%")])
                  true (where (case trashed
                                "with" nil
                                "only" [:<> :chdt nil]
                                [:= :chdt nil]))))]
     (jdbc/execute! db query))))

(defn get-rate-product-by-id
  [db id]
  (sql/get-by-id db :kdstblrprdkdeta id :tbid {}))

(defn insert-rate-product!
  [db rate]
  (let [query (h/format {:insert-into :kdstblrprdkdeta
                         :values [(merge rate {:crdt :current_timestamp
                                              :crtm :current_timestamp})]})]
    (jdbc/execute-one! db query)))

(defn update-rate-product!
  [db rate id]
  (sql/update! db :kdstblrprdkdeta rate {:tbid id}))

(defn soft-delete-rate-product!
  [db id]
  (let [query (h/format {:update :kdstblrprdkdeta
                         :set {:chdt :current_timestamp
                               :chtm :current_timestamp}
                         :where [:= :tbid id]})]
    (jdbc/execute-one! db query)))

(defn restore-deleted-rate-product!
  [db id]
  (let [query (h/format {:update :kdstblmastkat1
                         :set {:chdt :current_timestamp}
                         :where [:= :tbid id]})]
    (jdbc/execute-one! db query)))
