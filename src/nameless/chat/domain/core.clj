(ns nameless.chat.domain.core
  (:require [nameless.chat.db.core :as db]
            [immutant.web.async :as async]
            [nameless.cache :as cache]
            [clojure.walk :as wk]
            [cheshire.core :refer :all]
            [clj-time.core :as t]
            [clj-time.coerce :as c]
            [config.core :refer [env]]
            [taoensso.timbre :as log])
  (:import (java.net URLDecoder)))

(defn session->unique-id [channel]
  (-> (async/originating-request channel)
      (:uri)
      (subs 1)))

(defn transform-date [response]
  (map (fn [x] (update-in x [:dt] #(.getTime %))) response))

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

(defn save-message
  ([channel m] (let [data (decode m)
                     {:keys [data owner url]} (wk/keywordize-keys data)
                     saved-data (db/add! url data owner :message)]
                 (prepare-message channel :default saved-data)))
  ([url message owner type]
   (db/add! url message owner type)))

(defn create-ws-session [channel]
  (let [owner (-> (:query-string (async/originating-request channel))
                  (URLDecoder/decode "UTF-8"))
        uid (session->unique-id channel)
        message (str owner " joined the room")
        message-exists? (db/message-exists? uid message owner)]
    (cache/save-session uid channel)
    (log/info message uid)
    (when (false? message-exists?)
      (prepare-message channel :entry (save-message uid message owner :entry)))))

(defn remove-ws-session [channel]
  (let [owner (-> (:query-string (async/originating-request channel))
                  (URLDecoder/decode "UTF-8"))
        uid (session->unique-id channel)
        message (str owner " left the room")]
    (cache/remove-session uid channel)
    (log/info message uid)
    message))

(defn active-room? [url]
  (let [response (db/room-active? url)]
    (if (= :failure response)
      {:status :failure}
      {:status :success
       :data   response})))

(defn create-room [url host]
  (let [set-active true
        room-active? (db/room-exists? url)
        response (db/create-room! url host set-active)]
    (if (or room-active? (= :failure response))
      {:status :failure}
      {:status :success
       :data   response})))

(defn get-chats [url]
  (let [room-active? (db/room-exists? url)
        response (db/get-chats url)]
    (if (or (false? room-active?) (= :failure response))
      {:status :failure}
      (->> (transform-date response)
           (assoc {:status :success} :data)))))

(defn toggle-room-visibility [url]
  (let [response (db/toggle-visibility url)]
    (if (= :failure response)
      {:status :failure}
      {:status :success
       :data   response})))

(defn room-open? [url]
  (let [response (db/room-open? url)]
    (if (= :failure response)
      {:status :failure}
      {:status :success
       :data   response})))
