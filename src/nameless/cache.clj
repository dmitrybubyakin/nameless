(ns nameless.cache
  (:require [taoensso.carmine :as car :refer (wcar)]
            [mount.core :refer [defstate]]
            [config.core :refer [env]]
            [taoensso.nippy :as nippy]
            [clojure.walk :as wk]))

(def channel-store (atom {}))

(def session-key "session-")
(def conn {:pool {} :spec {:host (:host (:cache env)) :port (:port (:cache env))}})

(defmacro wcar* [& body] `(car/wcar conn ~@body))

(defn get-connected-clients [url]
  ((keyword url) @channel-store))

(defn update-store [uid channel]
  (let [uid-key (keyword uid)]
    (swap! channel-store assoc-in [uid-key] (concat (uid-key @channel-store) [channel]))))

(defn save-session
  [uid start-time channel]
  (update-store uid channel)
  (wcar* (car/set (str session-key uid) start-time)))

(defn get-session
  [uid]
  (wcar* (car/get uid)))

(defn active-sessions []
  (wcar* (car/keys (str session-key "*"))))

(defn delete-session [uid]
  (wcar* (car/del (str session-key uid))))
