package ai.mininmax

import java.util.concurrent.TimeUnit

import akka.actor.TypedActor.dispatcher
import akka.actor.{Actor, Props}
import akka.pattern.ask
import akka.util.Timeout
import controllers.GameLogic
import model.{MatchfieldModel, PlayerModel, RoundModel}

import scala.concurrent.Future
import scala.util.Success

trait MiniMaxActor extends Actor{

  implicit val timeout: Timeout = Timeout(5, TimeUnit.SECONDS)

  override def receive: Receive = {
    case requestMessage: RequestMessage =>
      val matchField = requestMessage.matchfield
      val aiPlayer = requestMessage.simulatedPlayer
      val otherPlayer = requestMessage.otherPlayer
      val depth = requestMessage.depth

      checkAndEvaluate(matchField, aiPlayer, otherPlayer, depth) match {
        case Some(i:Int) => sender ! ResponseMessage(i)
        case None =>
          // get number of columns in the matchfield which are not filled up yet
          val nrPossibleColumns = List[Int](1, 2, 3, 4, 5, 6, 7) // TODO: Function to get a list of columns that aren't filled up yet

          // Create the matchfield for each option and pass each one to the next child, which for a MaxActor is a MinActor.
          // When passing the
          println("Received Request Message")

          implicit val timeout: Timeout = Timeout(5, TimeUnit.SECONDS)
          val futures = for { i <- nrPossibleColumns
                              future = spawnNewChildActor(i, aiPlayer, otherPlayer, matchField, depth - 1)
                              } yield(future)

          Future.sequence(futures).onComplete {
            case Success(l:List[ResponseMessage]) =>
              val choice = makeScoreChoice(l)
              sender ! ResponseMessage(choice)
            // TODO: Failure
          }
      }

    // case responseMessage: ResponseMessage => context.parent ! responseMessage
    case test:String => println(s"I have received: ${test}")
  }

  def checkAndEvaluate(matchfield:MatchfieldModel[PlayerModel], aiPlayer:PlayerModel, otherPlayer:PlayerModel, depth:Int):Option[Int] = {
    if (depth == 0) Some(42) // we need an evaluation method that returns a value depending on the sitatuion in the matchfield
    else if (GameLogic.checkIfSomeoneWon(matchfield, aiPlayer)) Some(1)
    else if (GameLogic.checkIfSomeoneWon(matchfield, otherPlayer)) Some(-1)
    else if (GameLogic.checkIfDraw(matchfield)) Some(0)
    else None
  }

  def spawnNewChildActor(columnt: Int, aiPlayer:PlayerModel, otherPlayer:PlayerModel, matchfield:MatchfieldModel[PlayerModel], depth:Int) : Future[Any]
  def makeScoreChoice(choices:List[ResponseMessage]):Int

}
