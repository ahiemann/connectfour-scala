package extDsl

import model.{PlayerModel, RoundModel}

import scala.util.parsing.combinator.RegexParsers

class Connect4Parser extends RegexParsers {



  // private def round: Parser[RoundModel]

  private def player: Parser[PlayerModel] =
    "Player 1 has a name" ~ name ~
    "and a symbol" ~ symbol ^^ {
      case _ ~ n ~ _ ~ s =>
        PlayerModel(n,s.toList(0))
    }

  private def name: Parser[String] = lineOfText

  //matches a line of characters until line break
  private def lineOfText: Parser[String] = """[\v]+""".r  ^^ (_.trim)

  //matches one of the two available tokens
  private def symbol: Parser[String] = """[x|o]"""

}
