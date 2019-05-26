(ns lacinia-stock-example.scalars)

(def dollar->number
  (fn [^String v]
    (read-string (clojure.string/replace v #"USD" "")) ))

(def number->dollar
  (fn [v]
    (str "USD " v )))(ns lacinia-stock-example.scalars)
