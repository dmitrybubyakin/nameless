(ns nameless.core-test
  (:require [nameless.handler :refer :all]
            [clojure.test :refer :all]
            [nameless.fixtures :as fix]
            [nameless.core :refer :all]
            [ring.mock.request :refer [request json-body]]
            [config.core :refer [env]]
            [cheshire.core :as json]
            [clojure.walk :as wk]
            [ragtime.repl :as ragtime]
            [nameless.validation :as v]
            [nameless.chat.domain.core :as dc]
            [nameless.chat.db.core :as db]))

(use-fixtures :once fix/mount-sut)
(use-fixtures :each fix/clear)

(def failure "failure")
(def success "success")

(deftest test-app-startup-commands

  (testing "When server startup command is given"
    (testing "It should start service"
      (let [server-started? (atom false)]
        (with-redefs [start-api-server (fn []
                                         (reset! server-started? true))]
          (-main "server")
          (is (true? @server-started?))))))

  (testing "When migrate command is given"
    (testing "It should run migrations"
      (let [migration-ran? (atom false)]
        (with-redefs [ragtime/migrate (fn [_]
                                        (reset! migration-ran? true))]
          (-main "migrate")
          (is (true? @migration-ran?))))))

  (testing "When rollback command is given"
    (testing "It should rollback last migration"
      (let [rollback-ran? (atom false)]
        (with-redefs [ragtime/rollback (fn [_]
                                         (reset! rollback-ran? true))]
          (-main "rollback")
          (is (true? @rollback-ran?))))))

  (testing "When help command is given"
    (testing "It should show help"
      (let [help-shown? (atom false)]
        (with-redefs [get-help (fn []
                                 (reset! help-shown? true))]
          (-main "help")
          (is (true? @help-shown?))))))

  (testing "When no command is given"
    (testing "It should show help and report error"
      (let [help-shown? (atom false)]
        (with-redefs [get-help (fn []
                                 (reset! help-shown? true))]
          (-main "")
          (is (true? @help-shown?)))))))

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

(deftest test-apis

  (testing "When create room POST request is made"
    (testing "should create room and return 200 response code with valid response"
      (let [data {:host "John Doe"}
            expected-response {:url "loremIpsum" :host "John Doe" :active true}
            actual-response (handler (-> (request :post "/api/v1/room/loremIpsum")
                                         (json-body data)))
            response-data (-> (json/decode (:body actual-response))
                              (wk/keywordize-keys))]
        (is (= 200 (:status actual-response)))
        (is (= success (:status response-data)))
        (is (= expected-response (:data response-data)))))
    (testing "should return 400 if room already exists"
      (let [data {:host "John Doe"}
            expected-response "Failed to create room !"
            actual-response (handler (-> (request :post "/api/v1/room/loremIpsum")
                                         (json-body data)))
            response-data (-> (json/decode (:body actual-response))
                              (wk/keywordize-keys))]
        (is (= 400 (:status actual-response)))
        (is (= failure (:status response-data)))
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
            actual-response (handler (request :get "/api/v1/active/room/loremIpsumRoom"))
            response-data (-> (json/decode (:body actual-response))
                              (wk/keywordize-keys))]
        (is (= 200 (:status actual-response)))
        (is (= success (:status response-data)))
        (is (= expected-response (:data response-data)))))
    (testing "should return 200 and true when room is active"
      (let [data {:host "John Doe"}
            _ (handler (-> (request :post "/api/v1/room/loremIpsumRoom")
                           (json-body data)))
            expected-response {:active true}
            actual-response (handler (request :get "/api/v1/active/room/loremIpsumRoom"))
            response-data (-> (json/decode (:body actual-response))
                              (wk/keywordize-keys))]
        (is (= 200 (:status actual-response)))
        (is (= success (:status response-data)))
        (is (= expected-response (:data response-data)))))
    (testing "should return 500 when parse error"
      (with-redefs [v/parse (fn [_ _] {:error "Failed to parse error"})]
        (let [expected-response "Error while parsing request Failed to parse error"
              actual-response (handler (request :get "/api/v1/active/room/1242"))
              response-data (-> (json/decode (:body actual-response))
                                (wk/keywordize-keys))]
          (is (= 500 (:status actual-response)))
          (is (= expected-response response-data)))))
    (testing "should return 400 when failed to check active room"
      (with-redefs [db/room-active? (fn [_] :failure)]
        (let [expected-response "Failed to get active rooms !"
              actual-response (handler (request :get "/api/v1/active/room/1242"))
              response-data (-> (json/decode (:body actual-response))
                                (wk/keywordize-keys))]
          (is (= 400 (:status actual-response)))
          (is (= failure (:status response-data)))
          (is (= expected-response (:data response-data)))))))

  (testing "When GET chat api request is made"
    (testing "should return 200 response and empty array for new chat"
      (let [data {:host "John Doe"}
            _ (handler (-> (request :post "/api/v1/room/loremIpsumNewRoom")
                           (json-body data)))
            expected-response []
            actual-response (handler (request :get "/api/v1/chats/loremIpsumNewRoom"))
            response-data (-> (json/decode (:body actual-response))
                              (wk/keywordize-keys))]
        (is (= 200 (:status actual-response)))
        (is (= success (:status response-data)))
        (is (= expected-response (:data response-data)))))
    (testing "should return 400 when room does not exists"
      (let [expected-response "Failed to get chats !"
            actual-response (handler (request :get "/api/v1/chats/loremIpsumNoRoom"))
            response-data (-> (json/decode (:body actual-response))
                              (wk/keywordize-keys))]
        (is (= 400 (:status actual-response)))
        (is (= failure (:status response-data)))
        (is (= expected-response (:data response-data)))))
    (testing "should return 500 when parse error"
      (with-redefs [v/parse (fn [_ _] {:error "Failed to parse error"})]
        (let [expected-response "Error while parsing request Failed to parse error"
              actual-response (handler (request :get "/api/v1/chats/loremIpsumNoRoom"))
              response-data (-> (json/decode (:body actual-response))
                                (wk/keywordize-keys))]
          (is (= 500 (:status actual-response)))
          (is (= expected-response response-data)))))))
