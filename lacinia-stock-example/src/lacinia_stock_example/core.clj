(ns lacinia-stock-example.core
  (:require
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    [com.walmartlabs.lacinia.pedestal :refer [service-map]]
    [com.walmartlabs.lacinia.schema :as schema]
    [com.walmartlabs.lacinia.util :as util]
    [db.StockMarketAccess :as ma]
    [lacinia-stock-example.subs :as subs]
    [lacinia-stock-example.mutations :as mutations]
    [lacinia-stock-example.queries :as queries]
    [lacinia-stock-example.scalars :as scalars]
    [io.pedestal.http :as http]))

(def api-schema
  (-> "api-schema.edn"
      io/resource
      slurp
      edn/read-string
      (util/attach-scalar-transformers {:dollar->number scalars/dollar->number
                                        :number->dollar scalars/number->dollar})
      (util/attach-resolvers {:resolve-hello queries/resolve-hello
                              :get-stock-detail queries/get-stock-detail
                              :get-company-info queries/get-company-info
                              :resolve-rics queries/resolve-rics
                              :add-new-stock mutations/add-new-stock
                              :get-all-stock-quotes queries/get-all-stock-quotes})
      (util/attach-streamers {:stock-quote subs/watch-stock})
      schema/compile))


(ma/stock-price-updater)


(def service (-> api-schema
                 (service-map {:graphiql true :subscriptions true :port 8888})
                 http/create-server
                 http/start))

;; (com.walmartlabs.lacinia/execute api-schema "{hello}" nil nil)






