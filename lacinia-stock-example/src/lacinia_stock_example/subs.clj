(ns lacinia-stock-example.subs
  (:require     [clojure.core.async :as async :refer [chan <! >! timeout
                                                      pub sub unsub unsub-all go-loop]]
                [db.StockMarketAccess :as ma]
                [db.db-access :as db]))


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

(def producer-chan (chan))


(defn producer []
  (go-loop [n 1]
           (>! producer-chan n)
           (<! (timeout 5000))
           (recur (+ n 2))
           )
  )



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


