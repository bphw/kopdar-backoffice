(ns kopdarbackoffice.pages.rate-products
  (:require ["@inertiajs/inertia" :refer [Inertia]]
            ["@inertiajs/inertia-react" :refer [InertiaLink useForm Head]]
            [applied-science.js-interop :as j]
            [kopdarbackoffice.shared.buttons :refer [loading-button delete-button]]
            [kopdarbackoffice.shared.form-input :refer [text-input select-input]]
            [kopdarbackoffice.shared.icon :refer [icon]]
            [kopdarbackoffice.shared.search-filter :refer [search-filter]]
            [kopdarbackoffice.shared.trashed-message :refer [trashed-message]]))

(defn index [{:keys [rates]}]
  [:div
   [:> Head {:title "Rate Product"}]
   [:h1 {:class "mb-8 text-3xl font-bold"} "Rate Products"]
   [:div {:class "flex items-center justify-between mb-6"}
    [:f> search-filter]
    [:> InertiaLink {:class "btn-indigo focus:outline-none"
                     :href (js/route "rate-products.create")}
     [:span "Create"]
     [:span {:class "hidden md:inline"} " Rate Product"]]]
   [:div {:class "overflow-x-auto bg-white rounded shadow"}
    [:table {:class "w-full whitespace-nowrap"}
     [:thead
      [:tr {:class "font-bold text-left"}
       [:th {:class "px-6 pt-5 pb-4"} "TR No"]
       [:th {:class "px-6 pt-5 pb-4"} "Trit"]
       [:th {:class "px-6 pt-5 pb-4" :col-span "2"} "Rating"]]]
     [:tbody
      (for [rate rates
            :let [{:keys [tbid trno trit ratg]} (j/lookup rate)]]
        [:tr {:class "hover:bg-gray-100 focus-within:bg-gray-100"
              :key tbid}
         [:td {:class "border-t"}
          [:> InertiaLink {:href (js/route "rate-products.edit" tbid)
                           :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
           trno
           ;(when deleted_at
           ;  [icon {:name :trash
           ;         :class "flex-shrink-0 w-3 h-3 ml-2 text-gray-400 fill-current"}])
           ]]
         [:td {:class "border-t"}
          [:> InertiaLink {:href (js/route "rate-products.edit" tbid)
                           :tab-index "-1"
                           :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
           trit]]
         [:td {:class "border-t"}
          [:> InertiaLink {:href (js/route "rate-products.edit" tbid)
                           :tab-index "-1"
                           :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
           ;(if owner "Owner" "User")
           ratg
           ]]
         [:td {:class "w-px border-t"}
          [:> InertiaLink {:href (js/route "rate-products.edit" tbid)
                           :tab-index "-1"
                           :class "flex items-center px-4 focus:outline-none"}
           [icon {:name :cheveron-right
                  :class "block w-6 h-6 text-gray-400 fill-current"}]]]])
      (when (zero? (count rates))
        [:tr
         [:td {:class "px-6 py-4 border-t"
               :col-span "4"}
          "No Rates found."]])]]]])

(defn create-form []
  (let [{:keys [data setData errors post processing]}
        (j/lookup (useForm #js {:trno ""
                                :trit ""
                                :ratg ""
                                }))
        on-submit #(do (.preventDefault %)
                       (post (js/route "rate-products.store")))]
    [:div
     [:> Head {:title "Create Rating"}]
     [:h1 {:class "mb-8 text-3xl font-bold"}
      [:> InertiaLink {:href (js/route "rate-products")
                       :class "text-indigo-400 hover:text-indigo-600"}
       "Rating" [:span {:class "font-medium text-indigo-400"} " / "]]
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
                     :label "Rating"
                     :name "ratg"
                     :errors (.-ratg errors)
                     :value (.-ratg data)
                     :on-change #(setData "ratg" (.. % -target -value))}]
        ]
       [:div {:class "px-8 py-4 bg-gray-50 border-t border-gray-100 flex justify-end items-center"}
        [loading-button {:loading processing
                         :type "submit"
                         :class "btn-indigo"}
         "Create Rating"]]]]]))

(defn edit-form [^js rate]
  (let [{:keys [data setData errors post processing]}
        (j/lookup (useForm #js {:trno (or (.-trno rate) "")
                                :trit (or (.-trit rate) "")
                                :ratg (or (.-ratg rate) "")
                                }
                           ))
        on-submit #(do (.preventDefault %)
                       (post (js/route "rate-products.update" (.-tbid rate))))
        destroy #(when (js/confirm "Are you sure you want to delete this rating?")
                   (.delete Inertia (js/route "rate-products.destroy" (.-tbid rate))))
        restore #(when (js/confirm "Are you sure you want to restore this rating?")
                   (.put Inertia (js/route "rate-products.restore" (.-tbid rate))))]
    [:<>
     [:> Head {:title (str (j/get rate :trno) " " (j/get rate :trit))}]
     [:div {:class "flex justify-start max-w-lg mb-8"}
      [:h1 {:class "text-3xl font-bold"}
       [:> InertiaLink {:class "text-indigo-400 hover:text-indigo-700"
                        :href (js/route "rate-products")} "Rating"]
       [:span {:class "mx-2 font-medium text-indigo-400"} "/"]
       (.-trno data) " " (.-trit data)]]
     ;(when-not (empty? (j/get rate :deleted_at))
     ;  [trashed-message {:on-restore restore}
     ;   "This user has been deleted."])
     [:div {:class "max-w-3xl overflow-hidden bg-white rounded shadow"}
      [:form {:on-submit on-submit}
       [:div {:class "flex flex-wrap p-8 -mb-8 -mr-6"}
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Trno"
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
                     :label "Rating"
                     :name "ratg"
                     :errors (.-ratg errors)
                     :value (.-ratg data)
                     :on-change #(setData "ratg" (.. % -target -value))}]
        ]
       ;; TODO Add file input
       [:div {:class "flex items-center px-8 py-4 bg-gray-100 border-t border-gray-200"}
        ;(when (empty? (j/get rate :deleted_at))
        ;  [delete-button {:on-delete destroy}
        ;   "Delete User"])
        [loading-button {:loading processing
                         :type "submit"
                         :class "ml-auto btn-indigo"}
         "Update Rating"]]]]]))

(defn create []
  [:f> create-form])

(defn edit [{:keys [rate]}]
  [:f> edit-form rate])
