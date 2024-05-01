(ns kopdarbackoffice.pages.categories-one
  (:require ["@inertiajs/inertia" :refer [Inertia]]
            ["@inertiajs/inertia-react" :refer [InertiaLink useForm Head]]
            [applied-science.js-interop :as j]
            [kopdarbackoffice.shared.buttons :refer [loading-button delete-button]]
            [kopdarbackoffice.shared.form-input :refer [text-input select-input]]
            [kopdarbackoffice.shared.icon :refer [icon]]
            [kopdarbackoffice.shared.pagination :refer [pagination]]
            [kopdarbackoffice.shared.search-filter :refer [search-filter]]
            [kopdarbackoffice.shared.trashed-message :refer [trashed-message]]))

(defn index [{:keys [cats]}]
  (let [{:keys [data links]} (j/lookup cats)]
    [:div
     [:> Head {:title "First Categories"}]
     [:h1 {:class "mb-8 text-3xl font-bold"} "First Categoeies"]
     [:div {:class "flex items-center justify-between mb-6"}
      [:f> search-filter]
      [:> InertiaLink
       {:class "btn-indigo focus:outline-none",
        :href (js/route "categories-one.create")} [:span "Create "]
       [:span {:class "hidden md:inline"} "Categories"]]]
     [:div {:class "overflow-x-auto bg-white rounded shadow"}
      [:table {:class "w-full whitespace-nowrap"}
       [:thead
        [:tr {:class "font-bold text-left"}
         [:th {:class "px-6 pt-5 pb-4"} "Kode"]
         [:th {:class "px-6 pt-5 pb-4", :col-span "2"} "Nama"]]]
       [:tbody
        (for [etalase data
              :let [{:keys [tbid kode nama]} (j/lookup etalase)]]
          [:tr {:class "hover:bg-gray-100 focus-within:bg-gray-100"
                :key tbid}
           [:td {:class "border-t"}
            [:> InertiaLink {:href (js/route "categories-one.edit" tbid)
                             :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
             kode
             ;(when deldt
             ;  [icon {:name :trash
             ;         :class "flex-shrink-0 w-3 h-3 ml-2 text-gray-400 fill-current"}])
             ]]
           [:td {:class "border-t"}
            [:> InertiaLink {:href (js/route "categories-one.edit" tbid)
                             :tab-index "-1"
                             :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
             nama]]
           [:td {:class "w-px border-t"}
            [:> InertiaLink {:href (js/route "categories-one.edit" tbid)
                             :tab-index "-1"
                             :class "flex items-center px-4 focus:outline-none"}
             [icon {:name :cheveron-right
                    :class "block w-6 h-6 text-gray-400 fill-current"}]]]])
        (when (zero? (count data))
          [:tr
           [:td {:class "px-6 py-4 border-t"
                 :col-span "4"}
            "No 1st categories found."]])]]]
     [pagination links]]))

(defn create-form [^js etalases]
  (let [{:keys [data setData errors post processing]}
        (j/lookup (useForm #js {
                                :kode ""
                                :nama ""
                                :dsc1 ""
                                :dsc2 ""
                                :dsc3 ""
                                :stde ""}))
        on-submit #(do (.preventDefault %)
                       (post (js/route "categories-one.store")))]
    [:div
     [:> Head {:title "Create 1st Category"}]
     [:h1 {:class "mb-8 text-3xl font-bold"}
      [:> InertiaLink {:href (js/route "etalases")
                       :class "text-indigo-400 hover:text-indigo-600"}
       "Etalases"]
      [:span {:class ""} " / "]
      "Create"]
     [:div {:class "max-w-3xl overflow-hidden bg-white rounded shadow"}
      [:form {:on-submit on-submit}
       [:div {:class "flex flex-wrap p-8 -mb-8 -mr-6"}]
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
                     :label "Stde"
                     :name "stde"
                     :errors (.-stde errors)
                     :value (.-stde data)
                     :on-change #(setData "stde" (.. % -target -value))}]]
       [:div {:class "px-8 py-4 bg-gray-50 border-t border-gray-100 flex justify-end items-center"}
        [loading-button {:loading processing
                         :type "submit"
                         :class "btn-indigo"}
         "Create 1st Categories"]]]]))

(defn edit-form [^js cat]
  (let [{:keys [data setData errors put processing]}
        (j/lookup (useForm #js {
                                :kode (or (.-kode cat) "")
                                :nama (or (.-nama cat) "")
                                :dsc1 (or (.-dsc1 cat) "")
                                :dsc2 (or (.-dsc2 cat) "")
                                :dsc3 (or (.-dsc3 cat) "")
                                :stde (or (.-stde cat) "")}))
        on-submit #(do (.preventDefault %)
                       (put (js/route "categories-one.update" (.-tbid cat))))
        destroy #(when (js/confirm "Are you sure you want to delete this etalase?")
                   (.delete Inertia (js/route "categories-one.destroy" (.-tbid cat))))
        restore #(when (js/confirm "Are you sure you want to restore this etalase?")
                   (.put Inertia (js/route "categories-one.restore" (.-tbid cat))))]
    [:div
     [:> Head {:title (str (j/get cat :kode) " " (j/get cat :nama))}]
     [:h1 {:class "mb-8 text-3xl font-bold"}
      [:> InertiaLink {:href (js/route "categories-one")
                       :class "text-indigo-600 hover:text-indigo-700"}
       "1st Categories"]
      [:span {:class "mx-2 font-medium text-indigo-600"}
       "/"]
      (.-kode data) " " (.-nama data)]
     ;(when-not (empty? (j/get etalase :deldt))
     ;  [trashed-message {:on-reetalase reetalase}
     ;   "This etalase has been deleted."])
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
                     :label "Stde"
                     :name "stde"
                     :errors (.-stde errors)
                     :value (.-stde data)
                     :on-change #(setData "stde" (.. % -target -value))}]]
       [:div {:class "flex items-center px-8 py-4 bg-gray-100 border-t border-gray-200"}
        ;(when (empty? (j/get etalase :deldt))
        ;  [delete-button {:on-delete destroy}
        ;   "Delete Etalase"])
        [loading-button {:loading processing
                         :type "submit"
                         :class "ml-auto btn-indigo"}
         "Update Categories"]]]]

     
     ]))

(defn edit [{:keys [cat]}]
  [:f> edit-form cat])

(defn create []
  [:f> create-form])
