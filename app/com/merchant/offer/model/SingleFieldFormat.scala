package com.merchant.offer.model

import play.api.libs.json.Format

/**
 * Copied from:
 * https://gist.github.com/realpeterz/26cfd708575b75d26f82b4cc103b9f9b
 * to support use of AnyVal properties in case classes
 */
object SingleFieldFormat {

  import play.api.libs.functional.syntax._

  def format[A, B](f: A => B, g: B => A)(implicit fa: Format[A]): Format[B] = fa.inmap(f, g)
}
