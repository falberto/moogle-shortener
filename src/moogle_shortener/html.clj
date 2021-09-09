(ns moogle-shortener.html
  (:require [hiccup.page :as page]
            [clojure.java.io :as io]))

(defonce header
         [:head
          [:title "Moogle Url Shortener"]
          [:meta {:name    :viewport
                  :content "width=device-width, initial-scale=1"}]
          [:link {:href "https://cdn.jsdelivr.net/npm/bulma@0.9.3/css/bulma.min.css"
                  :rel  :stylesheet}]
          [:link {:href "https://cdn.jsdelivr.net/npm/bulma-social@2/css/all.min.css"
                  :rel  :stylesheet}]
          [:link {:href "https://cdn.jsdelivr.net/npm/@fortawesome/fontawesome-free@5/css/all.min.css"
                  :rel  :stylesheet}]
          [:link {:href "https://cdnjs.cloudflare.com/ajax/libs/jquery/3.3.1/jquery.min.js"}]])

(defn greet-page []
  (page/html5 {:lang :en}
              header
              [:body
               [:section {:class "hero-body"}
                [:div {:class "hero-body has-text-centered"}
                 [:h1 {:class "title"} "Moogle Shortener"]]
                [:div {:class "container box"}
                 [:div {:class "field has-addons"}
                  #_[:div {:class "columns is-mobile"}
                     [:div {:class "column is-expanded"}
                      [:figure {:class "image is-32x32 block"}
                       [:img {:src "assets/img/moogle.png"}]]]]]
                 [:p {:class "subtitle"} "Paste the URL to be shortened"]
                 [:form {:method "POST" :action "shortener"}
                  [:div {:class "field has-addons"}
                   [:div {:class "control is-expanded"}
                    [:input {:name "url" :pattern "https?://.+" :class "input is-large" :type "text"}]]
                   [:div {:class "control"}
                    [:input {:class "input is-large" :type "submit"}]]]]]]]))

(defn shortener [register]
  (page/html5 {:lang :en}
              header
              [:body
               [:section {:class "hero-body"}
                [:div {:class "hero-body has-text-centered"}
                 [:h1 {:class "title"} [:a {:href (:host register)} "Moogle Shortener"]]]
                [:div {:class "container box"}
                 [:h1 {:class "title is-4"} "Your shortened URL"]
                 [:h5 {:class "subtitle is-6"} "Copy the shortened link and share it in messages, texts, posts, websites and other locations"]
                 [:div {:class "field has-addons"}
                  [:div {:class "control is-expanded"}
                   [:input {:class "input is-large" :type "text" :value (:shortened register) :read-only true}]]
                  [:div {:class "control"}
                   [:button {:class "button is-success is-large"} "Copy URL"]]]
                 [:p {:class "subtitle is-6"} (str "Long URL: ") [:a {:href (:url register)} (:url register)]]
                 [:br]
                 [:br]
                 [:h2 {:class "title is-h2"} "Share URL"]
                 [:div {:class "buttons"}
                  [:a {:class "button is-medium is-facebook"}
                   [:span {:class "icon"}
                    [:i {:class "fab fa-facebook"}]]
                   [:span "Facebook"]]
                  [:a {:class "button is-medium is-twitter"}
                   [:span {:class "icon"}
                    [:i {:class "fab fa-twitter"}]]
                   [:span "Twitter"]]]
                 [:br]]]]))

