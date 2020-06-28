package com.merchant.offer.repository

import java.util.UUID

import com.google.inject.Singleton
import com.merchant.offer.model.OfferData
import org.joda.time.DateTime
import play.api.Logger

import scala.collection.mutable
import scala.util.Try

/**
 * Temporary stub until we plug in a database
 */
@Singleton
class OfferRepository extends Repository[OfferData] {
  private val logger = Logger(this.getClass)

  private val offerMap = mutable.HashMap[UUID, OfferData]()

  override def save(offer: OfferData): Boolean = {
    logger.debug(s"save:$offer")
    Try {
      offerMap.put(offer.uid, offer)
      true
    }.getOrElse(false)
  }

  //noinspection EmptyParenMethodAccessedAsParameterless
  override def list[String](params: String): Seq[OfferData] = {

    def getAll = {
      offerMap.values.toSeq
    }

    def getCurrent = {
      offerMap.values.filter(x => x.expiry.isAfterNow || x.expiry.isEqualNow).toSeq
    }

    def getExpired = {
      offerMap.values.filter(_.expiry.isBeforeNow).toSeq
    }

    params match {
      case "all" => getAll
      case "current" => getCurrent
      case "expired" => getExpired
      case _ => Seq()
    }
  }

  override def expire(id: UUID): Option[OfferData] = {
    val saved: Option[OfferData] = offerMap.get(id)
    val updated = updateExpiry(saved)
    updated match {
      case None => None
      case Some(v) => offerMap.put(id, v);
    }
    updated
  }

  private def updateExpiry(offer:Option[OfferData]) = {
    offer match {
      case None => None
      case Some(v) => Some(v.copy(expiry = DateTime.now()));
    }
  }
}
