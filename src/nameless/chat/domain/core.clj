(ns nameless.chat.domain.core
  (:require [nameless.chat.db.core :as db]
            [immutant.web.async :as async]))

(defn session->unique-id [channel]
  (-> (async/originating-request channel)
      (:uri)
      (subs 1)))

(defn save-message [ch m]
  (let [url (session->unique-id ch)
        author "Kshitij"]
    (db/add! url m author)))
