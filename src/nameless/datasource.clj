(ns nameless.datasource
  (:require [hikari-cp.core :as hikari]
            [nameless.migrations :as mg]
            [clojure.tools.logging :as log]
            [mount.core :refer [defstate]]
            [config.core :refer [env]]))

(defstate datasource
          :start (let [db-config (:hikari env)]
                   (do
                     (mg/migrate)
                     (log/info "Starting DB connection pool")
                     (hikari/make-datasource db-config)))
          :stop (do (hikari/close-datasource datasource)
                    (log/info "Closed DB connection pool")))

(defn conn []
  {:datasource datasource})
