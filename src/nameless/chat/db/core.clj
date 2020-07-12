(ns nameless.chat.db.core
  (:require [clojure.tools.logging :as log]
            [honeysql.core :as s]
            [honeysql.helpers :as h]
            [clojure.java.jdbc :as jdbc]
            [nameless.datasource :as ds]
            [honeysql.helpers :refer :all :as h]
            [honeysql.core :as s]))

(defn add! [url content author]
  (try
    (let [data {:url url :content content :author author}
          status (jdbc/execute! (ds/conn)
                                (-> (h/insert-into :chat)
                                    (values [data])
                                    (s/format))
                                {:transaction? false})]
      (if (= 1 (first status))
        content
        :failure))
    (catch Exception e
      (log/error "Failed to save chat : " (.getMessage e))
      :failure)))
