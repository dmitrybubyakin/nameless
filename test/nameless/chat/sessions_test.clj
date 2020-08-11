(ns nameless.chat.sessions-test
  (:require [nameless.chat.sessions :refer :all]
            [clojure.test :refer :all]
            [nameless.fixtures :as fix]
            [gniazdo.core :as ws]))

(use-fixtures :once fix/mount-sut-for-ws)
(use-fixtures :each fix/clear)

(def hostname "ws://localhost:8080/")

(deftest websocket-crud-test
  (testing "When connection is opened"
    (testing "Should return entry message with username"
      (let [expected-entry-msg "{\"type\":\"entry\",\"message\":{\"data\":\"Lorem Ipsum joined the room\"}}"
            _ (ws/connect (str hostname "porting?Lorem%20Ipsum")
                          :on-receive #(is (= expected-entry-msg %)))]))))






