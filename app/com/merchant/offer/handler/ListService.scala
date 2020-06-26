package com.merchant.offer.handler

import com.google.inject.Inject
import com.merchant.offer.model.{Description, ListRequest, Offer, OfferData, OfferDate, Price}
import com.merchant.offer.repository.Repository

class ListService @Inject() (repository:Repository[OfferData]){

  def listOffers(listRequest: ListRequest) = {
    (extractParams _ andThen retrieveOffers _ andThen transform _)(listRequest)
  }

  private def extractParams(listRequest: ListRequest) = {
    listRequest.filter
  }

  private def retrieveOffers (params:String) = {
    repository.list(params)
  }

  private def transform (data: Seq[OfferData]) = {
    data.map(d => Offer(Description(d.description),
      Price(d.price,d.currency),
      OfferDate(d.expiry.toString),
      Some(d.uid)))
  }

}
