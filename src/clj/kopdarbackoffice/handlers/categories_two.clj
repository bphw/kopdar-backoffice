(ns kopdarbackoffice.handlers.categories-two
  (:require [inertia.middleware :as inertia]
            [kopdarbackoffice.models.categories-two :as db]
            [kopdarbackoffice.shared.pagination :as pagination]
            [ring.util.response :as rr]
            [struct.core :as st]))

(def category-schema
  [[:nama st/required st/string]
   [:kode st/required st/string]])

(defn index
  [db]
  (fn [{:keys [params query-string uri] :as request}]
    (let [filters (select-keys params [:search :trashed])
          page (get-in request [:parameters :query :page] 1)
          offset (* (dec page) 10)
          count (:aggregate (first (db/retrieve-and-filter-categories db filters)))
          categories (db/retrieve-and-filter-categories db filters offset)
          props {:categories {:data categories
                            :current_page page
                            :links (pagination/pagination-links uri query-string page count 10)}
                 :filters filters}]
      (inertia/render "CategoriesTwo/Index" props))))

(defn category-form [_]
  (inertia/render "CategoriesTwo/Create"))

(defn store-category!
  [db]
  (fn [{:keys [body-params] :as req}]
    (if-let [errors (first (st/validate body-params category-schema))]
      (-> (rr/redirect "/categories/create")
          (assoc :flash {:error errors}))
      (let [crby (-> req :identity :kode)
            category-created? (db/insert-category! db (assoc body-params :crby crby))]
        (when category-created?
          (-> (rr/redirect "/categories")
              (assoc :flash {:success "Category created."})))))))

(defn edit-category!
  [db]
  (fn [{:keys [path-params]}]
    (let [props {:category (db/get-category-by-id db (:category-id path-params))}]
      (inertia/render "CategoriesTwo/Edit" props))))

(defn update-category!
  [db]
  (fn [{:keys [body-params] :as req}]
    (let [id (-> req :path-params :category-id)
          url (str (-> req :uri) "/edit")]
      (if-let [errors (first (st/validate body-params category-schema))]
        (-> (rr/redirect url :see-other)
            (assoc :flash {:error errors}))
        (let [category-updated? (db/update-category! db body-params id)]
             (when category-updated?
               (-> (rr/redirect url :see-other)
                   (assoc :flash {:success "Category updated."}))))))))

(defn delete-category!
  [db]
  (fn [req]
    (let [id (-> req :path-params :category-id)
          back (get (:headers req) "referer")
          category-deleted? (db/soft-delete-category! db id)]
      (when category-deleted?
        (-> (rr/redirect back :see-other)
            (assoc :flash {:success "Category deleted."}))))))

(defn restore-category!
  [db]
  (fn [req]
    (let [id (-> req :path-params :category-id)
          back (get (:headers req) "referer")
          category-restored? (db/restore-deleted-category! db id)]
      (when category-restored?
        (-> (rr/redirect back :see-other)
            (assoc :flash {:success "Category restored."}))))))
