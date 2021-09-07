(ns moogle-shortener.db
  (:require [moogle-shortener.config :as config]
            [next.jdbc.sql :as sql]
            [next.jdbc.result-set :as rs]
            [clojure.tools.logging :as log]))

(def default-row-mapper
  (rs/as-maps-adapter
    rs/as-unqualified-kebab-maps
    rs/clob-column-reader))

(defn query
  "Wrapper of function next.jdbc.sql/query with debug log for all DML."
  ([db sql-params]
   (query db sql-params {}))
  ([db sql-params opts]
   (log/debug (str "sql: [" sql-params "] - params: [" opts "]"))
   (sql/query db
              sql-params
              (merge {:builder-fn default-row-mapper} opts))))

(query config/db ["select current_timestamp"])