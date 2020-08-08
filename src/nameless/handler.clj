(ns nameless.handler
  (:require [cheshire.core :as json]
            [taoensso.timbre :as log]
            [nameless.chat.domain.core :as dc]
            [nameless.validation :as v]))

(defn respond!
  "response"
  [code response]
  {:status  code
   :headers {"Content-Type" "application/json"}
   :body    (json/encode response)})

(defn- produce-parse-err! [type err]
  (log/error "Error while parsing" type "request" err "!")
  (respond! 500 (str "Error while parsing request " err)))

(defn active-room? [url]
  (let [request (v/parse v/ActiveMeeting? {:url url})]
    (if (:error request)
      (produce-parse-err! :check-active-meeting (:error request))
      (let [{:keys [url]} request
            response (dc/active-room? url)]
        (if (= (:status response) :success)
          (respond! 200 response)
          (respond! 400 {:status :failure, :data "Failed to get active rooms !"}))))))

(defn create-room [url host]
  (let [request (v/parse v/CreateRoomReq {:url url :host host})]
    (if (:error request)
      (produce-parse-err! :create-new-room (:error request))
      (let [{:keys [url host]} request
            response (dc/create-room url host)]
        (if (= (:status response) :success)
          (respond! 200 response)
          (respond! 400 {:status :failure, :data "Failed to create room !"}))))))

(defn get-chats [url]
  (let [request (v/parse v/GetChatsReq {:url url})]
    (if (:error request)
      (produce-parse-err! :get-chats (:error request))
      (let [{:keys [url]} request
            response (dc/get-chats url)]
        (if (= (:status response) :success)
          (respond! 200 response)
          (respond! 400 {:status :failure, :data "Failed to create room !"}))))))
