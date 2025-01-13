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

(defn- udiff-apply-lns [s-lns udiff-lns]
  (-> udiff-lns
      UnifiedDiffUtils/parseUnifiedDiff
      (.applyTo s-lns)
      (->> (str/join "\n"))))

(defn- udiff-extract-src-hunk-start [hunk-header]
  (->> hunk-header (re-seq #"^@@[ ]+-(\d+)") first second Integer/parseInt))

(defn- relocate-hunk-leader [ln-pattern base s-lns]
  (->> (range 1 (count s-lns)) (mapcat #(vector (+ base %1) (- base %1)))
       (filter #(str/ends-with? ln-pattern (get s-lns %)))
       first))

(defn- udiff-lns-rectify-start [s-lns udiff-lns]
  (let [hunk-headers (keep-indexed #(when (re-find #"^@@" %2) (vector %1 %2)) udiff-lns)
        udiff-lns (transient udiff-lns)]
    (doseq [[i hunk-header] hunk-headers
            :let [udiff-hunk-leader (get udiff-lns (inc i))
                  src-hunk-start (udiff-extract-src-hunk-start hunk-header)
                  src-hunk-start (dec src-hunk-start)]
            :when (not (str/ends-with? udiff-hunk-leader (get s-lns src-hunk-start)))]
      (let [start (relocate-hunk-leader udiff-hunk-leader src-hunk-start s-lns)]
        (assoc! udiff-lns i (format "@@ -%d +1 @@" (inc start)))))
    (persistent! udiff-lns)))

(defn udiff-apply [s udiff & {:keys [strict] :or {strict false}}]
  (let [[s-lns udiff-lns] (map str/split-lines [s udiff])]
    (udiff-apply-lns s-lns (if strict udiff-lns
                             (udiff-lns-rectify-start s-lns udiff-lns)))))
