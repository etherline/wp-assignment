package com.merchant.offer.handler


import com.merchant.offer.model._
import com.merchant.offer.repository.OfferRepository
import org.joda.time.format.DateTimeFormat
import org.scalacheck.Gen
import org.scalatest.{Matchers, PrivateMethodTester, PropSpec}
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import play.api.Logger
import scala.language.postfixOps

/**
 * Generator driven property tests for OfferService
 *
 * @author kcampbell
 */
class TestOfferServiceSpec extends PropSpec with ScalaCheckDrivenPropertyChecks with Matchers with PrivateMethodTester {
  val logger = Logger(this.getClass())

  import com.merchant.testgens._

  //Reduced function's access to private - but keep test
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

    val accessValidate = PrivateMethod[RestOfferDTO[Offer,String]](Symbol("validate"))

    forAll(genRequest) {
      t => (handler invokePrivate accessValidate(t.offer) errors) should equal(List())
    }
  }

  //Reduced function's access to private - but keep test
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

    val accessValidate = PrivateMethod[RestOfferDTO[Offer,String]](Symbol("validate"))

    forAll(genFailAmountRequest) {
      t => (handler invokePrivate accessValidate(t.offer) errors).length should equal(1)
    }
  }

  //Reduced function's access to private - but keep test
  property("doValidate should report description failures") {

    val genFailDescriptionRequest = for {
      description <- stringGen(maxTextLength + 1).map(Description(_))
      currency <- currencyCodeGen
      dateTime <- dateTimeGen.map(OfferDate(_))
      amount <- amountGen.map(Amount(_))
    } yield OfferRequest(Offer(description, Price(amount, currency), dateTime))

    val repo = new OfferRepository()
    val handler = new OfferService(repo)

    val accessValidate = PrivateMethod[RestOfferDTO[Offer,String]](Symbol("validate"))

    forAll(genFailDescriptionRequest) {
      t => (handler invokePrivate accessValidate(t.offer) errors).length should equal(1)
    }
  }

  //Reduced function's access to private - but keep test
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
    val accessValidate = PrivateMethod[RestOfferDTO[Offer,String]](Symbol("validate"))

    forAll(genFailDateRequest) {
      t => {
          (handler invokePrivate accessValidate(t.offer) errors).length should equal(1)
      }
    }
  }

  //Reduced function's access to private - but keep test
  property("transform should not create an Offer from an OfferRequest when errors exist ") {
    val genFailDescriptionRequest = for {
      description <- stringGen(maxTextLength + 1).map(Description(_))
      currency <- currencyCodeGen
      dateTime <- dateTimeGen.map(OfferDate(_))
      amount <- amountGen.map(Amount(_))
    } yield RestOfferDTO(Offer(description, Price(amount, currency), dateTime),Seq("this is an error message"))

    val repo = new OfferRepository()
    val handler = new OfferService(repo)
    val accessTransform = PrivateMethod[RestOfferDTO[Option[OfferData],String]](Symbol("transform"))

    forAll(genFailDescriptionRequest) {
      t => {
        val result = (handler invokePrivate accessTransform(t)).data
        logger.trace(s"transform :${result}")

        val isResultCorrect = result match {
          case Some(_) => false
          case None => true
        }
        assert(isResultCorrect)
      }
    }
  }

  //Reduced function's access to private - but keep test
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
    val accessTransform = PrivateMethod[RestOfferDTO[Option[OfferData],String]](Symbol("transform"))

    forAll(genRequest) {
      t => (handler invokePrivate accessTransform(t)).data.isInstanceOf[Option[OfferData]]
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
