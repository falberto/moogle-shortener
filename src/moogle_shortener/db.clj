(ns moogle-shortener.db
  (:require [moogle-shortener.config :as config]
            [next.jdbc.sql :as sql]
            [next.jdbc.result-set :as rs]
            [clojure.tools.logging :as log]))

(def default-row-mapper
  (rs/as-maps-adapter
    rs/as-unqualified-kebab-maps
    rs/clob-column-reader))

(defn insert!
  "Wrapper da funcao next.jdbc/insert! com trace das instruções"
  ([db table key-map]
   (insert! db table key-map {}))
  ([db table key-map opts]
   (log/trace (str "table: [" table "] - key-map: [" key-map "] - opts: [" opts "]"))
   (sql/insert! db
                table
                key-map
                (merge {:builder-fn default-row-mapper} opts))))

(defn query
  "Wrapper of function next.jdbc.sql/query with debug log for all DML."
  ([db sql-params]
   (query db sql-params {}))
  ([db sql-params opts]
   (log/trace (str "sql: [" sql-params "] - params: [" opts "]"))
   (sql/query db
              sql-params
              (merge {:builder-fn default-row-mapper} opts))))
