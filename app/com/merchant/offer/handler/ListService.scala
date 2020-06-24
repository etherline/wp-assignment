package com.merchant.offer.handler

import com.google.inject.Inject
import com.merchant.offer.model.{ListRequest, Offer}
import com.merchant.offer.repository.Repository

class ListService @Inject() (repository:Repository[Offer]){

  def listOffers(listRequest: ListRequest) = ???

  val extractParams = (listRequest: ListRequest) => {
    listRequest.filter
  }

}
