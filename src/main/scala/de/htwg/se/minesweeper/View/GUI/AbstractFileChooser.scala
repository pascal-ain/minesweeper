package de.htwg.se.minesweeper.View.GUI

import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.JFileChooser
import scala.swing.FileChooser
import javax.swing.filechooser.FileFilter
import scala.swing.Component
import de.htwg.se.minesweeper.Controller.ControllerComponent.ControllerInterface
import java.io.File
import de.htwg.se.minesweeper.Config
import de.htwg.se.minesweeper.Model.FileIOComponent.{
  FileIOJSONImplementation,
  FileIOTOMLImplementation,
  FileIOXMLImplementation
}
import scala.util.Try
import scala.util.Failure
import scala.util.Success

object SaveDirectory:
  var dir = File(sys.props("user.home"))

enum FileOperation:
  case Load, Save

abstract class AbstractFileChooser extends FileChooser {
  this.peer.setAcceptAllFileFilterUsed(false)
  val filters = List(
    new FileNameExtensionFilter("TOML", "toml"),
    new FileNameExtensionFilter("XML", "xml"),
    new FileNameExtensionFilter("JSON", "json")
  )
  filters.foreach(filter => fileFilter = filter)
  fileFilter = acceptAllFileFilter
  multiSelectionEnabled = false
  fileSelectionMode = FileChooser.SelectionMode.FilesOnly

  def getFileIOImplementation(extension: String) =
    extension match
      case "toml" => FileIOTOMLImplementation.FileIO
      case "xml"  => FileIOXMLImplementation.FileIO
      case "json" => FileIOJSONImplementation.FileIO
      case _      => Config.defaultFileImplementation

  def processFile(file: File) =
    filters.find(_.accept(file)) match
      case None =>
        val extension = getSelectedFilter()
        (new File(s"${file.toString}.${extension}"), extension)
      case Some(extension) => (file, extension.getExtensions()(0))

  def getSelectedFilter(): String =
    Try(
      this.peer
        .getFileFilter()
        .asInstanceOf[FileNameExtensionFilter]
        .getExtensions()(0)
    ).getOrElse(Config.defaultFileExtension)

  def action(parent: Component, controller: ControllerInterface): Unit

  def doAction(
      parent: Component,
      controller: ControllerInterface,
      operation: FileOperation
  ): Unit =
    this.peer.setCurrentDirectory(SaveDirectory.dir)
    val (showOperation, ioOperation) = operation match
      case FileOperation.Load =>
        (
          (comp => this.showOpenDialog(comp)),
          (file, implementation) => controller.load(file, implementation)
        )
      case FileOperation.Save =>
        (
          (comp => this.showSaveDialog(comp)),
          (file, implementation) => controller.save(file, implementation)
        )

    showOperation(parent) match
      case FileChooser.Result.Approve => {
        val file = this.selectedFile
        SaveDirectory.dir = file.getAbsoluteFile().getParentFile()
        val (fileName, extension) = processFile(file)
        ioOperation(fileName, getFileIOImplementation(extension))
      }
      case _ =>
}

object LoadFileChooser extends AbstractFileChooser {
  title = "Load a save file"
  override def action(
      parent: Component,
      controller: ControllerInterface
  ): Unit =
    doAction(parent, controller, FileOperation.Load)
}

object SaveFileChooser extends AbstractFileChooser {
  title = "Create a save file"

  override def action(
      parent: Component,
      controller: ControllerInterface
  ): Unit =
    doAction(parent, controller, FileOperation.Save)
}
