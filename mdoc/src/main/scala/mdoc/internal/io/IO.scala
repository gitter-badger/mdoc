package mdoc.internal.io

import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import scala.meta.io.AbsolutePath
import scala.meta.io.RelativePath
import mdoc.internal.cli.InputFile
import mdoc.internal.cli.Settings

object IO {

  def foreachInputFile(settings: Settings)(fn: InputFile => Unit): Unit = {
    implicit val cwd = settings.cwd
    val root = settings.in.toNIO
    val visitor = new SimpleFileVisitor[Path] {
      override def visitFile(
          file: Path,
          attrs: BasicFileAttributes
      ): FileVisitResult = {
        settings.toInputFile(AbsolutePath(file)) match {
          case Some(inputFile) =>
            fn(inputFile)
          case None =>
            () // excluded
        }
        FileVisitResult.CONTINUE
      }

      override def preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult = {
        val relpath = RelativePath(root.relativize(dir))
        if (settings.isExplicitlyExcluded(relpath)) FileVisitResult.SKIP_SUBTREE
        else FileVisitResult.CONTINUE
      }
    }
    Files.walkFileTree(root, visitor)
  }

  val deleteVisitor: SimpleFileVisitor[Path] = new SimpleFileVisitor[Path] {
    override def visitFile(
        file: Path,
        attrs: BasicFileAttributes
    ): FileVisitResult = {
      Files.delete(file)
      FileVisitResult.CONTINUE
    }
    override def postVisitDirectory(
        dir: Path,
        exc: IOException
    ): FileVisitResult = {
      Files.delete(dir)
      FileVisitResult.CONTINUE
    }
  }

  def cleanTarget(dir: AbsolutePath): Unit = {
    Files.walkFileTree(dir.toNIO, deleteVisitor)
  }
}
