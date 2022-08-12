; Implement resolver and compile schema

; https://github.com/walmartlabs/lacinia
; https://lacinia.readthedocs.io/en/latest/tutorial/index.html

(ns abc.lacinia
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.walmartlabs.lacinia.util :refer [attach-resolvers]]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia :refer [execute]]))


(defn get-hero [context arguments value]
  (let [{:keys [episode]} arguments]
    (println (:system/db context))
    (if (= episode :NEWHOPE)
      {:id 1000
       :name "Luke"
       :home_planet "Tatooine"
       :appears_in ["NEWHOPE" "EMPIRE" "JEDI"]}
      {:id 2000
       :name "Lando Calrissian"
       :home_planet "Socorro"
       :appears_in ["EMPIRE" "JEDI"]})))

(def resolvers {:get-hero get-hero
                :get-droid (constantly {})})

(defn poc-schema [file-name resolvers]
  (-> file-name
      io/resource
      slurp
      edn/read-string
      (attach-resolvers resolvers)
      schema/compile))

;; Testing graphQL standalone

;(execute poc-schema "{ hero { id name }}" nil nil)
