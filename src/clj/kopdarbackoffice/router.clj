(ns kopdarbackoffice.router
  (:require [buddy.auth.backends :as backends]
            [buddy.auth.middleware :as bam]
            [inertia.middleware :as inertia]
            [kopdarbackoffice.handlers.auth :as auth]
            [kopdarbackoffice.handlers.stores :as stores]
            [kopdarbackoffice.handlers.dashboard :as dashboard]
            [kopdarbackoffice.handlers.products :as products]
            [kopdarbackoffice.handlers.reports :as reports]
            [kopdarbackoffice.handlers.etalases :as etalases]
            [kopdarbackoffice.handlers.categories-one :as categories-one]
            [kopdarbackoffice.handlers.categories-two :as categories-two]
            [kopdarbackoffice.handlers.categories-three :as categories-three]
            [kopdarbackoffice.handlers.payment-methods :as payment-methods]
            [kopdarbackoffice.handlers.user-addresses :as user-addresses]
            [kopdarbackoffice.handlers.deliveries :as deliveries]
            [kopdarbackoffice.handlers.trx-statuses :as trx-statuses]
            [kopdarbackoffice.handlers.apps-users :as apps-users]
            [kopdarbackoffice.handlers.rate-products :as rate-products]
            [kopdarbackoffice.handlers.rate-stores :as rate-stores]
            [kopdarbackoffice.handlers.chats :as chats]
            [kopdarbackoffice.handlers.trx-headers :as trx-headers]
            [kopdarbackoffice.handlers.trx-details :as trx-details]
            [kopdarbackoffice.handlers.user-groups :as user-groups]
            [kopdarbackoffice.middleware.auth :refer [wrap-auth]]
            [kopdarbackoffice.middleware.inertia :refer [wrap-inertia-share]]
            [kopdarbackoffice.templates.404 :as error]
            [kopdarbackoffice.templates.app :refer [template]]
            [reitit.coercion.schema :as schema-coercion]
            [reitit.dev.pretty :as pretty]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as rrc]
            [reitit.ring.middleware.parameters :as params]
            [ring.middleware.flash :refer [wrap-flash]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.session.cookie :refer [cookie-store]]
            [schema.core :as s]))

(def asset-version "1")

(def backend (backends/session))

(def cookie-store-secret (byte-array 16))

(defn config [db]
  {:conflicts nil
   :exception pretty/exception
   :data {:coercion   schema-coercion/coercion
          :middleware [params/parameters-middleware
                       rrc/coerce-exceptions-middleware
                       rrc/coerce-request-middleware
                       rrc/coerce-response-middleware
                       wrap-keyword-params
                       [wrap-session {:store (cookie-store {:key cookie-store-secret})}]
                       wrap-flash
                       [bam/wrap-authentication backend]
                       [wrap-inertia-share db]
                       [inertia/wrap-inertia template asset-version]]}})

