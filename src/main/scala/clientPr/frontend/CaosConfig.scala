package clientPr.frontend

import caos.frontend.Configurator.*
import caos.frontend.{Configurator, Documentation}
import clientPr.syntax.Parser.*

/** Object used to configure which analysis appear in the browser */
object CaosConfig extends Configurator[Program]:
  val name = "Client for a server that runs OS commands (CP 2024/25)"
  override val languageName: String = "Input program"

  /** Parser, converting a string into a System in client PR */
  val parser: String => Program =
    clientPr.syntax.Parser.parseProgram

  /** Examples of programs that the user can choose from. The first is the default one. */
  val examples = List(
    "ex1" -> "# comment\nserver: localhost:8080\npwd",
    "ex2" -> "server: localhost:8080\nls -a\nsleep 2; pwd\necho \"hello\"",
    "ex3" -> "server: localhost:8080\nls\npwd\nserver: 127.0.0.1:8080\nls\npwd",
  )

  /** Description of the widgets that appear in the dashboard. */
  val widgets = List(
    //"View data" -> view[Program](_.toString, Text).expand,
    "Remote" -> viewRemote[Program](Remote.buildCommandList, Remote.generateHtml),
  )

  def drawResult(msg:String): String = msg

  //// Documentation below

  override val footer: String =
    """Simple client for a command-runner, for use within the Concurrent Programming course (FCUP, Porto), using the
      | CAOS libraries to generate this website
      | (<a target="_blank" href="https://github.com/arcalab/CAOS">
      | https://github.com/arcalab/CAOS</a>).""".stripMargin

  override val documentation: Documentation = List(
    languageName -> "More information on the syntax of client PR" ->
      """<pre>server: SERVER</pre>
        | Sets the server to be <code>SERVER</code>
        | <pre>COMMAND</pre>
        | Requests to run <code>COMMAND</code> in the last set server.
        |""".stripMargin,
  )
