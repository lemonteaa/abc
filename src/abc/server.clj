; From https://cljdoc.org/d/metosin/reitit/0.5.18/doc/http/pedestal

; [io.pedestal/pedestal.service "0.5.5"]
; [io.pedestal/pedestal.jetty "0.5.5"]
; [metosin/reitit-pedestal "0.5.18"]
; [metosin/reitit "0.5.18"]

(ns abc.server
  (:require [io.pedestal.http :as server]
            [reitit.pedestal :as pedestal]
            [reitit.http :as http]
            [reitit.ring :as ring]
            [com.walmartlabs.lacinia.pedestal2 :as p2]
            [com.walmartlabs.lacinia.pedestal :refer [inject]]
            [reitit.interceptor :as interceptor])
  (:import (io.pedestal.interceptor Interceptor)))

(defn interceptor [number]
  {:enter (fn [ctx] (update-in ctx [:request :number] (fnil + 0) number))})

;; Using quickfix from https://github.com/metosin/reitit/issues/330
;; "Adding Reitit.pedestal io.pedestal.interceptor.Interceptor support"
(extend-protocol interceptor/IntoInterceptor
  Interceptor
  (into-interceptor [this data opts]
    (interceptor/into-interceptor (into {} this) data opts)))

; We should insert the GraphQL endpoints here
; TODO: graphiql asset relative path issue?
(defn make-reitit-routes [graphql-schema]
  ["/api"
   ["/graphql-q" {:post {:interceptors (p2/default-interceptors graphql-schema nil)}}]
   ;["/graphql-ide" {:get {:handler (p2/graphiql-ide-handler nil)}}]

   ;["/person"
   ; {:post {:interceptors [(schema-validation-interceptor my-schema)]
   ;         :handler add-person}
   ;  :get  {:handler get-person}}]

   ["/number"
    {:interceptors [(interceptor 10)]
     :get {:interceptors [(interceptor 100)]
           :handler (fn [req]
                      {:status 200
                       :body (select-keys req [:number])})}}]])


; Other reference:
; https://walmartlabs.github.io/apidocs/lacinia-pedestal/com.walmartlabs.lacinia.pedestal2.html
; https://lacinia-pedestal.readthedocs.io/en/latest/interceptors.html

; For integration with lacinia-pedestal, see
; https://github.com/walmartlabs/lacinia-pedestal/blob/master/src/com/walmartlabs/lacinia/pedestal2.clj#L326
; interceptors (default-interceptors compiled-schema app-context options)
;        routes (into #{[api-path :post interceptors :route-name ::graphql-api]
;                       [ide-path :get (graphiql-ide-handler options) :route-name ::graphiql-ide]}
;                     (graphiql-asset-routes asset-path))

(defn create-and-start-server [base-conf routes]
  (-> base-conf
      (server/default-interceptors)
    ;; swap the reitit router
      (pedestal/replace-last-interceptor
       (pedestal/routing-interceptor (http/router routes)))
      (server/dev-interceptors)
      (server/create-server)
      (server/start)))

