package com.merchant.offer.handler

import java.util.UUID

import com.google.inject.{ImplementedBy, Inject}
import com.merchant.offer.model.{Description, ExpireResponse, Offer, OfferData, OfferDate, Price}
import com.merchant.offer.repository.Repository

import scala.util.Try

@ImplementedBy(classOf[ExpireServiceImpl])
trait ExpireService {
  def expireOffer(id:String) : ExpireResponse
}


class ExpireServiceImpl @Inject()(repository: Repository[OfferData]) {

  def expireOffer(id: String): ExpireResponse = {
    (checkId andThen expire andThen respond) (id)
  }

  private val checkId = doCheckId _
  private val expire = doExpire _
  private val respond = doTransformResult _


  private def doCheckId(id: String) = {
    Try {
      Some(UUID.fromString(id))
    }.getOrElse(None)
  }

  private def doExpire(uid: Option[UUID]) = uid match {
    case None => None
    case Some(v) =>
      repository.expire(v)
  }

  private def doTransformResult(data: Option[OfferData]) = {

    data match {
      case None => ExpireResponse(None, isConfirmed = false)
      case Some(v) =>
        ExpireResponse(Some(
          Offer(
            Description(v.description),
            Price(v.price, v.currency),
            OfferDate(OfferDate.fromDateTimeZone(v.expiry)),
            Some(v.uid))
        ), isConfirmed = true)
    }
  }
}
