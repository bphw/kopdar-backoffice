(ns kopdarbackoffice.pages.trx-statuses
  (:require ["@inertiajs/inertia" :refer [Inertia]]
            ["@inertiajs/inertia-react" :refer [InertiaLink useForm Head]]
            [applied-science.js-interop :as j]
            [kopdarbackoffice.shared.buttons :refer [loading-button delete-button]]
            [kopdarbackoffice.shared.form-input :refer [text-input select-input]]
            [kopdarbackoffice.shared.icon :refer [icon]]
            [kopdarbackoffice.shared.search-filter :refer [search-filter]]
            [kopdarbackoffice.shared.trashed-message :refer [trashed-message]]))

(defn index [{:keys [statuses]}]
  [:div
   [:> Head {:title "Transaction Status"}]
   [:h1 {:class "mb-8 text-3xl font-bold"} "Transaction Status"]
   [:div {:class "flex items-center justify-between mb-6"}
    [:f> search-filter]
    [:> InertiaLink {:class "btn-indigo focus:outline-none"
                     :href (js/route "trx-statuses.create")}
     [:span "Create"]
     [:span {:class "hidden md:inline"} " TRX Status"]]]
   [:div {:class "overflow-x-auto bg-white rounded shadow"}
    [:table {:class "w-full whitespace-nowrap"}
     [:thead
      [:tr {:class "font-bold text-left"}
       [:th {:class "px-6 pt-5 pb-4"} "Kode"]
       [:th {:class "px-6 pt-5 pb-4" :col-span "2"} "Nama"]
       ;[:th {:class "px-6 pt-5 pb-4" :col-span "2"} "Group"]
       ]]
     [:tbody
      (for [status statuses
            :let [{:keys [tbid kode nama]} (j/lookup status)]]
        [:tr {:class "hover:bg-gray-100 focus-within:bg-gray-100"
              :key tbid}
         [:td {:class "border-t"}
          [:> InertiaLink {:href (js/route "trx-statuses.edit" tbid)
                           :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
           kode
           ;(when deleted_at
           ;  [icon {:name :trash
           ;         :class "flex-shrink-0 w-3 h-3 ml-2 text-gray-400 fill-current"}])
           ]]
         [:td {:class "border-t"}
          [:> InertiaLink {:href (js/route "trx-statuses.edit" tbid)
                           :tab-index "-1"
                           :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
           nama]]
         [:td {:class "w-px border-t"}
          [:> InertiaLink {:href (js/route "deliveries.edit" tbid)
                           :tab-index "-1"
                           :class "flex items-center px-4 focus:outline-none"}
           [icon {:name :cheveron-right
                  :class "block w-6 h-6 text-gray-400 fill-current"}]]]])
      (when (zero? (count statuses))
        [:tr
         [:td {:class "px-6 py-4 border-t"
               :col-span "4"}
          "No Status found."]])]]]])

(defn create-form []
  (let [{:keys [data setData errors post processing]}
        (j/lookup (useForm #js {:kode ""
                                :nama ""
                                :imag ""
                                :stde ""}))
        on-submit #(do (.preventDefault %)
                       (post (js/route "trx-statuses.store")))]
    [:div
     [:> Head {:title "Create Transaction Status"}]
     [:h1 {:class "mb-8 text-3xl font-bold"}
      [:> InertiaLink {:href (js/route "trx-statuses")
                       :class "text-indigo-400 hover:text-indigo-600"}
       "Courier Delivery" [:span {:class "font-medium text-indigo-400"} " / "]]
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
                     :label "Image"
                     :name "imag"
                     :errors (.-imag errors)
                     :value (.-imag data)
                     :on-change #(setData "imag" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Stde"
                     :name "stde"
                     :errors (.-stde errors)
                     :value (.-stde data)
                     :on-change #(setData "stde" (.. % -target -value))}]
       ]
       [:div {:class "px-8 py-4 bg-gray-50 border-t border-gray-100 flex justify-end items-center"}
        [loading-button {:loading processing
                         :type "submit"
                         :class "btn-indigo"}
         "Create Status Transaksi"]]]]]))

(defn edit-form [^js status]
  (let [{:keys [data setData errors post processing]}
        (j/lookup (useForm #js {:kode (or (.-kode status) "")
                                :nama (or (.-nama status) "")
                                :stde (or (.-stde status) "")
                                :imag (or (.-image status) "")
                                ;:owner (or (if (.-owner method) "1" "0") "0")
                                }))
        on-submit #(do (.preventDefault %)
                       (post (js/route "trx-statuses.update" (.-tbid status))))
        destroy #(when (js/confirm "Are you sure you want to delete this status?")
                   (.delete Inertia (js/route "trx-statuses.destroy" (.-tbid status))))
        restore #(when (js/confirm "Are you sure you want to restore this status?")
                   (.put Inertia (js/route "trx-statuses.restore" (.-tbid status))))]
    [:<>
     [:> Head {:title (str (j/get status :kode) " " (j/get status :nama))}]
     [:div {:class "flex justify-start max-w-lg mb-8"}
      [:h1 {:class "text-3xl font-bold"}
       [:> InertiaLink {:class "text-indigo-400 hover:text-indigo-700"
                        :href (js/route "trx-statuses")} "Transaction Status"]
       [:span {:class "mx-2 font-medium text-indigo-400"} "/"]
       (.-kode data) " " (.-nama data)]]
     ;(when-not (empty? (j/get method :deleted_at))
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
                     :label "Image"
                     :name "imag"
                     :errors (.-imag errors)
                     :value (.-imag data)
                     :on-change #(setData "imag" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Stde"
                     :name "stde"
                     :errors (.-stde errors)
                     :value (.-stde data)
                     :on-change #(setData "stde" (.. % -target -value))}]
        ]
       ;; TODO Add file input
       [:div {:class "flex items-center px-8 py-4 bg-gray-100 border-t border-gray-200"}
        ;(when (empty? (j/get method :deleted_at))
        ;  [delete-button {:on-delete destroy}
        ;   "Delete User"])
        [loading-button {:loading processing
                         :type "submit"
                         :class "ml-auto btn-indigo"}
         "Update Status"]]]]]))

(defn create []
  [:f> create-form])

(defn edit [{:keys [status]}]
  [:f> edit-form status])
