(ns nameless.chat.cache
  (:require [taoensso.carmine :as car :refer (wcar)]))

(def session-key "session-")
(def conn {:pool {} :spec {:host "127.0.0.1" :port 6379}})

(defmacro wcar* [& body] `(car/wcar conn ~@body))

(defn save-session
  [uid start-time]
  (wcar* (car/set (str session-key uid) start-time)))

(defn get-session
  [uid]
  (wcar* (car/get uid)))

(defn active-sessions []
  (wcar* (car/keys (str session-key "*"))))
