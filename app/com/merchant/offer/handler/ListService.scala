package com.merchant.offer.handler

import com.google.inject.{ImplementedBy, Inject}
import com.merchant.offer.model.{Description, ListRequest, Offer, OfferData, OfferDate, Price}
import com.merchant.offer.repository.Repository

@ImplementedBy(classOf[ListServiceImpl])
trait ListService {
  def listOffers(listRequest:ListRequest): Seq[Offer]
}

class ListServiceImpl @Inject() (repository:Repository[OfferData]) {

  def listOffers(listRequest: ListRequest): Seq[Offer] = {
    (extract andThen retrieve andThen respond)(listRequest)
  }

  private val extract = doExtractParams _
  private val retrieve = doRetrieveOffers _
  private val respond = doTransformResults _

  private def doExtractParams(listRequest: ListRequest) = {
    listRequest.filter
  }

  private def doRetrieveOffers (params:String) = {
    repository.list(params)
  }

  private def doTransformResults (data: Seq[OfferData]) = {
    data.map(d => Offer(Description(d.description),
      Price(d.price,d.currency),
      OfferDate(d.expiry.toString),
      Some(d.uid)))
  }

}
