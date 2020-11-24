import extDsl.Connect4Parser
import scala.language.postfixOps



val parser = new Connect4Parser()


// no round data
parser.parseConnect4Dsl("""
    |Player 1 has name Pascal and symbol x
    |Player 2 has name Andreas and symbol o
    |""".stripMargin)

// round of player 1 only
parser.parseConnect4Dsl("""
                          |Player 1 has name Pascal and symbol x
                          |Player 2 has name Andreas and symbol o
                          |Set chip for player 1 in column 1
                          |""".stripMargin)

// round of player 2 only
parser.parseConnect4Dsl("""
                          |Player 1 has name Pascal and symbol x
                          |Player 2 has name Andreas and symbol o
                          |Set chip for player 2 in column 1
                          |""".stripMargin)

// round of player 1 and player 2
parser.parseConnect4Dsl("""
                          |Player 1 has name Pascal and symbol x
                          |Player 2 has name Andreas and symbol o
                          |Set chip for player 1 in column 1
                          |Set chip for player 2 in column 1
                          |""".stripMargin)

// compact round definition
parser.parseConnect4Dsl("""
                          |Player 1 has name Pascal and symbol x
                          |Player 2 has name Andreas and symbol o
                          |p2 : c1
                          |p1 : c1
                          |p2 : c2
                          |p1 : c3
                          |""".stripMargin)


// Same player in two consecutive rounds is invalid
parser.parseConnect4Dsl("""
                          |Player 1 has name Pascal and symbol x
                          |Player 2 has name Andreas and symbol o
                          |p1 : c1
                          |p2 : c1
                          |p1 : c2
                          |p1 : c3
                          |""".stripMargin)

// Arbitrary text also is invalid
parser.parseConnect4Dsl(
 """
   |Player 1 wins directly!""".stripMargin)