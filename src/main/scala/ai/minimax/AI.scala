package ai.minimax

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import model.{AIPlayer, MatchfieldModel, PlayerModel, RealPlayer}

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import _root_.dsl.AutomaticMatchfieldImplicit.AutomaticMatchfield
import _root_.dsl.GameColumnImplicit.GameColumn
import util.GameLogic


//Vorhersage des besten Zugs für die CPU und die möglichen Verläufe zum Sieg des menschlichen Player voraus

object Main extends App {
  val system = ActorSystem("AISystem")
  val actor = system.actorOf(Props[MaxActor], "MaxActor")

  val playerAI = AIPlayer("Computer")
  val playerHuman = RealPlayer("Human", 'O')
  val noPlayer = RealPlayer("NoPlayer", '-')
  val matchfield = MatchfieldModel(Vector(
    Vector(playerHuman,playerAI,playerHuman,playerAI,playerHuman,playerAI,playerAI),
    Vector(playerAI,playerHuman,playerHuman,playerAI,playerHuman,playerHuman,playerAI),
    Vector(playerHuman,playerHuman,playerAI,playerHuman,playerHuman,playerAI,playerHuman),
    Vector(playerHuman,playerHuman,playerAI,playerHuman,playerAI,playerAI,playerAI),
    Vector(playerHuman,playerAI,noPlayer,playerHuman,playerHuman,playerAI,playerAI),
    Vector(playerAI,playerHuman,noPlayer,noPlayer,noPlayer,noPlayer,noPlayer)
  ))
  val initialMatchfield = GameLogic.getInitialMatchField()
  val matchfieldRoundOnly3FreeColumns = initialMatchfield.play(
    0 -> playerAI, 0 -> playerHuman,0 -> playerAI, 0 -> playerHuman,0 -> playerAI, 0 -> playerHuman,
    1 -> playerHuman, 1 -> playerAI,1 -> playerHuman, 1 -> playerAI,1 -> playerHuman, 1 -> playerAI,
    5 -> playerHuman, 5 -> playerAI,5 -> playerHuman, 5 -> playerAI,5 -> playerHuman, 5 -> playerAI,
    6 -> playerHuman, 6 -> playerAI,6 -> playerHuman, 6 -> playerAI,6 -> playerHuman, 6 -> playerAI,
  )

  implicit val timeout: Timeout = Timeout(10 seconds)
  val future = actor ? RequestMessage(None, playerAI, playerHuman, initialMatchfield, 2)

  val result = Await.result(future, timeout.duration)
  val column = result.asInstanceOf[ResponseMessage].columnNr.get
  println("Optimaler Zug zum Sieg der CPU: " + column)

}

