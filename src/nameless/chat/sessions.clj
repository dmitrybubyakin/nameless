(ns nameless.chat.sessions
  (:require [immutant.web.async :as async]
            [taoensso.timbre :as log]
            [nameless.cache :as cache]
            [nameless.chat.domain.core :as core]))

(def websocket-callbacks
  "WebSocket hooks"
  {:on-open    (fn [channel]
                 (core/create-ws-session channel))
   :on-close   (fn [channel {:keys [code reason]}]
                 (core/remove-ws-session channel)
                 (log/info "close code:" code "reason:" reason))
   :on-message (fn [channel m]
                 (core/save-message channel m))})
