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
   [taoensso.timbre :as log]
   [nameless.datasource :as ds]
   [config.core :refer [env]]
   [ring.middleware.defaults :refer :all]
   [ring.middleware.cors :refer [wrap-cors]]
   [nameless.utils :as utils])
  (:gen-class))

(defroutes app-routes
  (GET "/" [] "Welcome to the world of anonymity !")
  (GET "/ping" [] "pong")
  (context "/api/v1" []
    (GET "/room/:url" [url] (handler/active-room? url))
    (POST "/room/:url" [url host] (handler/create-room url host))
    (GET "/chats/:url" [url] (handler/get-chats url)))
  (route/resources "/"))

(defn init-ds []
  (mount.core/start)
  (utils/init-timbre-logger))

(defn run-job [command]
  (init-ds)
  (case command
    "migrate" (mg/migrate)
    "rollback" (mg/rollback)))

(def handler
  (-> app-routes
      (wrap-json-params)
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
      (wrap-cors :access-control-allow-origin [#"https://namelss.com",#"https://localhost:3000"]
                 :access-control-allow-methods [:get :put :post :delete])
      (web-middleware/wrap-websocket sessions/websocket-callbacks)))

(defn start-api-server []
  (init-ds)
  (web/run
   handler
   {"port" (:port (:server env))})
  (log/info (str "Running webserver at port " (:port (:server env)))))

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
