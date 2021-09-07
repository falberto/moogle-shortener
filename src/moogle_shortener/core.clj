(ns moogle-shortener.core
  (:require [clojure.tools.logging :as log]
            [moogle-shortener.html :as html]
            [nano-id.core :as nano]))

(defn main-page [req]
  {:status 200
   :body (html/greet-page)
   :headers {}})

(defn shortener [req]
  (let [url (get-in req [:form-params "url"])]
    (println url)
    url))

(defn -main [& args]
  (log/info "Starting App..."))
