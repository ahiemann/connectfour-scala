package model

case class MatchfieldModel[T](rows: Vector[Vector[T]]):
  def this(filling: T) =
    this(Vector.tabulate(6, 7) { (row, col) =>
      filling
    })

  val size: Int = rows.size

  def setToken(row: Int, col: Int, player: T): MatchfieldModel[T] =
    copy(rows.updated(row, rows(row).updated(col, player)))

  def cell(row:Int, col:Int): T = rows(row)(col)

  override def toString: String =
    val rowDelimiter = "+---+---+---+---+---+---+---+"
    val columnNrs = "  1   2   3   4   5   6   7"

    val rowStrings = for row <- rows.reverse 
      rowString = row.mkString(s"$rowDelimiter\n| ", " | " , " |")
    yield rowString

    rowStrings.mkString(s"", "\n", s"\n$rowDelimiter\n") + columnNrs