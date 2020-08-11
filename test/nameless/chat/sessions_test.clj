(ns nameless.chat.sessions-test
  (:require [nameless.chat.sessions :refer :all]
            [clojure.test :refer :all]
            [nameless.fixtures :as fix]
            [gniazdo.core :as ws]
            [cheshire.core :as json]
            [clojure.walk :as wk]
            [ring.mock.request :refer [request json-body]]
            [nameless.core :refer [handler]]))

(use-fixtures :once fix/mount-sut-for-ws)
(use-fixtures :each fix/clear)

(def hostname "ws://localhost:8080/")

(deftest websocket-crud-test
  (testing "When connection is opened"
    (testing "Should return entry message with username"
      (let [expected-entry-msg "{\"type\":\"entry\",\"message\":{\"data\":\"Lorem Ipsum joined the room\"}}"
            _ (ws/connect (str hostname "porting?Lorem%20Ipsum")
                          :on-receive #(is (= expected-entry-msg %)))])))

  (testing "When connection is closed"
    (testing "Should return exit message with username"
      (let [expected-entry-msg "JohnDoe left the room mycustomroom"
            sock (ws/connect (str hostname "mycustomroom?JohnDoe")
                             :on-close #(is (= expected-entry-msg %)))
            _ (ws/close sock)])))

  (testing "When message is sent over open ws connection"
    (testing "Should return valid response"
      (let [expected-response {:url "mychatroom" :owner "John Doe" :data "Typed Lorem Ipsum"}
            message (json/encode expected-response)
            sock (ws/connect (str hostname "mycustomroom?JohnDoe")
                             :on-receive #(is (= expected-response (-> (json/decode %)
                                                                       (wk/keywordize-keys)
                                                                       (:message)
                                                                       (dissoc :dt)))))
            _ (ws/send-msg sock message)]))
    (testing "Should save message to db"
      (let [expected-response {:url "mypersonalroom" :owner "John Pop" :data "Lorem Ipsum is the new trend"}
            data {:host (:owner expected-response)}
            _ (handler (-> (request :post "/api/v1/room/mypersonalroom")
                           (json-body data)))
            message (json/encode expected-response)
            sock (ws/connect (str hostname "mypersonalroom?JohnPop"))
            _ (ws/send-msg sock message)
            _ (Thread/sleep 1000)
            actual-response (handler (request :get "/api/v1/chats/mypersonalroom"))
            response-data (->> (json/decode (:body actual-response))
                              (wk/keywordize-keys)
                              (:data)
                              (map :data))]
        (is (some #(= (:data expected-response) %) response-data))))))






