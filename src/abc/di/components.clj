(ns abc.di.components
  (:require [integrant.core :as ig]
            [abc.lacinia :as lac]
            [com.walmartlabs.lacinia :refer [execute]]))


(def config
  (ig/read-string (slurp "integrant-config.edn")))

(defmethod ig/init-key :graphql/schema [_ {:keys [file]}]
  (lac/poc-schema file lac/resolvers))

;{:adapter/jetty {:port 8080, :handler #ig/ref :handler/greet}
; :handler/greet {:name "Alice"}}

;(defmethod ig/init-key :adapter/jetty [_ {:keys [handler] :as opts}]
;  (jetty/run-jetty handler (-> opts (dissoc :handler) (assoc :join? false))))
;(defmethod ig/init-key :handler/greet [_ {:keys [name]}]
;  (fn [_] (resp/response (str "Hello " name))))
;(defmethod ig/halt-key! :adapter/jetty [_ server]
;  (.stop server))

(def system
  (ig/init config))

(ig/halt! system)

(execute (:graphql/schema system) 
         "{ hero { id name }}" nil nil)

