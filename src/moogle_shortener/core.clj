(ns moogle-shortener.core
  (:require [clojure.tools.logging :as log]
            [moogle-shortener.html :as html]
            [nano-id.core :as nano]
            [ring.util.response :as response]
            [moogle-shortener.config :as config])
  (:import (java.time Instant)))

(defn- api-url [{:keys [scheme server-name server-port]}]
  (str (name scheme) "://" server-name ":" server-port "/api"))

(defn main-page [req]
  {:status 200
   :body (html/greet-page)
   :headers {}})

(defn shortener [req]
  (def s req)
  (let [base-url (api-url req)
        id (nano/nano-id (:size config/nano))
        url (get-in req [:form-params "url"])
        ip (:remote-addr req)]
    {:id        id
     :url       url
     :shortened (str base-url "/" id)
     :ip        ip
     :created   (Instant/now)}))

(defn -main [& args]
  (log/info "Starting App..."))


(comment
  (nano/nano-id 8)
  prn)