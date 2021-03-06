package tests.markdown

class ErrorSuite extends BaseMarkdownSuite {

  checkError(
    "crash",
    """
      |```scala mdoc
      |val x = 1
      |```
      |```scala mdoc
      |val y = 2
      |def crash() = ???
      |def z: Int = crash()
      |def safeMethod = 1 + 2
      |x + y + z
      |```
    """.stripMargin,
    """
      |error: crash.md:10:1: error: an implementation is missing
      |x + y + z
      |^^^^^^^^^
      |scala.NotImplementedError: an implementation is missing
      |	at scala.Predef$.$qmark$qmark$qmark(Predef.scala:284)
      |	at repl.Session.crash$1(crash.md:16)
      |	at repl.Session.z$1(crash.md:19)
      |	at repl.Session.app(crash.md:25)
      |""".stripMargin
  )

  checkError(
    "invalid-mod",
    """
      |```scala mdoc:foobaz
      |val x: Int = "String"
      |```
    """.stripMargin,
    """
      |error: invalid-mod.md:2:15: error: Invalid mode 'foobaz'
      |```scala mdoc:foobaz
      |              ^^^^^^
    """.stripMargin
  )

  checkError(
    "silent",
    """
      |```scala mdoc:passthrough
      |import scala.util._
      |```
      |
      |```scala mdoc:fail
      |List(1)
      |```
    """.stripMargin,
    """
      |error: silent.md:7:1: error: Expected compile error but statement type-checked successfully
      |List(1)
      |^^^^^^^
    """.stripMargin
  )
  checkError(
    "parse-error",
    """
      |```scala mdoc
      |val x =
      |```
    """.stripMargin,
    """
      |error: parse-error.md:3:8: error: illegal start of simple expression
      |val x =
      |       ^
    """.stripMargin
  )
}
