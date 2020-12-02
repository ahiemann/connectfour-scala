package ai.minimax

import java.util.concurrent.TimeUnit

import akka.actor.TypedActor.dispatcher
import akka.actor.{Actor, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.language.postfixOps
import controllers.GameLogic
import model.{MatchfieldModel, PlayerModel, RoundModel}

import scala.collection.IterableOnce.iterableOnceExtensionMethods
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

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
          sender ! bestOption
      }
  }

  def checkAndEvaluate(matchfield:MatchfieldModel[PlayerModel], aiPlayer:PlayerModel, otherPlayer:PlayerModel, depth:Int):Option[Int] = {
    if (depth == 0) Some(7)
    else if (GameLogic.checkIfSomeoneWon(matchfield, aiPlayer)) Some(1)
    else if (GameLogic.checkIfSomeoneWon(matchfield, otherPlayer)) Some(-1)
    else if (GameLogic.checkIfDraw(matchfield)) Some(0)
    else None
  }

  def spawnNewActor(columnNr:Int, matchField:MatchfieldModel[PlayerModel], aiPlayer:PlayerModel, otherPlayer:PlayerModel, depth:Int):Future[Any]

  def makeScoreBasedChoice(choices:List[ResponseMessage]):ResponseMessage

  def getPlayer(aiPlayer:PlayerModel, otherPlayer:PlayerModel): PlayerModel
}
