(ns kopdarbackoffice.models.etalases
  (:require [honey.sql :as h]
            [honey.sql.helpers :refer [where]]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

(defn list-etalases [db]
  (let [query (h/format {:select [:tbid :kode :nama]
                         :from [:kdstblmastetls]
                         :order-by [:nama]})]
    (jdbc/execute! db query)))

(defn retrieve-and-filter-etalases
  ([db filters]
   (retrieve-and-filter-etalases db filters nil))
  ([db {:keys [search trashed]} offset]
   (let [query (h/format
                (cond-> {:select (if offset [:*] [[:%count.* :aggregate]])
                         :from [:kdstblmastetls]
                         :order-by [:nama]}
                  offset (merge {:limit 10
                                 :offset offset})
                  search (where [:like :nama (str "%" search "%")])
                  true (where (case trashed
                                "with" nil
                                "only" [:<> :chdt nil]
                                [:= :chdt nil]))))]
     (jdbc/execute! db query))))

(defn get-etalase-by-id
  [db id]
  (sql/get-by-id db :kdstblmastetls id :tbid {}))

(defn insert-etalase!
  [db etalase]
  (let [query (h/format {:insert-into :kdstblmastetls
                         :values [(merge etalase {:crdt :current_timestamp
                                              :crtm :current_timestamp})]})]
    (jdbc/execute-one! db query)))

(defn update-etalase!
  [db etalase id]
  (sql/update! db :kdstblmastka1 etalase {:tbid id}))

(defn soft-delete-etalase!
  [db id]
  (let [query (h/format {:update :kdstblmastetls
                         :set {:chdt :current_timestamp
                               :chtm :current_timestamp}
                         :where [:= :tbid id]})]
    (jdbc/execute-one! db query)))

(defn restore-deleted-etalase!
  [db id]
  (let [query (h/format {:update :kdstblmastetls
                         :set {:chdt :current_timestamp}
                         :where [:= :tbid id]})]
    (jdbc/execute-one! db query)))
