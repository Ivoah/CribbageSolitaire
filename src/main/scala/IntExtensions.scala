package net.ivoah.cribbagesolitaire

extension(n: Int) {
  def ! : Int = {
    if (n == 0) 1 else n*(n - 1).!
  }
}
