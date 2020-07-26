(ns nameless.config
  (:require [mount.core :refer [defstate]]
            [config.core :refer [load-env]]
            [clojure.tools.logging :as log]))

(defstate env :start (load-env))

(defn db-jdbc-uri []
  (let [{:keys [type server port name user password]} (:db env)
        uri (format "jdbc:%s://%s:%s/%s/cloudsql/namelss:us-central1:namelss-db?user=%s&password=%s"
                    type server port name user password)]
    (log/info "Starting db Connection with uri" uri)
    uri))
