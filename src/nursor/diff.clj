(ns nursor.diff
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import [com.github.difflib DiffUtils UnifiedDiffUtils]))

(defrecord DiffFile [path text])

(defn resource->DiffFile [path]
  (->> path (io/resource) slurp (DiffFile. path)))

(defn udiff [old-file new-file]
  (let [{p0 :path s0 :text} old-file
        {p1 :path s1 :text} new-file
        [ls0 ls1] (map str/split-lines [s0 s1])
        diff (. DiffUtils diff ls0 ls1)
        udiff (. UnifiedDiffUtils generateUnifiedDiff p0 p1 ls0 diff 0)]
    (str/join "\n" udiff)))

(defn apply-udiff [s udiff]
  (-> udiff
      str/split-lines
      (->> (. UnifiedDiffUtils parseUnifiedDiff))
      (. applyTo (str/split-lines s))
      (->> (str/join "\n"))))
