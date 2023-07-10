package fs2streaming

import cats.effect.kernel.Async
import cats.effect.std.Queue
import cats.effect.{IO, IOApp, Resource, SyncIO}
import fs2.{Chunk, Pure, Stream}
import cats.effect.unsafe.implicits.global
import scala.concurrent.duration._
import java.time.Instant

object Streams extends IOApp.Simple{
  //1. clear stream
  val pureApply = Stream.apply(1,2,3)//.toList

  //2. extends to IO
  val ioApply: Stream[IO, Int] = pureApply.covary[IO]

  //3 stream from list
  val list = 1 :: 2 :: 3 :: 4 :: Nil
  Stream.emits(list)

  //4
  val a: Seq[Int] = pureApply.toList

  val aa: IO[List[Int]] = ioApply.compile.toList

  //5
  val unfolded = Stream.unfoldEval(0){ s =>
    val next = s +10
    if (s>=50) IO.none
    else IO.println(next.toString).as(Some((next.toString, next)))

  }

  //6
  val s = Stream.eval(IO.readLine).evalMap(x=> IO.println(s">>$x")).repeatN(3)

  //7 resources
  type Descriptor = String
  def openFile: IO[Descriptor] = IO.println("open file").as("file descriptor")
  def closeFile(desc: Descriptor): IO[Unit] = IO.println("close file")
  def readFile(desc: Descriptor): Stream[IO, Byte] =
    Stream.emits(s"File content".map(_.toByte).toArray)

  val fileResource = Resource.make(openFile)(closeFile)
  val resourceStream = Stream.resource(fileResource).flatMap(readFile).map(b=> b.toInt + 100)


  def writeToSocket[F[_] : Async](chunk: Chunk[String]): F[Unit] =
    Async[F].async_{ callback =>
      println(s"[thread: ${Thread.currentThread().getName}] :: Writing $chunk to socket")
      callback(Right())
    }

  //8 time
  val fixedDelayStream = Stream.fixedDelay[IO](1.second).evalMap(_=> IO.println(Instant.now))
  val fixedRateStream = Stream.fixedRate[IO](1.second).evalMap(_ => IO.println(Instant.now))

  //9 queue
  val queueIO = cats.effect.std.Queue.bounded[IO, Int](100)
  def putInQueue(queue: Queue[IO, Int], value: Int) =
    queue.offer(value)

  val queueStreamIO = for {
    q <- queueIO
    _ <- (IO.sleep(500.millis) *> putInQueue(q, 5)).replicateA(10).start
  } yield  Stream.fromQueueUnterminated(q)

  val queueStream = Stream.force(queueStreamIO)

  //10
  def increment(s: Stream[IO, Int]): Stream[IO, Int] = s.map(_+1)


  def run: IO[Unit] ={
    // for 7
   /*  Stream((1 to 100).map(_.toString) : _*)
      .chunkN(10)
      .covary[IO]
      .parEvalMapUnordered(10)(writeToSocket[IO])
      .compile
      .drain
*/

    /*
    fixedDelayStream.compile.drain
    2023-07-10T17:52:39.530015200Z
    2023-07-10T17:52:40.555766400Z
    2023-07-10T17:52:41.569965Z
    2023-07-10T17:52:42.574848400Z
    2023-07-10T17:52:43.591064800Z
     */
/*
    fixedRateStream.compile.drain
    2023-07-10T17:54:26.697460300Z
2023-07-10T17:54:27.679642200Z
2023-07-10T17:54:28.674432900Z
2023-07-10T17:54:29.675061600Z
2023-07-10T17:54:30.675080500Z
2023-07-10T17:54:31.676739800Z
 */

   // resourceStream.evalMap(IO.println).compile.drain

    //8
    //queueStream.evalMap(IO.println).compile.drain

    //9
//    queueStream.through(increment).through(increment).evalMap(IO.println).compile.drain

    //10
   // (queueStream ++ queueStream).evalMap(IO.println).compile.drain

    //11
    resourceStream.parEvalMap(3)(b=> IO.sleep(500.millis) *> IO.println(b)).compile.drain

  }

}