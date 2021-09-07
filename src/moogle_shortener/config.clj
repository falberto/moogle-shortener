(ns moogle-shortener.config
  (:require [environ.core :refer [env]]))

(def nano
  {:size (or (env :nano-size) 8)})