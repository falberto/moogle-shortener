(ns moogle-shortener.html
  (:require [hiccup.page :as page]))

(defonce header
         [:head
          [:title "Moogle Url Shortener"]
          [:meta {:name :viewport
                  :content "width=device-width, initial-scale=1"}]
          [:link {:href "https://cdn.jsdelivr.net/npm/bulma@0.9.3/css/bulma.min.css"
                  :rel :stylesheet}]
          [:link {:href "https://cdnjs.cloudflare.com/ajax/libs/jquery/3.3.1/jquery.min.js"}]])

(defn greet-page []
  (page/html5 {:lang :en}
              header
              [:body
               [:section {:class "section"}
                [:div {:class "container"}
                 [:h1 {:class "title"} "Moogle Shortener"]
                 [:p {:class "subtitle"} "Paste the URL to be shortened"]
                 [:form {:method "POST" :action "shortener"}
                  [:div {:class "field has-addons"}
                   [:div {:class "control is-expanded"}
                    [:input {:name "url" :pattern "https?://.+" :class "input is-large" :type "text"}]]
                   [:div {:class "control"}
                    [:input {:class "input is-large" :type "submit"}]]]]]]]))

(defn shortener [url]
  (page/html5 {:lang :en}
              header
              [:body
               [:section {:class "section "}
                [:div {:class "field has-addons"}
                 [:div {:class "control is-expanded"}
                  [:input {:class "input is-large" :type "text" :value url :read-only true}]]
                 [:div {:class "control"}
                  [:button {:class "button is-success is-large"} "Copy URL"]]]]]))
