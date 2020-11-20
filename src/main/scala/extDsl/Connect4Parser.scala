package extDsl

import model.{PlayerModel, RoundModel}

import scala.util.parsing.combinator.RegexParsers

class Connect4Parser extends RegexParsers {


  def parseDsl(input: String): Either[String, PlayerModel] =
    parseAll(player, input) match {
      case Success(t,_) => Right(t)
      case NoSuccess(msg,_)=>
        Left(s"Failed parsing: $msg")
    }

  // private def round: Parser[RoundModel]

  private def player: Parser[PlayerModel] =
    "Player" ~ number ~ "has a name" ~ name ~
    "and a symbol" ~ symbol ^^ {
      case _ ~ _ ~ _ ~ n ~ _ ~ s =>
        PlayerModel(n,s.toList(0))
    }

  private def name: Parser[String] = lineOfText

  //matches a line of characters until line break
  private def lineOfText: Parser[String] = """[^\v]+""".r  ^^ (_.trim)

  //matches one of the two available tokens
  private def symbol: Parser[String] = """[x|o]""".r

  //matches one of the two available tokens
  private def number: Parser[String] = """[1|2]""".r

}
