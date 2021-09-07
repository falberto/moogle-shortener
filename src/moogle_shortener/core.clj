(ns moogle-shortener.core
  (:require [clojure.tools.logging :as log]
            [moogle-shortener.html :as html]
            [nano-id.core :as nano]
            [ring.util.response :as response]
            [moogle-shortener.config :as config]
            [next.jdbc.sql :as sql])
  (:import (java.time Instant)))

(defn- api-url [{:keys [scheme server-name server-port]}]
  (str (name scheme) "://" server-name ":" server-port "/api"))

(defn main-page [req]
  {:status 200
   :body (html/greet-page)
   :headers {}})

(defn- register!
  "Register in the database all relevant info from request and the URL shortened"
  [db register]
  (sql/insert! db :shortened register))

(comment
  (sql/insert! @config/datasource
               :shortened
               {:id        1
                :url       1
                :shortened 1
                :ip        1
                :created   (Instant/now)})
  prn)

(defn shortener [db req]
  (def s req)
  (let [base-url (api-url req)
        id (nano/nano-id (:size config/nano))
        url (get-in req [:form-params "url"])
        ip (:remote-addr req)
        register {:id        id
                  :url       url
                  :shortened (str base-url "/" id)
                  :ip        ip
                  :created   (Instant/now)}]
    (println register)
    (register! db register)))


(defn -main [& args]
  (log/info "Starting App..."))


(comment
  (nano/nano-id 8)
  prn)