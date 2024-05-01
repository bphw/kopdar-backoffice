(ns kopdarbackoffice.pages.trx-details
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
   [:> Head {:title "Transaction Details"}]
   [:h1 {:class "mb-8 text-3xl font-bold"} "Transaction Details"]
   [:div {:class "flex items-center justify-between mb-6"}
    [:f> search-filter]
    [:> InertiaLink {:class "btn-indigo focus:outline-none"
                     :href (js/route "trx-details.create")}
     [:span "Create"]
     [:span {:class "hidden md:inline"} " Transaction"]]]
   [:div {:class "overflow-x-auto bg-white rounded shadow"}
    [:table {:class "w-full whitespace-nowrap"}
     [:thead
      [:tr {:class "font-bold text-left"}
       [:th {:class "px-6 pt-5 pb-4"} "TRNO"]
       [:th {:class "px-6 pt-5 pb-4"} "Kode Produk"]
       [:th {:class "px-6 pt-5 pb-4"} "Nama Produk"]
       [:th {:class "px-6 pt-5 pb-4"} "Harga Jual"]
       [:th {:class "px-6 pt-5 pb-4"} "Qty"]
       [:th {:class "px-6 pt-5 pb-4" :col-span "2"} "Gr Amount"]]]
     [:tbody
      (for [trx trxs
            :let [{:keys [tbid trno trit prdktbid prdkkode prdknama prdkhjua item quan disc potg pajk gamt namt]} (j/lookup trx)]]
        [:tr {:class "hover:bg-gray-100 focus-within:bg-gray-100"
              :key tbid}
         [:td {:class "border-t"}
          [:> InertiaLink {:href (js/route "trx-details.edit" tbid)
                           :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
           trno
           ;(when deleted_at
           ;  [icon {:name :trash
           ;         :class "flex-shrink-0 w-3 h-3 ml-2 text-gray-400 fill-current"}])
           ]]
         [:td {:class "border-t"}
          [:> InertiaLink {:href (js/route "trx-details.edit" tbid)
                           :tab-index "-1"
                           :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
           prdkkode]]
         [:td {:class "border-t"}
          [:> InertiaLink {:href (js/route "trx-details.edit" tbid)
                           :tab-index "-1"
                           :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
           prdknama]]
         [:td {:class "border-t"}
          [:> InertiaLink {:href (js/route "trx-details.edit" tbid)
                           :tab-index "-1"
                           :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
           prdkhjua]]
         [:td {:class "border-t"}
          [:> InertiaLink {:href (js/route "trx-details.edit" tbid)
                           :tab-index "-1"
                           :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
           quan]]
         [:td {:class "border-t"}
          [:> InertiaLink {:href (js/route "trx-details.edit" tbid)
                           :tab-index "-1"
                           :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
           gamt]]
         [:td {:class "w-px border-t"}
          [:> InertiaLink {:href (js/route "trx-details.edit" tbid)
                           :tab-index "-1"
                           :class "flex items-center px-4 focus:outline-none"}
           [icon {:name :cheveron-right
                  :class "block w-6 h-6 text-gray-400 fill-current"}]]]])
      (when (zero? (count trxs))
        [:tr
         [:td {:class "px-6 py-4 border-t"
               :col-span "4"}
          "No Transaction found."]])]]]])

(defn create-form []
  (let [{:keys [data setData errors post processing]}
        (j/lookup (useForm #js {:trnoe ""
                                :trit ""
                                :prdktbid ""
                                :prdkkode ""
                                :prdknama ""
                                :prdkhjua ""
                                :item ""
                                :quan ""
                                :disc ""
                                :potg ""
                                :pajk ""
                                :gamt ""
                                :namt ""
                                }))
        on-submit #(do (.preventDefault %)
                       (post (js/route "trx-details.store")))]
    [:div
     [:> Head {:title "Create Trx"}]
     [:h1 {:class "mb-8 text-3xl font-bold"}
      [:> InertiaLink {:href (js/route "users")
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
                     :label "Trit"
                     :name "trit"
                     :errors (.-trit errors)
                     :value (.-trit data)
                     :on-change #(setData "trit" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Produk Tbid"
                     :name "prdktbid"
                     :errors (.-prdktbid errors)
                     :value (.-prdktbid data)
                     :on-change #(setData "prdktbid" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Kode Produk"
                     :name "prdkkode"
                     :errors (.-prdkkode errors)
                     :value (.-prdkkode data)
                     :on-change #(setData "prdktbid" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Nama Produk"
                     :name "prdknama"
                     :errors (.-prdknama errors)
                     :value (.-prdknama data)
                     :on-change #(setData "prdknama" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Harga Jual"
                     :name "prdkhjua"
                     :errors (.-prdkhjua errors)
                     :value (.-prdkhjua data)
                     :on-change #(setData "prdkhjua" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Item"
                     :name "item"
                     :errors (.-item errors)
                     :value (.-item data)
                     :on-change #(setData "item" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Qty"
                     :name "quan"
                     :errors (.-quan errors)
                     :value (.-quan data)
                     :on-change #(setData "quan" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Diskon"
                     :name "disc"
                     :errors (.-disc errors)
                     :value (.-disc data)
                     :on-change #(setData "disc" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Potongan"
                     :name "potg"
                     :errors (.-potg errors)
                     :value (.-potg data)
                     :on-change #(setData "potg" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Pajak"
                     :name "pajk"
                     :errors (.-pajk errors)
                     :value (.-pajk data)
                     :on-change #(setData "disc" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Gros Amount"
                     :name "gamt"
                     :errors (.-gamt errors)
                     :value (.-gamt data)
                     :on-change #(setData "gamt" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Net Amount"
                     :name "namt"
                     :errors (.-namt errors)
                     :value (.-namt data)
                     :on-change #(setData "namt" (.. % -target -value))}]
        ]
       [:div {:class "px-8 py-4 bg-gray-50 border-t border-gray-100 flex justify-end items-center"}
        [loading-button {:loading processing
                         :type "submit"
                         :class "btn-indigo"}
         "Create Trx"]]]]]))

(defn edit-form [^js trx]
  (let [{:keys [data setData errors post processing]}
        (j/lookup (useForm #js {:trno (or (.-trno trx) "")
                                :trit (or (.-trit trx) "")
                                :prdktbid (or (.-prdktbid trx) "")
                                :prdkkode (or (.-prdkkode trx) "")
                                :prdknama (or (.-prdknama trx) "")
                                :prdkhjua (or (.-prdknama trx) "")
                                :item (or (.-item trx) "")
                                :quan (or (.-quan trx) "")
                                :disc (or (.-disc trx) "")
                                :potg (or (.-potg trx) "")
                                :pajk (or (.-pajk trx) "")
                                :gamt (or (.-gamt trx) "")
                                :namt (or (.-namt trx) "")
                                }))
        on-submit #(do (.preventDefault %)
                       (post (js/route "trx-details.update" (.-tbid trx))))
        destroy #(when (js/confirm "Are you sure you want to delete this trx?")
                   (.delete Inertia (js/route "trx-details.destroy" (.-tbid trx))))
        restore #(when (js/confirm "Are you sure you want to restore this trx?")
                   (.put Inertia (js/route "trx-details.restore" (.-tbid trx))))]
    [:<>
     [:> Head {:title (str (j/get trx :trno) " " (j/get trx :prdknama) " " (j/get trx :gamt))}]
     [:div {:class "flex justify-start max-w-lg mb-8"}
      [:h1 {:class "text-3xl font-bold"}
       [:> InertiaLink {:class "text-indigo-400 hover:text-indigo-700"
                        :href (js/route "trx-detailss")} "Trx"]
       [:span {:class "mx-2 font-medium text-indigo-400"} "/"]
       (.-trno data) " " (.-prdknama data)]]
     ;(when-not (empty? (j/get user :deleted_at))
     ;  [trashed-message {:on-restore restore}
     ;   "This user has been deleted."])
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
                     :label "Trit"
                     :name "trit"
                     :errors (.-trit errors)
                     :value (.-trit data)
                     :on-change #(setData "trit" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Produk Tbid"
                     :name "prdktbid"
                     :errors (.-prdktbid errors)
                     :value (.-prdktbid data)
                     :on-change #(setData "prdktbid" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Kode Produk"
                     :name "prdkkode"
                     :errors (.-prdkkode errors)
                     :value (.-prdkkode data)
                     :on-change #(setData "prdktbid" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Nama Produk"
                     :name "prdknama"
                     :errors (.-prdknama errors)
                     :value (.-prdknama data)
                     :on-change #(setData "prdknama" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Harga Jual"
                     :name "prdkhjua"
                     :errors (.-prdkhjua errors)
                     :value (.-prdkhjua data)
                     :on-change #(setData "prdkhjua" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Item"
                     :name "item"
                     :errors (.-item errors)
                     :value (.-item data)
                     :on-change #(setData "item" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Qty"
                     :name "quan"
                     :errors (.-quan errors)
                     :value (.-quan data)
                     :on-change #(setData "quan" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Diskon"
                     :name "disc"
                     :errors (.-disc errors)
                     :value (.-disc data)
                     :on-change #(setData "disc" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Potongan"
                     :name "potg"
                     :errors (.-potg errors)
                     :value (.-potg data)
                     :on-change #(setData "potg" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Pajak"
                     :name "pajk"
                     :errors (.-pajk errors)
                     :value (.-pajk data)
                     :on-change #(setData "disc" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Gros Amount"
                     :name "gamt"
                     :errors (.-gamt errors)
                     :value (.-gamt data)
                     :on-change #(setData "gamt" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Net Amount"
                     :name "namt"
                     :errors (.-namt errors)
                     :value (.-namt data)
                     :on-change #(setData "namt" (.. % -target -value))}]
        ]
       ;; TODO Add file input
       [:div {:class "flex items-center px-8 py-4 bg-gray-100 border-t border-gray-200"}
        ;(when (empty? (j/get trx :deleted_at))
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
