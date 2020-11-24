package extDsl

import dsl.GameColumnPlayerMapping
import scala.language.postfixOps
import model.{Connect4Model, PlayerModel}

import scala.util.parsing.combinator.RegexParsers

class Connect4Parser extends RegexParsers {

  def parseConnect4Dsl(input: String): Either[String, Connect4Model] =
    parseAll(connect4Parser, input) match {
      case Success(t,_) => Right(t)
      case NoSuccess(msg,_) =>
        Left(s"Failed parsing: $msg")
    }

  private def connect4Parser: Parser[Connect4Model] = {
    player ~
      player ~
      rounds ^^ {
      case player1 ~ player2 ~ roundData =>
        val roundDataWithRealPlayers = roundData.map(
          round => {
            val player = if (round._1 == 1) player1 else player2
            GameColumnPlayerMapping(round._2, player)
          })
        Connect4Model(player1, player2, roundDataWithRealPlayers)
    }
  }


  private def player: Parser[PlayerModel] =
    "Player" ~ playerNumber ~
      "has name" ~ name ~
      "and symbol" ~ symbol ^^ {
      case _ ~ _ ~ _ ~ n ~ _ ~ s =>
        PlayerModel(n, s)
    }

  private def rounds: Parser[List[(Int, Int)]] = multipleRounds | oneOrNoRound

  private def multipleRounds: Parser[List[(Int, Int)]] =
    (rep1(player1First) ~ opt(player1Round) | rep1(player2First) ~ opt(player2Round)) ^^ {
      case playerRounds ~ Some(optionalPlayerRound) => playerRounds.flatten.appended(optionalPlayerRound)
      case playerRounds ~ None => playerRounds.flatten
    }

  private def player1First : Parser[List[(Int, Int)]] =
    player1Round ~ player2Round ^^ {
      case p1round ~ p2round => List[(Int, Int)](p1round, p2round)
    }

  private def player2First : Parser[List[(Int, Int)]] =
    player2Round ~ player1Round ^^ {
      case p2round ~ p1round => List[(Int, Int)](p2round, p1round)
    }

  private def oneOrNoRound: Parser[List[(Int, Int)]] =
    opt(player1Round | player2Round) ^^ {
      case Some(round) => List[(Int, Int)](round)
      case None => List[(Int, Int)]()
    }

  private def player1Round : Parser[(Int, Int)] = player1RoundLong | player1RoundCompact
  private def player2Round : Parser[(Int, Int)] = player2RoundLong | player2RoundCompact

  private def player1RoundLong : Parser[(Int, Int)] =
    "Set" ~
      "chip" ~
      "for" ~ "player" ~ "1" ~ "in" ~ "column" ~ columnNumber ^^ { case _ ~ _ ~ _ ~ _ ~ _ ~ _ ~ _ ~ columnNr => (1, columnNr) }

  private def player2RoundLong : Parser[(Int, Int)] =
    "Set" ~
      "chip" ~
      "for" ~ "player" ~ "2" ~ "in" ~ "column" ~ columnNumber ^^ { case _ ~ _ ~ _ ~ _ ~ _ ~ _ ~ _ ~ columnNr => (2, columnNr) }

  private def player1RoundCompact : Parser[(Int, Int)] =
    "p" ~ "1" ~
      ":" ~
      "c" ~ columnNumber ^^ {
      case _ ~ _ ~ _ ~ _ ~ columnNr => (1, columnNr)
    }

  private def player2RoundCompact : Parser[(Int, Int)] =
    "p" ~ "2" ~
      ":" ~
      "c" ~ columnNumber ^^ {
      case _ ~ _ ~ _ ~ _ ~ columnNr => (2, columnNr)
    }


  private def name: Parser[String] = word

  private def word: Parser[String] = """\w+""".r

  //matches one of the two available tokens
  private def symbol: Parser[Char] = """[x|o]""".r ^^ (_.charAt(0))

  private def playerNumber: Parser[Int] = """[1|2]""".r ^^ (_.toInt)

  //matches a number in the range 1-7, corresponding to the number of columns in Connect Four
  private def columnNumber: Parser[Int] = """[1-7]""".r ^^ (_.toInt)

}
