(ns nameless.chat.domain.core
  (:require [nameless.chat.db.core :as db]
            [immutant.web.async :as async]
            [nameless.cache :as cache]
            [clojure.walk :as wk]
            [cheshire.core :refer :all]
            [clj-time.core :as t]
            [clj-time.coerce :as c]
            [config.core :refer [env]]
            [clojure.tools.logging :as log]))

(def channel-store (atom []))

(defn session->unique-id [channel]
  (-> (async/originating-request channel)
      (:uri)
      (subs 1)))

(defn broadcast-message [channel content]
  (let [chs (cache/get-connected-clients (session->unique-id channel))]
    (doseq [ch chs]
      (async/send! ch content))))

(defn prepare-message [channel type message]
  (->> (case type
         :entry {:type "entry", :message message}
         :default {:type "message", :message message})
       (generate-string)
       (broadcast-message channel)))

(defn create-ws-session [channel]
  (let [username (:query-string (async/originating-request channel))]
    (swap! channel-store conj channel)
    (log/info "New client connected !")
    (prepare-message channel :entry (str username " joined the chat"))))

(defn save-message [channel m]
  (let [data (decode m)
        {:keys [message author url]} (wk/keywordize-keys data)
        content (db/add! url message author)]
    (broadcast-message channel message)))

(defn active-room? [url]
  (let [response  (db/room-active? url)]
    (if (= :failure response)
      {:status :failure}
      {:status :success
       :data response})))

(defn create-room [url host]
  (let [set-active true
        response  (db/create-room! url host set-active)]
    (if (= :failure response)
      {:status :failure}
      {:status :success
       :data response})))
