(ns kopdarbackoffice.models.deliveries
  (:require [honey.sql :as h]
            [honey.sql.helpers :refer [where]]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

(defn list-deliveries [db]
  (let [query (h/format {:select [:tbid :kode :nama]
                         :from [:kdstblmastpengiriman]
                         :order-by [:nama]})]
    (jdbc/execute! db query)))

(defn retrieve-and-filter-deliveries
  ([db filters]
   (retrieve-and-filter-deliveries db filters nil))
  ([db {:keys [search trashed]} offset]
   (let [query (h/format
                (cond-> {:select (if offset [:*] [[:%count.* :aggregate]])
                         :from [:kdstblmastpengiriman]
                         :order-by [:nama]}
                  offset (merge {:limit 10
                                 :offset offset})
                  search (where [:like :nama (str "%" search "%")])
                  true (where (case trashed
                                "with" nil
                                "only" [:<> :chdt nil]
                                [:= :chdt nil]))))]
     (jdbc/execute! db query))))

(defn get-delivery-by-id
  [db id]
  (sql/get-by-id db :kdstblmastpengiriman id :tbid {}))

(defn insert-delivery!
  [db delv]
  (let [query (h/format {:insert-into :kdstblmastpengiriman
                         :values [(merge delv {:crdt :current_timestamp
                                              :crtm :current_timestamp})]})]
    (jdbc/execute-one! db query)))

(defn update-delivery!
  [db delv id]
  (sql/update! db :kdstblmastpengiriman cat {:tbid id}))

(defn soft-delete-delivery!
  [db id]
  (let [query (h/format {:update :kdstblmastpengiriman
                         :set {:chdt :current_timestamp
                               :chtm :current_timestamp}
                         :where [:= :tbid id]})]
    (jdbc/execute-one! db query)))

(defn restore-deleted-delivery!
  [db id]
  (let [query (h/format {:update :kdstblmastpengiriman
                         :set {:chdt :current_timestamp}
                         :where [:= :tbid id]})]
    (jdbc/execute-one! db query)))
