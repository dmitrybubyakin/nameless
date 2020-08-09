(ns nameless.utils
  (:require [taoensso.timbre :as timbre]))

(defn init-timbre-logger
  "intitilises timbre for logging with configs"
  []
  (timbre/set-level! :info))
