(ns nameless.validation
  (:require [schema.core :as s]
            [schema.coerce :as c]
            [clojure.walk :as wk]))

(s/defschema CreateRoomReq
  {:url s/Str
   :host s/Str})

(s/defschema ActiveMeeting?
  {:url s/Str})

(s/defschema GetChatsReq
  {:url s/Str})

(s/defschema ToggleVisibilityReq
  {:url s/Str})

(defn parse [schema-for-input input]
  (->> (wk/keywordize-keys input)
       ((c/coercer schema-for-input {}))))
