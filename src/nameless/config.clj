(ns nameless.config
  (:require [mount.core :refer [defstate]]
            [config.core :refer [env]]
            [clojure.tools.logging :as log]))

(defn db-jdbc-uri []
  (let [{:keys [type server port name user password]} (:db env)
        uri (format "jdbc:%s://%s:%s/%s?user=%s&password=%s"
                    type server port name user password)]
    (log/debug "Starting db Connection with uri" uri)
    uri))
