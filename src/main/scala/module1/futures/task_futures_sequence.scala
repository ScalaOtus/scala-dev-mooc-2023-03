package module1.futures

import HomeworksUtils.TaskSyntax

import scala.concurrent.{ExecutionContext, Future}

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
  def fullSequence[A](futures: List[Future[A]])
                     (implicit exc: ExecutionContext): Future[(List[A], List[Throwable])] =
    futures.foldLeft(Future(List.empty[A], List.empty[Throwable])) {
      (acc, f) => f.flatMap(res => acc.map(x => (x._1 :+ res, x._2))).recoverWith(ex => acc.map(x => (x._1, x._2 :+ ex)))
    }
}
