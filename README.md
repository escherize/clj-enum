# com.escherize/clj-enum

ADTs, Tagged Unions, Sum Types, or Rust-like enums, as a clojure function

## Usage

``` clojure
;; Given a multi schema:
(def PetSchema
  [:multi {:dispatch :type}
   [:dog [:map [:good-dog? :boolean]]]
   [:cat [:map [:temperment [:enum :nice :mean]]]]])

;; make a function that can dispatch on the keys
(def pet-noise
  (match-maker PetSchema
               :dog (fn [{:keys [good-dog?]}] (if good-dog? "good dog." "bad dog."))
               :cat (fn [{:keys [temperment]}] (str "kitty is " (name temperment)))))

;; call the function
(mapv pet-noise
      [{:type :dog :good-dog? true}
       {:type :dog :good-dog? false}
       {:type :cat :temperment :nice}
       {:type :cat :temperment :mean}])
;; => ["good dog." "bad dog." "kitty is nice" "kitty is mean"]

;; update the schema with a new branch
(def PetSchema
  [:multi {:dispatch :type}
   [:dog [:map [:good-dog? :boolean]]]
   [:cat [:map [:temperment [:enum :nice :mean]]]]
   [:bird [:map]]])

;; try to make pet-noise again:
(try (def pet-noise
       (match-maker PetSchema
                    :dog (fn [{:keys [good-dog?]}] (if good-dog? "good dog." "bad dog."))
                    :cat (fn [{:keys [temperment]}] (str "kitty is " (name temperment)))))
     (catch Exception e [(ex-message e) (ex-data e)]))
;; => ["Missing Branches.
;;      Expected: [:dog :cat :bird]
;;      Received: [:dog :cat]"
;;      {:exptected [:dog :cat :bird], :found [:dog :cat]}]

```
