(ns guestbook.models.db
  (:require [clojure.java.jdbc :as sql])
  (:import java.sql.DriverManager))

(def db {:class-name "org.sqlite.JDBC",
         :subprotocol "sqlite",
         :subname "db.sq3"})

(defn create-guestbook-table []
  (sql/with-connection
    db
    (sql/create-table
      :guestbook
      [:id "INTEGER PRIMARY KEY AUTOINCREMENT"]
      [:timestamp "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"]
      [:name "TEXT"]
      [:message "TEXT"])
    (sql/do-commands "CREATE INDEX timestamp_index ON guestbook (timestamp)")))
;(create-guestbook-table)

(defn read-guests []
  (sql/with-connection
    db
    (sql/with-query-results res
                            ["SELECT * FROM guestbook ORDER BY timestamp DESC"]
                            (doall res))))

(defn save-message [name message]
      (sql/with-connection
        db
        (sql/insert-values
          :guestbook
          [:name :message :timestamp]
          [name message (new java.util.Date)])))
;(ns-unmap 'user 'save-message)

;(use 'guestbook.models.db)

(save-message "Bob12" "Hello")

;(read-guests)
