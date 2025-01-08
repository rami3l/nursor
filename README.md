# nursor

This is an experiment to evaluate the feasibility of using a public LLM model to predict code changes
in the form of unified diffs.

``````console
> lein run
==== LLM ==== 
Based on the context, I predict the programmer is creating a `back()` method to complement the `front()` method for a deque data structure. 
The most natural next change would be to modify the implementation to access the last element instead of the first element.

Here's my predicted unified diff against the original file:

```diff
--- before.mbt
+++ after.mbt
@@ -9,7 +9,11 @@
-pub fn T::front[A](self : T[A]) -> A? {
+pub fn T::back[A](self : T[A]) -> A? {
   if self.len == 0 {
     None
   } else {
-    Some(self.buf[self.head])
+    let last_index = if self.head + self.len <= self.buf.len {
+      self.head + self.len - 1
+    } else {
+      (self.head + self.len - 1) % self.buf.len
+    }
+    Some(self.buf[last_index])
   }
 }
```

This prediction:
1. Changes the function name from `front` to `back` (matching the previous change)
2. Modifies the implementation to calculate the index of the last element, taking into account the circular nature of the deque's buffer
3. Returns the last element instead of the first element

The implementation handles both cases where the deque's elements wrap around the buffer and where they don't.
"Elapsed time: 9897.167584 msecs"
==== Prediction ====
///|
/// Return the front element from a deque, or `None` if it is empty.
///
/// # Example
/// ```
/// let dv = @deque.of([1, 2, 3, 4, 5])
/// assert_eq!(dv.front(), Some(1))
/// ```
pub fn T::back[A](self : T[A]) -> A? {
  if self.len == 0 {
    None
  } else {
    let last_index = if self.head + self.len <= self.buf.len {
      self.head + self.len - 1
    } else {
      (self.head + self.len - 1) % self.buf.len
    }
    Some(self.buf[last_index])
  }
}
``````
