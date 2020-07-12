(ns nameless.chat.sessions
  (:require [immutant.web.async :as async]
            [clojure.tools.logging :as log]))

(def channel-store (atom []))

(defn active-sessions []
  (count @channel-store))

(defn create-session-with-handshake [channel]
  (swap! channel-store conj channel)
  (async/send! channel "Ready to send nameless feedbacks !"))

(def websocket-callbacks
  "WebSocket hooks"
  {:on-open    (fn [channel]
                 (create-session-with-handshake channel))
   :on-close   (fn [channel {:keys [code reason]}]
                 (swap! channel-store (fn [store] (remove #(= channel %) store)))
                 (log/info "close code:" code "reason:" reason))
   :on-message (fn [ch m]
                 ; do something below saved to cache and send back etc
                 (async/send! ch (apply str (reverse m))))})
