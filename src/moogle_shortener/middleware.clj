(ns moogle-shortener.middleware
  (:require [clojure.string :as str]
            [camel-snake-kebab.core :refer [->snake_case_keyword
                                            ->kebab-case-keyword]]
            [camel-snake-kebab.extras :refer [transform-keys]]
            [reitit.ring.middleware.exception :as exception]
            [clojure.tools.logging :as log]
            [moogle-shortener.config :as config])
  (:import (clojure.lang ExceptionInfo)))

(defn wrap-trailing-slash
  "
  This middleware removes the trailing slash in the end of uri.

  In the following example both urls are considered the same:
    https://server:8080/xpto  => {:uri /xpto}
    https://server:8080/xpto/ => {:uri /xpto}
  "
  [handler]
  (fn [req]
    (let [trailing-slash-fn (fn [url]
                              ;; Remove slash in the end of uri
                              ;; Skip when it is the root path
                              (if (> (count url) 1)
                                (clojure.string/replace url #"/$" "")
                                url))
          request (update req :uri trailing-slash-fn)]
      (handler request))))

(defn wrap-db
  "Wraps the database connection"
  [handler]
  (fn [req]
    (handler (assoc req :moogle-shortener/db @config/datasource))))

(defn- bad-request [msg]
  {:status  400
   :headers {}
   :body    {:error   "BAD_REQUEST"
             :message msg}})

(defn- internal-server-error [msg]
  {:status  500
   :headers {}
   :body    {:status 500
             :error   "INTERNAL_SERVER_ERROR"
             :title "Something very bad has happened!"
             :message msg}})

(defn wrap-exception-mapper
  "This middleware adds a default exception handling for all uncaught exceptions.
   Returns a INTERNAL SERVER ERROR response body with the exception message inside.

   WARNING: It must wrap all handlers in order to work correctly."
  [handler]
  (fn [req]
    (try
      (handler req)
      (catch ExceptionInfo ex
        (let [msg (ex-message ex)]
          (log/error ex msg)
          (cond (= (:error (ex-data ex)) :invalid)
                (bad-request msg)

                :else
                (internal-server-error msg))))
      (catch Throwable ex
        (let [msg (ex-message ex)]
          (log/error ex msg)
          (internal-server-error (str ex)))))))


(defn exception-handler
  "Cria mensagem de erro a partir da requisição."
  [type exception request]
  {:status 500
   :body   {:uri       (:uri request)
            :type      type
            :message   (ex-message exception)
            :data      (ex-data exception)
            :exception (class exception)
            :cause     (ex-cause exception)}})

(def exception-middleware
  (exception/create-exception-middleware
    (merge
      exception/default-handlers
      {
       :reitit.coercion/request-coercion  (exception/create-coercion-handler 400)

       :reitit.coercion/response-coercion (exception/create-coercion-handler 500)

       ;; override the default handler
       ::exception/default                (partial exception-handler "default")

       ;; print stack-traces for all exceptions
       ::exception/wrap                   (fn [handler e request]
                                            (log/error e "ERROR" (prn-str (:uri request)))
                                            (handler e request))})))

(defn wrap-kebab-keys [handler]
  (fn [req]
    (let [body-params (transform-keys ->kebab-case-keyword (:body-params req))]
      (log/trace "KEY CONVERTION - REQUEST: " body-params)
      (handler (assoc req :body-params body-params)))))

(defn wrap-snakecase-keys [handler]
  (fn [req]
    (let [response (handler req)
          result (transform-keys ->snake_case_keyword (:body response))]
      (log/trace "KEY CONVERTION - RESPONSE: " result)
      (assoc response :body result))))

(defn wrap-log-request-time [handler]
  (fn [req]
    (log/debug ">>>")
    (log/debug (str/upper-case (name (:request-method req)))
              (:uri req))
    (let [init-time (System/currentTimeMillis)
          response (handler req)
          end-time (System/currentTimeMillis)]
      (log/debug (str/upper-case (name (:request-method req)))
                (:uri req)
                (str "(" (- end-time init-time) "ms)"))
      (log/debug "<<<")
      response)))

(comment
  (transform-keys ->kebab-case-keyword {:abc_dfe {:a-bca "abc"}}))
