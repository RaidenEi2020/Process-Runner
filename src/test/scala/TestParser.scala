class TestParser extends munit.FunSuite {
  import clientPr.syntax.*
  import Term.*

  test("example test that succeeds") {
    val obtained =
      clientPr.syntax.Parser.parseProgram("let P = a.b; in c.P")
    val expected =
      System(Map("P" -> Prefix("a", Prefix("b", End))), Prefix("c", Proc("P")), None)
    assertEquals(obtained, expected)
  }
}