package extDsl

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import extDsl.Connect4Parser
import org.scalactic.ErrorMessage

class Connect4ParserSpec extends AnyWordSpec with Matchers {

  val parser: Connect4Parser = new Connect4Parser()
  val playerDslExample =
    """
      | Player 1 has a name Pascal
      | and a symbol o
      |""".stripMargin

  parser.parseDsl(playerDslExample) match {
    case Right(playerModel)=> {
      playerModel.sign should be ('o')
      playerModel.name should be ("Pascal")
    }
    case Left(errorMessage: ErrorMessage) => {
      fail("Parsing failed! " + errorMessage)
    }
  }

  val playerDslExample2 =
    """
      | Player 2 has a name Andreas
      | and a symbol x
      |""".stripMargin

  parser.parseDsl(playerDslExample2) match {
    case Right(playerModel)=> {
      playerModel.sign should be ('x')
      playerModel.name should be ("Andreas")
    }
    case Left(errorMessage: ErrorMessage) => {
      fail("Parsing failed! " + errorMessage)
    }
  }

  val playerDslExample3 =
    """
      | Player 3 has a name Andreas
      | and a symbol x
      |""".stripMargin

  parser.parseDsl(playerDslExample3) match {
    case Right(playerModel)=> {
      playerModel.sign should be ('x')
      playerModel.name should be ("Andreas")
    }
    case Left(errorMessage: ErrorMessage) => {
      errorMessage should be ("Failed parsing: string matching regex '[1|2]' expected but '3' found")
    }
  }



}
