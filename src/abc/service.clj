(ns abc.service
  (:require [xtdb.api :as xt]
            [malli.core :as m]
            [malli.json-schema.parse :refer [schema->malli]]
            ))
; [malli.dev :as mdev]
; [malli.experimental :as mx]

;(def node (xt/start-node {}))

; https://docs.xtdb.com/language-reference/1.21.0/datalog-transactions/
; TODO: listen to event to ensure indexer has run to this trans?
(xt/submit-tx node [[::xt/put
                     {:xt/id :dbpedia.resource/Pablo-Picasso 
                      :first-name :Pablo
                      :last-name :Picasso
                      :born 1613
                      :remark "A Man of Greatest Talent"}]])

; https://docs.xtdb.com/language-reference/datalog-queries/
(xt/q
 (xt/db node)
 '{:find [yr]
   :where [[p1 :last-name n]
            [p1 :born yr]]
   :in [n]}
 :Picasso)

;(.close node)

; Schema validation as an interceptor?

(defn schema-validation-interceptor [malli-schema]
  (if (not (m/schema? malli-schema))
    (throw (ex-info "Invalid Malli Schema." {}))
    {:enter (fn [ctx]
                (do
                  (assert (= true (m/validate malli-schema (keywordize-keys (:request ctx)))))
                  ctx))}))

; Our application specific code below:


(defn add-person [req]
)

(defn get-person [req]
)