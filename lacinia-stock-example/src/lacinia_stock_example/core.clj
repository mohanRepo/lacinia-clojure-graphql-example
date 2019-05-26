(ns lacinia-stock-example.core
  (:require
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    [com.walmartlabs.lacinia.pedestal :refer [service-map]]
    [com.walmartlabs.lacinia.schema :as schema]
    [clojure.spec.alpha :as s]
    [com.walmartlabs.lacinia.util :as util]
    [clojure.core.async :as async :refer [chan <! >! timeout
                                          pub sub unsub unsub-all go-loop]]
    [lacinia-stock-example.StockMarketAccess :as ma]
    [io.pedestal.http :as http]))

(defn ^:private resolve-hello
  [context args value]
  context)


(defn producer [p-channel]
  (go-loop [n 1]
           (>! p-channel n)
           (<! (timeout 5000))
           (recur (+ n 2))
           )
  )

(def producer-chan (chan))


(defn log-message-streamer
  [context args source-stream]
  ;; Create an object for the subscription.
  (go-loop []
           (let [v (<! producer-chan)]
             (source-stream v)
             )
           (recur)
           )
  ;; Return a function to cleanup the subscription
  #(println "closing connection"))

(defn watch-stock [context args source-stream]
  (let [listener-chan (chan)]
    (println args)
    (println (-> args :ric keyword))
    (println (-> args :ric keyword type))
    (ma/watch-stock (-> args :ric keyword) listener-chan)
    (go-loop []
             (let [v (<! listener-chan)]
               (source-stream v)
               )
             (recur)
             )
    )
  #(println "closing connection"))

(defn get-stock-detail-from-db [ric]
  {:ric ric :name (str "stock name:" ric)  :description (str "description for : " ric)})

(defn get-company-detail-from-db [ric]
  {:ric ric :name (str "company: " ric) :boardMembers ["John" "Dan" "Ben"] :description (str "desc for :" ric) }  )

(defn get-stock-detail [context arguments value]
  (println "get-stock-detail args: " arguments)
  (println "get-stock-detail value: " value)
  (let [{:keys [ric]} value
        {ric :ric , :or {ric ric} } arguments]
    (get-stock-detail-from-db ric)))

(defn get-company-info [context arguments value]
  (println "get-company-info args: " arguments)
  (println "get-company-info value: " value)
  (let [{:keys [ric]} value
        {ric :ric , :or {ric ric} } arguments]
    (get-company-detail-from-db ric)
    ))


(defn resolve-rics [context arguments value]
  (println "resolve-rics args: " arguments)
  (println "resolve-rics value: " value)
  (map #(get-stock-detail-from-db %) (:rics arguments))
  )

(defn add-new-stock [context arguments value]
  (let [{:keys [ric price lastTraded]} (:stock arguments)]
    (ma/update-spot (keyword ric) price)
    (get-stock-detail-from-db ric)))

(defn get-all-stock-quotes [context args value]
  (println (ma/get-all-quotes))
  (let [q-s (ma/get-all-quotes)
        quotes (map #(hash-map :ric (-> % key name) :price (-> % val))  q-s)]
    (println quotes)
    quotes
    ))

(def dollar->number
  (fn [^String v]
    (read-string (clojure.string/replace v #"USD" "")) ))

(def number->dollar
  (fn [v]
    (str "USD " v )))

(defn ^:private schema
  []
  (-> "api-schema.edn"
      io/resource
      slurp
      edn/read-string
      (util/attach-scalar-transformers {:dollar->number dollar->number
                                        :number->dollar number->dollar})
      (util/attach-resolvers {:resolve-hello resolve-hello
                              :get-stock-detail get-stock-detail
                              :get-company-info get-company-info
                              :resolve-rics resolve-rics
                              :add-new-stock add-new-stock
                              :get-all-stock-quotes get-all-stock-quotes})
      (util/attach-streamers {:ping-response log-message-streamer
                              :stock-quote watch-stock})
      schema/compile))

(producer producer-chan)
(ma/stock-price-updater)


(def service (-> (schema)
                 (service-map {:graphiql true :subscriptions true})
                 http/create-server
                 http/start))






