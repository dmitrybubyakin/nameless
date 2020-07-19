(ns nameless.validation
  (:require [schema.core :as s]
            [schema.coerce :as c]
            [clojure.walk :as wk]))

(s/defschema CreateMeetingReq
  {:url s/Str})

(s/defschema ActiveMeeting?
  {:url s/Str})

(defn coerce-to-int [n]
  (if (string? n)
    (Integer/parseInt n)
    n))

(def input-coercion-map {s/Int coerce-to-int})

(defn parse [schema-for-input input]
  (->> (wk/keywordize-keys input)
    ((c/coercer schema-for-input input-coercion-map))))
