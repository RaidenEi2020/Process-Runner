package clientPr.frontend

object Remote {


  def buildCommandList(syntax:Map[String,List[String]]): List[(String, String)] = {
    for (service, lcmds) <- syntax.toList; cmd <- lcmds yield (service, cmd)
  }

  def generateHtml(reply: String): String = {
    s"""
       |<strong>OUTPUT INFO:</strong><br>
       |<pre>${parseReply(reply)}</pre>
    """.stripMargin
  }

  private def parseReply(reply: String): String = {
    val lines = reply.split("\n")
    var indexId = -1
    var indexCmd = -1
    var indexUser = -1
    var indexOutput = -1
    var i = 1

    while (indexId == -1 || indexCmd == -1 || indexUser == -1 || indexOutput == -1) {
      if (lines(i).startsWith("Id:") && lines(i + 1).startsWith("Command:")) {
        indexId = i
        indexCmd = i + 1
      } else if (lines(i).startsWith("User:") && lines(i + 1).startsWith("Output:")) {
        indexUser = i
        indexOutput = i + 2
      }
      i += 1
    }

    val id = lines(indexId).stripPrefix("Id:").trim
    val cmd = lines(indexCmd).stripPrefix("Command:").trim
    val user = lines(indexUser).stripPrefix("User:").trim

    val indexEnd = lines.indexWhere(_.startsWith("Process exited with code"))
    val outputLines = lines.slice(indexOutput, indexEnd + 1)
    val output = outputLines.mkString("\n")

    val info =
    //s"Id: $id\n" +
    s"Command: $cmd\n" +
    //s"User: $user\n" +
    s"Output:\n$output"

    info
  }
}
