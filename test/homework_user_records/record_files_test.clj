(ns homework-user-records.record-files-test
  (:import [java.time LocalDate]
           [java.io PipedOutputStream PipedInputStream StringWriter])
  (:require [homework-user-records.record-files :as record-files]
            [homework-user-records.record-gen :as record-gen]
            [clojure.test :refer [deftest is are testing]]))

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
  (testing "No Header writing"
    (are [delimiter example-str]
         (=  example-str
             (write-to-string {:delimiter delimiter
                               :records [example-record]}))
      "," "Smith,John,jsmith@gmail.com,Green,1/1/1999\n"
      " " "Smith John jsmith@gmail.com Green 1/1/1999\n"
      "|" "Smith|John|jsmith@gmail.com|Green|1/1/1999\n")))

(defn- round-trip [{:keys [delimiter records header?] :as args}]
  (let [out-stream (PipedOutputStream.)
        in-stream (PipedInputStream. out-stream)]
    (record-files/write-records! out-stream args)
    (record-files/read-records in-stream (select-keys args [:delimiter :header?]))))

(deftest read-write-symmetric []
  (testing "Example Record"
    (are [delimiter]
         (= [example-record] (round-trip {:delimiter delimiter
                                          :records [example-record]}))
      "," "|" " "))
  (testing "Generated records"
    (doseq [_ (range 100)]
      (let [records (repeatedly (rand-int 10) record-gen/gen-record)]
        (is (= records (round-trip {:delimiter ","
                                    :records records})))))))

(comment
  (write-to-string {:delimiter ","
                    :records [example-record]})
  (round-trip {:delimiter ","
               :records [example-record]})
  (doseq [_ (range 10)]
    (let [records (repeatedly (rand-int 5) record-gen/gen-record)]
      (is (= records (round-trip {:delimiter ","
                                  :records records}))))))
