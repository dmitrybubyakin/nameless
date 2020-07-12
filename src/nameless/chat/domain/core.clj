(ns nameless.chat.domain.core
  (:require [nameless.chat.db.core :as db]
            [immutant.web.async :as async]
            [nameless.cache :as cache]
            [clojure.walk :as wk]
            [cheshire.core :refer :all]
            [clj-time.core :as t]
            [clj-time.coerce :as c]))

(def channel-store (atom []))

(defn active-sessions []
  (count (cache/active-sessions)))

(defn session->unique-id [channel]
  (-> (async/originating-request channel)
      (:uri)
      (subs 1)))

(defn save-session [channel]
  (let [uid (session->unique-id channel)
        start-time (c/to-long (t/now))]
    (cache/save-session uid start-time)))

(defn create-session [channel]
  (swap! channel-store conj channel)
  (save-session channel)
  (async/send! channel "Ready to send nameless feedbacks !"))

(defn save-message [channel m]
  (let [data (decode m)
        {:keys [message author url]} (wk/keywordize-keys data)]
    (db/add! url message author)
    (async/send! channel message)))

(defn remove-session [channel]
  (let [uid (session->unique-id channel)]
    (swap! channel-store (fn [store] (remove #(= channel %) store)))
    (cache/delete-session uid)))
