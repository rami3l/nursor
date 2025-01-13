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
    Some(self.buf[self.head])
  }
}
