package catsconcurrency.cats_effect_homework

import cats.effect.Sync
import cats.implicits._
import Wallet._

import java.nio.file.{Files, Paths}
import scala.util.Try

// DSL управления электронным кошельком
trait Wallet[F[_]] {
  // возвращает текущий баланс
  def balance: F[BigDecimal]

  // пополняет баланс на указанную сумму
  def topup(amount: BigDecimal): F[Unit]

  // списывает указанную сумму с баланса (ошибка если средств недостаточно)
  def withdraw(amount: BigDecimal): F[Either[WalletError, Unit]]
}

// Игрушечный кошелек который сохраняет свой баланс в файл
// todo: реализовать используя java.nio.file._
// Насчёт безопасного конкуррентного доступа и производительности не заморачиваемся, делаем максимально простую рабочую имплементацию.
// (Подсказка - можно читать и сохранять файл на каждую операцию).
// Важно аккуратно и правильно завернуть в IO все возможные побочные эффекты.
//
// функции которые пригодятся:
// - java.nio.file.Files.write
// - java.nio.file.Files.readString
// - java.nio.file.Files.exists
// - java.nio.file.Paths.get
final class FileWallet[F[_] : Sync](id: WalletId) extends Wallet[F] {
  def getPath(id: WalletId) = Paths.get(id)
  def balance: F[BigDecimal] = Sync[F].delay(BigDecimal(Files.readString(getPath(id))))

  def topup(amount: BigDecimal): F[Unit] = Sync[F].delay({
    val cur = Try(Files.readString(getPath(id)).toInt).getOrElse(0)
    Files.writeString(getPath(id), (BigDecimal(cur) + amount).toString())
  })

  def withdraw(amount: BigDecimal): F[Either[WalletError, Unit]] = Sync[F].delay({
    val cur = BigDecimal(Files.readString(getPath(id)))
    Either.cond(amount < cur, {
      Files.writeString(getPath(id), (cur - amount).toString())
      ()
    }, BalanceTooLow)
  })
}

object Wallet {

  // todo: реализовать конструктор
  // внимание на сигнатуру результата - инициализация кошелька имеет сайд-эффекты
  // Здесь нужно использовать обобщенную версию уже пройденного вами метода IO.delay,
  // вызывается она так: Sync[F].delay(...)
  // Тайпкласс Sync из cats-effect описывает возможность заворачивания сайд-эффектов
  def fileWallet[F[_] : Sync](id: WalletId): F[Wallet[F]] = Sync[F].delay({
    val path = Paths.get(id)
    if(!Files.exists(path)) {
      Files.createFile(path)
      Files.writeString(path, "0")
    }
    new FileWallet(id)
  })

  type WalletId = String

  sealed trait WalletError
  case object BalanceTooLow extends WalletError
}
