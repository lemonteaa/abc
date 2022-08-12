(ns abc.service
  (:require [xtdb.api :as xt]
            [malli.core :as m]
            [malli.json-schema.parse :refer [schema->malli]]
            [clojure.walk :refer (keywordize-keys)]
            [abc.di.components :refer [system]]
            ))
; [malli.dev :as mdev]
; [malli.experimental :as mx]

;(def node (xt/start-node {}))

; https://docs.xtdb.com/language-reference/1.21.0/datalog-transactions/
; TODO: listen to event to ensure indexer has run to this trans?
;(xt/submit-tx node [[::xt/put
;                     {:xt/id :dbpedia.resource/Pablo-Picasso 
;                      :first-name :Pablo
;                      :last-name :Picasso
;                      :born 1613
;                      :remark "A Man of Greatest Talent"}]])


(xt/submit-tx
 (:db/node system) [[::xt/put
                     {:xt/id 1001
                      :name "Yoda"
                      :home_planet "Unknown"
                      :appears_in ["EMPIRE" "JEDI"]}]])
(xt/submit-tx
 (:db/node system) [[::xt/put
                     {:xt/id 1002
                      :name "Anakin"
                      :home_planet "Federation"
                      :appears_in ["NEWHOPE" "JEDI"]}]])
(xt/submit-tx
 (:db/node system) [[::xt/put
                     {:xt/id 1003
                      :name "Luke"
                      :home_planet "Tatooine"
                      :appears_in ["NEWHOPE" "EMPIRE" "JEDI"]}]])


; https://docs.xtdb.com/language-reference/datalog-queries/
;(xt/q
; (xt/db node)
; '{:find [yr]
;   :where [[p1 :last-name n]
;            [p1 :born yr]]
;   :in [n]}
; :Picasso)

(defn in?
  "true if coll contains elm"
  [coll elm]
  (some #(= elm %) coll))

(first (first (xt/q (xt/db (:db/node system))
      '{:find [(pull ?p [*])]
        :in [ep-name]
        :where [[?p :appears_in ep-name]]}
      "NEWHOPE")))

(name :NEWHOPE)

;[(abc.service/in? episodes ep-name)]

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