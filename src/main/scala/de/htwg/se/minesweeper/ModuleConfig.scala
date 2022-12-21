package de.htwg.se.minesweeper

import com.google.inject.{AbstractModule, TypeLiteral}
import de.htwg.se.minesweeper.Controller.ControllerComponent
import com.google.inject.name.Names
class ModuleConfig extends AbstractModule {
  override def configure(): Unit = {
    val game = Model.GameComponent.GameBaseImplementation.Game(10, 10, 0.2)
    bind(classOf[ControllerComponent.ControllerInterface])
      .to(classOf[ControllerComponent.ControllerBaseImplementation.Controller])
    bind(classOf[Model.GameInterface])
      .toInstance(game)
  }
}
