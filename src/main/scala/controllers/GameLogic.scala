package controllers

import model.{MatchfieldModel, PlayerModel}

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

class GameLogic () {
  /**
    * Check if a player has 4 chips in one row, column or diagonal
    *
    * @param matchfield
    * @param player
    * @return Option with the boolean if the game is won or None
    */
  def checkIfSomeoneWon(matchfield : MatchfieldModel[PlayerModel], player: PlayerModel) : Option[Boolean] = {
    val rows = matchfield.rows.map(_.map(_.name))
    val list = rows.map(_.map(_.contains(player.name))).toList

    def fourInColumn() : Boolean = {
      val resultList = list.transpose.map(x =>isSuccessively(x.toList,3)).filter(_ == true)
      if(resultList contains(true)) true else false
    }

    def fourInRow() : Boolean = {
      val resultList = list.map(x =>isSuccessively(x.toList,3)).filter(_ == true)
      if(resultList contains(true)) true else false
    }

    def fourDiagonalFromXTop() : Boolean = {
      val v1 = countDiagonal(4,0,2,"noPlayer", 0, 0, rows)
      val v2 = countDiagonal(5,0,1,"noPlayer", 0, 0, rows)
      val v3 = countDiagonal(6,0,0,"noPlayer", 0, 0, rows)
      val v4 = countDiagonal(6,1,0,"noPlayer", 0, 0, rows)
      val v5 = countDiagonal(5,2,0,"noPlayer", 0, 0, rows)
      val v6 = countDiagonal(4,3,0,"noPlayer", 0, 0, rows)
      if(v1>=3 || v2>=3 || v3>=3 || v4>=3 || v5>=3 || v6>=3) true else false
    }

    if(fourInColumn() || fourInRow() || fourDiagonalFromXTop()) return Some(true) else Some(false)
  }

  def isSuccessively(list: List[Boolean], endNum: Int): Boolean = {
    val consecutiveCount = numberOfSuccessivelySymbols(list)
    consecutiveCount >= endNum
  }

  @tailrec
  private def numberOfSuccessivelySymbols(list: List[Boolean], successivelyCount: Int = 0, maxCount: Int = 0): Int = {
    val currentSymbol = list.headOption
    val tail = if (list.nonEmpty) list.tail else Nil

    (currentSymbol, tail.headOption) match {
      case (Some(current), Some(next)) =>
          if (current == true) { // If current symbol is from selected player (true)
            if(current == next){
              val max = if (successivelyCount >= maxCount) successivelyCount + 1 else maxCount
              numberOfSuccessivelySymbols(tail, successivelyCount = successivelyCount + 1, maxCount = max) // If found next equal symbol
            } else{successivelyCount}
          }
          else {
            val max = if (successivelyCount > maxCount) successivelyCount else maxCount
            numberOfSuccessivelySymbols(tail, maxCount = max) // If row of equals symbols is broken
          }
      case _ => maxCount // If list is empty or last symbol
    }
  }

  def countDiagonal(maxLength: Int = 6, posX: Int = 0, posY: Int = 0, lastState: String = "", count: Int = 0, maxCount: Int = 0, rows: Vector[Vector[String]]): Int ={
    if(posX < maxLength){
      if((rows(posX)(posY) contains lastState) && !(rows(posX)(posY) contains "NoPlayer"))
        return countDiagonal(maxLength,posX+1, posY+1,rows(posX)(posY),count+1, maxCount+1,rows)
      else
        if(count > maxCount)
          return countDiagonal(maxLength,posX+1, posY+1,rows(posX)(posY),0, count,rows)
        else
          return countDiagonal(maxLength,posX+1, posY+1,rows(posX)(posY),0, maxCount,rows)
    }
    maxCount
  }

  def checkIfDraw(matchField:MatchfieldModel[PlayerModel]): Boolean = {
    val result = matchField.rows.forall(
      row => row.forall(player => { player.sign != '-' })
    )

    result
  }

  def getInitialMatchField() = {
    new MatchfieldModel[PlayerModel](new PlayerModel("NoPlayer", '-'))
  }

  def setChip(column : Int, matchField : MatchfieldModel[PlayerModel], player : PlayerModel):Either[String, Option[MatchfieldModel[PlayerModel]]] = {
    Try(getNextEmptyRow(column, matchField)) match {
      case Success(result) =>  result match {
        case Some(rowIndex) => Right(Some(matchField.setToken(rowIndex, column,player)))
        case None => Right(None)
      }
      case Failure(exception) => Left(exception.getMessage)
    }
  }

  def getNextEmptyRow(column: Int, matchField: MatchfieldModel[PlayerModel]): Option[Int] = {

    if (! (0 to 5 contains column) ) throw new Exception("Invalid column")

    @tailrec
    def getNextEmptyRow(rowIndex: Int, column:Int, matchField: MatchfieldModel[PlayerModel]) : Option[Int] = {
      if (rowIndex == matchField.rows.size) {
        None
      }
      else {
        val rows = matchField.rows
        if (matchField.rows(rowIndex)(column).sign != '-') {
          getNextEmptyRow(rowIndex + 1, column, matchField)
        } else {
          Some(rowIndex)
        }
      }
    }

    getNextEmptyRow(0, column, matchField)
  }
}
