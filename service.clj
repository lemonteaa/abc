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

; Experimental code from PR
; https://github.com/metosin/malli/pull/211
; Fork at hkupty:js-schema-to-malli
(require '[clojure.walk :refer (keywordize-keys)])

(def data {"$id" "https://example.com/person.schema.json",
           "$schema" "https://json-schema.org/draft/2020-12/schema",
           "title" "Person",
           "type" "object",
           "properties"
           {"firstName"
            {"type" "string", "description" "The person's first name."},
            "lastName"
            {"type" "string", "description" "The person's last name."},
            "age"
            {"description"
             "Age in years which must be equal to or greater than zero.",
             "type" "integer",
             "minimum" 0}}})

(def my-schema (schema->malli (keywordize-keys data)))
(m/schema? my-schema)
(m/form my-schema)
(assert (= true (m/validate my-schema (keywordize-keys {"firstName" "John", "lastName" "Doe", "age" 21}))))

(assert (not= true (m/validate my-schema (keywordize-keys {"firstName" "John", "lastName" 1, "age" 21}))))

