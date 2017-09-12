(ns guestbook.routes.auth
  (:require [compojure.core :refer [defroutes GET POST]]
            [guestbook.views.layout :as layout]
            [hiccup.form :refer
             [form-to label text-field password-field submit-button]]
            [noir.response :refer [redirect]]
            [noir.session :as session]
            [noir.validation :refer
             [rule errors? has-value? on-error]]
            [noir.util.crypt :as crypt]
            [guestbook.models.db :as db]))


(defn format-error [[error]]
  [:p.error error])

(defn control [field name text]
  (list (on-error name format-error)
        (label name text)
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

(defn login-page [& [error]]
  (layout/common
    ;(if error [:div.error "Login error: " error])
    (form-to [:post "/login"]
             (control text-field :id "Screen Name")
             (control password-field :pass "Password")
             (submit-button "Login"))))

(defn handle-registration [id pass pass1]
  (let [user (db/get-user id)]
    (rule (has-value? id)
          [:id "Username field is empty"])
    (rule (= user (:id user))
          [:id "User exists"])
    (rule (has-value? pass)
          [:pass "password field is empty"])
    (rule (has-value? pass1)
          [:pass1 "please confirm your password"])
    (rule (= pass pass1)
          [:pass "password was not typed correctly"])
    (if (errors? :id :pass)
      (registration-page)
      (do
        (db/add-user-record {:id id :pass (crypt/encrypt pass)})
        (redirect "/login")))
    )
  )

(defn handle-login [id pass]
  (let [user (db/get-user id)]
    (rule (has-value? id)
          [:id "screen name is required"])
    (rule (has-value? pass)
          [:pass "password is required"])
    (rule (and user (crypt/compare pass (:pass user)))
          [:pass "invalid password"])
    (if (errors? :id :pass)
      (login-page)
      (do
        (session/put! :user id)
        (redirect "/")))))

(defn logout []
  (layout/common
    (form-to [:post "/logout"]
             (submit-button "Logout"))))

(defroutes auth-routes
           (GET "/register" [] (registration-page))
           (POST "/register" [id pass pass1]
             ;(if (= pass pass1)
             ;(redirect "/")
             ; (registration-page))
             (handle-registration id pass pass1))
           (GET "/login" [] (login-page))
           (POST "/login" [id pass]
             (handle-login id pass)
             ;(session/put! :user id)
             ;(redirect "/")
             )
           (GET "/logout" [] (logout))
           (POST "/logout" []
             (session/clear!)
             (redirect "/login"))
           )
