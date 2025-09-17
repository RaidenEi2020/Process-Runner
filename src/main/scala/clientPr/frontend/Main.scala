package clientPr.frontend

import caos.frontend.Site.initSite

/** Main function called by ScalaJS' compiled javascript when loading. */
object Main {
  def main(args: Array[String]):Unit =
    initSite[clientPr.syntax.Parser.Program](CaosConfig)
}