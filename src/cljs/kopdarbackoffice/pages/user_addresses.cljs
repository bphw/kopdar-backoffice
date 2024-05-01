(ns kopdarbackoffice.pages.user-addresses
  (:require ["@inertiajs/inertia" :refer [Inertia]]
            ["@inertiajs/inertia-react" :refer [InertiaLink useForm Head]]
            [applied-science.js-interop :as j]
            [kopdarbackoffice.shared.buttons :refer [loading-button delete-button]]
            [kopdarbackoffice.shared.form-input :refer [text-input select-input]]
            [kopdarbackoffice.shared.icon :refer [icon]]
            [kopdarbackoffice.shared.search-filter :refer [search-filter]]
            [kopdarbackoffice.shared.trashed-message :refer [trashed-message]]))

(defn index [{:keys [useraddresses]}]
  [:div
   [:> Head {:title "User Addresses"}]
   [:h1 {:class "mb-8 text-3xl font-bold"} "User Addresses"]
   [:div {:class "flex items-center justify-between mb-6"}
    [:f> search-filter]
    [:> InertiaLink {:class "btn-indigo focus:outline-none"
                     :href (js/route "user-addresses.create")}
     [:span "Create"]
     [:span {:class "hidden md:inline"} " User Address"]]]
   [:div {:class "overflow-x-auto bg-white rounded shadow"}
    [:table {:class "w-full whitespace-nowrap"}
     [:thead
      [:tr {:class "font-bold text-left"}
       [:th {:class "px-6 pt-5 pb-4"} "Kode"]
       [:th {:class "px-6 pt-5 pb-4"} "Nama"]
       [:th {:class "px-6 pt-5 pb-4"} "Jalan"]
       [:th {:class "px-6 pt-5 pb-4"} "Kelurahan"]
       [:th {:class "px-6 pt-5 pb-4"} "Kota"]
       [:th {:class "px-6 pt-5 pb-4"} "Provinsi"]
       [:th {:class "px-6 pt-5 pb-4" :col-span "2"} "Kodepos"]]]
     [:tbody
      (for [addr useraddresses
            :let [{:keys [tbid kode nama jln1 jln2 kelu kota prop ctry kpos]} (j/lookup addr)]]
        [:tr {:class "hover:bg-gray-100 focus-within:bg-gray-100"
              :key tbid}
         [:td {:class "border-t"}
          [:> InertiaLink {:href (js/route "user-addresses.edit" tbid)
                           :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
           kode
           ;(when deleted_at
           ;  [icon {:name :trash
           ;         :class "flex-shrink-0 w-3 h-3 ml-2 text-gray-400 fill-current"}])
           ]]
         [:td {:class "border-t"}
          [:> InertiaLink {:href (js/route "users-addresses.edit" tbid)
                           :tab-index "-1"
                           :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
           nama]]
         [:td {:class "border-t"}
          [:> InertiaLink {:href (js/route "user-addressess.edit" tbid)
                           :tab-index "-1"
                           :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
           jln1]]
         [:td {:class "w-px border-t"}
          [:> InertiaLink {:href (js/route "user-addresses.edit" tbid)
                           :tab-index "-1"
                           :class "flex items-center px-4 focus:outline-none"}
           [icon {:name :cheveron-right
                  :class "block w-6 h-6 text-gray-400 fill-current"}]]]])
      (when (zero? (count useraddresses))
        [:tr
         [:td {:class "px-6 py-4 border-t"
               :col-span "4"}
          "No Addresses found."]])]]]])

(defn create-form []
  (let [{:keys [data setData errors post processing]}
        (j/lookup (useForm #js {:kode ""
                                :nama ""
                                :jln1 ""
                                :jln2 ""
                                :kelu ""
                                :kota ""
                                :prop ""
                                :kpos ""}))
        on-submit #(do (.preventDefault %)
                       (post (js/route "user-addresses.store")))]
    [:div
     [:> Head {:title "Create Address"}]
     [:h1 {:class "mb-8 text-3xl font-bold"}
      [:> InertiaLink {:href (js/route "user-addresses")
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
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Jalan"
                     :name "jln1"
                     :errors (.-jln1 errors)
                     :value (.-jln1 data)
                     :on-change #(setData "jln1" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Jalan (opsional)"
                     :name "jln2"
                     :errors (.-jln2 errors)
                     :value (.-jln2 data)
                     :on-change #(setData "jln2" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Kelurahan"
                     :name "kelu"
                     ;:type "password"
                     :errors (.-kelu errors)
                     :value (.-kelu data)
                     :on-change #(setData "kelu" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Kota"
                     :name "kota"
                     :errors (.-kota errors)
                     :value (.-kota data)
                     :on-change #(setData "kota" (.. % -target -value))}]
        [select-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                       :label "Kodepos"
                       :name "kpos"
                       :errors (.-kpos errors)
                       :value (.-kpos data)
                       :on-change #(setData "kpos" (.. % -target -value))}
         [:option {:value "1"} "Yes"]
         [:option {:value "0"} "No"]]]
       [:div {:class "px-8 py-4 bg-gray-50 border-t border-gray-100 flex justify-end items-center"}
        [loading-button {:loading processing
                         :type "submit"
                         :class "btn-indigo"}
         "Create User Address"]]]]]))

(defn edit-form [^js addr]
  (let [{:keys [data setData errors post processing]}
        (j/lookup (useForm #js {:kode (or (.-kode addr) "")
                                :nama (or (.-nama addr) "")
                                :jln1 (or (.-jln1 addr) "")
                                :jln2 (or (.-jln2 addr) "")
                                :kelu (or (.-kelu addr) "")
                                :kota (or (.-kota addr) "")
                                :kpos (or (.-kpos addr) "")}))
        on-submit #(do (.preventDefault %)
                       (post (js/route "user-addresses.update" (.-tbid addr))))
        destroy #(when (js/confirm "Are you sure you want to delete this address?")
                   (.delete Inertia (js/route "users.destroy" (.-tbid addr))))
        restore #(when (js/confirm "Are you sure you want to restore this address?")
                   (.put Inertia (js/route "users.restore" (.-tbid addr))))]
    [:<>
     [:> Head {:title (str (j/get addr :kode) " " (j/get addr :nama))}]
     [:div {:class "flex justify-start max-w-lg mb-8"}
      [:h1 {:class "text-3xl font-bold"}
       [:> InertiaLink {:class "text-indigo-400 hover:text-indigo-700"
                        :href (js/route "user-addresses")} "User Addresses"]
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
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Jalan"
                     :name "jln1"
                     :errors (.-jln1 errors)
                     :value (.-jln1 data)
                     :on-change #(setData "jln1" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Jalan (opsional)"
                     :name "jln2"
                     ;:type "password"
                     :errors (.-jln2 errors)
                     :value (.-jln2 data)
                     :on-change #(setData "jln2" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Kelurahan"
                     :name "kelu"
                     :errors (.-kelu errors)
                     :value (.-kelu data)
                     :on-change #(setData "kelu" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Kota"
                     :name "kota"
                     :errors (.-kota errors)
                     :value (.-kota data)
                     :on-change #(setData "kota" (.. % -target -value))}]
        [select-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                       :label "Kodepos"
                       :name "kpos"
                       :errors (.-kpos errors)
                       :value (.-kpos data)
                       :on-change #(setData "kpos" (.. % -target -value))}
         [:option {:value "1"} "Yes"]
         [:option {:value "0"} "No"]]]
       ;; TODO Add file input
       [:div {:class "flex items-center px-8 py-4 bg-gray-100 border-t border-gray-200"}
        ;(when (empty? (j/get addr :deleted_at))
        ;  [delete-button {:on-delete destroy}
        ;   "Delete User Address"])
        [loading-button {:loading processing
                         :type "submit"
                         :class "ml-auto btn-indigo"}
         "Update User Address"]]]]]))

(defn create []
  [:f> create-form])

(defn edit [{:keys [addr]}]
  [:f> edit-form addr])
