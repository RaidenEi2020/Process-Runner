package clientPr.syntax

import cats.parse.Parser.*
import cats.parse.{LocationMap, Parser as P, Parser0 as P0}

import scala.sys.error

object Parser :

  type Program = Map[String,List[String]]
  type Line = Option[Either[String,String]] // server or command (or none)
//  type ProgramBf = (Option[String],Map[String,List[String]])
  /** Parse a command  */
  def parseProgram(str:String): Program =
    pp(lines,str+"\n") match {
      case Left(e) => error(e)
      case Right(c) => c
    }

  /** Applies a parser to a string, and prettifies the error message */
  def pp[A](parser:P[A], str:String): Either[String,A] =
    parser.parseAll(str+"\n") match
      case Left(e) => Left(prettyError(str,e))
      case Right(x) => Right(x)

  /** Prettifies an error message */
  private def prettyError(str:String, err:Error): String =
    val loc = LocationMap(str)
    val pos = loc.toLineCol(err.failedAtOffset) match
      case Some((x,y)) =>
        s"""at ($x,$y):
           |"${loc.getLine(x).getOrElse("-")}"
           |${("-" * (y+1))+"^\n"}""".stripMargin
      case _ => ""
    s"${pos}expected: ${err.expected.toList.mkString(", ")}\noffsets: ${
      err.failedAtOffset};${err.offsets.toList.mkString(",")}"

  // Simple parsers for spaces and comments
  /** Parser for a sequence of spaces or comments */
  val whitespace: P[Unit] = P.charIn(" \t\r").void
  val comment: P[Unit] = string("#") *> P.charWhere(_!='\n').rep0.void
  val sps: P0[Unit] = (whitespace | comment).rep0.void
  val spsne: P[Unit] = (whitespace | comment | char('\n')).rep.void

  // Parsing smaller tokens
  private def alphaDigit: P[Char] =
    P.charIn('A' to 'Z') | P.charIn('a' to 'z') | P.charIn('0' to '9') | P.charIn('_')
  private def varName: P[String] =
    (charIn('a' to 'z') ~ alphaDigit.rep0).string
  private def procName: P[String] =
    (charIn('A' to 'Z') ~ alphaDigit.rep0).string
  private def symbols: P[String] =
    // symbols starting with "--" are meant for syntactic sugar of arrows, and ignored as symbols of terms
    P.not(string("--")).with1 *>
    oneOf("+-><!%/*=|&".toList.map(char)).rep.string
  private def url: P[String] =
    P.charWhere(c => !"\n\t ".contains(c)).rep.string

  import scala.language.postfixOps

  def newserver: P[Line] =
    string("server")*>sps*>char(':')*>sps*>url.map(x => Some(Left(x)))
  def command: P[Line] =
    P.charWhere(_!='\n').rep.string.map(x => Some(Right(x)))
//  def skip: P[Unit] =
//    sps

  def line: P[Line] = P.recursive( rest =>
    sps.with1 *> (
      (newserver <* sps)|
      (command <* sps) |
      (char('\n') *> rest.?.map(_.getOrElse(None)))
    ))

  def lines: P[Program] =
    (line.repSep(char('\n'))<*sps).map(x => flattenProgram(x.toList))

  def flattenProgram(l:List[Line], curr:Option[String]=None): Program = (l,curr) match
    case (Nil,_) => Map()
    case (None::rest,_) => flattenProgram(rest,curr)
    case (Some(Left(s))::rest,_) => flattenProgram(rest,Some(s))
    case (Some(Right(c))::_,None) => sys.error(s"No server found and found command '$c'. Use \"server: http://...\" to define a server.")
    case (Some(Right(c))::rest,Some(s)) =>
      val p = flattenProgram(rest,Some(s))
      p ++ Map(s -> (c::p.getOrElse(s,Nil)))

//  def program: P[Program] =
//    spsne.as(Map(""->Nil)) | lines
