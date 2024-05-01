(ns kopdarbackoffice.pages.chats
  (:require ["@inertiajs/inertia" :refer [Inertia]]
            ["@inertiajs/inertia-react" :refer [InertiaLink useForm Head]]
            [applied-science.js-interop :as j]
            [kopdarbackoffice.shared.buttons :refer [loading-button delete-button]]
            [kopdarbackoffice.shared.form-input :refer [text-input select-input]]
            [kopdarbackoffice.shared.icon :refer [icon]]
            [kopdarbackoffice.shared.search-filter :refer [search-filter]]
            [kopdarbackoffice.shared.trashed-message :refer [trashed-message]]))

(defn index [{:keys [chats]}]
  [:div
   [:> Head {:title "Chats"}]
   [:h1 {:class "mb-8 text-3xl font-bold"} "Chats"]
   [:div {:class "flex items-center justify-between mb-6"}
    [:f> search-filter]
    [:> InertiaLink {:class "btn-indigo focus:outline-none"
                     :href (js/route "chats.create")}
     [:span "Create"]
     [:span {:class "hidden md:inline"} " Chat"]]]
   [:div {:class "overflow-x-auto bg-white rounded shadow"}
    [:table {:class "w-full whitespace-nowrap"}
     [:thead
      [:tr {:class "font-bold text-left"}
       [:th {:class "px-6 pt-5 pb-4"} "TRNO"]
       [:th {:class "px-6 pt-5 pb-4" :col-span "2"} "Deskripsi"]]]
     [:tbody
      (for [chat chats
            :let [{:keys [tbid trno dsc1]} (j/lookup chat)]]
        [:tr {:class "hover:bg-gray-100 focus-within:bg-gray-100"
              :key tbid}
         [:td {:class "border-t"}
          [:> InertiaLink {:href (js/route "chats.edit" tbid)
                           :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
           trno
           ;(when deleted_at
           ;  [icon {:name :trash
           ;         :class "flex-shrink-0 w-3 h-3 ml-2 text-gray-400 fill-current"}])
           ]]
         [:td {:class "border-t"}
          [:> InertiaLink {:href (js/route "chats.edit" tbid)
                           :tab-index "-1"
                           :class "flex items-center px-6 py-4 focus:text-indigo-700 focus:outline-none"}
           dsc1]]
         [:td {:class "w-px border-t"}
          [:> InertiaLink {:href (js/route "chats.edit" tbid)
                           :tab-index "-1"
                           :class "flex items-center px-4 focus:outline-none"}
           [icon {:name :cheveron-right
                  :class "block w-6 h-6 text-gray-400 fill-current"}]]]])
      (when (zero? (count chats))
        [:tr
         [:td {:class "px-6 py-4 border-t"
               :col-span "4"}
          "No Chats found."]])]]]])

(defn create-form []
  (let [{:keys [data setData errors post processing]}
        (j/lookup (useForm #js {:trno ""
                                :trit ""
                                :tokotbid ""
                                :usertbid ""
                                :custtbid ""
                                :sendtbid ""
                                :recptbid ""
                                :subj ""
                                :stat ""
                                :dsc1 ""
                                :dsc2 ""
                                :dsc3 ""
                                :stde ""}))
        on-submit #(do (.preventDefault %)
                       (post (js/route "chats.store")))]
    [:div
     [:> Head {:title "Create Chat"}]
     [:h1 {:class "mb-8 text-3xl font-bold"}
      [:> InertiaLink {:href (js/route "chats")
                       :class "text-indigo-400 hover:text-indigo-600"}
       "Chats" [:span {:class "font-medium text-indigo-400"} " / "]]
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
                     :label "TRIT"
                     :name "trit"
                     :errors (.-trit errors)
                     :value (.-trit data)
                     :on-change #(setData "trit" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Toko Tbid"
                     :name "tokotbid"
                     :errors (.-tokotbid errors)
                     :value (.-tokotbid data)
                     :on-change #(setData "tokotbid" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "User Tbid"
                     :name "usertbid"
                     :errors (.-usertbid errors)
                     :value (.-usertbid data)
                     :on-change #(setData "usertbid" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Cust Tbid"
                     :name "custtbid"
                     :errors (.-custtbid errors)
                     :value (.-custtbid data)
                     :on-change #(setData "custtbid" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Send Tbid"
                     :name "sendtbid"
                     :errors (.-sendtbid errors)
                     :value (.-sendtbid data)
                     :on-change #(setData "sendtbid" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Recpt Tbid"
                     :name "recptbid"
                     :errors (.-recptbid errors)
                     :value (.-recptbid data)
                     :on-change #(setData "recptbid" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Subject"
                     :name "subj"
                     :errors (.-subj errors)
                     :value (.-subj data)
                     :on-change #(setData "subj" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Status"
                     :name "stat"
                     :errors (.-stat errors)
                     :value (.-stat data)
                     :on-change #(setData "stat" (.. % -target -value))}]
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
                     :on-change #(setData "stde" (.. % -target -value))}]
        ]
       [:div {:class "px-8 py-4 bg-gray-50 border-t border-gray-100 flex justify-end items-center"}
        [loading-button {:loading processing
                         :type "submit"
                         :class "btn-indigo"}
         "Create Chat"]]]]]))

