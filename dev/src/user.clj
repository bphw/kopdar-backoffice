(ns user
  (:require [integrant.repl :as ig-repl]
            [integrant.repl.state :as state]
            [kopdarbackoffice.models.products :as prd]
            [kopdarbackoffice.models.apps-users :as users]
            [kopdarbackoffice.system :as system]))

(ig-repl/set-prep!
 (fn [] system/config))

(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)
(def reset-all ig-repl/reset-all)

(def app (-> state/system :kopdarbackoffice/app))
(def db (-> state/system :database.sql/connection))

(comment
  (prd/retrieve-and-filter-products db {})

  (prd/count-products db)

  (users/retrieve-and-filter-apps-users nil)

  (prd/get-product-by-id db 23))
