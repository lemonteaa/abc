(ns abc.service
  (:require [xtdb.api :as xt]))


(def node (xt/start-node {}))

; https://docs.xtdb.com/language-reference/1.21.0/datalog-transactions/
; TODO: listen to event to ensure indexer has run to this trans?
(xt/submit-tx node [[::xt/put
                     {:xt/id :dbpedia.resource/Pablo-Picasso 
                      :first-name :Pablo}]])

; https://docs.xtdb.com/language-reference/datalog-queries/
(xt/q
 (xt/db node)
 '{:find [p1]
   :where [[p1 :name n]
           [p1 :last-name n]
           [p1 :name name]]
   :in [name]}
    "Ivan")

(.close node)
