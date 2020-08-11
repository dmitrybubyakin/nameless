(ns nameless.migrations
  (:require [taoensso.timbre :as log]
            [ragtime.jdbc :as jdbc]
            [ragtime.repl :as ragtime]
            [ragtime.strategy :as strategy]
            [nameless.chat.db.config :as config]))

(defn- migration-config []
  {:datastore  (jdbc/sql-database {:connection-uri (config/db-jdbc-uri)})
   :migrations (jdbc/load-resources "migrations")
   :strategy    strategy/apply-new
   :reporter   (fn [_ op id]
                 (case op
                   :up (log/info "Applying migration" id)
                   :down (log/info "Rolling back migration" id)))})

(defn migrate []
  (log/info "Running migration now !!")
  (ragtime/migrate (migration-config)))

(defn rollback []
  (ragtime/rollback (update (migration-config) :migrations #(vector (last %)))))
