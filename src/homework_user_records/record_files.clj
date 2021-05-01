(ns homework-user-records.record-files
  (:import [java.util.regex Pattern]
           [java.time LocalDate]
           [java.time.format DateTimeFormatter])
  (:require [clojure.java.io :as io]
            [clojure.string]))

(def ^:private date-formatter (DateTimeFormatter/ofPattern "M/d/yyyy"))

(def ^:private field-order [:last-name :first-name :email :favorite-color :birthdate])
(def ^:private header-names-record {:last-name "LastName"
                                    :first-name "FirstName"
                                    :email "Email"
                                    :favorite-color "FavoriteColor"
                                    :birthdate "DateOfBirth"})

(def ^:private header-name->field (zipmap (vals header-names-record) (keys header-names-record)))

(def ^:private record->line-data
  (apply juxt field-order))

(defn- stringify-birthdate [record]
  (update record :birthdate #(.format date-formatter %)))

(defn- ->line-maker [delimiter]
  (fn record->line [record]
    (let [line-data (record->line-data (stringify-birthdate record))]
      (str (clojure.string/join delimiter line-data) "\n"))))

(defn write-records! [output-stream {:keys [delimiter records]}]
  (with-open [writer (io/writer output-stream)]
    (let [line-maker (->line-maker delimiter)]
      (doseq [record records]
        (.write writer (line-maker record))))))

(defn parse-birthdate [record]
  (update record :birthdate #(LocalDate/parse % date-formatter)))

(defn read-records [input-stream {:keys [delimiter]}]
  ;;quoting to support pipe-separation
  (let [split-pattern (re-pattern (Pattern/quote delimiter))]
    (with-open [reader (io/reader input-stream)]
      (->> (line-seq reader)
           (map #(clojure.string/split % split-pattern))
           (map zipmap (repeat field-order))
           (map parse-birthdate)
           doall))))

(comment
  (LocalDate/parse (.format date-formatter (LocalDate/of 1999 1 1)) date-formatter)
  (record->line-data header-names-record))
