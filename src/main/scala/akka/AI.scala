package akka

import akka.AI.Test
import akka.actor._

object AI extends App{
  class Test extends Actor {
    def receive = {
      case s:String => println("Hello" + s)
      case _      => println("Nope")
    }
  }
  val system = ActorSystem("Root")
  val actor = system.actorOf(Props[Test], "A")

  actor ! "Sfds"
}

object Main extends App {
  val a = new Test
}
