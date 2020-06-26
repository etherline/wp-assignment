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

  private val offerMap = mutable.HashMap[UUID, OfferData]()

  override def save(offer: OfferData) = {
    logger.debug(s"save:${offer}")
    Try {
      offerMap.put(offer.uid, offer)
      true
    }.getOrElse(false)
  }

  override def list[String](params:String): Seq[OfferData] = {


    def getAll() = {
      offerMap.map(v => v._2).toSeq
    }

    def getCurrent() = {
      offerMap.filter(x => x._2.expiry.isAfterNow || x._2.expiry.isEqualNow).map(v => v._2).toSeq
    }

    def getExpired() = {
      offerMap.filter(x => x._2.expiry.isBeforeNow).map(v => v._2).toSeq
    }

    params match {
      case "all" => getAll
      case "current" => getCurrent
      case "expired" => getExpired
    }
  }

  override def expire(id: UUID): Option[OfferData] = ???
}
