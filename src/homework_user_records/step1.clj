(ns homework-user-records.step1
  (:require [homework-user-records.record-files :as record-files]
            [clojure.java.io :as io]
            [homework-user-records.record-gen :as record-gen]))

(def ^:private extension->delimiter
  {"psv" " | "
   "csv" ", "
   ;;Space-separated values, not to be confused for the real separator-separated-values filetype
   "ssv" " "})

(defn- filename->extension [filename]
  (second (re-find #"\.([^.]+)$" filename)))

(defn- read-file [filename]
  (let [extension (filename->extension filename)
        delimiter (extension->delimiter extension)]
    (if delimiter
      (record-files/read-records filename {:delimiter delimiter})
      (throw (ex-info "Unsupported file extension" {:filename filename})))))

(defn gen-samples!
  ([] (gen-samples! {}))
  ([{:keys [num-samples] :or {num-samples 5}}]
   (doseq [[filename delimiter] (map (fn [[extension delim]]
                                       [(str "sample." extension) delim])
                                     extension->delimiter)]
     (let [records (repeatedly num-samples record-gen/gen-record)
           full-filename (str "resources/" filename)]
       (io/make-parents full-filename)
       (record-files/write-records! full-filename
                                    {:delimiter delimiter
                                     :records records})))))

(defn- file-exists? [filename]
  (.exists (io/as-file filename)))

(defn read-all [base-filename]
  (->> (keys extension->delimiter)
       (map #(str base-filename "." %))
       (filter file-exists?)
       (mapcat read-file)))

(comment
  (gen-samples!)
  (filename->extension "resources/sample.ssv")
  (read-file "resources/sample.psv")
  (read-all "resources/sample"))
