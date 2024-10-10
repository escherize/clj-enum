(ns com.escherize.clj-enum-test
  (:require
   [clojure.string :as str]
   [clojure.test :refer :all]
   [com.escherize.clj-enum :refer :all]))

;; Given a multi schema:
(def Pets
  [:multi {:dispatch :type}
   [:dog [:map [:good-dog? :boolean]]]
   [:cat [:map [:temperment [:enum :nice :mean]]]]])

(deftest make-a-function-that-can-dispatch-on-the-keys
  (let [pet-noise
        (match-maker Pets
                     :dog (fn [{:keys [good-dog?]}] (if good-dog? "good dog." "bad dog."))
                     :cat (fn [{:keys [temperment]}] (str "kitty is " (name temperment))))]
    (is (=
          ["good dog." "bad dog."
           "kitty is nice" "kitty is mean"]
          (mapv pet-noise
                [{:type :dog :good-dog? true}
                 {:type :dog :good-dog? false}
                 {:type :cat :temperment :nice}
                 {:type :cat :temperment :mean}])))
    ;; updated schema with a new branch
    (let [PetsWithBird [:multi {:dispatch :type}
                        [:dog [:map [:good-dog? :boolean]]]
                        [:cat [:map [:temperment [:enum :nice :mean]]]]
                        [:bird [:map]]]]
      (is (= [(str/join "\n"
                        ["Error: mismatched branches in match-maker."
                         "Expected: [:dog :cat :bird]"
                         "Received: [:dog :cat]"])
              {:expected [:dog :cat :bird], :found [:dog :cat]}]
             ;; try to make pet-noise again:
             (try (match-maker PetsWithBird
                    :dog (fn [{:keys [good-dog?]}] (if good-dog? "good dog." "bad dog."))
                    :cat (fn [{:keys [temperment]}] (str "kitty is " (name temperment))))
                  (catch Exception e [(ex-message e) (ex-data e)])))))))
