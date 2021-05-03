(ns homework-user-records.core
  (:import [java.io StringWriter])
  (:require [homework-user-records.step1 :as step1]
            [homework-user-records.step2 :as step2]
            [homework-user-records.record-files :as record-files]))

(defn- vec-directional-comparator [directions]
  (fn comparator [vec-a vec-b]
    (->> (map (fn swap-compare [dir a b]
                (if (#{:desc} dir)
                  (compare b a)
                  (compare a b)))
              directions vec-a vec-b)
         (remove zero?)
         first)))

(defn- print-records [records]
  (let [s-out (StringWriter.)]
    (record-files/write-records! s-out {:delimiter ", "
                                        :records records})
    (println (.toString s-out))))

(defn step1 [{:keys [base-filename] :or {base-filename "resources/sample"}}]
  (let [all-records (step1/read-all base-filename)]
    (println "=============Output 1=============")
    (print-records (sort-by (juxt :email :last-name)
                            (vec-directional-comparator [:desc :asc])
                            all-records))
    (println "=============Output 2=============")
    (print-records (sort-by :birthdate all-records))
    (println "=============Output 3=============")
    (print-records (sort-by :last-name #(compare %2 %1)
                            all-records))))

(defn- nillable-parse-int [x]
  (when x
    (Integer/parseInt x)))

(defn -main [& [base-filename port]]
  (step2/run-server base-filename (nillable-parse-int port)))

(comment
  (step1 "resources/sample"))
