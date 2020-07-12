(ns nameless.config
  (:require [mount.core :refer [defstate]]
            [config.core :refer [load-env]]
            [clojure.tools.logging :as log]))

(defstate env :start (load-env))

(defn db-jdbc-uri []
  (let [{:keys [type server port name user password]} (:db env)]
    (log/info "Starting db Connection with uri" (format "jdbc:%s://%s:%s/%s?user=%s&password=%s"
                                                        type server port name user password))
    (format "jdbc:%s://%s:%s/%s?user=%s&password=%s"
            type server port name user password)))
