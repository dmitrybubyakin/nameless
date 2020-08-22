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
    [taoensso.timbre :as timbre]
    [clojure.string :as string])
  (:gen-class))

(defroutes app-routes
           (GET "/" [] "Welcome to the world of anonymity !")
           (GET "/ping" [] "pong")
           (context "/api/v1" []
             (POST "/room/:url" [url host] (handler/create-room url host))
             (GET "/active/room/:url" [url] (handler/active-room? url))
             (GET "/chats/:url" [url] (handler/get-chats url))
             (PUT "/room/:url/visibility/toggle" [url] (handler/toggle-room-visibility url)))
           (route/resources "/"))

(defn init-ds []
  (mount.core/start)
  (timbre/set-level! :info))

(defn run-job [command]
  (init-ds)
  (case command
    "migrate" (mg/migrate)
    "rollback" (mg/rollback)))

(def handler
  (let [cors-allowed-domains (if (= (:env env) "prod")
                               #"https://namelss.com"
                               #".*")]
    (-> app-routes
        (wrap-json-params)
        (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
        (wrap-cors :access-control-allow-origin cors-allowed-domains
                   :access-control-allow-methods [:get :post :options])
        (web-middleware/wrap-websocket sessions/websocket-callbacks))))

(defn start-api-server []
  (init-ds)
  (web/run
    handler
    {"port" (:port (:server env))})
  (log/info (str "Running webserver at port " (:port (:server env)))))

(defn get-help []
  (->> [""
        "Usage:"
        "lein run <command>"
        ""
        "Valid Commands:"
        "server    Starts the api server"
        "migrate   Runs the new migrations"
        "rollback  Rolls back the last run migration"
        "help      Prints the help"]
       (string/join \newline)))

(defn -main [& [args]]
  (case args
    "server" (start-api-server)
    "migrate" (run-job "migrate")
    "rollback" (run-job "rollback")
    "help" (do (println (get-help)))

    (do
      (prn "Must supply a valid command to run")
      (println (get-help)))))
