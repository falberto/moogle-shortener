(defproject moogle-shortener "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [mount "0.1.16"]
                 [environ "1.2.0"]
                 [org.clojure/tools.logging "1.1.0"]
                 [ch.qos.logback/logback-classic "1.2.5"]
                 [camel-snake-kebab "0.4.2"]
                 [ring/ring-jetty-adapter "1.9.3"]
                 [ring "1.9.4"]
                 [ring/ring-json "0.5.1"]
                 [metosin/reitit "0.5.15"]
                 [commons-validator "1.7"]
                 [hiccup "1.0.5"]
                 [nano-id "1.0.0"]
                 [cheshire "5.10.1"]
                 [borkdude/sci "0.2.6"]
                 [org.webjars.npm/bulma "0.9.2"]
                 [org.webjars.npm/material-icons "1.0.0"]]
  :main moogle-shortener.core
  ;:aot :all
  :profiles {:dev     {:source-paths ["dev"]
                       :dependencies [[org.clojure/test.check "1.1.0"]
                                      [ch.qos.logback/logback-classic "1.2.5"]]}}
  :omit-source true
  :plugins [[lein-environ "1.2.0"]
            [lein-ring "0.12.5"]]
  :repl-options {:init-ns moogle-shortener.core}
  :ring {:handler moogle-shortener.web/app
         :init    moogle-shortener.core/-main})

