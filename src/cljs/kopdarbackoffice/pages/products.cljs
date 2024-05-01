(ns kopdarbackoffice.pages.products
  (:require ["@inertiajs/inertia" :refer [Inertia]]
            ["@inertiajs/inertia-react" :refer [InertiaLink useForm Head]]
            [applied-science.js-interop :as j]
            [kopdarbackoffice.shared.buttons :refer [loading-button delete-button]]
            [kopdarbackoffice.shared.form-input :refer [text-input select-input]]
            [kopdarbackoffice.shared.icon :refer [icon]]
            [kopdarbackoffice.shared.pagination :refer [pagination]]
            [kopdarbackoffice.shared.search-filter :refer [search-filter]]
            [kopdarbackoffice.shared.trashed-message :refer [trashed-message]]))

(defn index
  [{:keys [products]}]
  (let [{:keys [data links]} (j/lookup products)]
    [:div
     [:> Head {:title "Products"}]
     [:h1 {:class "mb-8 text-3xl font-bold"} "Products"]
     [:div {:class "flex items-center justify-between mb-6"}
      [:f> search-filter]
      [:> InertiaLink
       {:class "btn-indigo focus:outline-none",
        :href (js/route "products.create")} [:span "Create "]
       [:span {:class "hidden md:inline"} "Product"]]]
     [:div {:class "overflow-x-auto bg-white rounded shadow"}
      [:table {:class "w-full whitespace-nowrap"}
       [:thead
        [:tr {:class "font-bold text-left"}
         [:th {:class "px-6 pt-5 pb-4"} "Kode"]
         [:th {:class "px-6 pt-5 pb-4"} "Nama Produk"]
         [:th {:class "px-6 pt-5 pb-4"} "Toko"]
         [:th {:class "px-6 pt-5 pb-4", :col-span "2"} "Harga Jual"]]]
       [:tbody
        (for [product data
              :let [{:keys [tbid nama kode store hjua kdstblmasttoko]} (j/lookup product)]]
          [:tr {:class "hover:bg-gray-100 focus-within:bg-gray-100"
                :key tbid}
           [:td {:class "border-t"}
            [:> InertiaLink {:href (js/route "products.edit" tbid)
                             :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
             kode
             ;(when deldt
             ;  [icon {:name :trash
             ;         :class "flex-shrink-0 w-3 h-3 ml-2 text-gray-400 fill-current"}])
             ]]
           [:td {:class "border-t"}
            [:> InertiaLink {:href (js/route "products.edit" tbid)
                             :tab-index "-1"
                             :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
             nama]]
           [:td {:class "border-t"}
            [:> InertiaLink {:href (js/route "products.edit" tbid)
                             :tab-index "-1"
                             :class "flex intems-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
             (when kdstblmasttoko (j/get kdstblmasttoko :nama))]]
           [:td {:class "border-t"}
            [:> InertiaLink {:href (js/route "products.edit" tbid)
                             :tab-index "-1"
                             :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
             hjua]]
           [:td {:class "w-px border-t"}
            [:> InertiaLink {:href (js/route "products.edit" tbid)
                             :tab-index "-1"
                             :class "flex items-center px-4 focus:outline-none"}
             [icon {:name :cheveron-right
                    :class "block w-6 h-6 text-gray-400 fill-current"}]]]])
        (when (zero? (count data))
          [:tr
           [:td {:class "px-6 py-4 border-t"
                 :col-span "5"}
            "No products found."]])]]]
     [pagination links]]))

(defn create-form []
  (let [{:keys [data setData errors post processing]}
        (j/lookup (useForm #js {:nama ""
                                :kode ""
                                :kat1bid ""
                                :kat2bid ""
                                :kat3bid ""
                                :hbli ""
                                :hjua ""
                                :qstk ""
                                :ratg ""
                                :dsc1 ""
                                :dsc2 ""
                                :dsc3 ""
                                :imag ""}))
        on-submit #(do (.preventDefault %)
                       (post (js/route "products.store")))]
    [:div
     [:> Head {:title "Create Product"}]
     [:h1 {:class "mb-8 text-3xl font-bold"}
      [:> InertiaLink {:href (js/route "products")
                       :class "text-indigo-400 hover:text-indigo-600"}
       "Products"]
      [:span {:class ""} " / "]
      "Create"]
     [:div {:class "max-w-3xl overflow-hidden bg-white rounded shadow"}
      [:form {:on-submit on-submit}
       [:div {:class "flex flex-wrap p-8 -mb-8 -mr-6"}
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Nama"
                     :name "nama"
                     :errors (.-nama errors)
                     :value (.-nama data)
                     :on-change #(setData "nama" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Kode"
                     :name "kode"
                     :errors (.-kode errors)
                     :value (.-kode data)
                     :on-change #(setData "kode" (.. % -target -value))}]
        [select-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                       :label "Kategori 1"
                       :name "kat1bid"
                       :errors (.-kat1bid errors)
                       :value (.-kat1bid data)
                       :on-change #(setData "kat1bid" (.. % -target -value))}
         [:option {:value ""}]
         [:option {:value "000001"} "Fashion Pria"]
         [:option {:value "000002"} "Fashion Wanita"]
         [:option {:value "000003"} "Fashion Anak dan Bayi"]
         [:option {:value "000004"} "Fashion Muslim"]
         [:option {:value "000005"} "Makanan dan Minuman"]]
        [select-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                       :label "Kategori 2"
                       :name "kat2bid"
                       :errors (.-kat2bid errors)
                       :value (.-kat2bid data)
                       :on-change #(setData "kat2bid" (.. % -target -value))}
         [:option {:value ""}]
         [:option {:value "000001000001"} "Atasan Pria"]
         [:option {:value "000001000002"} "Aksesoris Pria"]
         [:option {:value "000001000003"} "Baju Tidur Pria"]
         [:option {:value "000001000004"} "Seragam Pria"]
         [:option {:value "000001000005"} "Masker Pria"]
         [:option {:value "000002000001"} "Atasan Wanita"]
         [:option {:value "000002000001"} "Aksesoris Wanita"]
         [:option {:value "000002000001"} "Batik Wanita"]
         [:option {:value "000002000001"} "Seragam Wanita"]
         [:option {:value "000002000001"} "Masker Wanita"]
         [:option {:value "000002000011"} "Makanan Hewan"]]
        [select-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                       :label "Kategori 3"
                       :name "kat3bid"
                       :errors (.-kat3bid errors)
                       :value (.-kat3bid data)
                       :on-change #(setData "kat3bid" (.. % -target -value))}
         [:option {:value ""}]
         [:option {:value "000001000001"} "Kaos Polo Pria"]
         [:option {:value "000001000002"} "Kaos Pria"]
         [:option {:value "000001000003"} "Kemeja Pria"]
         [:option {:value "000002000004"} "Aksesoris Kacamata"]
         [:option {:value "000002000005"} "Ikat Pinggang Pria"]
         [:option {:value "000002000006"} "Kacamata Hitam Pria"]
         [:option {:value "000002000007"} "Kacamata Pria"]
         [:option {:value "000002000008"} "Sapu Tangan Pria"]
         [:option {:value "000002000009"} "Sarung Tangan Pria"]
         [:option {:value "000002000010"} "Scarf Pria"]
         [:option {:value "000002000011"} "Insole Sepatu Pria"]
         [:option {:value "000002000012"} "Kaos Kaki Pria"]
         [:option {:value "000002000013"} "Perawatan Sepatu"]
         [:option {:value "000002000014"} "Perawatan Sepatu"]
         [:option {:value "000002000015"} "Sendok Sepatu"]
         [:option {:value "000002000016"} "Tali Sepatu Pria"]
         [:option {:value "000003000017"} "Celana Tidur Pria"]
         [:option {:value "000003000018"} "Set Piyama Pria"]
         [:option {:value "000004000019"} "Seragam Driver Pria"]
         [:option {:value "000004000020"} "Seragam Koki Pria"]
         [:option {:value "000004000021"} "Seragam PNS Pria"]
         [:option {:value "000004000022"} "Seragam Perawat Pria"]
         [:option {:value "000004000023"} "Seragam Security Pria"]
         [:option {:value "000005000024"} "Masker Kain"]]
        
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Harga Beli"
                     :name "hbli"
                     :errors (.-hbli errors)
                     :value (.-hbli data)
                     :on-change #(setData "hbli" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Harga Jual"
                     :name "hjua"
                     :errors (.-hjua errors)
                     :value (.-hjua data)
                     :on-change #(setData "hjua" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Qstk"
                     :name "qstk"
                     :errors (.-qstk errors)
                     :value (.-qstk data)
                     :on-change #(setData "qstk" (.. % -target -value))}]
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
         "Create Product"]]]]]))

(defn edit-form [^js product]
  (let [{:keys [data setData errors put processing]}
        (j/lookup (useForm #js {:nama (or (.-nama product) "")
                                :kode (or (.-kode product) "")
                                :kat1bid (or (.-kat1bid product) "")
                                :kat2bid (or (.-kat2bid product) "")
                                :kat3bid (or (.-kat3bid product) "")
                                :hbli (or (.-hbli product) "")
                                :hjua (or (.-hjua product) "")
                                :qstk (or (.-qstk product) "")
                                :ratg (or (.-ratg product) "")
                                :dsc1 (or (.-dsc1 product) "")
                                :dsc2 (or (.-dsc2 product) "")
                                :dsc3 (or (.-dsc3 product) "")
                                :imag (or (.-imag product) "")}))
        on-submit #(do (.preventDefault %)
                       (put (js/route "products.update" (.-id product))))
        destroy #(when (js/confirm "Are you sure you want to delete this product?")
                   (.delete Inertia (js/route "products.destroy" (.-id product))))
        restore #(when (js/confirm "Are you sure you want to restore this product?")
                   (.put Inertia (js/route "products.restore" (.-id product))))]
    [:div
     [:> Head {:title (j/get product :nama)}]
     [:h1 {:class "mb-8 text-3xl font-bold"}
      [:> InertiaLink {:href (js/route "products")
                       :class "text-indigo-600 hover:text-indigo-700"}
       "Products"]
      [:span {:class "mx-2 font-medium text-indigo-600"} "/"]
      (.-name data)]
     (when-not (empty? (j/get product :chdt))
       [trashed-message {:on-restore restore}
        "This product has been deleted."])
     [:div {:class "max-w-3xl overflow-hidden bg-white rounded shadow"}
      [:form {:on-submit on-submit}
       [:div {:class "flex flex-wrap p-8 -mb-8 -mr-6"}
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Nama"
                     :name "nama"
                     :errors (.-nama errors)
                     :value (.-nama data)
                     :on-change #(setData "nama" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Kode"
                     :name "kode"
                     :errors (.-kode errors)
                     :value (.-kode data)
                     :on-change #(setData "kode" (.. % -target -value))}]
        [select-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                       :label "Kategori 1"
                       :name "kat1bid"
                       :type "text"
                       :errors (.-kat1bid errors)
                       :value (.-kat1bid data)
                       :on-change #(setData "kat1bid" (.. % -target -value))}
         [:option {:value ""}]
         [:option {:value "000001"} "Fashion Pria"]
         [:option {:value "000002"} "Fashion Wanita"]
         [:option {:value "000003"} "Fashion Anak dan Bayi"]
         [:option {:value "000004"} "Fashion Muslim"]
         [:option {:value "000005"} "Makanan dan Minuman"]]
        [select-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                       :label "Kategori 2"
                       :name "kat2bid"
                       :type "text"
                       :errors (.-kat2bid errors)
                       :value (.-kat2bid data)
                       :on-change #(setData "kat2bid" (.. % -target -value))}
         [:option {:value ""}]
         [:option {:value "000001000001"} "Atasan Pria"]
         [:option {:value "000001000002"} "Aksesoris Pria"]
         [:option {:value "000001000003"} "Baju Tidur Pria"]
         [:option {:value "000001000004"} "Seragam Pria"]
         [:option {:value "000001000005"} "Masker Pria"]
         [:option {:value "000002000001"} "Atasan Wanita"]
         [:option {:value "000002000001"} "Aksesoris Wanita"]
         [:option {:value "000002000001"} "Batik Wanita"]
         [:option {:value "000002000001"} "Seragam Wanita"]
         [:option {:value "000002000001"} "Masker Wanita"]
         [:option {:value "000002000011"} "Makanan Hewan"]]
        [select-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                       :label "Kategori 3"
                       :name "kat3bid"
                       :type "text"
                       :errors (.-kat3bid errors)
                       :value (.-kat3bid data)
                       :on-change #(setData "kat3bid" (.. % -target -value))}
         [:option {:value ""}]
         [:option {:value "000001000001"} "Kaos Polo Pria"]
         [:option {:value "000001000002"} "Kaos Pria"]
         [:option {:value "000001000003"} "Kemeja Pria"]
         [:option {:value "000002000004"} "Aksesoris Kacamata"]
         [:option {:value "000002000005"} "Ikat Pinggang Pria"]
         [:option {:value "000002000006"} "Kacamata Hitam Pria"]
         [:option {:value "000002000007"} "Kacamata Pria"]
         [:option {:value "000002000008"} "Sapu Tangan Pria"]
         [:option {:value "000002000009"} "Sarung Tangan Pria"]
         [:option {:value "000002000010"} "Scarf Pria"]
         [:option {:value "000002000011"} "Insole Sepatu Pria"]
         [:option {:value "000002000012"} "Kaos Kaki Pria"]
         [:option {:value "000002000013"} "Perawatan Sepatu"]
         [:option {:value "000002000014"} "Perawatan Sepatu"]
         [:option {:value "000002000015"} "Sendok Sepatu"]
         [:option {:value "000002000016"} "Tali Sepatu Pria"]
         [:option {:value "000003000017"} "Celana Tidur Pria"]
         [:option {:value "000003000018"} "Set Piyama Pria"]
         [:option {:value "000004000019"} "Seragam Driver Pria"]
         [:option {:value "000004000020"} "Seragam Koki Pria"]
         [:option {:value "000004000021"} "Seragam PNS Pria"]
         [:option {:value "000004000022"} "Seragam Perawat Pria"]
         [:option {:value "000004000023"} "Seragam Security Pria"]
         [:option {:value "000005000024"} "Masker Kain"]]
        
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Harga Beli"
                     :name "hbli"
                     :errors (.-hbli errors)
                     :value (.-hbli data)
                     :on-change #(setData "hbli" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Harga Jual"
                     :name "hjua"
                     :errors (.-hjua errors)
                     :value (.-hjua data)
                     :on-change #(setData "hjua" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Qstk"
                     :name "qstk"
                     :errors (.-qstk errors)
                     :value (.-qstk data)
                     :on-change #(setData "qstk" (.. % -target -value))}]
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
       [:div {:class "flex items-center px-8 py-4 bg-gray-100 border-t border-gray-200"}
        (when (empty? (j/get product :chdt))
          [delete-button {:on-delete destroy}
           "Delete Product"])
        [loading-button {:loading processing
                         :type "submit"
                         :class "ml-auto btn-indigo"}
         "Update Product"]]]]
     [:h2 {:class "mt-12 text-2xl font-bold"} "Stores"]
     [:div {:class "mt-6 overflow-x-auto bg-white rounded shadow"}
      [:table {:class "w-full whitespace-nowrap"}
       [:thead
        [:tr {:class "font-bold text-left"}
         [:th {:class "px-6 pt-5 pb-4"} "Nama"]
         [:th {:class "px-6 pt-5 pb-4"} "Email"]
         [:th {:class "px-6 pt-5 pb-4" :col-span "2"} "Telp"]]]
       [:tbody
        (for [item (.-stores product)
              :let [{:keys [tbid nama emai telp]} (j/lookup item)]]
          [:tr {:class "hover:bg-gray-100 focus-within:bg-gray-100"
                :key tbid}
           [:td.border-t
            [:> InertiaLink {:href (js/route "stores.edit" tbid)
                             :class "flex items-center px-6 py-4 focus:text-indigo focus:outline-none"}
             nama
             ;(when deldt
             ;  [icon {:name :trash
             ;         :class "flex-shrink-0 w-3 h-3 ml-2 text-gray-400 fill-current"}])
             ]]
           [:td.border-t
            [:> InertiaLink {:tab-index "-1"
                             :href (js/route "stores.edit" tbid)
                             :class "flex items-center px-6 py-4 focus:text-indigo focus:outline-none"}
             emai]]
           [:td.border-t
            [:> InertiaLink {:tab-index "-1"
                             :href (js/route "stores.edit" tbid)
                             :class "flex items-center px-6 py-4 focus:text-indigo focus:outline-none"}
             telp]]
           [:td.border-t.w-px
            [:> InertiaLink {:tab-index "-1"
                             :href (js/route "stores.edit" tbid)
                             :class "flex items-center px-4"}
             [icon {:name :cheveron-right
                    :class "block w-6 h-6 text-gray-400 fill-current"}]]]])
        (when (empty? (j/get product :stores))
          [:tr
           [:td {:class "px-6 py-4 border-t"
                 :col-span "4"}
            "No stores found."]])]]]]))

(defn edit [{:keys [product]}]
  [:f> edit-form product])

(defn create []
  [:f> create-form])
