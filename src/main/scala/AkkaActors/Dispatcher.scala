package AkkaActors

import AkkaActors.Dispatcher.JsonParser.{Parse, ParseResponse}
import AkkaActors.Dispatcher.LogWorker.{LogRequest, LogResponse}
import AkkaActors.Dispatcher.TaskDispatcher.{LogWork, ParseUrl}
import akka.NotUsed
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}

import java.util.UUID


object  Dispatcher extends App {
  // main actor
  object TaskDispatcher {
    sealed trait CommandDispatcher

    case class ParseUrl(url: String) extends CommandDispatcher

    case class LogWork(url: String) extends CommandDispatcher

    case class LogReponseWrapper(msg: LogResponse) extends CommandDispatcher

    case class ParseReponseWrapper(msg: ParseResponse) extends CommandDispatcher

    def apply(): Behavior[CommandDispatcher] = Behaviors.setup { ctx =>
      val logAdapter: ActorRef[LogResponse] = ctx.messageAdapter[LogResponse](rs => LogReponseWrapper(rs))
      val parseAdapter: ActorRef[ParseResponse] = ctx.messageAdapter[ParseResponse](rs => ParseReponseWrapper(rs))


      Behaviors.receiveMessage {
        case LogWork(work) =>
          val logWorker: ActorRef[LogRequest] = ctx.spawn(LogWorker(), s"LogWorkerNo${UUID.randomUUID()}")
          ctx.log.info(s"Dispatcher received log ${work}")
          logWorker ! LogWorker.Log(work, logAdapter)
          Behaviors.same
        case ParseUrl(url) =>
          val urlParser = ctx.spawn(JsonParser(), s"JsonParserNo${UUID.randomUUID()}")
          ctx.log.info(s"Dispatcher received url ${url}")
          urlParser ! Parse(url, parseAdapter)
          Behaviors.same
        case LogReponseWrapper(m) =>
          ctx.log.info("Log done")
          Behaviors.same
        case ParseReponseWrapper(m) =>
          ctx.log.info("Parse done")
          Behaviors.same
      }

    }
  }


  object LogWorker {
    sealed trait LogRequest

    case class Log(l: String, replyTo: ActorRef[LogResponse]) extends LogRequest

    sealed trait LogResponse

    case class LogDone() extends LogResponse

    def apply(): Behavior[LogRequest] = Behaviors.setup { ctx =>
      Behaviors.receiveMessage{
        case Log(l, replyTo)=>
          ctx.log.info("log work in progress")
          replyTo ! LogDone()
          Behaviors.stopped
      }
    }
  }

  object JsonParser {
    sealed trait ParseCommand

    case class Parse(json: String, replyTo: ActorRef[ParseResponse]) extends ParseCommand

    sealed trait ParseResponse

    case class ParseDone() extends ParseResponse

    def apply(): Behavior[ParseCommand] = Behaviors.setup { ctx =>
      Behaviors.receiveMessage {
        case Parse(json, replyTo) =>
          ctx.log.info(s"parsing $json done")
          replyTo ! ParseDone()
          Behaviors.stopped
      }
    }
  }


  def apply(): Behavior[NotUsed] =
    Behaviors.setup { ctx =>
      val dispatcherActorRef = ctx.spawn(TaskDispatcher(), "disp.")

      dispatcherActorRef ! LogWork("bla bla bla")
      dispatcherActorRef ! ParseUrl("url url url")

      Behaviors.same
    }

  implicit val system = ActorSystem(Dispatcher(), "disp_actor_system")

  Thread.sleep(3000)
  system.terminate()
}