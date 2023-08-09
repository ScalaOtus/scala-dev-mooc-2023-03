package AkkaDataStreams

import akka.{AkkaException, NotUsed}
import akka.actor.{DeathPactException, Kill, SupervisorStrategy}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import org.scalatest.wordspec.AnyWordSpecLike

sealed trait Command

case class StartChild(name: String) extends Command
case class SendMessageToChild(name: String, msg:String, num:Int) extends Command
case class StopChild(name: String) extends Command
case object Stop extends Command


object Parent{
  def apply(): Behavior[Command] = withChildren(Map())

  def withChildren(childs: Map[String, ActorRef[Command]]): Behavior[Command] =
    Behaviors.setup{ ctx =>

      Behaviors.receiveMessage{
        case StartChild(name) =>
          ctx.log.info(s"Start child $name")
          val newChild = ctx.spawn(Child(), name)
          withChildren(childs + (name -> newChild))
        case msg @ SendMessageToChild(name, _, i) =>
          ctx.log.info(s"Send message to child $name num=$i")
          val childOption = childs.get(name)
          childOption.foreach(childRef => childRef ! msg)
          Behaviors.same
        case StopChild(name) =>
          ctx.log.info(s"Stopping child with name $name")
          val childOption = childs.get(name)
          childOption match {
            case Some(childRef) =>
              ctx.stop(childRef)
              Behaviors.same
              //withChildren(childs - name) //todo, potential error
            case None => Behaviors.same
          }

        case Stop =>
          ctx.log.info("Stopped parent")
          Behaviors.stopped
      }
    }
}

object Child{
  def apply(): Behavior[Command] = Behaviors.setup{ ctx=>
    Behaviors.receiveMessage{  msg=>
      ctx.log.info(s"Child got message $msg")
      Behaviors.same
    }
  }
}

object  StartStopSpec extends App {
  def apply():Behavior[NotUsed] =
    Behaviors.setup { ctx =>
      val parent = ctx.spawn(Parent(), "parent")
      parent ! StartChild("child1")
      parent ! SendMessageToChild("child1", "message to child1", 0)
      parent ! StopChild("child1")
      for (i <- 1 to 15) parent ! SendMessageToChild("child1", "message to child1", i)
      Behaviors.same
    }

  val value = StartStopSpec()
  implicit  val systme = ActorSystem(value, "akka_typed")
  Thread.sleep(5000)
  systme.terminate()

}
