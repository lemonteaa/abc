(ns abc.di.components
  (:require [integrant.core :as ig]
            [clojure.java.io :as io]
            [abc.lacinia :as lac]
            [com.walmartlabs.lacinia :refer [execute]]
            [xtdb.api :as xt]
            [clojure.data.json :as json]
            [clojure.walk :refer (keywordize-keys)]
            [malli.json-schema.parse :refer [schema->malli]]))

; util
(defn fmap [m f]
  (into {} (for [[k v] m] [k (f v)])))

(def config
  (ig/read-string (slurp (io/resource "integrant-config.edn"))))

(defmethod ig/init-key :graphql/schema [_ {:keys [file]}]
  (lac/poc-schema file lac/resolvers))

(defmethod ig/init-key :db [_ conf]
  (xt/start-node conf))

(defmethod ig/halt-key! :db [_ node]
  (.close node))

; Modified from:
; Experimental code from PR
; https://github.com/metosin/malli/pull/211
; Fork at hkupty:js-schema-to-malli
(defmethod ig/init-key :api/schema [_ m]
  (fmap m (fn [file]
            (-> file
                (io/resource)
                (slurp)
                (json/read-str)
                (keywordize-keys)
                (schema->malli)))))

;(m/schema? my-schema)
;(m/form my-schema)
;(assert (= true (m/validate my-schema (keywordize-keys {"firstName" "John", "lastName" "Doe", "age" 21}))))
;(assert (not= true (m/validate my-schema (keywordize-keys {"firstName" "John", "lastName" 1, "age" 21}))))


;{:adapter/jetty {:port 8080, :handler #ig/ref :handler/greet}
; :handler/greet {:name "Alice"}}


(def system
  (ig/init config))

(ig/halt! system)

; Test below

(execute (:graphql/schema system) 
         "{ hero { id name }}" nil nil)

