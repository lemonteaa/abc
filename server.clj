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
            [com.walmartlabs.lacinia.pedestal :refer [inject]]))

(defn interceptor [number]
  {:enter (fn [ctx] (update-in ctx [:request :number] (fnil + 0) number))})

; We should insert the GraphQL endpoints here
; TODO: graphiql asset relative path issue?
(def routes
  ["/api"
   ["/graphql-q" {:post {:interceptors [(p2/default-interceptors poc-schema nil)]}}]
   ["/graphql-ide" {:get {:handler (p2/graphiql-ide-handler nil)}}]

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

(-> {::server/type :jetty
     ::server/port 3000
     ::server/join? false
     ;; no pedestal routes
     ::server/routes []}
    (server/default-interceptors)
    ;; swap the reitit router
    (pedestal/replace-last-interceptor
      (pedestal/routing-interceptor
        (http/router routes)))
    (server/dev-interceptors)
    (server/create-server)
    (server/start))
