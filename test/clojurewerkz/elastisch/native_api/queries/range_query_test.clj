(ns clojurewerkz.elastisch.native-api.queries.range-query-test
  (:require [clojurewerkz.elastisch.native.document :as doc]
            [clojurewerkz.elastisch.native.index    :as idx]
            [clojurewerkz.elastisch.query           :as q]
            [clojurewerkz.elastisch.fixtures        :as fx]
            [clojurewerkz.elastisch.test.helpers    :as th])
  (:use clojure.test clojurewerkz.elastisch.native.response))

(th/maybe-connect-native-client)
(use-fixtures :each fx/reset-indexes fx/prepopulate-people-index fx/prepopulate-tweets-index)

;;
;; range query
;;

(deftest ^{:query true :native true} test-range-query-over-numerical-field
  (let [index-name   "people"
        mapping-type "person"
        response     (doc/search index-name mapping-type :query (q/range :age :from 27 :to 29))
        hits         (hits-from response)]
    (is (any-hits? response))
    (is (= 2 (total-hits response)))
    (is (= #{"2" "4"} (set (map :_id hits))))))


(let [index-name   "tweets"
      mapping-type "tweet"]
  (deftest ^{:query true :native true} test-range-query-over-string-field
    (let [response (doc/search index-name mapping-type :query (q/range :username :from "c" :to "j"))
          ids      (ids-from response)]
      (is (= 2 (total-hits response)))
      (is (= #{"1" "2"} ids))))

  (deftest ^{:query true :native true} test-range-query-over-date-time-field-with-from
    (let [response (doc/search index-name mapping-type :query (q/range :timestamp :from "20120801T160000+0100"))
          ids      (ids-from response)]
      (is (= 2 (total-hits response)))
      (is (= #{"1" "2"} ids))))

  (deftest ^{:query true :native true} test-range-query-over-date-time-field-with-from-and-to
     (let [response (doc/search index-name mapping-type :query (q/range :timestamp :from "20120801T160000+0100" :to "20120801T180000+0100"))
           ids      (ids-from response)]
       (is (= 1 (total-hits response)))
       (is (= #{"2"} ids)))))