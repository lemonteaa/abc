(ns abc.di.components
  (:require [integrant.core :as ig]
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
  (ig/read-string (slurp "integrant-config.edn")))

(defmethod ig/init-key :graphql/schema [_ {:keys [file]}]
  (lac/poc-schema file lac/resolvers))

(defmethod ig/init-key :db [_ conf]
  (xt/start-node conf))

(defmethod ig/halt-key! :db [_ node]
  (.close node))

(defmethod ig/init-key :api/schema [_ m]
  (fmap m (fn [file]
            (-> file
                (slurp)
                (json/read-str)
                (keywordize-keys)
                (schema->malli)))))

;{:adapter/jetty {:port 8080, :handler #ig/ref :handler/greet}
; :handler/greet {:name "Alice"}}


(def system
  (ig/init config))

(ig/halt! system)

; Test below

(execute (:graphql/schema system) 
         "{ hero { id name }}" nil nil)

