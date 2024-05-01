(ns kopdarbackoffice.models.payment-methods
  (:require [honey.sql :as h]
            [honey.sql.helpers :refer [where]]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

(defn list-payment-methods [db]
  (let [query (h/format {:select [:tbid :kode :nama]
                         :from [:kdstblmastmethbayr]
                         :order-by [:nama]})]
    (jdbc/execute! db query)))

(defn retrieve-and-filter-payment-methods
  ([db filters]
   (retrieve-and-filter-payment-methods db filters nil))
  ([db {:keys [search trashed]} offset]
   (let [query (h/format
                (cond-> {:select (if offset [:*] [[:%count.* :aggregate]])
                         :from [:kdstblmastmethbayr]
                         :order-by [:nama]}
                  offset (merge {:limit 10
                                 :offset offset})
                  search (where [:like :nama (str "%" search "%")])
                  true (where (case trashed
                                "with" nil
                                "only" [:<> :chdt nil]
                                [:= :chdt nil]))))]
     (jdbc/execute! db query))))

(defn get-payment-method-by-id
  [db id]
  (sql/get-by-id db :kdstblmastmethbayr id :tbid {}))

(defn insert-payment-method!
  [db method]
  (let [query (h/format {:insert-into :kdstblmastmethbayr
                         :values [(merge method {:crdt :current_timestamp
                                              :crtm :current_timestamp})]})]
    (jdbc/execute-one! db query)))

(defn update-payment-method!
  [db method id]
  (sql/update! db :kdstblmastmethbayr method {:tbid id}))

(defn soft-delete-payment-method!
  [db id]
  (let [query (h/format {:update :kdstblmastmethbayr
                         :set {:chdt :current_timestamp
                               :chtm :current_timestamp}
                         :where [:= :tbid id]})]
    (jdbc/execute-one! db query)))

(defn restore-deleted-payment-method!
  [db id]
  (let [query (h/format {:update :kdstblmastmethbayr
                         :set {:chdt :current_timestamp}
                         :where [:= :tbid id]})]
    (jdbc/execute-one! db query)))
