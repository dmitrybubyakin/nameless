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
        (is (= expected-response (:data response-data)))))
    (testing "should return 400 if room already exists"
      (let [data {:host "John Doe"}
            expected-response "Failed to create room !"
            actual-response (handler (-> (request :post "/api/v1/room/loremIpsum")
                                         (json-body data)))
            response-data (-> (json/decode (:body actual-response))
                              (wk/keywordize-keys))]
        (is (= 400 (:status actual-response)))
        (is (= "failure" (:status response-data)))
        (is (= expected-response (:data response-data)))))
    (testing "should return 500 if valid data is not passed"
      (let [data {:toast "John Doe"}
            expected-response "Error while parsing request {:host (not (instance? java.lang.String nil))}"
            actual-response (handler (-> (request :post "/api/v1/room/loremIpsum")
                                         (json-body data)))
            response-data (-> (json/decode (:body actual-response))
                              (wk/keywordize-keys))]
        (is (= 500 (:status actual-response)))
        (is (= expected-response response-data)))))

  (testing "When check if room active GET request is made"
    (testing "should return 200 and false when room is not active"
      (let [expected-response {:active false}
            actual-response (handler (request :get "/api/v1/room/loremIpsumRoom"))
            response-data (-> (json/decode (:body actual-response))
                              (wk/keywordize-keys))]
        (is (= 200 (:status actual-response)))
        (is (= "success" (:status response-data)))
        (is (= expected-response (:data response-data)))))
    (testing "should return 200 and true when room is active"
      (let [data {:host "John Doe"}
            _ (handler (-> (request :post "/api/v1/room/loremIpsumRoom")
                                              (json-body data)))
            expected-response {:active true}
            actual-response (handler (request :get "/api/v1/room/loremIpsumRoom"))
            response-data (-> (json/decode (:body actual-response))
                              (wk/keywordize-keys))]
        (is (= 200 (:status actual-response)))
        (is (= "success" (:status response-data)))
        (is (= expected-response (:data response-data)))))))
