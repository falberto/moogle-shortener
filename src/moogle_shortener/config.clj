(ns moogle-shortener.config
  (:require [environ.core :refer [env]]))

(def db
  {:classname "org.sqlite.JDBC"
   :dbtype    "sqlite"
   :dbname    "url_shortened.db"
   :subname   ":memory:"})

(def nano
  {:size (or (env :nano-size) 8)})