(ns db.db-access)


(defn get-stock-detail-from-db [ric]
  {:ric ric :name (str "stock name:" ric)  :description (str "description for : " ric)})

(defn get-company-detail-from-db [ric]
  {:ric ric :name (str "company: " ric) :boardMembers ["John" "Dan" "Ben"] :description (str "desc for :" ric) }  )