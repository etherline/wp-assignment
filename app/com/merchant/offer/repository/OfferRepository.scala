package com.merchant.offer.repository

import java.util.UUID

import com.google.inject.Singleton
import com.merchant.offer.model.OfferData
import play.api.Logger

import scala.collection.mutable
import scala.util.Try

/**
 * Temporary stub until we plug in a database
 */
@Singleton
class OfferRepository extends Repository[OfferData] {
  val logger = Logger(this.getClass())
  val offerMap = mutable.HashMap[UUID, OfferData]()

  override def save(offer: OfferData) = {
    logger.debug(s"save:${offer}")
    Try {
      offerMap.put(offer.uid, offer)
      true
    }.getOrElse(false)

  }

  override def list[QueryParams](params:QueryParams): Seq[OfferData] = ???

  override def expire(id: UUID): OfferData = ???
}
