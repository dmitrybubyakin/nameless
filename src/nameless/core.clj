(ns nameless.core
  (:require
    [immutant.web :as web]
    [immutant.web.middleware :as web-middleware]
    [compojure.route :as route]
    [compojure.core :refer (ANY GET defroutes)]
    [ring.util.response :refer (response redirect content-type)]
    [nameless.chat.sessions :as sessions]
    [ring.middleware.params :refer [wrap-params]]
    [nameless.migrations :as mg]
    [clojure.tools.logging :as log]
    [nameless.datasource :as ds]
    [config.core :refer [env]])
  (:gen-class))

(defroutes routes
           (GET "/" [] "Hello ####")
           (GET "/sessions" [] (str "Active Sessions - " (sessions/active-sessions)))
           (route/resources "/"))

(defn run-job [command]
  (mount.core/start)
  (case command
    "migrate" (mg/migrate)
    "rollback" (mg/rollback)))

(defn start-api-server []
  (mount.core/start)
  (web/run
    (-> routes
        (wrap-params)
        ;; wrap the handler with websocket support
        ;; websocket requests will go to the callbacks, ring requests to the handler
        (web-middleware/wrap-websocket sessions/websocket-callbacks))
    {"port" (:port (:server env))})
  (log/info (str "Running webserver at http://127.0.0.1:" (:port (:server env)))))

(defn -main [& [args]]
  (case args
    "api" (start-api-server)
    "migrate" (run-job "migrate")
    "rollback" (run-job "rollback")
    "help" (do (prn "A Clojure service")
               (System/exit 0))

    (do
      (prn "Must supply a valid command to run")
      (System/exit 1))))
