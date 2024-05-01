(ns kopdarbackoffice.app
  (:require ["@inertiajs/inertia" :refer [Inertia]]
            ["@inertiajs/inertia-react" :refer [createInertiaApp]]
            ["@inertiajs/progress" :refer [InertiaProgress]]
            [applied-science.js-interop :as j]
            [kopdarbackoffice.pages.etalases :as etalases]
            [kopdarbackoffice.pages.categories-one :as categories-one]
            [kopdarbackoffice.pages.categories-two :as categories-two]
            [kopdarbackoffice.pages.categories-three :as categories-three]
            [kopdarbackoffice.pages.user-addresses :as user-addresses]
            [kopdarbackoffice.pages.payment-methods :as payment-methods]
            [kopdarbackoffice.pages.deliveries :as deliveries]
            [kopdarbackoffice.pages.trx-statuses :as trx-statuses]
            [kopdarbackoffice.pages.user-groups :as user-groups]
            [kopdarbackoffice.pages.apps-users :as apps-users]
            [kopdarbackoffice.pages.rate-products :as rate-products]
            [kopdarbackoffice.pages.rate-stores :as rate-stores]
            [kopdarbackoffice.pages.chats :as chats]
            [kopdarbackoffice.pages.trx-details :as trx-details]
            [kopdarbackoffice.pages.trx-headers :as trx-headers]
            [kopdarbackoffice.pages.stores :as stores]
            [kopdarbackoffice.pages.dashboard :as dashboard]
            [kopdarbackoffice.pages.login :refer [login]]
            [kopdarbackoffice.pages.products :as products]
            [kopdarbackoffice.pages.reports :as reports]
            [kopdarbackoffice.shared.layout :refer [layout]]
            [reagent.core :as r]
            [reagent.dom :as d]))

(.init InertiaProgress)

(def pages {"Dashboard/Index" dashboard/index
            "Auth/Login" login
            "Etalases/Index" etalases/index
            "Etalases/Create" etalases/create
            "Etalases/Edit" etalases/edit
            "CategoriesOne/Index" categories-one/index
            "CategoriesOne/Create" categories-one/create
            "CategoriesOne/Edit" categories-one/edit
            "CategoriesTwo/Index" categories-two/index
            "CategoriesTwo/Create" categories-two/create
            "CategoriesTwo/Edit" categories-two/edit
            "CategoriesThree/Index" categories-three/index
            "CategoriesThree/Create" categories-three/create
            "CategoriesThree/Edit" categories-three/edit            
            "UserAddresses/Index" user-addresses/index
            "UserAddresses/Create" user-addresses/create
            "UserAddresses/Edit" user-addresses/edit
            "PaymentMethods/Index" payment-methods/index
            "PaymentMethods/Create" payment-methods/create
            "PaymentMethods/Edit" payment-methods/edit
            "Deliveries/Index" deliveries/index
            "Deliveries/Create" deliveries/create
            "Deliveries/Edit" deliveries/edit
            "TrxStatuses/Index" trx-statuses/index
            "TrxStatuses/Create" trx-statuses/create
            "TrxStatuses/Edit" trx-statuses/edit
            "UserGroups/Index" user-groups/index
            "UserGroups/Create" user-groups/create
            "UserGroups/Edit" user-groups/edit
            "AppsUsers/Index" apps-users/index
            "AppsUsers/Create" apps-users/create
            "AppsUsers/Edit" apps-users/edit
            "RateProducts/Index" rate-products/index
            "RateProducts/Create" rate-products/create
            "RateProducts/Edit" rate-products/edit
            "RateStores/Index" rate-stores/index
            "RateStores/Create" rate-stores/create
            "RateStores/Edit" rate-stores/edit
            "Chats/Index" chats/index
            "Chats/Create" chats/create
            "Chats/Edit" chats/edit
            "TrxDetails/Index" trx-details/index
            "TrxDetails/Create" trx-details/create
            "TrxDetails/Edit" trx-details/edit
            "TrxHeaders/Index" trx-headers/index
            "TrxHeaders/Create" trx-headers/create
            "TrxHeaders/Edit" trx-headers/edit
            "Reports/Index" reports/index
            "Products/Index" products/index
            "Products/Create" products/create
            "Products/Edit" products/edit
            "Stores/Index" stores/index
            "Stores/Create" stores/create
            "Stores/Edit" stores/edit
            })

(defn init! []
  (createInertiaApp
   #js {:resolve (fn [name]
                   (let [^js comp (r/reactify-component (get pages name))]
                     (when-not (= name "Auth/Login")
                       (set! (.-layout comp) (fn [page] (r/as-element [layout page]))))
                     comp))
        :title (fn [title] (str title " | Kopdar Backoffice"))
        :setup (j/fn [^:js {:keys [el App props]}]
                 (d/render (r/as-element [:f> App props]) el))}))

(defn ^:dev/after-load reload []
  (.reload Inertia))
