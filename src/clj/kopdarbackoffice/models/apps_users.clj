(ns kopdarbackoffice.models.apps-users
  (:require [honey.sql :as h]
            [honey.sql.helpers :refer [where]]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

(defn list-apps-user [db]
  (let [query (h/format {:select [:tbid :kode :nama :almttbid :emai :telp :pswd]
                         :from [:kdstblmastuser]
                         :order-by [:nama]})]
    (jdbc/execute! db query)))

(defn retrieve-and-filter-apps-users
  ([db filters]
   (retrieve-and-filter-apps-users db filters nil))
  ([db {:keys [search trashed]} offset]
   (let [query (h/format
                (cond-> {:select (if offset [:*] [[:%count.* :aggregate]])
                         :from [:kdstblmastuser]
                         :order-by [:nama]}
                  offset (merge {:limit 10
                                 :offset offset})
                  search (where [:like :nama (str "%" search "%")])
                  true (where (case trashed
                                "with" nil
                                "only" [:<> :chdt nil]
                                [:= :chdt nil]))))]
     (jdbc/execute! db query))))

(defn get-apps-user-by-id
  [db id]
  (let [user (sql/get-by-id db :kdstblmastuser id :tbid {})
        comp (sql/get-by-id db :kdstblusercomp_ id :usertbid {})
        company-id (:comptbid comp)
        company (sql/get-by-id db :kdstblmastcomp_ company-id :tbid {})
        sanitized-user (dissoc user :pswd)]
    (assoc sanitized-user :company company))
  )

(defn get-apps-user-by-email
  [db email]
  (sql/get-by-id db :kdstblmastuser email :emai {}))

(defn insert-apps-user!
  [db cat]
  (let [query (h/format {:insert-into :kdstblmastuser
                         :values [(merge cat {:crdt :current_timestamp
                                              :crtm :current_timestamp})]})]
    (jdbc/execute-one! db query)))

(defn update-apps-user!
  [db cat id]
  (sql/update! db :kdstblmastuser cat {:tbid id}))

(defn soft-delete-apps-user!
  [db id]
  (let [query (h/format {:update :kdstblmastuser
                         :set {:chdt :current_timestamp
                               :chtm :current_timestamp}
                         :where [:= :tbid id]})]
    (jdbc/execute-one! db query)))

(defn restore-deleted-apps-user!
  [db id]
  (let [query (h/format {:update :kdstblmastuser
                         :set {:chdt :current_timestamp}
                         :where [:= :tbid id]})]
    (jdbc/execute-one! db query)))
