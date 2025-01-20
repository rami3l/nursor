(ns nursor.diff
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str])
  (:import [com.github.difflib DiffUtils UnifiedDiffUtils]))

(s/def ::diff-file (s/keys :req-un [::path ::text]))
(s/def ::path string?)
(s/def ::text string?)
(defrecord DiffFile [path text])

(s/fdef udiff
  :args (s/cat :old-file ::diff-file :new-file ::diff-file)
  :ret string?)
(defn udiff [old-file new-file]
  (let [{p0 :path s0 :text} old-file
        {p1 :path s1 :text} new-file
        [ls0 ls1] (map str/split-lines [s0 s1])
        diff (DiffUtils/diff ls0 ls1)
        udiff (UnifiedDiffUtils/generateUnifiedDiff p0 p1 ls0 diff 0)]
    (str/join "\n" udiff)))

(s/fdef udiff-apply-lns
  :args (s/cat :s-lns (s/coll-of string?)
               :udiff-lns (s/coll-of string?)
               :max-fuzz int?)
  :ret string?)
(defn- udiff-apply-lns [s-lns udiff-lns max-fuzz]
  (-> udiff-lns
      UnifiedDiffUtils/parseUnifiedDiff
      (.applyFuzzy s-lns max-fuzz)
      (->> (str/join "\n"))))

(s/fdef udiff-apply
  :args (s/cat :s string? :udiff string? :kwargs (s/keys* :opt-un [::strict]))
  :ret string?)
(s/def ::strict boolean?)
(defn udiff-apply [s udiff & {:keys [strict] :or {strict false}}]
  (let [[s-lns udiff-lns] (map str/split-lines [s udiff])]
    (udiff-apply-lns s-lns udiff-lns (if strict 0 5))))
