(ns kopdarbackoffice.handlers.etalases
  (:require [inertia.middleware :as inertia]
            [kopdarbackoffice.models.etalases :as db]
            [kopdarbackoffice.shared.pagination :as pagination]
            [ring.util.response :as rr]
            [struct.core :as st]))

(def etalase-schema
  [[:kode st/required st/string]
   [:nama st/required st/string]])

(defn index
  [db]
  (fn [{:keys [params uri query-string] :as request}]
    (let [filters (select-keys params [:search :trashed])
          page (get-in request [:parameters :query :page] 1)
          offset (* (dec page) 10)
          count (:aggregate (first (db/retrieve-and-filter-etalases db filters)))
          etalases (db/retrieve-and-filter-etalases db filters offset)
          ;stores-formated (map #(assoc % :product {:name (:product %)}) etalases)
          props {:etalases {:data etalases;stores-formated
                            :current_page page
                            :links (pagination/pagination-links uri query-string page count 10)}
                 :filters filters}]
      (inertia/render "Etalases/Index" props))))

(defn etalase-form [db]
  (fn [_]
    (inertia/render "Etalases/Create")))

(defn store-etalase! [db]
  (fn [{:keys [body-params] :as req}]
    (if-let [errors (first (st/validate body-params etalase-schema))]
      (-> (rr/redirect "/etalases/create")
          (assoc :flash {:error errors}))
      (let [crby (-> req :identity :kode)
            etalases-created? (db/insert-etalase! db (assoc body-params :crby crby))]
        (when etalases-created?
          (-> (rr/redirect "/etalases")
              (assoc :flash {:success "Etalase created."})))))))

(defn edit-etalase! [db]
  (fn [{:keys [path-params]}]
    (let [props {:etalase (db/get-etalase-by-id db (:etalase-id path-params))}]
      (inertia/render "Etalase/Edit" props))))

(defn update-etalase! [db]
  (fn [{:keys [body-params] :as req}]
    (let [id (-> req :path-params :store-id)
          url (str (-> req :uri) "/edit")]
      (if-let [errors (first (st/validate body-params etalase-schema))]
        (-> (rr/redirect url :see-other)
            (assoc :flash {:error errors}))
        (let [etalase-updated? (db/update-etalase! db body-params id)]
          (when etalase-updated?
            (-> (rr/redirect url :see-other)
                (assoc :flash {:success "Etalase updated."}))))))))

(defn delete-etalase! [db]
  (fn [req]
    (let [id (-> req :path-params :etalase-id)
          back (get (:headers req) "referer")
          etalase-deleted? (db/soft-delete-etalase! db id)]
      (when etalase-deleted?
        (-> (rr/redirect back :see-other)
            (assoc :flash {:success "Etalase deleted."}))))))

(defn restore-etalase! [db]
  (fn [req]
    (let [id (-> req :path-params :etalase-id)
          back (get (:headers req) "referer")
          etalase-restored? (db/restore-deleted-etalase! db id)]
      (when etalase-restored?
        (-> (rr/redirect back :see-other)
            (assoc :flash {:success "Etalase restored."}))))))
