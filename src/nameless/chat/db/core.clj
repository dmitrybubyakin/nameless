(ns nameless.chat.db.core
  (:require [clojure.tools.logging :as log]
            [honeysql.core :as s]
            [honeysql.helpers :as h]
            [clojure.java.jdbc :as jdbc]
            [nameless.datasource :as ds]
            [honeysql.helpers :refer :all :as h]
            [honeysql.core :as s]))

(defn get-chat-data [url message owner]
  (try
    (let [response (->> (-> {:select [:url :data :owner [:created_at :dt]]
                             :from   [:chat]
                             :where  [:and [:= :url url] [:= :data message] [:= :owner owner]]}
                            (s/format))
                        (jdbc/query (ds/conn))
                        (first))]
      (if (empty? response)
        :failure
        response))
    (catch Exception e
      (log/error "Failed to get chat message : " (.getMessage e))
      :failure)))

(defn add! [url message owner type]
  (try
    (let [data {:url url :data message :owner owner :type (name type)}
          status (jdbc/execute! (ds/conn)
                                (-> (h/insert-into :chat)
                                    (values [data])
                                    (s/format))
                                {:transaction? false})]
      (if (= 1 (first status))
        (get-chat-data url message owner)
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

(defn get-chats [url]
  (try
    (let [response (->> (-> {:select   [:data :owner :type [:created_at :dt]]
                             :from     [:chat]
                             :where    [:= :url url]
                             :order-by [[:created_at :desc]]}
                            (s/format))
                        (jdbc/query (ds/conn)))]
      (if (empty? response)
        ()
        response))
    (catch Exception e
      (log/error "Failed to get chat history : " (.getMessage e))
      :failure)))

(defn message-exists? [uid message owner]
  (try
    (let [response (->> (-> {:select [:id]
                             :from   [:chat]
                             :where  [:and [:= :url uid] [:= :data message] [:= :owner owner]]}
                            (s/format))
                        (jdbc/query (ds/conn)))]
      (if (empty? response)
        false
        true))
    (catch Exception e
      (log/error "Failed to get existing message : " (.getMessage e))
      :failure)))
