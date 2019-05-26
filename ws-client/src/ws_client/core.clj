(ns ws-client.core
  (:require [gniazdo.core :as ws]
            [cheshire.core :refer :all])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(def socket
  (ws/connect
    "ws://localhost:8888/graphql-ws"
    :on-receive #(prn 'received %)))

(def s {:id 1
        :type :start
        :payload
        {:query "subscription{ping}"}})

(def watch-s {:id 2
        :type :start
        :payload
        {:query "subscription{watchStock(ric: \"GOOG\"){ric,price}}"}})

(ws/send-msg socket (generate-string s))


;; (ws/send-msg socket "hello")
;; (ws/close socket)


