; Implement resolver and compile schema

; https://github.com/walmartlabs/lacinia
; https://lacinia.readthedocs.io/en/latest/tutorial/index.html

(ns abc.lacinia
  (:require [clojure.edn :as edn]
            [com.walmartlabs.lacinia.util :refer [attach-resolvers]]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia :refer [execute]]))

(def schema-name "lacinia-schema-sample.edn")


(defn get-hero [context arguments value]
  (let [{:keys [episode]} arguments]
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

(def poc-schema
  (-> schema-name
      slurp
      edn/read-string
      (attach-resolvers resolvers)
      schema/compile))

;; Testing graphQL standalone

(execute poc-schema "{ hero { id name }}" nil nil)
