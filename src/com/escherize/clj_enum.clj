(ns com.escherize.clj-enum
  (:require [malli.core :as m]
            [malli.error :as me]
            [malli.util :as mu]
            [malli.provider :as mp]
            [malli.generator :as mg]
            [malli.transform :as mt]))

(defmacro match-maker
  "Return a function that takes 2 things:
  a malli multi schema,
  pairs of multi schema dispatch values -> functions

  And returns a function, that when given a value that adheres to the
  multischema M, calls the dispatch function and calls the right value on it.
  "
  [schema & efp]
  `(let [_# (#'clojure.core/assert-args (even? (count '~efp)) "an even number of forms in evp vector")
         enum->fn-pairs# (mapv vec (partition 2 '~efp))
         children# (m/children ~schema)
         expected-branches# (mapv first children#)
         found-branches# (mapv first enum->fn-pairs#)
         dispatch-val->f# (into {} (eval enum->fn-pairs#))
         dispatch-fn# (-> ~schema m/properties :dispatch)]
     (when (not= expected-branches# found-branches#)
       (throw (ex-info (str "Error: mismatched branches in match-maker."
                            "\nExpected: " (pr-str expected-branches#)
                            "\nReceived: " (pr-str found-branches#))
                       {:expected expected-branches#
                        :found found-branches#})))
     (fn [value#]
       (let [branch# (dispatch-fn# value#)
             branch-fn# (get dispatch-val->f# branch#)]
         (branch-fn# value#)))))
