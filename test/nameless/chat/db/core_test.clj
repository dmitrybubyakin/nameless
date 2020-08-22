(ns nameless.chat.db.core-test
  (:require [clojure.test :refer :all]
            [nameless.chat.db.core :refer :all]
            [nameless.fixtures :as fix]
            [clojure.java.jdbc :as jdbc]))

(use-fixtures :once fix/mount-sut)
(use-fixtures :each fix/clear)

(deftest get-chat-data-test
  (testing "When request is made to create room"
    (testing "should create a room if data is valid"
      (let [host "John Doe"
            response (create-room! "lorem-ipsum" host true)]
        (is (= host (:host response)))))
    (testing "should report failure if db command returns 0 status code"
      (with-redefs [jdbc/execute! (fn [_ _ _]
                                    '(0))]
        (let [host "John Doe"
              response (create-room! "lorem-ipsu" host true)]
          (is (= :failure response)))))
    (testing "should report failure if exception is thrown"
      (with-redefs [jdbc/execute! (fn [_ _ _]
                                    (throw (Exception. "Db connection could not be establised")))]
        (let [host "John Doe"
              response (create-room! "lorem-ipsu" host true)]
          (is (= :failure response)))))))

(deftest add!-test
  (testing "When request is made to add message"
    (testing "should save message if data is valid"
      (let [message "John Doe said Lorem Ipsum"
            response (add! "lorem-ipsum" message "John Doe" :message)]
        (is (= message (:data response)))))
    (testing "should report failure if db command returns 0 status code"
      (with-redefs [jdbc/execute! (fn [_ _ _]
                                    '(0))]
        (let [message "John Doe said Lorem Ipsum"
              response (add! "lorem-ipsum" message "John Doe" :message)]
          (is (= :failure response)))))
    (testing "should report failure if get chat data returns no message"
      (with-redefs [jdbc/query (fn [_ _]
                                    '())]
        (let [message "John Doe said Lorem Ipsum"
              response (add! "lorem-ipsum" message "John Doe" :message)]
          (is (= :failure response)))))
    (testing "should report failure if exception is thrown"
      (with-redefs [jdbc/execute! (fn [_ _ _]
                                    (throw (Exception. "Db connection could not be establised")))]
        (let [message "John Doe said Lorem Ipsum"
              response (add! "lorem-ipsum" message "John Doe" :message)]
          (is (= :failure response)))))
    (testing "should report failure if get chat data throws exception"
      (with-redefs [jdbc/query (fn [_ _]
                                    (throw (Exception. "Db connection could not be establised")))]
        (let [message "John Doe said Lorem Ipsum"
              response (add! "lorem-ipsum" message "John Doe" :message)]
          (is (= :failure response)))))))

(deftest room-active?-test
  (testing "When request is made to check if room active"
    (testing "should return room active as false, if room doesn't exists"
      (let [url "lorem-ipsum-dolor"
            response (room-active? url)]
        (is (false? (:active response)))))
    (testing "should return as room active if room already exists"
      (let [url "lorem-ipsum-dolor"
            _ (create-room! url "John Doe" true)
            response (room-active? url)]
        (is (true? (:active response)))))
    (testing "should report failure if exception is thrown"
      (with-redefs [jdbc/query (fn [ _ _]
                                    (throw (Exception. "Db connection could not be establised")))]
        (let [url "lorem-ipsum-dolor"
              response (room-active? url)]
          (is (= :failure response)))))))

(deftest get-chats-test
  (testing "When request is made to get chats from active room"
    (testing "should return empty chat list if room doesn't exist"
      (let [url "lorem-ipsum-dolor"
            response (get-chats url)]
        (is (= 0 (count response)))))
    (testing "should return as room active if room already exists"
      (let [url "lorem-ipsum-dolor-sit"
            message "John Doe said Lorem Ipsum"
            _ (create-room! url "John Doe" true)
            _ (add! url message "John Doe" :message)
            response (get-chats url)]
        (is (= message (:data (first response))))))
    (testing "should report failure if exception is thrown"
      (with-redefs [jdbc/query (fn [ _ _]
                                 (throw (Exception. "Db connection could not be establised")))]
        (let [url "lorem-ipsum-dolor-sit"
              response (get-chats url)]
          (is (= :failure response)))))))

(deftest message-exists?-test
  (testing "When request is made to check if message exists"
    (testing "should return true if it doesn't exist"
      (let [response (message-exists? "lorem-ipsum-dolor" "John Doe said Lorem Ipsum" "John Doe")]
        (is (false? response))))
    (testing "should return true if message already exists"
      (let [url "lorem-ipsum-dolor-sit"
            message "John Doe said Lorem Ipsum"
            owner "John Doe"
            _ (create-room! url owner true)
            _ (add! url message owner :message)
            response (message-exists? url message owner)]
        (is (true? response))))
    (testing "should report failure if exception is thrown"
      (with-redefs [jdbc/query (fn [ _ _]
                                 (throw (Exception. "Db connection could not be establised")))]
        (let [url "lorem-ipsum-dolor-sit"
              response (message-exists? url "John Doe said Lorem Ipsum" "John Doe")]
          (is (= :failure response)))))))

(deftest room-exists?-test
  (testing "When request is made to check if room exists"
    (testing "should return true if it doesn't exist"
      (let [response (room-exists? "lorem-ipsum-dolor-amet")]
        (is (false? response))))
    (testing "should return true if message already exists"
      (let [url "lorem-ipsum-dolor-amet"
            owner "John Doe"
            _ (create-room! url owner true)
            response (room-exists? url)]
        (is (true? response))))
    (testing "should report failure if exception is thrown"
      (with-redefs [jdbc/query (fn [_ _]
                                 (throw (Exception. "Db connection could not be establised")))]
        (let [url "lorem-ipsum-dolor-amet"
              response (room-exists? url)]
          (is (= :failure response)))))))

(deftest toggle-visibility-test
  (testing "When request is made to toggle room visibilty"
    (testing "should return status if it exists and status is toggled"
      (let [url "lorem-ipsum-dolor-amets"
            owner "John Doe"
            _ (create-room! url owner true)
            response (toggle-visibility "lorem-ipsum-dolor-amets")]
        (is (false? (:status response)))))
    (testing "should return :failure if room doesn't exists"
      (let [url "lorem-random-ipsum"
            response (toggle-visibility url)]
        (is (= :failure response))))
    (testing "should report failure if exception is thrown"
      (with-redefs [jdbc/query (fn [_ _]
                                 (throw (Exception. "Db connection could not be establised")))]
        (let [url "lorem-ipsum-dolor-amets"
              response (toggle-visibility url)]
          (is (= :failure response)))))))

(deftest mark-room-visibility!-test
  (testing "When request is made to mark room visibilty"
    (testing "should return status if it exists and status is toggled"
      (let [url "lorem-ipsum-dolor-mone"
            owner "John Doe"
            _ (create-room! url owner true)
            response (mark-room-visibility! "lorem-ipsum-dolor-mone" false)]
        (is (false? (:status response)))))
    (testing "should return :failure if room doesn't exists"
      (let [url "lorem-ipsum-dolor-404"
            response (mark-room-visibility! url false)]
        (is (= :failure response))))
    (testing "should report failure if exception is thrown"
      (with-redefs [jdbc/execute! (fn [_ _ _]
                                 (throw (Exception. "Db connection could not be establised")))]
        (let [url "lorem-ipsum-dolor-mone"
              response (toggle-visibility url)]
          (is (= :failure response)))))))
