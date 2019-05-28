# lacinia-stock-example

service:\
cd lacinia-stock-example\
lein repl

***should host the api in localhost:8888

client:  
cd ws-client  
lein repl  

***would sunscribe listen to the pub events from service



Sample requests :

subscription {watchStock(ric: "IBM")
  {ric
    price
  }
 
  }




 queries: 
  
  {
  getAllStockQuotes {
    ric
    price
  }
}
  
  {
  BulkStockDetails(rics: ["IBM", "GOOG"]) {
ric
    company {
      ric
      boardMembers
      description
      name
    }
  }
}



mutation {
 AddNewStock(stock: {ric: "XYZ" price: "123USD"})
  {
    ric
    company{boardMembers}
  }
  }