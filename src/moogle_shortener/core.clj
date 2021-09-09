(ns moogle-shortener.core
  (:require [clojure.tools.logging :as log]
            [moogle-shortener.html :as html]
            [nano-id.core :as nano]
            [moogle-shortener.config :as config]
            [moogle-shortener.db :as db]
            [next.jdbc.sql :as sql])
  (:import (java.time Instant)))

(defonce SHORTENED "shortened")

(defn- api-url [{:keys [scheme server-name server-port]}]
  (let [port (when-not (= 80 server-port) (str ":" server-port))]
    (str (name scheme) "://" server-name port)))

(defn main-page [req]
  {:status  200
   :body    (html/greet-page)
   :headers {}})

(defn- save-shortener!
  "Register in the database all relevant info from request and the URL shortened"
  [db register]
  (db/insert! db :shortened register))

(defn save-access!
  "Register in the database all relevant info from URL shortened access"
  [db access]
  (db/insert! db :access access))


(defn shortener [db id]
  (-> (db/query db
                 [(str "select url from " SHORTENED " where id = ?")
                  id])
      first
      :url))

(defn shortener! [db req]
  (log/info "Shortining URL... [" (get-in req [:form-params "url"]) "]")
  (let [base-url (api-url req)
        id (nano/nano-id (:size config/nano))
        url (get-in req [:form-params "url"])
        ip (:remote-addr req)
        register {:id        id
                  :url       url
                  :shortened (str base-url "/" id)
                  :ip        ip
                  :created   (Instant/now)}]
    (save-shortener! db register)
    (html/shortener (assoc register :host base-url))))

(comment
  (nano/nano-id 8)
  prn)