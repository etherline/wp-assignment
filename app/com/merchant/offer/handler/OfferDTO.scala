package com.merchant.offer.handler

trait OfferDTO[T,U] {
  val data:T
  val errors:Seq[U]
}

case class RestOfferDTO[T,U](data: T, errors:Seq[U] = List()) extends OfferDTO[T,U]



