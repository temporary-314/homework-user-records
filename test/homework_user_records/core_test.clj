(ns homework-user-records.core-test
  (:require  [clojure.test :refer [deftest are]]
             [homework-user-records.core :as records-core]))

(def ^:private example-data
  [{:word "aardvark" :num 5 :id 0}
   {:word "banana" :num 3 :id 1}
   {:word "banana" :num 2 :id 2}
   {:word "cat" :num 1 :id 3}])

(deftest test-vector-directional-comparator
  (are [key-fn dirs final-ids]
       (let [sorted (sort-by key-fn
                             (#'records-core/vec-directional-comparator dirs)
                             example-data)
             actual-ids (map :id sorted)]
         (= actual-ids final-ids))
    (juxt :word :num) [:asc :asc] [0 2 1 3]
    (juxt :word :num) [:asc :desc] [0 1 2 3]
    (juxt :word :num) [:desc :asc] [3 2 1 0]
    (juxt :word :num) [:desc :desc] [3 1 2 0]
    (juxt :num) [:desc] [0 1 2 3]
    (juxt :num :word) [:asc :desc] [3 2 1 0]
    (juxt :num :word) [:desc :desc] [0 1 2 3]))

(comment
  (sort-by (juxt :word :num) (#'records-core/vec-directional-comparator [:asc :desc])
           example-data))
