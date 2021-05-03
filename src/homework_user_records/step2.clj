(ns homework-user-records.step2
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [homework-user-records.step1 :as step1]
            [homework-user-records.record-files :as record-files]))

(defonce ^:private records (atom []))

(def ^:private base-content-type "text/plain+")

(def ^:private augment->delimiter
  {"csv" ", "
   "psv" " | "
   "ssv" " "})

(def ^:private content-type->delimiter
  (into {}
        (map (fn [[augment delimiter]]
               [(str base-content-type augment)
                delimiter])
             augment->delimiter)))

(defn- read-records [body content-type]
  (record-files/read-records body
                             {:delimiter (content-type->delimiter content-type)}))

(def ^:private app
  (api
   {:swagger {:ui "/api-docs"
              :spec "/swagger.json"
              :data {:info {:title "Homework User Records"
                            :description "Simple API for Step 2"}}}}
   (context "/" []
     (GET "/" []
       :no-doc true
       (moved-permanently "/api-docs"))
     (context "/records" []
       (POST "/" {body :body
                  content-type :content-type}
         :swagger {:consumes (keys content-type->delimiter)
                   :parameters {:body String}}
         ;; The doc said to support single records,
         ;; but supporting N was more featureful and easier
         (let [new-records (read-records body content-type)]
           (swap! records concat new-records)
           (ok {:new-records new-records})))
       ;; Prompt didn't include direction of ordering and didn't match the step1 prompts
       (GET "/records/email" []
         (ok {:records (sort-by :email @records)}))
       (GET "/records/birthdate" []
         (ok {:records (sort-by :birthdate @records)}))
       (GET "/records/name" []
         (ok {:records (sort-by (juxt :last-name :first-name)
                                @records)}))))))

(defn- load-records! [& [base-filename]]
  (reset! records (step1/read-all (or base-filename "resources/sample"))))

(defonce ^:private server (atom nil))

(defn- dev-start []
  (swap! server
         (fn start-if-nil [curr]
           (or curr
               (jetty/run-jetty app
                                {:join? false
                                 :port 8080})))))

(defn- dev-stop []
  (swap! server
         (fn stop-if-present [curr]
           (when curr
             (.stop curr))
           nil)))

(defn- dev-restart []
  (dev-stop)
  (dev-start))

(defn run-server [base-filename port]
  (load-records! base-filename)
  (jetty/run-jetty app {:join? true
                        :port (or port 8080)}))

(comment
  (load-records!)
  (dev-restart))
