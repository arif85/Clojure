(ns guestbook.routes.auth
  (:require [compojure.core :refer [defroutes GET POST]]
            [guestbook.views.layout :as layout]
            [hiccup.form :refer
             [form-to label text-field password-field submit-button]]
            [noir.response :refer [redirect]]))



(defn control [field name text]
  (list (label name text)
        (field name)
        [:br]))

(defn registration-page []
  #_(layout/common
      (form-to [:post "/register"]
               (label "id" "screen name")
               (text-field "id")
               [:br]
               (label "pass" "password")
               (password-field "pass")
               [:br]
               (label "pass1" "password1")
               (password-field "pass1")
               [:br]
               (submit-button "create account")))
  (layout/common
    (form-to [:post "/register"]
             (control text-field :id "Screen name ")
             (control password-field :pass "Password ")
             (control password-field :pass1 "Retype Password ")
             (submit-button "Create Account"))))

(defroutes auth-routes
           (GET "/register" [] (registration-page))
           (POST "/register" [id pass pass1]
             (if (= pass pass1)
               (redirect "/")
               (registration-page))))
