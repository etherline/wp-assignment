package com.merchant.offer.handler

import com.google.inject.{ImplementedBy, Inject}
import com.merchant.offer.model.{Description, ListRequest, Offer, OfferData, OfferDate, Price}
import com.merchant.offer.repository.Repository

@ImplementedBy(classOf[ListServiceImpl])
trait ListService {
  def listOffers(listRequest:ListRequest): Seq[Offer]
}
