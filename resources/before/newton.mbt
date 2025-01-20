///|
pub fn sqrt(x : Float) -> Float {
  sqrt_iter(1.0, x)
}

///|
fn sqrt_iter(guess : Float, x : Float) -> Float {
  loop guess, x {
    guess, x => {
      guard abs(guess * guess - x) < 0.001 else {
        continue (guess + x / guess) / 2.0, x
      }
      guess
    }
  }
}

///|
fn abs(x : Float) -> Float {
  guard x >= 0 else { -x }
  x
}
