(ns guestbook.routes.home
  (:require [compojure.core :refer :all]
            [guestbook.views.layout :as layout]))

(defn home []
  (layout/common [:h1 "Hello World of Clojure!"]
                  [:h1 (str"this is 'p' tag! " (+ 1 2)) ]))

(defroutes home-routes
  (GET "/" [] (home)))
