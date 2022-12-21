package de.htwg.se.minesweeper

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import net.codingwell.scalaguice.ScalaModule
import de.htwg.se.minesweeper.Controller

class ModuleConfig extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[Controller.ControllerInterface])
    // bind(classOf[Model.GameInterface]).to(classOf[GameBaseImplementation.Game])
  }
}
