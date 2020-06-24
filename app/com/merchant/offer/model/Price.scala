package com.merchant.offer.model

import play.api.libs.json.{Format, Json}

case class Price(amount: Amount, currency: String)

object Price {
  implicit val format: Format[Price] = Json.format
}
