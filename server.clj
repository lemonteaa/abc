; From https://cljdoc.org/d/metosin/reitit/0.5.18/doc/http/pedestal

; [io.pedestal/pedestal.service "0.5.5"]
; [io.pedestal/pedestal.jetty "0.5.5"]
; [metosin/reitit-pedestal "0.5.18"]
; [metosin/reitit "0.5.18"]

(ns abc.server
  (:require [io.pedestal.http :as server]
            [reitit.pedestal :as pedestal]
            [reitit.http :as http]
            [reitit.ring :as ring]))

(defn interceptor [number]
  {:enter (fn [ctx] (update-in ctx [:request :number] (fnil + 0) number))})

(def routes
  ["/api"
   {:interceptors [(interceptor 1)]}

   ["/number"
    {:interceptors [(interceptor 10)]
     :get {:interceptors [(interceptor 100)]
           :handler (fn [req]
                      {:status 200
                       :body (select-keys req [:number])})}}]])

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
