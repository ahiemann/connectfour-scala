package model

case class MatchfieldModel[T](rows: Vector[Vector[T]]) {
  def this(filling: T) =
    this(Vector.tabulate(6, 7) { (row, col) =>
      filling
    })

  val size: Int = rows.size

  def setToken(row: Int, col: Int, player: T): MatchfieldModel[T] =
    copy(rows.updated(row, rows(row).updated(col, player)))
}
