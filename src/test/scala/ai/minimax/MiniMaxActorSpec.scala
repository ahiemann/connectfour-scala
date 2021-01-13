package ai.minimax

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import model.{AIPlayer, MatchfieldModel, PlayerModel, RealPlayer}
import org.scalatest.matchers.must.Matchers.be
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import org.scalatest.wordspec.AnyWordSpec

import scala.language.postfixOps
import scala.concurrent.Await

class MiniMaxActorSpec extends AnyWordSpec{

    val system = ActorSystem("AISystem")
    val actor = system.actorOf(Props[MaxActor], "MaxActor")

    val playerAI = AIPlayer("Computer")
    val playerHuman = RealPlayer("Human", 'O')
    val noPlayer = RealPlayer("NoPlayer", '-')
    val matchfield = MatchfieldModel[PlayerModel](Vector(Vector(playerHuman,playerAI,playerHuman,playerAI,playerHuman,playerAI,playerAI),Vector(playerAI,playerHuman,playerHuman,playerAI,playerHuman,playerHuman,playerAI),Vector(playerHuman,playerHuman,playerAI,playerHuman,playerHuman,playerAI,playerHuman),Vector(playerHuman,playerHuman,playerAI,playerHuman,playerAI,playerAI,playerAI),Vector(playerHuman,playerAI,noPlayer,playerHuman,playerHuman,playerAI,playerAI),Vector(playerAI,playerHuman,noPlayer,noPlayer,noPlayer,noPlayer,noPlayer)))
    implicit val timeout: Timeout = Timeout(2 seconds)

    "receive an Answer from the request message" in {
      val future = actor ? RequestMessage(None, playerAI, playerHuman, matchfield, 50)
      Await.result(future, timeout.duration) should be(ResponseMessage(Some(3), 50.0))
    }
}
