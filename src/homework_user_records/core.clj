(ns homework-user-records.core
  (:require [homework-user-records.step1 :as step1]))

(defn -main []
  (println "Hello World"))

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
  (doseq [rec records]
    (println rec)))

(defn step1 [base-filename]
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

(comment
  (step1 "resources/sample"))
