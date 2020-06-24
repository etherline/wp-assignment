package com.merchant.offer.model

import java.util.UUID

import org.joda.time.DateTime

case class OfferData(uid: UUID, description: String, price:Amount, currency:String, expiry: DateTime)
