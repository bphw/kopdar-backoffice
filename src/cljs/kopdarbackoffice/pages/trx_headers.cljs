(ns kopdarbackoffice.pages.trx-headers
  (:require ["@inertiajs/inertia" :refer [Inertia]]
            ["@inertiajs/inertia-react" :refer [InertiaLink useForm Head]]
            [applied-science.js-interop :as j]
            [kopdarbackoffice.shared.buttons :refer [loading-button delete-button]]
            [kopdarbackoffice.shared.form-input :refer [text-input select-input]]
            [kopdarbackoffice.shared.icon :refer [icon]]
            [kopdarbackoffice.shared.search-filter :refer [search-filter]]
            [kopdarbackoffice.shared.trashed-message :refer [trashed-message]]))

(defn index [{:keys [trxs]}]
  [:div
   [:> Head {:title "Transaction Headers"}]
   [:h1 {:class "mb-8 text-3xl font-bold"} "Transaction Headers"]
   [:div {:class "flex items-center justify-between mb-6"}
    [:f> search-filter]
    [:> InertiaLink {:class "btn-indigo focus:outline-none"
                     :href (js/route "trx-headers.create")}
     [:span "Create"]
     [:span {:class "hidden md:inline"} " Transaction"]]]
   [:div {:class "overflow-x-auto bg-white rounded shadow"}
    [:table {:class "w-full whitespace-nowrap"}
     [:thead
      [:tr {:class "font-bold text-left"}
       [:th {:class "px-6 pt-5 pb-4"} "TRNO"]
       [:th {:class "px-6 pt-5 pb-4"} "Gr. Amount Total"]
       [:th {:class "px-6 pt-5 pb-4" :col-span "2"} "Status"]]]
     [:tbody
      (for [trx trxs
            :let [{:keys [tbid trno tokotbid custtbid almttbid kirimtbid mbyrtbid quanitem quantota disctota potgtota pajktota gamttota namttota hkir stat]} (j/lookup trx)]]
        [:tr {:class "hover:bg-gray-100 focus-within:bg-gray-100"
              :key tbid}
         [:td {:class "border-t"}
          [:> InertiaLink {:href (js/route "trx-headers.edit" tbid)
                           :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
           trno
           ;(when deleted_at
           ;  [icon {:name :trash
           ;         :class "flex-shrink-0 w-3 h-3 ml-2 text-gray-400 fill-current"}])
           ]]
         [:td {:class "border-t"}
          [:> InertiaLink {:href (js/route "trx-headers.edit" tbid)
                           :tab-index "-1"
                           :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
           gamttota]]
         [:td {:class "border-t"}
          [:> InertiaLink {:href (js/route "trx-headers.edit" tbid)
                           :tab-index "-1"
                           :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
           stat]]
         [:td {:class "w-px border-t"}
          [:> InertiaLink {:href (js/route "trx-headers.edit" tbid)
                           :tab-index "-1"
                           :class "flex items-center px-4 focus:outline-none"}
           [icon {:name :cheveron-right
                  :class "block w-6 h-6 text-gray-400 fill-current"}]]]])
      (when (zero? (count trxs))
        [:tr
         [:td {:class "px-6 py-4 border-t"
               :col-span "4"}
          "No Trx found."]])]]]])

(defn create-form []
  (let [{:keys [data setData errors post processing]}
        (j/lookup (useForm #js {:trno ""
                                :tokotbid ""
                                :custtbid ""
                                :almttbid ""
                                :kirimtbid ""
                                :mbyrtbid ""
                                :quanitem ""
                                :quantota ""
                                :disctota ""
                                :potgtota ""
                                :pajktota ""
                                :gamttota ""
                                :namttota ""
                                :hkir ""
                                :stat ""}))
        on-submit #(do (.preventDefault %)
                       (post (js/route "trx-headerss.store")))]
    [:div
     [:> Head {:title "Create Trx"}]
     [:h1 {:class "mb-8 text-3xl font-bold"}
      [:> InertiaLink {:href (js/route "trx-headers")
                       :class "text-indigo-400 hover:text-indigo-600"}
       "Trx" [:span {:class "font-medium text-indigo-400"} " / "]]
      "Create"]
     [:div {:class "max-w-3xl overflow-hidden bg-white rounded shadow"}
      [:form {:on-submit on-submit}
       [:div {:class "flex flex-wrap p-8 -mb-8 -mr-6"}
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "TRNO"
                     :name "trno"
                     :errors (.-trno errors)
                     :value (.-trno data)
                     :on-change #(setData "trno" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Toko Tbid"
                     :name "tokotbid"
                     :errors (.-tokotbid errors)
                     :value (.-tokotbid data)
                     :on-change #(setData "tokotbid" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Cust Tbid"
                     :name "custtbid"
                     :errors (.-custtbid errors)
                     :value (.-custtbid data)
                     :on-change #(setData "custtbid" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Alamat Tbid"
                     :name "almttbid"
                     :errors (.-almttbid errors)
                     :value (.-almttbid data)
                     :on-change #(setData "almttbid" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Kirim Tbid"
                     :name "kirimtbid"
                     :errors (.-kirimtbid errors)
                     :value (.-kirimtbid data)
                     :on-change #(setData "kirimtbid" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Mbayar Tbid"
                     :name "mbyrtbid"
                     :errors (.-mbyrtbid errors)
                     :value (.-mbyrtbid data)
                     :on-change #(setData "mbyrtbid" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Quan Item"
                     :name "quanitem"
                     :errors (.-quanitem errors)
                     :value (.-quanitem data)
                     :on-change #(setData "quanitem" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Quanity Total"
                     :name "quantota"
                     :errors (.-quantota errors)
                     :value (.-quantota data)
                     :on-change #(setData "quantota" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Discount Total"
                     :name "disctota"
                     :errors (.-disctota errors)
                     :value (.-disctota data)
                     :on-change #(setData "disctota" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Potongan Total"
                     :name "potgtota"
                     :errors (.-potgtota errors)
                     :value (.-potgtota data)
                     :on-change #(setData "potgtota" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Pajak Total"
                     :name "pajktota"
                     :errors (.-pojktota errors)
                     :value (.-pojktota data)
                     :on-change #(setData "pajktota" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Gross Amount Total"
                     :name "gamttota"
                     :errors (.-gamttota errors)
                     :value (.-gamttota data)
                     :on-change #(setData "gamttota" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Net Amount Total"
                     :name "namttota"
                     :errors (.-namttota errors)
                     :value (.-namttota data)
                     :on-change #(setData "namttota" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Harga Ongkir"
                     :name "hkir"
                     :errors (.-hkir errors)
                     :value (.-hkir data)
                     :on-change #(setData "hkir" (.. % -target -value))}]
        [select-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                       :label "Status"
                       :name "stat"
                       :errors (.-stat errors)
                       :value (.-stat data)
                       :on-change #(setData "stat" (.. % -target -value))}
         [:option {:value "Terkonfirmasi"} "Terkonfirmasi"]
         [:option {:value "Dalam Proses"} "Dalam Proses"]
         [:option {:value "Dalam Pengiriman"} "Dalam Pengiriman"]
         [:option {:value "Sampai Di LokasiDalam Pengiriman"} "Sampai Di Lokasi"]
         [:option {:value "Selesai"} "Selesai"]
         ]]
       [:div {:class "px-8 py-4 bg-gray-50 border-t border-gray-100 flex justify-end items-center"}
        [loading-button {:loading processing
                         :type "submit"
                         :class "btn-indigo"}
         "Create Trx"]]]]]))

(defn edit-form [^js trx]
  (let [{:keys [data setData errors post processing]}
        (j/lookup (useForm #js {:trno (or (.-trno trx) "")
                                :tokotbid (or (.-tokotbid trx) "")
                                :custtbid (or (.-custtbid trx) "")
                                :almttbid (or (.-almttbid trx) "")
                                :kirimtbid (or (.-kirimtbid trx) "")
                                :mbyrtbid (or (.-mbyrtbid trx) "")
                                :quanitem (or (.-quanitem trx) "")
                                :quantota (or (.-quantota trx) "")
                                :disctota (or (.-disctota trx) "")
                                :potgtota (or (.-potgtota trx) "")
                                :pajktota (or (.-pajktota trx) "")
                                :gamttota (or (.-gamttota trx) "")
                                :namttota (or (.-namttota trx) "")
                                :hkir (or (.-hkir trx) "")
                                :stat (or (.-stat trx) "")
                                }))
        on-submit #(do (.preventDefault %)
                       (post (js/route "trx-headers.update" (.-tbid trx))))
        destroy #(when (js/confirm "Are you sure you want to delete this trx?")
                   (.delete Inertia (js/route "trx-headers.destroy" (.-tbid trx))))
        restore #(when (js/confirm "Are you sure you want to restore this trx?")
                   (.put Inertia (js/route "trx-headers.restore" (.-tbid trx))))]
    [:<>
     [:> Head {:title (str (j/get trx :trno) " " (j/get trx :gamttota))}]
     [:div {:class "flex justify-start max-w-lg mb-8"}
      [:h1 {:class "text-3xl font-bold"}
       [:> InertiaLink {:class "text-indigo-400 hover:text-indigo-700"
                        :href (js/route "trx-headers")} "Transaction Header"]
       [:span {:class "mx-2 font-medium text-indigo-400"} "/"]
       (.-trno data) " " (.-gamttota data)]]
     ;(when-not (empty? (j/get trx :deleted_at))
     ;  [trashed-message {:on-restore restore}
     ;   "This trx has been deleted."])
     [:div {:class "max-w-3xl overflow-hidden bg-white rounded shadow"}
      [:form {:on-submit on-submit}
       [:div {:class "flex flex-wrap p-8 -mb-8 -mr-6"}
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "TRNO"
                     :name "trno"
                     :errors (.-trno errors)
                     :value (.-trno data)
                     :on-change #(setData "trno" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Toko Tbid"
                     :name "tokotbid"
                     :errors (.-tokotbid errors)
                     :value (.-tokotbid data)
                     :on-change #(setData "tokotbid" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Cust Tbid"
                     :name "custtbid"
                     :errors (.-custtbid errors)
                     :value (.-custtbid data)
                     :on-change #(setData "custtbid" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Alamat Tbid"
                     :name "almttbid"
                     :errors (.-almttbid errors)
                     :value (.-almttbid data)
                     :on-change #(setData "almttbid" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Kirim Tbid"
                     :name "kirimtbid"
                     :errors (.-kirimtbid errors)
                     :value (.-kirimtbid data)
                     :on-change #(setData "kirimtbid" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Mbayar Tbid"
                     :name "mbyrtbid"
                     :errors (.-mbyrtbid errors)
                     :value (.-mbyrtbid data)
                     :on-change #(setData "mbyrtbid" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Quan Item"
                     :name "quanitem"
                     :errors (.-quanitem errors)
                     :value (.-quanitem data)
                     :on-change #(setData "quanitem" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Quanity Total"
                     :name "quantota"
                     :errors (.-quantota errors)
                     :value (.-quantota data)
                     :on-change #(setData "quantota" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Discount Total"
                     :name "disctota"
                     :errors (.-disctota errors)
                     :value (.-disctota data)
                     :on-change #(setData "disctota" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Potongan Total"
                     :name "potgtota"
                     :errors (.-potgtota errors)
                     :value (.-potgtota data)
                     :on-change #(setData "potgtota" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Pajak Total"
                     :name "pajktota"
                     :errors (.-pojktota errors)
                     :value (.-pojktota data)
                     :on-change #(setData "pajktota" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Gross Amount Total"
                     :name "gamttota"
                     :errors (.-gamttota errors)
                     :value (.-gamttota data)
                     :on-change #(setData "gamttota" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Net Amount Total"
                     :name "namttota"
                     :errors (.-namttota errors)
                     :value (.-namttota data)
                     :on-change #(setData "namttota" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Harga Ongkir"
                     :name "hkir"
                     :errors (.-hkir errors)
                     :value (.-hkir data)
                     :on-change #(setData "hkir" (.. % -target -value))}]
        [select-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                       :label "Status"
                       :name "stat"
                       :errors (.-stat errors)
                       :value (.-stat data)
                       :on-change #(setData "stat" (.. % -target -value))}
         [:option {:value "Terkonfirmasi"} "Terkonfirmasi"]
         [:option {:value "Dalam Proses"} "Dalam Proses"]
         [:option {:value "Dalam Pengiriman"} "Dalam Pengiriman"]
         [:option {:value "Sampai Di LokasiDalam Pengiriman"} "Sampai Di Lokasi"]
         [:option {:value "Selesai"} "Selesai"]
         ]]
       ;; TODO Add file input
       [:div {:class "flex items-center px-8 py-4 bg-gray-100 border-t border-gray-200"}
        ;(when (empty? (j/get user :deleted_at))
        ;  [delete-button {:on-delete destroy}
        ;   "Delete User"])
        [loading-button {:loading processing
                         :type "submit"
                         :class "ml-auto btn-indigo"}
         "Update Trx"]]]]]))

(defn create []
  [:f> create-form])

(defn edit [{:keys [trx]}]
  [:f> edit-form trx])
