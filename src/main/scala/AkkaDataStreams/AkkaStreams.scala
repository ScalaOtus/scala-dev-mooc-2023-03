package AkkaDataStreams

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, RunnableGraph, Sink, Source}

import scala.concurrent.Future


object AkkaStreams extends App {

  implicit val system = ActorSystem("fusion")
  implicit val materializer = ActorMaterializer()

  val source: Source[Int, NotUsed] = Source(1 to 10)
  val flow = Flow[Int].map(x=>x+1)
  val sink = Sink.foreach[Int](println)

  val graph1 = source.to(sink)
  val graph2 = source.via(flow).to(sink)
//  graph2.run()

  //2

  val simpleSource: Source[Int, NotUsed] = Source(1 to 10)
  val simpleFlow = Flow[Int].map(_+1)
  val simpleFlow2 = Flow[Int].map(_*10)
  val simpleSink = Sink.foreach[Int](println)

  val runnableGraph = simpleSource
    .via(simpleFlow)
    .via(simpleFlow2)
    .to(simpleSink)

//  runnableGraph.run()


  val hardFlow3 = Flow[Int].map{ x=>
    Thread.sleep(1000)
    x+1
  }

  val hardFlow4 = Flow[Int].map{ x=>
    Thread.sleep(1000)
    x*10
  }

  simpleSource.async
    .via(hardFlow3).async
    .via(hardFlow4).async
    .to(simpleSink)
    .run()


}