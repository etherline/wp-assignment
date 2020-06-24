package com.merchant.offer.handler


import java.util.{Calendar, Currency}

import com.merchant.config.MerchantConfigs
import com.merchant.offer.model._
import com.merchant.offer.repository.OfferRepository
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import org.scalacheck.Gen
import org.scalatest.{Matchers, PropSpec}
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import play.api.Logger

/**
 * Generator driven property tests for OfferHandler
 *
 * @author kcampbell
 */
class TestOfferServiceSpec extends PropSpec with ScalaCheckDrivenPropertyChecks with Matchers {
  val logger = Logger(this.getClass())

  val minTextLength = MerchantConfigs.getDescriptionConstraints().minLength
  val maxTextLength = MerchantConfigs.getDescriptionConstraints().maxLength
  val scaleMax = MerchantConfigs.getDecimalConstraints().scaleMax

  val currencyCodes: Array[String] = Currency.getAvailableCurrencies
    .toArray
    .map[Currency](_.asInstanceOf[Currency])
    .map[String](_.getCurrencyCode)

  val currencyCodeGen = Gen.oneOf(currencyCodes)
  val stringGen = (n: Int) => Gen.listOfN(n, Gen.alphaNumChar).map(_.mkString)
  val singleNumGen = Gen.chooseNum(minTextLength, maxTextLength)
  val amountGen = for {
    amt <- Gen.chooseNum(0, 100000000)
    amtDecimal = BigDecimal.valueOf(amt).setScale(scaleMax)
  } yield amtDecimal

  val dateTimeGen = Gen.calendar
    .map(t => LocalDateTime.fromCalendarFields(Calendar.getInstance()).withMillisOfSecond(0))
    .map(_.toString)


  property("doValidate should enforce validation") {
    val genRequest = for {
      num <- singleNumGen
      description <- stringGen(num).map(Description(_))
      currency <- currencyCodeGen
      dateTime <- dateTimeGen.map(OfferDate(_))
      amount <- amountGen.map(Amount(_))
    } yield OfferRequest(Offer(description, Price(amount, currency), dateTime))

    val repo = new OfferRepository()
    val handler = new OfferService(repo)

    forAll(genRequest) {
      t => handler.validate(t.offer).errors should equal(List())
    }
  }

  property("doValidate should report amount failures") {
    val failAmountGen = for {
      amt <- Gen.chooseNum(0, 100000000)
      amtDecimal = BigDecimal.valueOf(amt).setScale(4)
    } yield amtDecimal

    val genFailAmountRequest = for {
      num <- singleNumGen
      description <- stringGen(num).map(Description(_))
      currency <- currencyCodeGen
      dateTime <- dateTimeGen.map(OfferDate(_))
      amount <- failAmountGen.map(Amount(_))
    } yield OfferRequest(Offer(description, Price(amount, currency), dateTime))

    val repo = new OfferRepository()
    val handler = new OfferService(repo)

    forAll(genFailAmountRequest) {
      t => handler.validate(t.offer).errors.length should equal(1)
    }
  }

  property("doValidate should report description failures") {

    val genFailDescriptionRequest = for {
      description <- stringGen(maxTextLength + 1).map(Description(_))
      currency <- currencyCodeGen
      dateTime <- dateTimeGen.map(OfferDate(_))
      amount <- amountGen.map(Amount(_))
    } yield OfferRequest(Offer(description, Price(amount, currency), dateTime))

    val repo = new OfferRepository()
    val handler = new OfferService(repo)

    forAll(genFailDescriptionRequest) {
      t => handler.validate(t.offer).errors.length should equal(1)
    }
  }

  property("doValidate should report date failures") {
    val formatter = DateTimeFormat forPattern "yyyy/MM/dd HH:mm:ss"
    val dateTime = "2020/12/01 13:00:01"

    val genFailDateRequest = for {
      description <- stringGen(10).map(Description(_))
      currency <- currencyCodeGen
      amount <- amountGen.map(Amount(_))
    } yield OfferRequest(Offer(description, Price(amount, currency), OfferDate(dateTime)))


    val repo = new OfferRepository()
    val handler = new OfferService(repo)

    forAll(genFailDateRequest) {
      t => {
        println(handler.validate(t.offer))
        handler.validate(t.offer).errors.length should equal(1)
      }
    }
  }

  property("transform should not create an Offer from an OfferRequest when errors exist ") {
    val genFailDescriptionRequest = for {
      description <- stringGen(maxTextLength + 1).map(Description(_))
      currency <- currencyCodeGen
      dateTime <- dateTimeGen.map(OfferDate(_))
      amount <- amountGen.map(Amount(_))
    } yield RestOfferDTO(Offer(description, Price(amount, currency), dateTime),Seq("this is an error message"))

    val repo = new OfferRepository()
    val handler = new OfferService(repo)

    forAll(genFailDescriptionRequest) {
      t => {
        val res = handler.transform(t)
        logger.info(s"transform :${res}")

        val isResultCorrect = res.data match {
          case Some(_) => false
          case None => true
        }
        assert(isResultCorrect)
      }
    }
  }

  property("transform should create an Offer from an OfferRequest when there are no errors") {
    val genRequest = for {
      num <- singleNumGen
      description <- stringGen(num).map(Description(_))
      currency <- currencyCodeGen
      dateTime <- dateTimeGen.map(OfferDate(_))
      amount <- amountGen.map(Amount(_))
    } yield RestOfferDTO(Offer(description, Price(amount, currency), dateTime),Seq[String]())

    val repo = new OfferRepository()
    val handler = new OfferService(repo)

    forAll(genRequest) {
      t => handler.transform(t).data.isInstanceOf[Option[OfferData]]
    }
  }

  property("doCreateOffer should do something") {
    val genFailDescriptionRequest = for {
      description <- stringGen(maxTextLength + 1).map(Description(_))
      currency <- currencyCodeGen
      dateTime <- dateTimeGen.map(OfferDate(_))
      amount <- amountGen.map(Amount(_))
    } yield OfferRequest(Offer(description, Price(amount, currency), dateTime))

    val repo = new OfferRepository()
    val handler = new OfferService(repo)

    forAll(genFailDescriptionRequest) {
      t => handler.doCreateOffer(t)
    }
  }

}
