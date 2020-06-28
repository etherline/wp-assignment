package com.merchant.offer.handler

import java.util.UUID

import com.google.inject.ImplementedBy
import com.merchant.offer.model._
import com.merchant.offer.repository.{OfferRepository, Repository}
import javax.inject.Inject
import play.api.Logger

@ImplementedBy(classOf[OfferServiceImpl])
trait OfferService {
  def doCreateOffer(request:OfferRequest): OfferResponse
}