(defn edit-form [^js chat]
  (let [{:keys [data setData errors post processing]}
        (j/lookup (useForm #js {:trno (or (.-trno chat) "")
                                :trit (or (.-trit chat) "")
                                :tokotbid (or (.-tokotbid chat) "")
                                :usertbid (or (.-usertbid chat) "")
                                :custtbid (or (.-custtbid chat) "")
                                :sendtbid (or (.-sendtbid chat) "")
                                :recptbid (or (.-recptbid chat) "")
                                :subj (or (.-subj chat) "")
                                :stat (or (.-stat chat) "")
                                :dsc1 (or (.-dsc1 chat) "")
                                :dsc2 (or (.-dsc2 chat) "")
                                :dsc3 (or (.-dsc3 chat) "")
                                :stde (or (.-stde chat) "")
                                }))
        on-submit #(do (.preventDefault %)
                       (post (js/route "chats.update" (.-tbid chat))))
        destroy #(when (js/confirm "Are you sure you want to delete this chat?")
                   (.delete Inertia (js/route "chats.destroy" (.-tbid chat))))
        restore #(when (js/confirm "Are you sure you want to restore this chat?")
                   (.put Inertia (js/route "chats.restore" (.-tbid chat))))]
    [:<>
     [:> Head {:title (str (j/get chat :trno) " " (j/get chat :trit))}]
     [:div {:class "flex justify-start max-w-lg mb-8"}
      [:h1 {:class "text-3xl font-bold"}
       [:> InertiaLink {:class "text-indigo-400 hover:text-indigo-700"
                        :href (js/route "chats")} "Chats"]
       [:span {:class "mx-2 font-medium text-indigo-400"} "/"]
       (.-trno data) " " (.-trit data)]]
     ;(when-not (empty? (j/get chat :deleted_at))
     ;  [trashed-message {:on-restore restore}
     ;   "This chat has been deleted."])
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
                     :label "Toko Tbid"
                     :name "tokotbid"
                     :errors (.-tokotbid errors)
                     :value (.-tokotbid data)
                     :on-change #(setData "tokotbid" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "User Tbid"
                     :name "usertbid"
                     :errors (.-usertbid errors)
                     :value (.-usertbid data)
                     :on-change #(setData "usertbid" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Cust Tbid"
                     :name "custtbid"
                     :errors (.-custtbid errors)
                     :value (.-custtbid data)
                     :on-change #(setData "custtbid" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Send Tbid"
                     :name "sendtbid"
                     :errors (.-sendtbid errors)
                     :value (.-sendtbid data)
                     :on-change #(setData "sendtbid" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Recpt Tbid"
                     :name "recptbid"
                     :errors (.-recptbid errors)
                     :value (.-recptbid data)
                     :on-change #(setData "recptbid" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Subject"
                     :name "subj"
                     :errors (.-subj errors)
                     :value (.-subj data)
                     :on-change #(setData "subj" (.. % -target -value))}]
        [text-input {:class "w-full pb-8 pr-6 lg:w-1/2"
                     :label "Status"
                     :name "stat"
                     :errors (.-stat errors)
                     :value (.-stat data)
                     :on-change #(setData "stat" (.. % -target -value))}]
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
                     :on-change #(setData "stde" (.. % -target -value))}]
        ]
       ;; TODO Add file input
       [:div {:class "flex items-center px-8 py-4 bg-gray-100 border-t border-gray-200"}
        (when (empty? (j/get chat :deleted_at))
          [delete-button {:on-delete destroy}
           "Delete Chat"])
        [loading-button {:loading processing
                         :type "submit"
                         :class "ml-auto btn-indigo"}
         "Update Chat"]]]]]))

(defn create []
  [:f> create-form])

(defn edit [{:keys [chat]}]
  [:f> edit-form chat])
