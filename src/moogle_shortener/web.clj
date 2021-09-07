(ns moogle-shortener.web
  (:require [reitit.ring :as ring]
            [reitit.coercion.malli]
            [reitit.ring.malli]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.ring.coercion :as coercion]
            [reitit.dev.pretty :as pretty]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.middleware.parameters :as parameters]
            [ring.middleware.params :refer [wrap-params]]
            [clojure.java.io :as io]
            [muuntaja.core :as m]
            [malli.util :as mu]
            [cheshire.core :as json]
            [moogle-shortener.core :as core]
            [moogle-shortener.middleware :refer [#_wrap-db
                                                 wrap-trailing-slash
                                                 wrap-kebab-keys
                                                 wrap-snakecase-keys
                                                 exception-middleware
                                                 wrap-log-request-time]])
  (:import (java.util Properties)))

(defn- api-url [{:keys [scheme server-name server-port]}]
  (str (name scheme) "://" server-name ":" server-port "/api"))

(defn- success [body]
  {:status  200
   :headers {}
   :body    body})

(defn- no-content []
  {:status  204
   :headers {}
   :body    nil})

(defn- not-found []
  {:status  404
   :headers {}
   :body    nil})

(defn- accepted [location body]
  {:status  202
   :headers {"Location" location}
   :body    body})

(def handle-show-version
  {:summary ""
   :handler (fn [req]
              (with-open [pom-reader (io/reader (io/resource "META-INF/maven/moogle-shortener/moogle-shortener/pom.properties"))]
                (let [pom (doto (Properties.)
                            (.load pom-reader))]
                  {:status  200
                   :headers {"Content-Type" "application/json"}
                   :body    (json/generate-string pom)})))})

(def handle-shortener
  {:summary ""
   :handler (fn [req]
              {:status  200
               :headers {"Content-Type" "application/json"}
               :body    (json/generate-string (select-keys req
                                                           [:server-port :server-name :remote-addr :uri :character-encoding
                                                            :path-params
                                                            :query-string
                                                            :request-method
                                                            :scheme
                                                            :content-type
                                                            :content-length
                                                            :headers
                                                            :protocol
                                                            :ssl-client-cert]))})})



(def handle-main-page
  {:summary "Exibe a versão da aplicação em execução"
   :handler core/main-page})

(def handle-swagger
  {:no-doc  true
   :swagger {:info {:title       "Moogle URL Shortner"
                    :description ""}
             :tags [{:name "api", :description "API Description"}]}
   :handler (swagger/create-swagger-handler)})

(def ^:private router-opts
  {:exception pretty/exception
   :data      {:coercion (reitit.coercion.malli/create
                           {;; set of keys to include in error messages
                            :error-keys       #{#_:type :coercion :in :schema :value :errors :humanized #_:transformed}
                            ;; schema identity function (default: close all map schemas)
                            :compile          mu/closed-schema
                            ;; strip-extra-keys (effects only predefined transformers)
                            :strip-extra-keys true
                            ;; add/set default values
                            :default-values   true
                            ;; malli options
                            :options          nil})
               :muuntaja m/instance}})

(def app
  (ring/ring-handler
    (ring/router
      [["/" {:get handle-main-page}]
       ["/api/shortener" {:post handle-shortener}]
       ["/api/version" {:get handle-show-version}]
       ["/swagger.json" {:middleware [swagger/swagger-feature
                                      ;; query-params & form-params
                                      parameters/parameters-middleware
                                      ;; content-negotiation
                                      muuntaja/format-negotiate-middleware
                                      ;; encoding response body
                                      muuntaja/format-response-middleware
                                      ;; exception handling (customizado)
                                      ;exception-middleware
                                      ;exception/exception-middleware
                                      ;; decoding request body
                                      muuntaja/format-request-middleware
                                      ;; coercing response bodys
                                      coercion/coerce-response-middleware
                                      ;; coercing request parameters
                                      coercion/coerce-request-middleware
                                      ;; multipart
                                      multipart/multipart-middleware]
                         :get        handle-swagger}]

       ["/api" {:middleware [;;custom
                             wrap-log-request-time
                             ;; query-params & form-params
                             parameters/parameters-middleware
                             ;wrap-fetch-parameters
                             ;; content-negotiation
                             muuntaja/format-negotiate-middleware
                             ;; encoding response body
                             muuntaja/format-response-middleware
                             ;; custom
                             wrap-snakecase-keys
                             ;; custom
                             exception-middleware
                             ;; decoding request body
                             muuntaja/format-request-middleware
                             ;; coercing response bodys
                             coercion/coerce-response-middleware
                             ;; coercing request parameters
                             coercion/coerce-request-middleware
                             ;; multipart
                             multipart/multipart-middleware
                             ;; custom
                             wrap-kebab-keys
                             #_wrap-db]}

        #_["/show" {:swagger {:tags ["api"]}
                    :post handle-postman-send}]]]

      router-opts)
    (ring/routes
      (swagger-ui/create-swagger-ui-handler
        {:path   "/api-docs"
         :config {:validatorUrl     nil
                  :operationsSorter "alpha"}})
      (ring/create-default-handler))))

(comment

  (prn))
