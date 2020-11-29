package ai.mininmax

import java.util.concurrent.TimeUnit

import akka.actor.TypedActor.dispatcher
import akka.actor.{Actor, Props}
import akka.pattern.ask
import akka.util.Timeout
import controllers.GameLogic
import model.{MatchfieldModel, PlayerModel, RoundModel}

import scala.collection.IterableOnce.iterableOnceExtensionMethods
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

trait MiniMaxActor extends Actor{

  implicit val timeout: Timeout = Timeout(5, TimeUnit.SECONDS)

  override def receive: Receive = {
    case requestMessage: RequestMessage =>
      val matchField = requestMessage.matchfield
      val aiPlayer = requestMessage.simulatedPlayer
      val otherPlayer = requestMessage.otherPlayer
      val depth = requestMessage.depth

      checkAndEvaluate(matchField, aiPlayer, otherPlayer, depth) match {
        case Some(i) => sender ! ResponseMessage(i)
        case None =>
          // get number of columns in the matchfield which are not filled up yet
          val possibleColumns = List[Int](0, 1, 3) // TODO: Function in GameLogic to get a list of columns that aren't filled up yet

          // Create the matchfield for each option and pass each one to the next child, which for a MaxActor is a MinActor.
          // When passing the
          println("Received Request Message")

          implicit val timeout: Timeout = Timeout(5, TimeUnit.SECONDS)
          val futures = for {columnNr <- possibleColumns
                             future = spawnNewActor(columnNr, matchField, aiPlayer, otherPlayer, depth - 1)
                             } yield(future)

          val futureResults = for {
            future <- futures
            res = Await.result(future, timeout.duration)
          } yield(res)

          val choice = makeScoreChoice(futureResults.asInstanceOf[List[ResponseMessage]])
          sender ! ResponseMessage(choice)
      }

    // case responseMessage: ResponseMessage => context.parent ! responseMessage
    case responseMessage: ResponseMessage => println("response message received")
  }

  def checkAndEvaluate(matchfield:MatchfieldModel[PlayerModel], aiPlayer:PlayerModel, otherPlayer:PlayerModel, depth:Int):Option[Int] = {
    if (depth == 0) Some(7) // we need an evaluation method that returns a value depending on the sitatuion in the matchfield
    else if (GameLogic.checkIfSomeoneWon(matchfield, aiPlayer)) Some(1)
    else if (GameLogic.checkIfSomeoneWon(matchfield, otherPlayer)) Some(-1)
    else if (GameLogic.checkIfDraw(matchfield)) Some(0)
    else None
  }

  def spawnNewActor(columnNr:Int, matchField:MatchfieldModel[PlayerModel], aiPlayer:PlayerModel, otherPlayer:PlayerModel, depth:Int):Future[Any]

  def makeScoreChoice(choices:List[ResponseMessage]):Int

  def getPlayer(aiPlayer:PlayerModel, otherPlayer:PlayerModel): PlayerModel
}
