(ns nursor.text
  (:require [clojure.string :as str]))

(defn extract-code-block [s]
  (-> s (str/split #"```.*\n" 2) second (str/split #"```" 2) first))
