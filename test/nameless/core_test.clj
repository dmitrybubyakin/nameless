(ns nameless.core-test
  (:require [nameless.handler :refer :all]
            [clojure.test :refer :all]
            [nameless.fixtures :as fix]
            [nameless.core :refer [handler]]
            [ring.mock.request :refer [request json-body]]
            [config.core :refer [env]]
            [cheshire.core :as json]
            [clojure.walk :as wk]))

(use-fixtures :once fix/mount-sut)
(use-fixtures :each fix/clear)

(deftest test-dummy-routes
  (testing "When homepage is opened"
    (testing "should return valid string when landed on homepage"
      (let [response (handler (request :get "/"))]
        (is (= (:status response) 200))
        (is (= (:body response) "Welcome to the world of anonymity !")))))

  (testing "When ping request is made"
    (testing "should return pong response"
      (let [response (handler (request :get "/ping"))]
        (is (= (:status response) 200))
        (is (= (:body response) "pong"))))))

(deftest test-api

  (testing "When create room POST request is made"
    (testing "should create room and return 200 response code with valid response"
      (let [data {:host "John Doe"}
            expected-response {:url "loremIpsum" :host "John Doe" :active true}
            actual-response (handler (-> (request :post "/api/v1/room/loremIpsum")
                                         (json-body data)))
            response-data (-> (json/decode (:body actual-response))
                               (wk/keywordize-keys))]
        (is (= 200 (:status actual-response)))
        (is (= "success" (:status response-data)))
        (is (= expected-response (:data response-data)))))))
