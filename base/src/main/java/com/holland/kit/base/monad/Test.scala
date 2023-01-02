package com.holland.kit.base.monad

//import cats.MonadError

object Test extends App {
//    def getDataById[F[_] : MonadError[F[_], String]](id: String): F[String] = {
//      val F = implicitly[MonadError[F, String]]
//      if (id.toUpperCase() == "A") {
//        F.pure("Hello Monad")
//      }
//      else {
//        F.raiseError(s"Id $id is not found")
//      }
//    }
//    import ShellConverts.optionAsMonadError

  def getDataById(id: String): Either[String, String] = {
    if (id.toUpperCase() == "A") {
      Right("Hello Monad")
    }
    else {
      Left(s"Id $id is not found")
    }
  }

//  def getDataById(id: String): Option[ String] = {
//    if (id.toUpperCase() == "A") {
//      Option("Hello Monad")
//    }
//    else {
//      None
//    }
//  }

  val tmp = for {
    x <- getDataById("A")
    x1 <- getDataById("z")
  } yield (x, x1)


  println(tmp)
}
