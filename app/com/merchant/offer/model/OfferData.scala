package com.merchant.offer.model

import java.util.UUID

import org.joda.time.DateTime

//Represent the persisted form
case class OfferData(uid: UUID, description: String, price:Amount, currency:String, expiry: DateTime)
