package com.merchant

import java.util.{Calendar, Currency, UUID}

import com.merchant.config.MerchantConfigs
import com.merchant.offer.model.{Amount, Description, Offer, OfferData, OfferDate, OfferRequest, Price}
import org.joda.time.LocalDateTime
import org.scalacheck.Gen

/**
 * Generators used in common for testing
 */
package object testgens {

  val minTextLength = MerchantConfigs.getDescriptionConstraints().minLength
  val maxTextLength = MerchantConfigs.getDescriptionConstraints().maxLength
  val maxScale = MerchantConfigs.getDecimalConstraints().scaleMax
  val minScale = MerchantConfigs.getDecimalConstraints().scaleMin

  val currencyCodes: Array[String] = Currency.getAvailableCurrencies
    .toArray
    .map[Currency](_.asInstanceOf[Currency])
    .map[String](_.getCurrencyCode)

  val currencyCodeGen = Gen.oneOf(currencyCodes)

  val stringGen = (n: Int) => Gen.listOfN(n, Gen.alphaNumChar).map(_.mkString)

  val singleNumGen = Gen.chooseNum(minTextLength, maxTextLength)

  val descriptionGen = for {
    num <- singleNumGen
    description <- stringGen(num)
  } yield Description(description)

  val amountGen = for {
    amt <- Gen.chooseNum(0, 100000000)
    amtDecimal = BigDecimal.valueOf(amt).setScale(maxScale)
  } yield amtDecimal

  val dateTimeGen = Gen.calendar
    .map(t => LocalDateTime.fromCalendarFields(Calendar.getInstance()).withMillisOfSecond(0))
    .map(_.toString)

  val offerRequestGen = for {
    num <- singleNumGen
    description <- stringGen(num).map(Description(_))
    currency <- currencyCodeGen
    dateTime <- dateTimeGen.map(OfferDate(_))
    amount <- amountGen.map(Amount(_))
  } yield OfferRequest(Offer(description, Price(amount, currency), dateTime))

  val offerDataGen = for {
    num <- singleNumGen
    description <- stringGen(num).map(Description(_))
    currency <- currencyCodeGen
    dateTime <- dateTimeGen.map(OfferDate(_))
    amount <- amountGen.map(Amount(_))
  } yield (OfferData(UUID.randomUUID(),
    description.value,
    amount, currency,
    OfferDate.toDateTime(dateTime.strValue)))

  val listOfOfferDataGen = for {
    offers <- Gen.listOf(offerDataGen)
  } yield offers

  val filterGen = Gen.oneOf("all", "current", "expired")

}
