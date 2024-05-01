(ns kopdarbackoffice.models.categories-one
  (:require [honey.sql :as h]
            [honey.sql.helpers :refer [where]]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

;; cats = categories
(defn list-first-cats [db]
  (let [query (h/format {:select [:tbid :kode :nama]
                         :from [:kdstblmastkat1]
                         :order-by [:nama]})]
    (jdbc/execute! db query)))

(defn retrieve-and-filter-categories
  ([db filters]
   (retrieve-and-filter-categories db filters nil))
  ([db {:keys [search trashed]} offset]
   (let [query (h/format
                (cond-> {:select (if offset [:*] [[:%count.* :aggregate]])
                         :from [:kdstblmastkat1]
                         :order-by [:nama]}
                  offset (merge {:limit 10
                                 :offset offset})
                  search (where [:like :nama (str "%" search "%")])
                  true (where (case trashed
                                "with" nil
                                "only" [:= :chdt nil]
                                [:<> :chdt nil]))))]
     (jdbc/execute! db query))))

(defn get-category-by-id
  [db id]
  (sql/get-by-id db :kdstblmastkat1 id :tbid {}))

(defn insert-category!
  [db cat]
  (let [query (h/format {:insert-into :kdstblmastkat1
                         :values [(merge cat {:crdt :current_timestamp
                                              :crtm :current_timestamp})]})]
    (jdbc/execute-one! db query)))

(defn update-category!
  [db cat id]
  (sql/update! db :kdstblmastka1 cat {:tbid id}))

(defn soft-delete-category!
  [db id]
  (let [query (h/format {:update :kdstblmastkat1
                         :set {:chdt :current_timestamp
                               :chtm :current_timestamp}
                         :where [:= :tbid id]})]
    (jdbc/execute-one! db query)))

(defn restore-deleted-category!
  [db id]
  (let [query (h/format {:update :kdstblmastkat1
                         :set {:chdt :current_timestamp}
                         :where [:= :tbid id]})]
    (jdbc/execute-one! db query)))
