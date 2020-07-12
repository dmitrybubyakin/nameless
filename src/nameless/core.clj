(ns nameless.core
  (:require
    [immutant.web :as web]
    [immutant.web.middleware :as web-middleware]
    [compojure.route :as route]
    [environ.core :refer (env)]
    [compojure.core :refer (ANY GET defroutes)]
    [ring.util.response :refer (response redirect content-type)]
    [nameless.chat.sessions :as sessions])
  (:gen-class))

(defroutes routes
           (GET "/" [] "Hello ####")
           (GET "/sessions" [] (str "Active Sessions - " (sessions/active-sessions)))
           (route/resources "/"))

(defn -main [& {:as args}]
  (web/run
    (-> routes
        ;; wrap the handler with websocket support
        ;; websocket requests will go to the callbacks, ring requests to the handler
        (web-middleware/wrap-websocket sessions/websocket-callbacks))
    (merge {"host" (env :port), "port" 8080}  args)))
