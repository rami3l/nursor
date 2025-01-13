(ns nursor.diff
  (:require [clojure.string :as str])
  (:import [com.github.difflib DiffUtils UnifiedDiffUtils]))

(defrecord DiffFile [path text])

(defn udiff [old-file new-file]
  (let [{p0 :path s0 :text} old-file
        {p1 :path s1 :text} new-file
        [ls0 ls1] (map str/split-lines [s0 s1])
        diff (DiffUtils/diff ls0 ls1)
        udiff (UnifiedDiffUtils/generateUnifiedDiff p0 p1 ls0 diff 0)]
    (str/join "\n" udiff)))

(defn- udiff-apply-lns [s-lns udiff-lns max-fuzz]
  (-> udiff-lns
      UnifiedDiffUtils/parseUnifiedDiff
      (.applyFuzzy s-lns max-fuzz)
      (->> (str/join "\n"))))

(defn udiff-apply [s udiff & {:keys [strict] :or {strict false}}]
  (let [[s-lns udiff-lns] (map str/split-lines [s udiff])]
    (udiff-apply-lns s-lns udiff-lns (if strict 0 5))))
