package com.merchant.offer

import java.util.UUID

import play.api.libs.json.{Format, Json}

package object model {

  trait OfferCommand

  trait OfferReply

  case class Offer(description: Description, price: Price, expiryDateTime: OfferDate, id: Option[UUID] = None)

  object Offer {
    implicit val format: Format[Offer] = Json.format
  }

  case class OfferRequest(offer:Offer)

  object OfferRequest {
    implicit val format: Format[OfferRequest] = Json.format
  }

  case class OfferResponse(offer: Option[Offer], errors: Seq[String] = Seq()) extends OfferReply

  object OfferResponse {
    implicit val format: Format[OfferResponse] = Json.format
  }

  case class ExpireRequest(guid: String) extends OfferCommand

  object ExpireRequest {
    implicit val format: Format[ExpireRequest] = Json.format
  }

  case class ExpireResponse(offer: Option[Offer], isConfirmed:Boolean) extends OfferReply

  object ExpireResponse {
    implicit val format: Format[ExpireResponse] = Json.format
  }

  case class ListRequest(filter: String) extends OfferCommand

  object ListRequest {
    implicit val format: Format[ListRequest] = Json.format
  }

  case class ListResponse(offers: Seq[Offer] = Seq()) extends OfferReply

  object ListResponse {
    implicit val format: Format[ListResponse] = Json.format
  }

}


