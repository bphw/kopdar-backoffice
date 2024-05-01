(ns kopdarbackoffice.models.user-groups
  (:require [honey.sql :as h]
            [honey.sql.helpers :refer [where]]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

(defn list-user-groups [db]
  (let [query (h/format {:select [:tbid :kode :nama]
                         :from [:kdstblmastugrp]
                         :order-by [:nama]})]
    (jdbc/execute! db query)))

(defn retrieve-and-filter-user-groups
  ([db filters]
   (retrieve-and-filter-user-groups db filters nil))
  ([db {:keys [search trashed]} offset]
   (let [query (h/format
                (cond-> {:select (if offset [:*] [[:%count.* :aggregate]])
                         :from [:kdstblmasturp]
                         :order-by [:nama]}
                  offset (merge {:limit 10
                                 :offset offset})
                  search (where [:like :nama (str "%" search "%")])
                  true (where (case trashed
                                "with" nil
                                "only" [:<> :chdt nil]
                                [:= :chdt nil]))))]
     (jdbc/execute! db query))))

(defn get-user-group-by-id
  [db id]
  (sql/get-by-id db :kdstblmastugrp id :tbid {}))

(defn insert-user-group!
  [db grp]
  (let [query (h/format {:insert-into :kdstblmastugrp
                         :values [(merge grp {:crdt :current_timestamp
                                              :crtm :current_timestamp})]})]
    (jdbc/execute-one! db query)))

(defn update-user-group!
  [db grp id]
  (sql/update! db :kdstblmastugrp grp {:tbid id}))

(defn soft-delete-user-group!
  [db id]
  (let [query (h/format {:update :kdstblmastugrp
                         :set {:chdt :current_timestamp
                               :chtm :current_timestamp}
                         :where [:= :tbid id]})]
    (jdbc/execute-one! db query)))

(defn restore-deleted-user-group!
  [db id]
  (let [query (h/format {:update :kdstblmastugrp
                         :set {:chdt :current_timestamp}
                         :where [:= :tbid id]})]
    (jdbc/execute-one! db query)))
