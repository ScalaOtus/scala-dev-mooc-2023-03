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
    task"Реализуйте метод `fullSequence`" ()


  def fullSequence[A](futures: List[Future[A]]): Future[(List[A], List[Throwable])] = {
    val p = Promise[Future[(List[A], List[Throwable])]]
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
}
