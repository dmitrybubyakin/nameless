(ns nameless.chat.db.core
  (:require [clojure.tools.logging :as log]
            [honeysql.core :as s]
            [honeysql.helpers :as h]
            [clojure.java.jdbc :as jdbc]
            [nameless.datasource :as ds]
            [honeysql.helpers :refer :all :as h]
            [honeysql.core :as s]))

(defn add! [url message author]
  (try
    (let [data {:uuid url :message message :author author}
          status (jdbc/execute! (ds/conn)
                                (-> (h/insert-into :chat)
                                    (values [data])
                                    (s/format))
                                {:transaction? false})]
      (if (= 1 (first status))
        message
        :failure))
    (catch Exception e
      (log/error "Failed to save chat : " (.getMessage e))
      :failure)))

(defn create-room! [url host set-active]
  (try
    (let [data {:url url :host host :active set-active}
          status (jdbc/execute! (ds/conn)
                                (-> (h/insert-into :room)
                                    (values [data])
                                    (s/format))
                                {:transaction? false})]
      (if (= 1 (first status))
        data
        :failure))
    (catch Exception e
      (log/error "Failed to create room : " (.getMessage e))
      :failure)))

(defn room-active? [url]
  (try
    (let [response (->> (-> {:select [:active]
                             :from   [:room]
                             :where  [:= :url url]}
                            (s/format))
                        (jdbc/query (ds/conn))
                        (first))]
      (if (empty? response)
        {:active false}
        response))
    (catch Exception e
      (log/error "Failed to check active room : " (.getMessage e))
      :failure)))
