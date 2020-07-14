(ns nameless.handler
  (:require [cheshire.core :as json]
            [clojure.tools.logging :as log]
            [nameless.chat.domain.core :as dc]
            [nameless.validation :as v]))

(defn respond!
  "response"
  [code response]
  (let [valid-response (dissoc response :status)]
    {:status  code
     :headers {"Content-Type" "application/json"}
     :body    (json/encode valid-response)}))

(defn- produce-parse-err! [type err]
  (log/error "Error while parsing" type "request" err "!")
  (respond! 500 (str "Error while parsing request " err)))

(defn create-meeting [req]
  (let [request (v/parse v/CreateMeetingReq (:params req))]
    (if (:error request)
      (produce-parse-err! :add-announcement (:error request))
      (let [{:keys [url]} request
            response (dc/create-meeting url)]
        (if (= (:status response) :success)
          (respond! 201 response)
          (respond! 400 (:error "Failed to create meeting !")))))))
