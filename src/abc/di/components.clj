(ns abc.di.components
  (:require [integrant.core :as ig]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [abc.lacinia :as lac]
            [abc.server :as server]
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

(defmethod ig/init-key :db/node [_ conf]
  (xt/start-node conf))

(defmethod ig/halt-key! :db/node [_ node]
  (.close node))

; Be careful:
; 1) slurp return string only - to parse, further process or use other fn
; 2) ::name for namespaced keyword does auto-resolve - this obviously won't work in edn file, so be explicit there
; 3) pedestal server start/stop need to be careful
(defmethod ig/init-key :server [_ {:keys [base-conf db]}]
  (let [base-conf (edn/read-string (slurp (io/resource (:file base-conf))))]
    (println base-conf)
    (server/create-and-start-server base-conf server/routes)))

(defmethod ig/halt-key! :server [_ server]
  (io.pedestal.http/stop server))

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
