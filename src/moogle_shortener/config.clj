(ns moogle-shortener.config
  (:require [environ.core :refer [env]]))

#_(defn make-datasource []
    (hikari/make-datasource {:class-name "org.sqlite.SQLiteDataSource"
                             :jdbc-url            "url_shortened.db"
                             :validation-timeout  1000
                             :pool-name           (str "POOL-123")
                             :maximum-pool-size   30
                             :connection-init-sql "select CURRENT_TIMESTAMP"}))

(defn db []
  {:classname "org.sqlite.JDBC"
   :dbtype    "sqlite"
   :dbname    "url_shortened.db"
   :subname   ":memory:"})

(defonce datasource (delay (db)))

(def nano
  {:size (or (env :nano-size) 8)})