(ns kopdarbackoffice.pages.payment-methods
  (:require ["@inertiajs/inertia" :refer [Inertia]]
            ["@inertiajs/inertia-react" :refer [InertiaLink useForm Head]]
            [applied-science.js-interop :as j]
            [kopdarbackoffice.shared.buttons :refer [loading-button delete-button]]
            [kopdarbackoffice.shared.form-input :refer [text-input select-input]]
            [kopdarbackoffice.shared.icon :refer [icon]]
            [kopdarbackoffice.shared.search-filter :refer [search-filter]]
            [kopdarbackoffice.shared.trashed-message :refer [trashed-message]]))

(defn index [{:keys [methods]}]
  [:div
   [:> Head {:title "Payment Methods"}]
   [:h1 {:class "mb-8 text-3xl font-bold"} "Payment Methods"]
   [:div {:class "flex items-center justify-between mb-6"}
    [:f> search-filter]
    [:> InertiaLink {:class "btn-indigo focus:outline-none"
                     :href (js/route "payment-methods.create")}
     [:span "Create"]
     [:span {:class "hidden md:inline"} " Payment Methods"]]]
   [:div {:class "overflow-x-auto bg-white rounded shadow"}
    [:table {:class "w-full whitespace-nowrap"}
     [:thead
      [:tr {:class "font-bold text-left"}
       [:th {:class "px-6 pt-5 pb-4"} "Kode"]
       [:th {:class "px-6 pt-5 pb-4"} "Nama"]
       [:th {:class "px-6 pt-5 pb-4" :col-span "2"} "Image"]]]
     [:tbody
      (for [method methods
            :let [{:keys [tbid kode nama imag]} (j/lookup method)]]
        [:tr {:class "hover:bg-gray-100 focus-within:bg-gray-100"
              :key tbid}
         [:td {:class "border-t"}
          [:> InertiaLink {:href (js/route "payment-methods.edit" tbid)
                           :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
           kode
           ;(when deleted_at
           ;  [icon {:name :trash
           ;         :class "flex-shrink-0 w-3 h-3 ml-2 text-gray-400 fill-current"}])
           ]]
         [:td {:class "border-t"}
          [:> InertiaLink {:href (js/route "payment-methods.edit" tbid)
                           :tab-index "-1"
                           :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
           nama]]
         [:td {:class "border-t"}
          [:> InertiaLink {:href (js/route "payment-methods.edit" tbid)
                           :tab-index "-1"
                           :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
           ;(if owner "Owner" "User")
           imag
           ]]
         [:td {:class "w-px border-t"}
          [:> InertiaLink {:href (js/route "payment-methods.edit" tbid)
                           :tab-index "-1"
                           :class "flex items-center px-4 focus:outline-none"}
           [icon {:name :cheveron-right
                  :class "block w-6 h-6 text-gray-400 fill-current"}]]]])
      (when (zero? (count methods))
        [:tr
         [:td {:class "px-6 py-4 border-t"
               :col-span "4"}
          "No Payment Method found."]])]]]])

(defn create-form []
  (let [{:keys [data setData errors post processing]}
        (j/lookup (useForm #js {:kode ""
                                :nama ""
                                :dsc1 ""
                                :dsc2 ""
                                :dsc3 ""
                                :imag ""
                                :stde ""}))
        on-submit #(do (.preventDefault %)
                       (post (js/route "payment-methods.store")))]
    [:div
     [:> Head {:title "Create Payment Method"}]
     [:h1 {:class "mb-8 text-3xl font-bold"}
      [:> InertiaLink {:href (js/route "payment-methods")
                       :class "text-indigo-400 hover:text-indigo-600"}
       "Payment Method" [:span {:class "font-medium text-indigo-400"} " / "]]
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
                     :label "Deskripsi 1"
                     :name "dsc1"
                     :errors (.-dsc1 errors)
                     :value (.-dsc1 data)
                     :on-change #(setData "dsc1" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Deskripsi 2"
                     :name "dsc2"
                     ;:type "password"
                     :errors (.-ds2 errors)
                     :value (.-dsc2 data)
                     :on-change #(setData "dsc2" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Deskripsi 3"
                     :name "dsc3"
                     :errors (.-dsc3 errors)
                     :value (.-dsc3 data)
                     :on-change #(setData "dsc3" (.. % -target -value))}]
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
         "Create Payment Method"]]]]]))

(defn edit-form [^js method]
  (let [{:keys [data setData errors post processing]}
        (j/lookup (useForm #js {:kode (or (.-kode method) "")
                                :nama (or (.-nama method) "")
                                :dsc1 (or (.-dsc1 method) "")
                                :dsc2 (or (.-dsc2 method) "")
                                :dsc3 (or (.-dsc3 method) "")
                                :stde (or (.-stde method) "")
                                ;:owner (or (if (.-owner method) "1" "0") "0")
                                }))
        on-submit #(do (.preventDefault %)
                       (post (js/route "payment-methods.update" (.-tbid method))))
        destroy #(when (js/confirm "Are you sure you want to delete this payment method?")
                   (.delete Inertia (js/route "payment-methods.destroy" (.-tbid method))))
        restore #(when (js/confirm "Are you sure you want to restore this payment method?")
                   (.put Inertia (js/route "payment-method.restore" (.-tbid method))))]
    [:<>
     [:> Head {:title (str (j/get method :kode) " " (j/get method :nama))}]
     [:div {:class "flex justify-start max-w-lg mb-8"}
      [:h1 {:class "text-3xl font-bold"}
       [:> InertiaLink {:class "text-indigo-400 hover:text-indigo-700"
                        :href (js/route "payment-methods")} "Payment Method"]
       [:span {:class "mx-2 font-medium text-indigo-400"} "/"]
       (.-kode data) " " (.-nama data)]]
     ;(when-not (empty? (j/get method :deleted_at))
     ;  [trashed-message {:on-restore restore}
     ;   "This method has been deleted."])
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
                     :label "Deskripsi 1"
                     :name "dsc1"
                     :errors (.-dsc1 errors)
                     :value (.-dsc1 data)
                     :on-change #(setData "dsc1" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Deskripsi 2"
                     :name "dsc2"
                     ;:type "password"
                     :errors (.-dsc2 errors)
                     :value (.-dsc2 data)
                     :on-change #(setData "dsc2" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Deskripsi 3"
                     :name "dsc3"
                     :errors (.-dsc3 errors)
                     :value (.-dsc3 data)
                     :on-change #(setData "dsc3" (.. % -target -value))}]
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
        ;   "Delete Method"])
        [loading-button {:loading processing
                         :type "submit"
                         :class "ml-auto btn-indigo"}
         "Update Method"]]]]]))

(defn create []
  [:f> create-form])

(defn edit [{:keys [method]}]
  [:f> edit-form method])
