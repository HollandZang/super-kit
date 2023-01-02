package com.holland.kit.base.monad

import cats.MonadError

object ShellConverts {
  implicit object optionAsMonadError extends MonadError[Option, String] {
    override def flatMap[A, B](fa: Option[A])(f: A => Option[B]): Option[B] = fa.flatMap(f)

    override def tailRecM[A, B](a: A)(f: A => Option[Either[A, B]]): Option[B] = f(a) match {
      case Some(Right(b)) => Option(b)
      case Some(Left(a)) => None
      case None => None
    }


    override def raiseError[A](e: String): Option[A] = None

    override def handleErrorWith[A](fa: Option[A])(f: String => Option[A]): Option[A] = None

    override def pure[A](x: A): Option[A] = Option(x)
  }
}
