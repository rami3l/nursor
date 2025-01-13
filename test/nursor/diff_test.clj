(ns nursor.diff-test
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is testing]]
            [nursor.diff :refer [udiff-apply]]))

(def ^:private test-src "///|
/// Return the front element from a deque, or `None` if it is empty.
///
/// # Example
/// ```
/// let dv = @deque.of([1, 2, 3, 4, 5])
/// assert_eq!(dv.front(), Some(1))
/// ```
pub fn T::front[A](self : T[A]) -> A? {
  if self.len == 0 {
    None
  } else {
    Some(self.buf[self.head])
  }
}
")

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