(defn routes [db]
  (ring/ring-handler
   (ring/router
    [;; Authentication routes
     ["/login" {:get  {:handler auth/login}
                :post {:handler (auth/login-authenticate db)}}]
     ["/logout" {:delete {:handler auth/logout}}]

     ;; Dashboard route
     ["/" {:get        dashboard/index
           :middleware [wrap-auth]}]

     ;; Reports route
     ["/reports" reports/index]

     ["/etalases" {:middleware [wrap-auth]}
      ["" {:get {:handler (etalases/index db)}
           :post {:handler (etalases/store-etalase! db)}}]
      ["/create" {:get etalases/etalase-form}]
      ["/:etalase-id" {:post {:handler (etalases/update-etalase! db)}
                       :delete {:handler (etalases/delete-etalase! db)}}]
      ["/:etalase-id/edit" {:get {:handler (etalases/edit-etalase! db)}}]
      ["/:etalase-id/restore" {:put {:handler (etalases/restore-etalase! db)}}]]

     ["/user-address" {:middleware [wrap-auth]}
      ["" {:get {:handler (user-addresses/index db)}
           :post {:handler (user-addresses/store-user-address! db)}}]
      ["/create" {:get user-addresses/user-address-form}]
      ["/:user-address-id" {:post {:handler (user-addresses/update-user-address! db)}
                       :delete {:handler (user-addresses/delete-user-address! db)}}]
      ["/:user-address-id/edit" {:get {:handler (user-addresses/edit-user-address! db)}}]
      ["/:user-address-id/restore" {:put {:handler (user-addresses/restore-user-address! db)}}]]

     ["/payment-methods" {:middleware [wrap-auth]}
      ["" {:get {:handler (payment-methods/index db)}
           :post {:handler (payment-methods/payment-method-store! db)}}]
      ["/create" {:get payment-methods/payment-methods-form}]
      ["/:payment-method-id" {:post {:handler (payment-methods/update-payment-method! db)}
                             :delete {:handler (payment-methods/delete-payment-method! db)}}]
      ["/:payment-method-id/edit" {:get {:handler (payment-methods/edit-payment-method! db)}}]
      ["/:payment-method-id/restore" {:put {:handler (payment-methods/restore-payment-method! db)}}]]

     ["/deliveries" {:middleware [wrap-auth]}
      ["" {:get {:handler (deliveries/index db)}
           :post {:handler (deliveries/store-delivery! db)}}]
      ["/create" {:get deliveries/deliveries-form}]
      ["/:delivery-id" {:post {:handler (deliveries/update-delivery! db)}
                       :delete {:handler (deliveries/delete-delivery! db)}}]
      ["/:delivery-id/edit" {:get {:handler (deliveries/edit-delivery! db)}}]
      ["/:delivery-id/restore" {:put {:handler (deliveries/restore-delivery! db)}}]]

     ["/trx-statuses" {:middleware [wrap-auth]}
      ["" {:get {:handler (trx-statuses/index db)}
           :post {:handler (trx-statuses/store-trx-status! db)}}]
      ["/create" {:get trx-statuses/trx-statuses-form}]
      ["/:trx-status-id" {:post {:handler (trx-statuses/update-trx-status! db)}
                          :delete {:handler (trx-statuses/delete-trx-status! db)}}]
      ["/:trx-status-id/edit" {:get {:handler (trx-statuses/edit-trx-status! db)}}]
      ["/:trx-status-id/restore" {:put {:handler (trx-statuses/restore-trx-status! db)}}]]

     ["/user-group" {:middleware [wrap-auth]}
      ["" {:get {:handler (user-groups/index db)}
           :post {:handler (user-groups/store-user-group! db)}}]
      ["/create" {:get user-groups/user-groups-form}]
      ["/:user-group-id" {:post {:handler (user-groups/update-user-group! db)}
                       :delete {:handler (user-groups/delete-user-group! db)}}]
      ["/:user-group-id/edit" {:get {:handler (user-groups/edit-user-group! db)}}]
      ["/:user-group-id/restore" {:put {:handler (user-groups/restore-user-group! db)}}]]

     ["/apps-users" {:middleware [wrap-auth]}
      ["" {:get {:handler (apps-users/get-users db)}
           :post {:handler (apps-users/store-user! db)}}]
      ["/create" {:get apps-users/user-form}]
      ["/:user-id" {:post {:handler (apps-users/update-user! db)}
                    :delete {:handler (apps-users/delete-user! db)}}]
      ["/:user-id/edit" {:get {:handler (apps-users/edit-user! db)}}]
      ["/:user-id/restore" {:put {:handler (apps-users/restore-user! db)}}]]


     ["/rate-products" {:middleware [wrap-auth]}
      ["" {:get {:handler (rate-products/index db)}
           :post {:handler (rate-products/store-rate-product! db)}}]
      ["/create" {:get rate-products/rate-products-form}]
      ["/:rate-product-id" {:post {:handler (rate-products/update-rate-product! db)}
                            :delete {:handler (rate-products/delete-rate-product! db)}}]
      ["/:rate-product-id/edit" {:get {:handler (rate-products/edit-rate-product! db)}}]
      ["/:rate-product-id/restore" {:put {:handler (rate-products/restore-rate-product! db)}}]]

     ["/rate-stores" {:middleware [wrap-auth]}
      ["" {:get {:handler (rate-stores/index db)}
           :post {:handler (rate-stores/store-rate! db)}}]
      ["/create" {:get rate-stores/rate-form}]
      ["/:rate-store-id" {:post {:handler (rate-stores/update-rate! db)}
                       :delete {:handler (rate-stores/delete-rate! db)}}]
      ["/:rate-store-id/edit" {:get {:handler (rate-stores/edit-rate! db)}}]
      ["/:rate-store-id/restore" {:put {:handler (rate-stores/restore-rate! db)}}]]


     ["/chats" {:middleware [wrap-auth]}
      ["" {:get {:handler (chats/index db)}
           :post {:handler (chats/store-chat! db)}}]
      ["/create" {:get chats/chats-form}]
      ["/:chat-header-id" {:post {:handler (chats/update-chat! db)}
                           :delete {:handler (chats/delete-chat! db)}}]
      ["/:chat-header-id/edit" {:get {:handler (chats/edit-chat! db)}}]
      ["/:chat-header-id/restore" {:put {:handler (chats/restore-chat! db)}}]]

     ["/trx-details" {:middleware [wrap-auth]}
      ["" {:get {:handler (trx-details/index db)}
           :post {:handler (trx-details/store-trx-detail! db)}}]
      ["/create" {:get trx-details/trx-details-form}]
      ["/:trx-detail-id" {:post {:handler (trx-details/update-trx-detail! db)}
                       :delete {:handler (trx-details/delete-trx-detail! db)}}]
      ["/:trx-detail-id/edit" {:get {:handler (trx-details/edit-trx-detail! db)}}]
      ["/:trx-detail-id/restore" {:put {:handler (trx-details/restore-trx-detail! db)}}]]

     ["/trx-headers" {:middleware [wrap-auth]}
      ["" {:get {:handler (trx-headers/index db)}
           :post {:handler (trx-headers/store-trx-header! db)}}]
      ["/create" {:get trx-headers/trx-headers-form}]
      ["/:trx-header-id" {:post {:handler (trx-headers/update-trx-header! db)}
                       :delete {:handler (trx-headers/delete-trx-header! db)}}]
      ["/:trx-header-id/edit" {:get {:handler (trx-headers/edit-trx-header! db)}}]
      ["/:trx-header-id/restore" {:put {:handler (trx-headers/restore-trx-header! db)}}]]
     
     ;; Categories route
     ;["/categories1" categories1/index]
     ["/categories-one" {:middleware [wrap-auth]}
      ["" {:get {:handler (categories-one/index db)}
           :post {:handler (categories-one/store-category! db)}}]
      ["/create" {:get categories-one/category-form}]
      ["/:category-id" {:post {:handler (categories-one/update-category! db)}
                       :delete {:handler (categories-one/delete-category! db)}}]
      ["/:category-id/edit" {:get {:handler (categories-one/edit-category! db)}}]
      ["/:category-id/restore" {:put {:handler (categories-one/restore-category! db)}}]]

     ["/categories-two" {:middleware [wrap-auth]}
      ["" {:get {:handler (categories-two/index db)}
           :post {:handler (categories-two/store-category! db)}}]
      ["/create" {:get categories-two/category-form}]
      ["/:category-id" {:post {:handler (categories-two/update-category! db)}
                       :delete {:handler (categories-two/delete-category! db)}}]
      ["/:category-id/edit" {:get {:handler (categories-two/edit-category! db)}}]
      ["/:category-id/restore" {:put {:handler (categories-two/restore-category! db)}}]]
     
     ["/categories-three" {:middleware [wrap-auth]}
      ["" {:get {:handler (categories-three/index db)}
           :post {:handler (categories-three/store-category! db)}}]
      ["/create" {:get categories-three/category-form}]
      ["/:category-id" {:post {:handler (categories-three/update-category! db)}
                       :delete {:handler (categories-three/delete-category! db)}}]
      ["/:category-id/edit" {:get {:handler (categories-three/edit-category! db)}}]
      ["/:category-id/restore" {:put {:handler (categories-three/restore-category! db)}}]]
     

     ;; Users routes
     ;["/users" {:middleware [wrap-auth]}
     ; ["" {:get  {:handler (users/get-users db)}
     ;      :post {:handler (users/store-user! db)}}]
     ; ["/create" {:get users/user-form}]
     ; ["/:user-id" {:post   {:handler (users/update-user! db)}
     ;               :delete {:handler (users/delete-user! db)}}]
     ; ["/:user-id/edit" {:get {:handler (users/edit-user! db)}}]
     ; ["/:user-id/restore" {:put {:handler (users/restore-user! db)}}]]

     ;; Products routes
     ["/products" {:middleware [wrap-auth]}
      ["" {:get  {:handler    (products/index db)
                  :parameters {:query {(s/optional-key :page) Long}}}
           :post {:handler (products/store-product! db)}}]
      ["/create" {:get products/product-form}]
      ["/:product-id" {:put {:handler (products/update-product! db)}
                       :delete {:handler (products/delete-product! db)}}]
      ["/:product-id/edit" {:get {:handler (products/edit-product! db)}}]
      ["/:product-id/restore" {:put {:handler (products/restore-product! db)}}]]

     ;; Store routes
     ["/stores" {:middleware [wrap-auth]}
      ["" {:get  {:handler    (stores/index db)
                  :parameters {:query {(s/optional-key :page) Long}}}
           :post {:handler (stores/store-store! db)}}]
      ["/create" {:get (stores/stores-form db)}]
      ["/:store-id" {:put    {:handler (stores/update-store! db)}
                       :delete {:handler (stores/delete-store! db)}}]
      ["/:store-id/edit" {:get {:handler (stores/edit-store! db)}}]
      ["/:store-id/restore" {:put {:handler (stores/restore-store! db)}}]]]
    (config db))
   (ring/routes
    (ring/create-file-handler {:path "/"})
    (ring/create-default-handler
     {:not-found (constantly {:status 404, :body error/not-found})}))))
