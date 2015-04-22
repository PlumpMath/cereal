(ns cereal.core
  (:require [clojure.core.async :as a]))

(def stop-val (atom false)) ;; maybe this should be made into a map to support starting/stopping multiple concurrent loops

(defn stop
  []
  (reset! stop-val true))

(defn start
  []
  (reset! stop-val false))

(defn timed-loop
  [wait input-ch handler]
  (let [ch (a/chan)]
    (a/go
      (while (not @stop-val)
        (a/<! (a/timeout wait))
        (handler (a/<! ch))))
    (a/go
      (while (not @stop-val)
        (a/<! (a/timeout wait))
        (a/>! ch (a/<! input-ch))))
    input-ch))
