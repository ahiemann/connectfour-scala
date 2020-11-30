package akka

import akka.AI.NextMove
import akka.actor.TypedActor.dispatcher
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration.DurationInt

object AI extends App{
  class NextMove(MatchField :String) extends Actor { // PARENT

    private var running = false
    private val matchFieldSize = 5

    def receive = {
      case "Hello" => {
        context.actorOf(Props(new NextNextMove(""))) ! "PlayerOne"
      }
      case _      => println("Das ist kein Matchfield")
    }
  }

  class NextNextMove(Matchfield :String) extends Actor { //CHILD

    def receive = {
      case s:String => println("Hello " + s)
      case _      => println("Nope")
    }
  }

}

object Maino extends App {
  val system = ActorSystem("System")
  val actor = system.actorOf(Props(new NextMove("")))
  //implicit val timeout = Timeout(25 seconds)
  //val future = actor ? "Hello"
  //future.map{
//    result =>
  //    println("Best next move is " + result)
    //system.shutdown
  //}
}
