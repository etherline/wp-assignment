package com.merchant.offer.repository

import java.util.UUID

import com.google.inject.ImplementedBy

//Ideally would move this binding out into a Module
@ImplementedBy(classOf[OfferRepository])
trait Repository[T] {

  def save(t: T): Boolean

  def list[P](params:P): Seq[T]

  def expire(id: UUID): Option[T]
}
