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
