package module1.futures

import HomeworksUtils.TaskSyntax

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success, Try}

object task_futures_sequence {

  /**
   * В данном задании Вам предлагается реализовать функцию fullSequence,
   * похожую на Future.sequence, но в отличии от нее,
   * возвращающую все успешные и не успешные результаты.
   * Возвращаемое тип функции - кортеж из двух списков,
   * в левом хранятся результаты успешных выполнений,
   * в правово результаты неуспешных выполнений.
   * Не допускается использование методов объекта Await и мутабельных переменных var
   */
  /**
   * @param futures список асинхронных задач
   * @return асинхронную задачу с кортежом из двух списков
   */
  def fullSequence2[A](futures: List[Future[A]])
                     (implicit ex: ExecutionContext): Future[(List[A], List[Throwable])] =
    task"Реализуйте метод `fullSequence`"()


  def fullSequence[A](futures: List[Future[A]]): Future[(List[A], List[Throwable])] = {
    val p = Promise[Future[(List[A], List[Throwable])]] // лишний промис :D
    val acc: (List[A], List[Throwable]) = futures
      .foldLeft((List.empty[A], List.empty[Throwable])) { (acc, fut) =>
        fut.onComplete {
          case Failure(exception) => (acc._1, exception :: acc._2)
          case Success(value) => (value :: acc._1, acc._2)
        }
        acc
      }
    Future(acc)
  }

  /**
   * Похоже я изобрел велосипед, но это было прикольно = )
   * канонический вариант мне приехал с одним из коммитов, когда я уже придумал свой,
   * так, что я решил не списывать.
   */
  def fullSequenceX[A](futures: List[Future[A]])(implicit ex: ExecutionContext): Future[(List[A], List[Throwable])] = {
    implicit val ctx = ExecutionContext.global //почему-то traverse не подхватывает имплисит ex

    def transformer[A](in: Future[A]): Future[(List[A], List[Throwable])] = {
      val mockIn = (List.empty[A], List.empty[Throwable])
      in.transform {
        case Success(value) => Success((value :: mockIn._1, mockIn._2))
        case Failure(cause) => Success((mockIn._1, cause :: mockIn._2))
      }
    }

    val traversed: Future[List[(List[A], List[Throwable])]] = Future.traverse(futures)(transformer)

    traversed.map { indexed =>
      indexed.tail.foldLeft(indexed.head) { (acc, tup) =>
        (tup._1 ::: acc._1, tup._2 ::: acc._2)
      }
    }.map { tupl =>
      (tupl._1.reverse, tupl._2.reverse) //подгоняем под тесты: в поставке задачи не был оговорен порядок сортировки
    }


  }

}
