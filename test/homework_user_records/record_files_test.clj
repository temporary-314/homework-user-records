(ns homework-user-records.record-files-test
  (:import [java.time LocalDate]
           [java.io StringReader StringWriter])
  (:require [homework-user-records.record-files :as record-files]
            [homework-user-records.record-gen :as record-gen]
            [clojure.test :refer [deftest is are testing]]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as tc-prop]
            [clojure.test.check.generators :as tc-gen]))

(def example-record
  {:first-name "John"
   :last-name "Smith"
   :favorite-color "Green"
   :email "jsmith@gmail.com",
   :birthdate (LocalDate/of 1999 1 1)})

(defn- write-to-string [{:keys [delimiter records header?] :as args}]
  (let [s-out (StringWriter.)
        _ (record-files/write-records! s-out args)]
    (.toString s-out)))

(deftest write-results []
  (are [delimiter example-str]
       (=  example-str
           (write-to-string {:delimiter delimiter
                             :records [example-record]}))
    "," "Smith,John,jsmith@gmail.com,Green,1/1/1999\n"
    " " "Smith John jsmith@gmail.com Green 1/1/1999\n"
    "|" "Smith|John|jsmith@gmail.com|Green|1/1/1999\n"))

(deftest read-results []
  (are [delimiter example-str]
       (=  example-record
           (first (record-files/read-records (StringReader. example-str) {:delimiter delimiter})))
    "," "Smith,John,jsmith@gmail.com,Green,1/1/1999\n"
    " " "Smith John jsmith@gmail.com Green 1/1/1999\n"
    "|" "Smith|John|jsmith@gmail.com|Green|1/1/1999\n"))

(defn- round-trip [{:keys [delimiter records] :as args}]
  (let [in-stream (StringReader. (write-to-string args))]
    (record-files/read-records in-stream (select-keys args [:delimiter :header?]))))

(deftest read-write-symmetric []
  (testing "Example Record"
    (are [delimiter]
         (= [example-record] (round-trip {:delimiter delimiter
                                          :records [example-record]}))
      "," "|" " "))
  (testing "No Records"
    (is (= [] (round-trip {:delimiter ","
                           :records []})))))

(defspec round-trip-identity
  1000
  ;; Coercing a simple rand function into a test.check generator
  (tc-prop/for-all [records (tc-gen/vector (tc-gen/let [_ tc-gen/nat]
                                             (record-gen/gen-record)))
                    delimiter (tc-gen/elements ["," "|" " "])]
                   (= records (round-trip {:records records
                                           :delimiter delimiter}))))

(comment
  (tc-gen/sample)

  (write-to-string {:delimiter ","
                    :records [example-record]})
  (round-trip {:delimiter ","
               :records [example-record]})
  (doseq [_ (range 10)]
    (let [records (repeatedly (rand-int 5) record-gen/gen-record)]
      (is (= records (round-trip {:delimiter ","
                                  :records records}))))))
