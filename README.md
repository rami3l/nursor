# nursor

This is an experiment to evaluate the feasibility of using a public LLM model to predict code changes
in the form of unified diffs.

````console
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
==== LLM ====
Based on the changes made by the programmer, it seems they are refactoring the `sqrt_iter` function to use the newly introduced helper functions `is_good_enough` and `improve_guess`. The next logical change would be to update the `sqrt_iter` function to utilize these helper funct
ions.

Here is the predicted unified diff representing the next change:

```diff
--- middle/newton.mbt
+++ after/newton.mbt
@@ -13,10 +13,8 @@
 fn sqrt_iter(guess : Float, x : Float) -> Float {
   loop guess, x {
     guess, x => {
-      guard abs(guess * guess - x) < 0.001 else {
-        continue (guess + x / guess) / 2.0, x
-      }
-      guess
+      guard is_good_enough(guess, x) else { continue improve_guess(guess, x), x }
+      return guess
     }
   }
 }
```

This change replaces the inline logic in `sqrt_iter` with calls to `is_good_enough` and `improve_guess`, making the code more modular and easier to read. The `return` keyword is also added to make it explicit that `guess` is the final value to be returned when the loop exits.
"Elapsed time: 7376.45975 msecs"
==== Prediction ====
///|
pub fn sqrt(x : Float) -> Float {
  sqrt_iter(1.0, x)
}

///|
fn sqrt_iter(guess : Float, x : Float) -> Float {
  loop guess, x {
    guess, x => {
      guard is_good_enough(guess, x) else { continue improve_guess(guess, x), x }
      return guess
    }
  }
}

///|
fn abs(x : Float) -> Float {
  guard x >= 0 else { -x }
  x
}

///|
fn is_good_enough(guess : Float, x : Float) -> Bool {
  abs(guess * guess - x) < 0.001
}

///|
fn improve_guess(guess : Float, x : Float) -> Float {
  (guess + x / guess) / 2.0
}

````
