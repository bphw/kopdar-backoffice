(ns kopdarbackoffice.models.stores
  (:require [honey.sql :as h]
            [honey.sql.helpers :refer [where]]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

(defn list-stores [db]
  (let [query (h/format {:select [:tbid :kode :nama :almttbid :emai :telp]
                         :from [:kdstblmasttoko]
                         :order-by [:nama]})]
    (jdbc/execute! db query)))

(defn retrieve-and-filter-stores
  ([db filters]
   (retrieve-and-filter-stores db filters nil))
  ([db {:keys [search trashed]} offset]
   (let [query (h/format
                (cond-> {:select (if offset [:*] [[:%count.* :aggregate]])
                         :from [:kdstblmasttoko]
                         :order-by [:nama]}
                  offset (merge {:limit 10
                                 :offset offset})
                  search (where [:like :nama (str "%" search "%")])
                  true (where (case trashed
                                "with" nil
                                "only" [:= :chdt nil]
                                ;; by default all records currently predefined as chdt has no nil value
                                [:<> :chdt nil]))))]
     (jdbc/execute! db query))))

(defn get-store-by-id
  [db id]
  ;;TODO: Use with-open for better performance
  (let [store (sql/get-by-id db :kdstblmasttoko id :tbid {})
        products (sql/find-by-keys db :kdstblmastprdk {:tokotbid id} {:columns [:tbid :tokotbid :kode :nama :hjua :dsc1]})]
    (assoc store :products products)))

(defn insert-store!
  [db store]
  (let [query (h/format {:insert-into :kdstblmasttoko
                         :values [(merge store {:crdt :current_timestamp
                                                :crtm :current_timestamp})]})]
    (jdbc/execute-one! db query)))

(defn update-store!
  [db store id]
  (sql/update! db :kdstblmasttoko store {:tbid id}))

(defn soft-delete-store!
  [db id]
  (let [query (h/format {:update :kdstblmasttoko
                         :set {:chdt :current_timestamp
                               :chtm :current_timestamp}
                         :where [:= :tbid id]})]
    (jdbc/execute-one! db query)))

(defn restore-deleted-store!
  [db id]
  (let [query (h/format {:update :kdstblmasttoko
                         :set {:chdt :current_timestamp}
                         :where [:= :tbid id]})]
    (jdbc/execute-one! db query)))
