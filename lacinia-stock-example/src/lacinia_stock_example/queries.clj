(ns lacinia-stock-example.queries
  (:require [db.StockMarketAccess :as ma]
            [db.db-access :as db]))

(defn get-all-stock-quotes [context args value]
  (println (ma/get-all-quotes))
  (let [q-s (ma/get-all-quotes)
        quotes (map #(hash-map :ric (-> % key name) :price (-> % val))  q-s)]
    (println quotes)
    quotes
    ))

(defn get-stock-detail [context arguments value]
  (println "get-stock-detail args: " arguments)
  (println "get-stock-detail value: " value)
  (let [{:keys [ric]} value
        {ric :ric , :or {ric ric} } arguments]
    (db/get-stock-detail-from-db ric)))

(defn get-company-info [context arguments value]
  (println "get-company-info args: " arguments)
  (println "get-company-info value: " value)
  (let [{:keys [ric]} value
        {ric :ric , :or {ric ric} } arguments]
    (db/get-company-detail-from-db ric)
    ))

(defn resolve-rics [context arguments value]
  (println "resolve-rics args: " arguments)
  (println "resolve-rics value: " value)
  (map #(db/get-stock-detail-from-db %) (:rics arguments))
  )

(defn resolve-hello
  [context args value]
  "Hello World!")
