(ns homework-user-records.record-gen
  (:import [java.time LocalDate]
           [java.time.temporal TemporalAdjusters]))

(def ^:private first-names ["Bob" "Sally" "Alice" "John"])

(def ^:private last-names ["Smith" "Doe" "Bean" "Jones"])

(def ^:private email-domains ["gmail.com" "hotmail.com" "live.com" "yahoo.com"])

(def ^:private colors ["Red" "Blue" "Green" "Yellow" "Black" "White"])

(def ^:private phonemes ["mo" "th" "ca" "da" "bo" "al" "do" "me" "mi" "su" "r"])

(defn- rand-bounded [min-inclusive max-exclusive]
  (+ min-inclusive (rand-int (- max-exclusive min-inclusive))))

(defn- gen-many [generator min-count max-count]
  (repeatedly (rand-bounded min-count max-count) generator))

(def ^:private gen-chained-str
  (comp #(apply str %) gen-many))

(defn- gen-email []
  (str (gen-chained-str #(rand-nth phonemes) 4 8)
       (gen-chained-str #(rand-int 10) 0 4)
       "@" (rand-nth email-domains)))

(defn- gen-birthdate []
  (let [base-date (LocalDate/of (rand-bounded 1950 2008) (rand-bounded 1 13) 1)
        last-of-month (.with base-date (TemporalAdjusters/lastDayOfMonth))
        chosen-date (rand-bounded 1 (.getDayOfMonth last-of-month))]
    (.withDayOfMonth base-date chosen-date)))

(defn gen-record []
  {:first-name (rand-nth first-names)
   :last-name (rand-nth last-names)
   :favorite-color (rand-nth colors)
   :email (gen-email)
   :birthdate (gen-birthdate)})

(comment
  (gen-record)
  (gen-many gen-record 1 10))
