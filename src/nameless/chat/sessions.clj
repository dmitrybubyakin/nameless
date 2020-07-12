(ns nameless.chat.sessions
  (:require [immutant.web.async :as async]
            [clojure.tools.logging :as log]
            [nameless.cache :as cache])
  (:import (org.joda.time Instant)))

(def channel-store (atom []))

(defn active-sessions []
  (count (cache/active-sessions)))

(defn session->unique-id [channel]
  (-> (async/originating-request channel)
      (:uri)
      (subs 1)))

(defn save-session [channel]
  (let [unique-id (session->unique-id channel)
        start-time (Instant/now)]
    (cache/save-session unique-id start-time)))

(defn create-session [channel]
  (swap! channel-store conj channel)
  (save-session channel)
  (async/send! channel "Ready to send nameless feedbacks !"))

(def websocket-callbacks
  "WebSocket hooks"
  {:on-open    (fn [channel]
                 (create-session channel))
   :on-close   (fn [channel {:keys [code reason]}]
                 (swap! channel-store (fn [store] (remove #(= channel %) store)))
                 (log/info "close code:" code "reason:" reason))
   :on-message (fn [ch m]
                 ; do something below saved to cache and send back etc
                 (async/send! ch (apply str (reverse m))))})
