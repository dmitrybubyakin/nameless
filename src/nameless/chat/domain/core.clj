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
  (let [owner (:query-string (async/originating-request channel))
        uid (session->unique-id channel)
        message (str owner " joined the room")
        message-exists? (db/message-exists? uid message owner)]
    (cache/save-session uid channel)
    (log/info message uid)
    (if (false? message-exists?)
      (save-message uid message owner :entry)
      (prepare-message channel :entry {:data message}))))

(defn remove-ws-session [channel]
  (let [owner (:query-string (async/originating-request channel))
        uid (session->unique-id channel)
        message (str owner " left the room")]
    (cache/remove-session uid channel)
    (log/info message uid)))

(defn active-room? [url]
  (let [response (db/room-active? url)]
    (if (= :failure response)
      {:status :failure}
      {:status :success
       :data   response})))

(defn create-room [url host]
  (let [set-active true
        response (db/create-room! url host set-active)]
    (if (= :failure response)
      {:status :failure}
      {:status :success
       :data   response})))

(defn get-chats [url]
  (let [response (db/get-chats url)]
    (if (= :failure response)
      {:status :failure}
      (->> (transform-date response)
           (assoc {:status :success} :data)))))
