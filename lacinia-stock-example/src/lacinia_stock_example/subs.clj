(ns lacinia-stock-example.subs
  (:require     [clojure.core.async :as async :refer [chan <! >! timeout
                                                      pub sub unsub unsub-all go-loop]]
                [db.StockMarketAccess :as ma]
                [db.db-access :as db]))


(defn watch-stock [context args source-stream]
  (let [listener-chan (chan)]
    (println args)
    (ma/watch-stock (-> args :ric keyword) listener-chan)
    (go-loop []
             (let [v (<! listener-chan)]
               (source-stream v)
               )
             (recur)
             )
    )
  #(println "closing connection"))




