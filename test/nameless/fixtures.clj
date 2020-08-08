(ns nameless.fixtures
  (:require [clojure.test :refer :all]
            [nameless.datasource :as db]
            [clojure.java.jdbc :as jdbc]
            [clojure.tools.logging :as log]
            [mount.core :as mount])
  (:import (org.postgresql.util PSQLException)))

(defn mount-sut [f]
  (mount/start (mount/only [#'db/datasource]))
  (f)
  (mount/stop))

(defn truncate [table]
  (try
    (jdbc/execute! (db/conn) [(str "SET lock_timeout TO '1s'; " "TRUNCATE " table)] {:transaction? false})
    (catch PSQLException e
      (log/error (str "unable to truncate table with " (.getMessage e)))
      (throw (Exception. "truncation-failure")))))

(defn clear [f]
  (truncate "chat")
  (truncate "room")
  (f))
