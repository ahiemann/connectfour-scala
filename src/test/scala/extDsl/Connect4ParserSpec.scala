package extDsl

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import extDsl.Connect4Parser
import model.Connect4Model
import org.scalactic.ErrorMessage

class Connect4ParserSpec extends AnyWordSpec with Matchers {

  "A Connect4Parser" should {
    val parser = new Connect4Parser()

    val validDslExample = """Player 1 has name Pascal and symbol x
                       |Player 2 has name Andreas and symbol o
                       |Set chip for player 1 in column 1
                       |Set chip for player 2 in column 1
                       |Set chip for player 1 in column 2""".stripMargin

    val invalidDslExample = "Let Player 1 win directly!"

    "return the expected instance of the model for a valid input text" in {
      parser.parseConnect4Dsl(validDslExample).fold(l => l, r => r) shouldBe a [Connect4Model]
    }

    "return an error string for an invalid input text" in {
      parser.parseConnect4Dsl(invalidDslExample).fold(l => l, r => r) shouldBe a [String]
    }

    "also recognize given round data when it was written in a compact definition" in {
      val validWithCompactRounds = """
          |Player 1 has name Pascal and symbol x
          |Player 2 has name Andreas and symbol o
          |p1 : c1
          |p2 : c2
          |""".stripMargin

      parser.parseConnect4Dsl(validWithCompactRounds).fold(l => l, r => r) shouldBe a [Connect4Model]
    }

  }





/*
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

*/

}
