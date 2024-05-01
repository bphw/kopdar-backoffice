(ns kopdarbackoffice.pages.user-groups
  (:require ["@inertiajs/inertia" :refer [Inertia]]
            ["@inertiajs/inertia-react" :refer [InertiaLink useForm Head]]
            [applied-science.js-interop :as j]
            [kopdarbackoffice.shared.buttons :refer [loading-button delete-button]]
            [kopdarbackoffice.shared.form-input :refer [text-input select-input]]
            [kopdarbackoffice.shared.icon :refer [icon]]
            [kopdarbackoffice.shared.search-filter :refer [search-filter]]
            [kopdarbackoffice.shared.trashed-message :refer [trashed-message]]))

(defn index [{:keys [groups]}]
  [:div
   [:> Head {:title "User Groups"}]
   [:h1 {:class "mb-8 text-3xl font-bold"} "User Groups"]
   [:div {:class "flex items-center justify-between mb-6"}
    [:f> search-filter]
    [:> InertiaLink {:class "btn-indigo focus:outline-none"
                     :href (js/route "user-groups.create")}
     [:span "Create"]
     [:span {:class "hidden md:inline"} " User Groups"]]]
   [:div {:class "overflow-x-auto bg-white rounded shadow"}
    [:table {:class "w-full whitespace-nowrap"}
     [:thead
      [:tr {:class "font-bold text-left"}
       [:th {:class "px-6 pt-5 pb-4"} "Kode"]
       [:th {:class "px-6 pt-5 pb-4" :col-span "2"} "Nama"]]]
     [:tbody
      (for [grp groups
            :let [{:keys [tbid kode nama]} (j/lookup grp)]]
        [:tr {:class "hover:bg-gray-100 focus-within:bg-gray-100"
              :key tbid}
         [:td {:class "border-t"}
          [:> InertiaLink {:href (js/route "user-groups.edit" tbid)
                           :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
           kode
           ;(when deleted_at
           ;  [icon {:name :trash
           ;         :class "flex-shrink-0 w-3 h-3 ml-2 text-gray-400 fill-current"}])
           ]]
         [:td {:class "border-t"}
          [:> InertiaLink {:href (js/route "users-groups.edit" tbid)
                           :tab-index "-1"
                           :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
           nama]]
         [:td {:class "w-px border-t"}
          [:> InertiaLink {:href (js/route "user-groups.edit" tbid)
                           :tab-index "-1"
                           :class "flex items-center px-4 focus:outline-none"}
           [icon {:name :cheveron-right
                  :class "block w-6 h-6 text-gray-400 fill-current"}]]]])
      (when (zero? (count groups))
        [:tr
         [:td {:class "px-6 py-4 border-t"
               :col-span "4"}
          "No Group found."]])]]]])

(defn create-form []
  (let [{:keys [data setData errors post processing]}
        (j/lookup (useForm #js {:kode ""
                                :nama ""
                                }))
        on-submit #(do (.preventDefault %)
                       (post (js/route "user-groups.store")))]
    [:div
     [:> Head {:title "Create Group"}]
     [:h1 {:class "mb-8 text-3xl font-bold"}
      [:> InertiaLink {:href (js/route "user-groups")
                       :class "text-indigo-400 hover:text-indigo-600"}
       "User Address" [:span {:class "font-medium text-indigo-400"} " / "]]
      "Create"]
     [:div {:class "max-w-3xl overflow-hidden bg-white rounded shadow"}
      [:form {:on-submit on-submit}
       [:div {:class "flex flex-wrap p-8 -mb-8 -mr-6"}
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Kode"
                     :name "kode"
                     :errors (.-kode errors)
                     :value (.-kode data)
                     :on-change #(setData "kode" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Nama"
                     :name "nama"
                     :errors (.-nama errors)
                     :value (.-nama data)
                     :on-change #(setData "nama" (.. % -target -value))}]
        ]
       [:div {:class "px-8 py-4 bg-gray-50 border-t border-gray-100 flex justify-end items-center"}
        [loading-button {:loading processing
                         :type "submit"
                         :class "btn-indigo"}
         "Create User Group"]]]]]))

(defn edit-form [^js grp]
  (let [{:keys [data setData errors post processing]}
        (j/lookup (useForm #js {:kode (or (.-kode grp) "")
                                :nama (or (.-nama grp) "")
                                }))
        on-submit #(do (.preventDefault %)
                       (post (js/route "user-groups.update" (.-tbid grp))))
        destroy #(when (js/confirm "Are you sure you want to delete this group?")
                   (.delete Inertia (js/route "user-groups.destroy" (.-tbid grp))))
        restore #(when (js/confirm "Are you sure you want to restore this group?")
                   (.put Inertia (js/route "user-groups.restore" (.-tbid grp))))]
    [:<>
     [:> Head {:title (str (j/get grp :kode) " " (j/get grp :nama))}]
     [:div {:class "flex justify-start max-w-lg mb-8"}
      [:h1 {:class "text-3xl font-bold"}
       [:> InertiaLink {:class "text-indigo-400 hover:text-indigo-700"
                        :href (js/route "user-groups")} "User Groups"]
       [:span {:class "mx-2 font-medium text-indigo-400"} "/"]
       (.-kode data) " " (.-nama data)]]
     ;(when-not (empty? (j/get user :deleted_at))
     ;  [trashed-message {:on-restore restore}
     ;   "This user has been deleted."])
     [:div {:class "max-w-3xl overflow-hidden bg-white rounded shadow"}
      [:form {:on-submit on-submit}
       [:div {:class "flex flex-wrap p-8 -mb-8 -mr-6"}
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Kode"
                     :name "kode"
                     :errors (.-kode errors)
                     :value (.-kode data)
                     :on-change #(setData "kode" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Nama"
                     :name "nama"
                     :errors (.-nama errors)
                     :value (.-nama data)
                     :on-change #(setData "nama" (.. % -target -value))}]
        ]
       ;; TODO Add file input
       [:div {:class "flex items-center px-8 py-4 bg-gray-100 border-t border-gray-200"}
        ;(when (empty? (j/get addr :deleted_at))
        ;  [delete-button {:on-delete destroy}
        ;   "Delete User Address"])
        [loading-button {:loading processing
                         :type "submit"
                         :class "ml-auto btn-indigo"}
         "Update User Group"]]]]]))

(defn create []
  [:f> create-form])

(defn edit [{:keys [grp]}]
  [:f> edit-form grp])
