(ns nameless.cache
  (:require [mount.core :refer [defstate]]
            [config.core :refer [env]]
            [clojure.walk :as wk]))

(def channel-store (atom {}))

(defn get-connected-clients [url]
  ((keyword url) @channel-store))

(defn update-store [uid channel]
  (let [uid-key (keyword uid)]
    (swap! channel-store assoc-in [uid-key] (concat (uid-key @channel-store) [channel]))))

(defn save-session
  [uid channel]
  (update-store uid channel))

(defn remove-session [uid channel]
  (let [uid-key (keyword uid)
        active-sessions (or (uid-key @channel-store) ())]
    (if (not-empty active-sessions)
      (swap! channel-store assoc-in [uid-key] (remove #(= channel %) active-sessions)))))


