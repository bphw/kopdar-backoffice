(ns kopdarbackoffice.models.products
  (:require [honey.sql :as h]
            [honey.sql.helpers :refer [where]]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

(defn list-products [db]
  (let [query (h/format {:select [:tbid :nama :hjua :imag :dsc1]
                         :from [:kdstblmastprdk]
                         ;;:where [:and
                         ;;        [:= :chdt nil]]
                         :order-by [:nama]})]
    (jdbc/execute! db query)))

(defn retrieve-and-filter-products
  ([db filters]
   (retrieve-and-filter-products db filters nil))
  ([db {:keys [search trashed]} offset]
   (let [query (h/format
                (cond-> {:select (if offset [[:p.*] [:t.nama :kdstblmasttoko]] [[:%count.* :aggregate]])
                         :from [[:kdstblmastprdk :p]]
                         :left-join [[:kdstblmasttoko :t] [:= :p.tokotbid :t.tbid]]
                         :order-by [:p.nama]}
                  offset (merge {:limit 10
                                 :offset offset})
                  search (where [:or [:like :p.nama (str "%" search "%")]
                                 [:like :t.nama (str "%" search "%")]])
                  true (where (case trashed
                                "with" nil
                                "only" [:= :p.chdt nil]
                                [:<> :p.chdt nil]))))]
     (jdbc/execute! db query))))

(defn get-product-by-id
  [db id]
  ;;TODO: Use with-open for better performance
  (sql/get-by-id db :kdstblmastprdk id :tbid {}))

(defn get-product-and-detail-by-id
  [db id]
  (let [product (sql/get-by-id db :kdstblmastprdk id :tbid {})
        images (sql/find-by-keys db :kdstblmastprdkimag_ {:prdktbid id})
        detail (sql/find-by-keys db :kdstblmastprdkdeta_ {:prdktbid id})]
    (assoc product :images images :detail detail)))

(defn insert-product!
  [db product]
  (let [query (h/format {:insert-into :kdstblmastprdk
                         :values [(merge product {:crdt :current_timestamp
                                                  :crtm :current_timestamp})]})]
    (jdbc/execute-one! db query)))

(defn update-product!
  [db product id]
  (sql/update! db :kdstblmastprdk product {:tbid id}))

(defn soft-delete-product!
  [db id]
  (let [query (h/format {:update :kdstblmastprdk
                         :set {:chdt :current_timestamp
                               :chtm :current_timestamp}
                         :where [:= :tbid id]})]
    (jdbc/execute-one! db query)))

(defn restore-deleted-product!
  [db id]
  (let [query (h/format {:update :kdstblmastprdk
                         :set {:crdt :current_timestamp
                               :crtm :current_timestamp}
                         :where [:= :tbid id]})]
    (jdbc/execute-one! db query)))
