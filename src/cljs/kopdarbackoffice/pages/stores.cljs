(ns kopdarbackoffice.pages.stores
  (:require ["@inertiajs/inertia" :refer [Inertia]]
            ["@inertiajs/inertia-react" :refer [InertiaLink useForm Head]]
            [applied-science.js-interop :as j]
            [kopdarbackoffice.shared.buttons :refer [loading-button delete-button]]
            [kopdarbackoffice.shared.form-input :refer [text-input select-input]]
            [kopdarbackoffice.shared.icon :refer [icon]]
            [kopdarbackoffice.shared.pagination :refer [pagination]]
            [kopdarbackoffice.shared.search-filter :refer [search-filter]]
            [kopdarbackoffice.shared.trashed-message :refer [trashed-message]]))

(defn index [{:keys [stores]}]
  (let [{:keys [data links]} (j/lookup stores)]
    [:div
     [:> Head {:title "Stores"}]
     [:h1 {:class "mb-8 text-3xl font-bold"} "Stores"]
     [:div {:class "flex items-center justify-between mb-6"}
      [:f> search-filter]
      [:> InertiaLink
       {:class "btn-indigo focus:outline-none",
        :href (js/route "stores.create")} [:span "Create "]
       [:span {:class "hidden md:inline"} "Store"]]]
     [:div {:class "overflow-x-auto bg-white rounded shadow"}
      [:table {:class "w-full whitespace-nowrap"}
       [:thead
        [:tr {:class "font-bold text-left"}
         [:th {:class "px-6 pt-5 pb-4"} "Kode"]
         [:th {:class "px-6 pt-5 pb-4"} "Nama"]
         [:th {:class "px-6 pt-5 pb-4"} "Email"]
         [:th {:class "px-6 pt-5 pb-4", :col-span "2"} "Telp"]]]
       [:tbody
        (for [store data
              :let [{:keys [id kode nama emai telp deldt]} (j/lookup store)]]
          [:tr {:class "hover:bg-gray-100 focus-within:bg-gray-100"
                :key id}
           [:td {:class "border-t"}
            [:> InertiaLink {:href (js/route "stores.edit" id)
                             :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
             kode
             (when deldt
               [icon {:name :trash
                      :class "flex-shrink-0 w-3 h-3 ml-2 text-gray-400 fill-current"}])]]
           [:td {:class "border-t"}
            [:> InertiaLink {:href (js/route "stores.edit" id)
                             :tab-index "-1"
                             :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
             nama]]
           [:td {:class "border-t"}
            [:> InertiaLink {:href (js/route "stores.edit" id)
                             :tab-index "-1"
                             :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
             emai]]
           [:td {:class "border-t"}
            [:> InertiaLink {:href (js/route "stores.edit" id)
                             :tab-index "-1"
                             :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
             telp]]
           [:td {:class "w-px border-t"}
            [:> InertiaLink {:href (js/route "stores.edit" id)
                             :tab-index "-1"
                             :class "flex items-center px-4 focus:outline-none"}
             [icon {:name :cheveron-right
                    :class "block w-6 h-6 text-gray-400 fill-current"}]]]])
        (when (zero? (count data))
          [:tr
           [:td {:class "px-6 py-4 border-t"
                 :col-span "4"}
            "No stores found."]])]]]
     [pagination links]]))

(defn create-form [^js products]
  (let [{:keys [data setData errors post processing]}
        (j/lookup (useForm #js {:usertbid ""
                                :kode ""
                                :nama ""
                                :almttbid ""
                                :emai ""
                                :telp ""
                                :ratg ""
                                :dsc1 ""
                                :dsc2 ""
                                :dsc3 ""
                                :imag ""
                                :stde ""}))
        on-submit #(do (.preventDefault %)
                       (post (js/route "stores.store")))]
    [:div
     [:> Head {:title "Create Store"}]
     [:h1 {:class "mb-8 text-3xl font-bold"}
      [:> InertiaLink {:href (js/route "stores")
                       :class "text-indigo-400 hover:text-indigo-600"}
       "Stores"]
      [:span {:class ""} " / "]
      "Create"]
     [:div {:class "max-w-3xl overflow-hidden bg-white rounded shadow"}
      [:form {:on-submit on-submit}
       [:div {:class "flex flex-wrap p-8 -mb-8 -mr-6"}
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "User TBID"
                     :name "usertbid"
                     :errors (.-usertbid errors)
                     :value (.-usertbid data)
                     :on-change #(setData "usertbid" (.. % -target -value))}]
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
                     :label "Alamat TBID"
                     :name "almttbid"
                     :errors (.-almttbid errors)
                     :value (.-almttbid data)
                     :on-change #(setData "almttbid" (.. % -target -value))}]
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
                     :label "Ratg"
                     :name "ratg"
                     :errors (.-ratg errors)
                     :value (.-ratg data)
                     :on-change #(setData "ratg" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Deskripsi 1"
                     :name "dsc1"
                     :errors (.-dsc1 errors)
                     :value (.-dsc1 data)
                     :on-change #(setData "dsc1" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Deskripsi 2"
                     :name "dsc2"
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
                     :on-change #(setData "imag" (.. % -target -value))}]]
       [:div {:class "px-8 py-4 bg-gray-50 border-t border-gray-100 flex justify-end items-center"}
        [loading-button {:loading processing
                         :type "submit"
                         :class "btn-indigo"}
         "Create Store"]]]]]))

(defn edit-form [^js store ^js products]
  (let [{:keys [data setData errors put processing]}
        (j/lookup (useForm #js {:usertbid (or (.-usertbid store) "")
                                :kode (or (.-kode store) "")
                                :nama (or (.-nama store) "")
                                :almttbid (or (.-almttbid store) "")
                                :emai (or (.-emai store) "")
                                :telp (or (.-telp store) "")
                                :ratg (or (.-ratg store) "")
                                :dsc1 (or (.-dsc1 store) "")
                                :dsc2 (or (.-dsc2 store) "")
                                :dsc3 (or (.-dsc3 store) "")
                                :imag (or (.-imag store) "")
                                :stde (or (.-stde store) "")}))
        on-submit #(do (.preventDefault %)
                       (put (js/route "stores.update" (.-id store))))
        destroy #(when (js/confirm "Are you sure you want to delete this store?")
                   (.delete Inertia (js/route "stores.destroy" (.-id store))))
        restore #(when (js/confirm "Are you sure you want to restore this store?")
                   (.put Inertia (js/route "stores.restore" (.-id store))))]
    [:div
     [:> Head {:title (str (j/get store :kode) " " (j/get store :nama))}]
     [:h1 {:class "mb-8 text-3xl font-bold"}
      [:> InertiaLink {:href (js/route "stores")
                       :class "text-indigo-600 hover:text-indigo-700"}
       "Stores"]
      [:span {:class "mx-2 font-medium text-indigo-600"}
       "/"]
      (.-kode data) " " (.-nama data)]
     (when-not (empty? (j/get store :deldt))
       [trashed-message {:on-restore restore}
        "This store has been deleted."])
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
                     :label "Alamat TBID"
                     :name "almttbid"
                     :errors (.-almttbid errors)
                     :value (.-almttbid data)
                     :on-change #(setData "almttbid" (.. % -target -value))}]
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
                     :label "Ratg"
                     :name "ratg"
                     :errors (.-ratg errors)
                     :value (.-ratg data)
                     :on-change #(setData "ratg" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Deskripsi 1"
                     :name "dsc1"
                     :errors (.-dsc1 errors)
                     :value (.-dsc1 data)
                     :on-change #(setData "dsc1" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Deskripsi 2"
                     :name "dsc2"
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
                     :errors (.-imag_code errors)
                     :value (.-imag data)
                     :on-change #(setData "imag" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Stde"
                     :name "stde"
                     :errors (.-stde errors)
                     :value (.-stde data)
                     :on-change #(setData "stde" (.. % -target -value))}]]
       [:div {:class "flex items-center px-8 py-4 bg-gray-100 border-t border-gray-200"}
        (when (empty? (j/get store :deldt))
          [delete-button {:on-delete destroy}
           "Delete Store"])
        [loading-button {:loading processing
                         :type "submit"
                         :class "ml-auto btn-indigo"}
         "Update Store"]]]]

     [:h2 {:class "mt-12 text-2xl font-bold"} "Products"]
     [:div {:class "mt-6 overflow-x-auto bg-white rounded shadow"}
      [:table {:class "w-full whitespace-nowrap"}
       [:thead
        [:tr {:class "font-bold text-left"}
         [:th {:class "px-6 pt-5 pb-4"} "Nama"]
         [:th {:class "px-6 pt-5 pb-4"} "Harga Jual"]
         [:th {:class "px-6 pt-5 pb-4" :col-span "2"} "Qtsk"]]]
       [:tbody
        (for [item (.-products store)
              :let [{:keys [id kode nama hjua qtsk deldt]} (j/lookup item)]]
          [:tr {:class "hover:bg-gray-100 focus-within:bg-gray-100"
                :key id}
           [:td.border-t
            [:> InertiaLink {:href (js/route "products.edit" id)
                             :class "flex items-center px-6 py-4 focus:text-indigo focus:outline-none"}
             nama
             (when deldt
               [icon {:name :trash
                      :class "flex-shrink-0 w-3 h-3 ml-2 text-gray-400 fill-current"}])]]
           [:td.border-t
            [:> InertiaLink {:tab-index "-1"
                             :href (js/route "products.edit" id)
                             :class "flex items-center px-6 py-4 focus:text-indigo focus:outline-none"}
             hjua]]
           [:td.border-t
            [:> InertiaLink {:tab-index "-1"
                             :href (js/route "products.edit" id)
                             :class "flex items-center px-6 py-4 focus:text-indigo focus:outline-none"}
             qtsk]]
           [:td.border-t.w-px
            [:> InertiaLink {:tab-index "-1"
                             :href (js/route "products.edit" id)
                             :class "flex items-center px-4"}
             [icon {:name :cheveron-right
                    :class "block w-6 h-6 text-gray-400 fill-current"}]]]])
        (when (empty? (j/get store :products))
          [:tr
           [:td {:class "px-6 py-4 border-t"
                 :col-span "4"}
            "No products found."]])]]]]))

(defn edit [{:keys [store products]}]
  [:f> edit-form store products])

(defn create [{:keys [products]}]
  [:f> create-form products])
