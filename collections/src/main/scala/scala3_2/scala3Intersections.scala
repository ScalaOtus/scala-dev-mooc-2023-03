package scala3_2.extendingclasses


//1. intersection

trait Test1:
  def reset() : Unit

trait Test2[T]:
  def add(t:T):Unit

def f (x: Test1 & Test2[String]) =
  x.reset()
  x.add("sdfsdf")

// union
case class UserName(name: String)
case class Password(pass: Int)
val password: Password = Password(123)
val userName: UserName = UserName("123")
val either: Password | UserName = if true then password else userName


