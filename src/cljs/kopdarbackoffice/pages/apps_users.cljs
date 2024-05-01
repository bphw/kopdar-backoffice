(ns kopdarbackoffice.pages.apps-users
  (:require ["@inertiajs/inertia" :refer [Inertia]]
            ["@inertiajs/inertia-react" :refer [InertiaLink useForm Head]]
            [applied-science.js-interop :as j]
            [kopdarbackoffice.shared.buttons :refer [loading-button delete-button]]
            [kopdarbackoffice.shared.form-input :refer [text-input select-input]]
            [kopdarbackoffice.shared.icon :refer [icon]]
            [kopdarbackoffice.shared.search-filter :refer [search-filter]]
            [kopdarbackoffice.shared.trashed-message :refer [trashed-message]]))

(defn index [{:keys [users]}]
  [:div
   [:> Head {:title "Apps User"}]
   [:h1 {:class "mb-8 text-3xl font-bold"} "Users"]
   [:div {:class "flex items-center justify-between mb-6"}
    [:f> search-filter]
    [:> InertiaLink {:class "btn-indigo focus:outline-none"
                     :href (js/route "apps-users.create")}
     [:span "Create"]
     [:span {:class "hidden md:inline"} " Apps User"]]]
   [:div {:class "overflow-x-auto bg-white rounded shadow"}
    [:table {:class "w-full whitespace-nowrap"}
     [:thead
      [:tr {:class "font-bold text-left"}
       [:th {:class "px-6 pt-5 pb-4"} "Kode"]
       [:th {:class "px-6 pt-5 pb-4"} "Nama"]
       [:th {:class "px-6 pt-5 pb-4" :col-span "2"} "Email"]]]
     [:tbody
      (for [user users
            :let [{:keys [tbid nama emai telp]} (j/lookup user)]]
        [:tr {:class "hover:bg-gray-100 focus-within:bg-gray-100"
              :key tbid}
         [:td {:class "border-t"}
          [:> InertiaLink {:href (js/route "apps-users.edit" tbid)
                           :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
           nama
           ;(when deleted_at
           ;  [icon {:name :trash
           ;         :class "flex-shrink-0 w-3 h-3 ml-2 text-gray-400 fill-current"}])
           ]]
         [:td {:class "border-t"}
          [:> InertiaLink {:href (js/route "apps-users.edit" tbid)
                           :tab-index "-1"
                           :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
           emai]]
         [:td {:class "border-t"}
          [:> InertiaLink {:href (js/route "apps-users.edit" tbid)
                           :tab-index "-1"
                           :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
                                        ;(if owner "Owner" "User")
           telp
           ]]
         [:td {:class "w-px border-t"}
          [:> InertiaLink {:href (js/route "apps-users.edit" tbid)
                           :tab-index "-1"
                           :class "flex items-center px-4 focus:outline-none"}
           [icon {:name :cheveron-right
                  :class "block w-6 h-6 text-gray-400 fill-current"}]]]])
      (when (zero? (count users))
        [:tr
         [:td {:class "px-6 py-4 border-t"
               :col-span "4"}
          "No Apps Users found."]])]]]])

(defn create-form []
  (let [{:keys [data setData errors post processing]}
        (j/lookup (useForm #js {:kode ""
                                :nama ""
                                :emai ""
                                :telp ""
                                :almttbid ""
                                :tokotbid ""
                                :ugrptbid ""
                                :pswd ""
                                :dsc1 ""
                                :dsc2 ""
                                :dsc3 ""
                                :imag ""}))
        on-submit #(do (.preventDefault %)
                       (post (js/route "apps-users.store")))]
    [:div
     [:> Head {:title "Create Apps User"}]
     [:h1 {:class "mb-8 text-3xl font-bold"}
      [:> InertiaLink {:href (js/route "apps-users")
                       :class "text-indigo-400 hover:text-indigo-600"}
       "Apps Users" [:span {:class "font-medium text-indigo-400"} " / "]]
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
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Email"
                     :name "emai"
                     :errors (.-emai errors)
                     :value (.-emai data)
                     :on-change #(setData "emai" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Telp"
                     :name "telp"
                     :errors (.-telp errors)
                     :value (.-telp data)
                     :on-change #(setData "telp" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Password"
                     :name "pswd"
                     :type "password"
                     :errors (.-pswd errors)
                     :value (.-pswd data)
                     :on-change #(setData "pswd" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Image"
                     :name "imag"
                     :errors (.-imag errors)
                     :value (.-imag data)
                     :on-change #(setData "imag" (.. % -target -value))}]
        ]
       [:div {:class "px-8 py-4 bg-gray-50 border-t border-gray-100 flex justify-end items-center"}
        [loading-button {:loading processing
                         :type "submit"
                         :class "btn-indigo"}
         "Create App User"]]]]]))

(defn edit-form [^js user]
  (let [{:keys [data setData errors post processing]}
        (j/lookup (useForm #js {:kode (or (.-kode user) "")
                                :nama (or (.-nama user) "")
                                :emai (or (.-emai user) "")
                                :telp (or (.-telp user) "")
                                :imag (or (.-imag user) "")
                                :pswd (or (.-pswd user) "")
                                ;:owner (or (if (.-owner user) "1" "0") "0")
                                }))
        on-submit #(do (.preventDefault %)
                       (post (js/route "apps-users.update" (.-tbid user))))
        destroy #(when (js/confirm "Are you sure you want to delete this user?")
                   (.delete Inertia (js/route "apps-users.destroy" (.-tbid user))))
        restore #(when (js/confirm "Are you sure you want to restore this user?")
                   (.put Inertia (js/route "apps-users.restore" (.-tbid user))))]
    [:<>
     [:> Head {:title (str (j/get user :kode) " " (j/get user :nama))}]
     [:div {:class "flex justify-start max-w-lg mb-8"}
      [:h1 {:class "text-3xl font-bold"}
       [:> InertiaLink {:class "text-indigo-400 hover:text-indigo-700"
                        :href (js/route "apps-users")} "Apps Users"]
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
                     :on-change #(setData "last_name" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Email"
                     :name "emai"
                     :errors (.-emai errors)
                     :value (.-emai data)
                     :on-change #(setData "emai" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Telp"
                     :name "telp"
                     :errors (.-telp errors)
                     :value (.-telp data)
                     :on-change #(setData "telp" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Password"
                     :name "pswd"
                     :type "password"
                     :errors (.-pswd errors)
                     :value (.-pswd data)
                     :on-change #(setData "pswd" (.. % -target -value))}]
        
        ]
       ;; TODO Add file input
       [:div {:class "flex items-center px-8 py-4 bg-gray-100 border-t border-gray-200"}
        ;(when (empty? (j/get user :deleted_at))
        ;  [delete-button {:on-delete destroy}
        ;   "Delete User"])
        [loading-button {:loading processing
                         :type "submit"
                         :class "ml-auto btn-indigo"}
         "Update User"]]]]]))

(defn create []
  [:f> create-form])

(defn edit [{:keys [user]}]
  [:f> edit-form user])
