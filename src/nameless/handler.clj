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
      (produce-parse-err! :check-active-room (:error request))
      (let [{:keys [url]} request
            response (dc/active-room? url)]
        (if (v/Success? response)
          (respond! 200 response)
          (respond! 400 {:status :failure, :data "Failed to get active rooms !"}))))))

(defn create-room [url host]
  (let [request (v/parse v/CreateRoomReq {:url url :host host})]
    (if (:error request)
      (produce-parse-err! :create-new-room (:error request))
      (let [{:keys [url host]} request
            response (dc/create-room url host)]
        (if (v/Success? response)
          (respond! 200 response)
          (respond! 400 {:status :failure, :data "Failed to create room !"}))))))

(defn get-chat-data [url]
  (let [request (v/parse v/GetChatsReq {:url url})]
    (if (:error request)
      (produce-parse-err! :get-chats (:error request))
      (let [{:keys [url]} request
            chats (dc/get-chats url)
            open? (dc/room-open? url)
            data {:chats  (:data chats)
                  :isOpen (:data open?)}]
        (if (and (v/Success? chats) (v/Success? open?))
          (respond! 200 (assoc chats :data data))
          (respond! 400 {:status :failure, :data "Failed to get chats !"}))))))

(defn toggle-room-visibility [url]
  (let [request (v/parse v/ToggleVisibilityReq {:url url})]
    (if (:error request)
      (produce-parse-err! :toggle-visibility (:error request))
      (let [{:keys [url]} request
            response (dc/toggle-room-visibility url)]
        (if (v/Success? response)
          (respond! 200 response)
          (respond! 400 {:status :failure, :data "Failed to toggle room visibility !"}))))))
