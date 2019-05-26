(ns db.StockMarketAccess
  (:require
    [clojure.core.async :as async :refer [chan <! >! timeout
                                          pub sub unsub unsub-all
                                          go go-loop]]))






(def  stocks (atom   {:IBM 132.2 :MSFT 128.1 :AMZN 1896 :GOOG 1168} ))

(defn get-all-quotes []
  @stocks
  )

(defn stock-quote [ric]
  (ric @stocks)
)

(defn update-spot [ric price]
  (swap! stocks assoc ric price)
)

(def publisher (chan))

(def publication
  (pub publisher #(:ric %)))

(def subscribed-stocks (atom #{}))


(defn watch-stock [ric listener-channel]
  (swap! subscribed-stocks conj ric)
  (sub publication ric listener-channel)
)


(defn push-stock-price [ric price]
  (when (contains? @subscribed-stocks ric)
    (go (>! publisher { :ric ric :price price })))
  )

(defn take-and-print [channel prefix]
  (go-loop []
    (println prefix ": " (<! channel))
    (recur)))


(defn stock-price-updater []
  (go-loop []
    (<! (timeout 500))

    (let [rand-stock (-> @stocks keys rand-nth)
          rand-spread (rand 2)]
      (swap! stocks update rand-stock + rand-spread)
      (push-stock-price rand-stock (rand-stock @stocks))
     )
    (recur)
  )
)


