(ns nameless.core
  (:require
    [immutant.web :as web]
    [immutant.web.middleware :as web-middleware]
    [compojure.route :as route]
    [compojure.core :refer :all]
    [ring.util.response :refer (response redirect content-type)]
    [nameless.chat.sessions :as sessions]
    [nameless.chat.domain.core :as core]
    [nameless.handler :as handler]
    [ring.middleware.json :refer [wrap-json-response wrap-json-body wrap-json-params]]
    [nameless.migrations :as mg]
    [clojure.tools.logging :as log]
    [nameless.datasource :as ds]
    [config.core :refer [env]]
    [ring.middleware.defaults :refer :all])
  (:gen-class))

(defroutes app-routes
           (GET "/" [] "Welcome to the world of anonymity !")
           (context "/api" []
             (GET "/sessions" [] (str "Active Sessions - " (core/active-sessions)))
             (POST "/v1/meeting" req (handler/create-meeting req)))
           (route/resources "/"))

(defn run-job [command]
  (mount.core/start)
  (case command
    "migrate" (mg/migrate)
    "rollback" (mg/rollback)))

(defn start-api-server []
  (mount.core/start)
  (web/run
    (-> app-routes
        (wrap-json-params)
        (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
        ;; wrap the handler with websocket support
        ;; websocket requests will go to the callbacks, ring requests to the handler
        (web-middleware/wrap-websocket sessions/websocket-callbacks))
    {"port" (:port (:server env))})
  (log/info (str "Running webserver at http://127.0.0.1:" (:port (:server env)))))

(defn -main [& [args]]
  (case args
    "server" (start-api-server)
    "migrate" (run-job "migrate")
    "rollback" (run-job "rollback")
    "help" (do (prn "A Clojure service")
               (System/exit 0))

    (do
      (prn "Must supply a valid command to run")
      (System/exit 1))))
