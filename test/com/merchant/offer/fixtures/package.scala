package com.merchant.offer

import java.util.{Calendar, Currency, UUID}

import com.merchant.config.MerchantConfigs
import com.merchant.offer.handler.RestOfferDTO
import com.merchant.offer.model.{Amount, Description, Offer, OfferData, OfferDate, OfferRequest, Price}
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import org.scalacheck.Gen

import scala.annotation.tailrec

package object fixtures {

  val minTextLength: Int = MerchantConfigs.getDescriptionConstraints.minLength
  private val maxTextLength = MerchantConfigs.getDescriptionConstraints.maxLength
  private val maxScale = MerchantConfigs.getDecimalConstraints.scaleMax
  private val minScale = MerchantConfigs.getDecimalConstraints.scaleMin
  private val dateTimePattern = MerchantConfigs.getDateConstraints.pattern
  private val timeZonePattern = MerchantConfigs.getDateConstraints.timeZonePattern
  private val dateTimeFormatter = DateTimeFormat forPattern dateTimePattern
  private val timeZoneFormatter = DateTimeFormat forPattern timeZonePattern

  val currencyCodes: Seq[String] = Currency.getAvailableCurrencies
    .toArray
    .map[Currency](_.asInstanceOf[Currency])
    .map[String](_.getCurrencyCode).toIndexedSeq

  val currencyCodeGen: Gen[String] = Gen.oneOf(currencyCodes)

  val stringGen: Int => Gen[String] = (n: Int) => Gen.listOfN(n, Gen.alphaNumChar).map(_.mkString)

  val singleNumGen: Gen[Int] = Gen.chooseNum(minTextLength, maxTextLength)

  val descriptionGen: Gen[Description] = for {
    num <- singleNumGen
    description <- stringGen(num)
  } yield Description(description)

  val amountGen: Gen[BigDecimal] = for {
    amt <- Gen.chooseNum(0, 100000000)
    amtDecimal = BigDecimal.valueOf(amt).setScale(maxScale)
  } yield amtDecimal

  val currentDateTimeGen: Gen[String] = Gen.calendar
    .map(_ => LocalDateTime.fromCalendarFields(Calendar.getInstance()).withMillisOfSecond(0))
    .map(_.toString)

  private val future = LocalDateTime.parse("2021-01-01T00:00:00.000", dateTimeFormatter)
  private val past = LocalDateTime.parse("2019-01-01T00:00:00.000", dateTimeFormatter)

  private val dateRange = getDates(Seq(future))(past)
  val dateRangeGen: Gen[String] = Gen.oneOf(dateRange).map(d => d.toString())

  val offerGen: Gen[Offer] = for {
    num <- singleNumGen
    description <- stringGen(num).map(Description(_))
    currency <- currencyCodeGen
    dateTime <- dateRangeGen.map(OfferDate(_))
    amount <- amountGen.map(Amount(_))
  } yield Offer(description, Price(amount, currency), dateTime)

  val offerRequestGen: Gen[OfferRequest] = for {
    offer <- offerGen
  } yield OfferRequest(offer)

  val offerDataGen: Gen[OfferData] = for {
    num <- singleNumGen
    description <- stringGen(num).map(Description(_))
    currency <- currencyCodeGen
    dateTime <- currentDateTimeGen.map(OfferDate(_))
    amount <- amountGen.map(Amount(_))
  } yield OfferData(UUID.randomUUID(),
    description.value,
    amount, currency,
    OfferDate.toDateTime(dateTime.strValue))

  val listOfOfferDataGen: Gen[List[OfferData]] = for {
    offers <- Gen.listOf(offerDataGen)
  } yield offers

  val filterGen: Gen[String] = Gen.oneOf("all", "current", "expired")


  val failAmountGen: Gen[BigDecimal] = for {
    amt <- Gen.chooseNum(0, 100000000)
    amtDecimal = BigDecimal.valueOf(amt).setScale(4)
  } yield amtDecimal

  val failAmountRequestGen: Gen[OfferRequest] = for {
    num <- singleNumGen
    description <- stringGen(num).map(Description(_))
    currency <- currencyCodeGen
    dateTime <- currentDateTimeGen.map(OfferDate(_))
    amount <- failAmountGen.map(Amount(_))
  } yield OfferRequest(Offer(description, Price(amount, currency), dateTime))

  val failDescriptionOfferGen: Gen[Offer] = for {
    description <- stringGen(maxTextLength + 1).map(Description(_))
    currency <- currencyCodeGen
    dateTime <- currentDateTimeGen.map(OfferDate(_))
    amount <- amountGen.map(Amount(_))
  } yield Offer(description, Price(amount, currency), dateTime)

  val failDTOGen: Gen[RestOfferDTO[Offer, String]] = for {
    failOffer <- failDescriptionOfferGen
  } yield RestOfferDTO(failOffer, Seq("I have failed on so many levels"))

  val succeedDTOGen: Gen[RestOfferDTO[Offer, Nothing]] = for {
    succeedOffer <- offerGen
  } yield RestOfferDTO(succeedOffer)


  def isBeforeNow(timeZoneStr: String): Boolean = {
    OfferDate.toDateTimeZone(timeZoneStr).isBeforeNow
  }

  def isEqualNow(timeZoneStr: String): Boolean = {
    OfferDate.toDateTimeZone(timeZoneStr).isEqualNow
  }

  @tailrec
  def getDates(r: Seq[LocalDateTime] = Seq())(implicit lim:LocalDateTime):Seq[LocalDateTime] = {
    r match {
      case h :: _ if h.isAfter(lim) => getDates(h.minusMonths(3) +: r)
      case _ => r
    }
  }
}
