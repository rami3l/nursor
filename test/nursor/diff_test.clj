(ns nursor.diff-test
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest testing is]]
            [nursor.diff :refer [resource-path->DiffFile udiff-apply]]))

(def ^:private test-src (:text (resource-path->DiffFile "before.mbt")))

(defn- replace-ln [s idx ln]
  (as-> s $ (str/split-lines $) (assoc $ (dec idx) ln) (str/join "\n" $)))

(deftest udiff-apply-test

  (testing "can apply udiff without rectifying start line"
    (is (= (replace-ln test-src 13 "    Some(self.buf[self.tail])")
           (as-> "--- before.mbt
+++ after.mbt
@@ -13 +122 @@
-    Some(self.buf[self.head])
+    Some(self.buf[self.tail])" $
             (udiff-apply test-src $ :strict true)))))

  (testing "can apply udiff by rectifying start line"
    (is (= (replace-ln test-src 13 "    Some(self.buf[self.tail])")
           (->> "--- before.mbt
+++ after.mbt
@@ -12,4 +122 @@
-    Some(self.buf[self.head])
+    Some(self.buf[self.tail])"
                (udiff-apply test-src))))))
