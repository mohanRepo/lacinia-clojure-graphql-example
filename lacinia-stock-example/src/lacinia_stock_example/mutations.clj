(ns lacinia-stock-example.mutations
  (:require [db.StockMarketAccess :as ma]
            [db.db-access :as db]))

(defn add-new-stock [context arguments value]
  (let [{:keys [ric price lastTraded]} (:stock arguments)]
    (ma/update-spot (keyword ric) price)
    (db/get-stock-detail-from-db ric)))
