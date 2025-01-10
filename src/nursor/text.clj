(ns nursor.text
  (:require [clojure.string :as str]))

(defn extract-code-block [s]
  (->> s str/split-lines
       (drop-while #(not (re-matches #"^```.*$" %)))
       (drop 1)
       (take-while #(not (re-matches #"^```" %)))
       (str/join "\n")))
