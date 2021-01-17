package ai.minimax

import akka.actor.Actor
import akka.util.Timeout

import scala.language.postfixOps
import model.{MatchfieldModel, PlayerModel}
import util.GameLogic

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

trait MiniMaxActor extends Actor{

  implicit val timeout: Timeout = Timeout(5 seconds)

  override def receive: Receive = {

    case requestMessage: RequestMessage =>
      val matchField = requestMessage.matchfield
      val aiPlayer = requestMessage.simulatedPlayer
      val otherPlayer = requestMessage.otherPlayer
      val depth = requestMessage.depth
      val columnNr = requestMessage.columnNr

      checkAndEvaluate(matchField, aiPlayer, otherPlayer, depth) match {
        case Some(score) => sender ! ResponseMessage(columnNr, score)

        case None =>
          val possibleColumns = GameLogic.getEmptyColumns(matchField)

          implicit val timeout: Timeout = Timeout(5 seconds)
          val futures = for {
            columnNr <- possibleColumns
            future = spawnNewActor(columnNr, matchField, aiPlayer, otherPlayer, depth - 1)
          } yield future

          val futureResults = for {
            future <- futures
            res = Await.result(future, timeout.duration)
          } yield res

          val bestOption = makeScoreBasedChoice(futureResults.asInstanceOf[List[ResponseMessage]])

          columnNr match {
            // not at the root node yet, overwrite columnNr of best score and send to next level
            case Some(_) => sender ! ResponseMessage(columnNr, bestOption.score)
            // we are at the root actor, simply forward
            case None => sender ! bestOption
          }
      }
  }

  def checkAndEvaluate(matchfield:MatchfieldModel[PlayerModel], aiPlayer:PlayerModel, otherPlayer:PlayerModel, depth:Int):Option[Double] = {
    if (depth == 0) Some(calculateScore(matchfield, aiPlayer))
    else if (GameLogic.checkIfSomeoneWon(matchfield, aiPlayer)) Some(50.0)
    else if (GameLogic.checkIfSomeoneWon(matchfield, otherPlayer)) Some(-50.0)
    else if (GameLogic.checkIfDraw(matchfield)) Some(0.0)
    else None
  }

  private def calculateScore(matchfield:MatchfieldModel[PlayerModel], aiPlayer:PlayerModel):Double = {

    // matrix with ratings for every cell in the matchfield
    val scoreMatrix: MatchfieldModel[Double] = MatchfieldModel[Double](Vector(
      Vector(0.03, 0.04, 0.05, 0.07, 0.05, 0.04, 0.03),
      Vector(0.04, 0.06, 0.08, 0.10, 0.8, 0.06, 0.04),
      Vector(0.05, 0.08, 0.11, 0.13, 0.11, 0.8, 0.05),
      Vector(0.05, 0.08, 0.11, 0.13, 0.11, 0.8, 0.05),
      Vector(0.04, 0.06, 0.08, 0.10, 0.8, 0.06, 0.04),
      Vector(0.03, 0.04, 0.05, 0.07, 0.05, 0.04, 0.03)
    ))


    val scoresForSetChips = for {
      i <- matchfield.rows.indices; j <- matchfield.rows(i).indices
      scoreValue = if (matchfield.cell(i, j) == aiPlayer) scoreMatrix.cell(i,j) else 0.0
    } yield scoreValue

    scoresForSetChips.sum
  }



  def spawnNewActor(columnNr:Int, matchField:MatchfieldModel[PlayerModel], aiPlayer:PlayerModel, otherPlayer:PlayerModel, depth:Int):Future[Any]

  def makeScoreBasedChoice(choices:List[ResponseMessage]):ResponseMessage

  def getPlayer(aiPlayer:PlayerModel, otherPlayer:PlayerModel): PlayerModel
}
