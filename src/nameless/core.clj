(ns nameless.core
  (:require
    [immutant.web             :as web]
    [immutant.web.async       :as async]
    [immutant.web.middleware  :as web-middleware]
    [compojure.route          :as route]
    [environ.core             :refer (env)]
    [compojure.core           :refer (ANY GET defroutes)]
    [ring.util.response       :refer (response redirect content-type)])
  (:gen-class))


(def channel-store (atom []))

(defn send-message-to-all []
  "Sends a message to all connected ws connections"
  (doseq [ch @channel-store]
    (async/send! ch "Message Received")))

(def websocket-callbacks
  "WebSocket hooks"
  {:on-open   (fn [channel]
                (swap! channel-store conj channel) ;store channels for later
                (prn "channels " @channel-store)
                (async/send! channel "Ready to send nameless feedbacks !"))
   :on-close   (fn [channel {:keys [code reason]}]
                 (println "close code:" code "reason:" reason))
   :on-message (fn [ch m]
                 (send-message-to-all)
                 ; do something below saved to cache and send back etc
                 (async/send! ch (apply str (reverse m))))})

(defroutes routes
           ;(GET "/" {c :context} (redirect (str c "/index.html")))
           (route/resources "/"))

(defn -main [& {:as args}]
  (web/run
    (-> routes
        ;; wrap the handler with websocket support
        ;; websocket requests will go to the callbacks, ring requests to the handler
        (web-middleware/wrap-websocket websocket-callbacks))
    (merge {"host" (env :port), "port" 8080}  args)))
