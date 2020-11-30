package ai.minimax

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import controllers.GameLogic
import model.{MatchfieldModel, PlayerModel}

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

//Vorhersage des besten Zugs für die CPU und die möglichen Verläufe zum Sieg des menschlichen Player voraus

object Main extends App {
  val system = ActorSystem("AISystem")
  val actor = system.actorOf(Props[MaxActor], "MaxActor")

  val playerAI = PlayerModel("Computer")
  val playerHuman = PlayerModel("Human", 'O')
  val noPlayer = PlayerModel("NoPlayer", '-')
  val matchfield = MatchfieldModel(Vector(Vector(playerHuman,playerAI,playerHuman,playerAI,playerHuman,playerAI,playerAI),Vector(playerAI,playerHuman,playerHuman,playerAI,playerHuman,playerHuman,playerAI),Vector(playerHuman,playerHuman,playerAI,playerHuman,playerHuman,playerAI,playerHuman),Vector(playerHuman,playerHuman,playerAI,playerHuman,playerAI,playerAI,playerAI),Vector(playerHuman,playerAI,noPlayer,playerHuman,playerHuman,playerAI,playerAI),Vector(playerAI,playerHuman,noPlayer,noPlayer,noPlayer,noPlayer,noPlayer)))

  implicit val timeout: Timeout = Timeout(2 seconds)
  val future = actor ? RequestMessage(None, playerAI, playerHuman, matchfield, 50)

  val result = Await.result(future, timeout.duration)
  println("Optimaler Zug zum Sieg der CPU: " + result)

  def possibleMoves (s:MatchfieldModel[PlayerModel]){
    println("Möglicher Spielverlauf zum Sieg des P1: " + s)
  }
}

