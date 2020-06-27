package com.merchant.offer.handler


import com.merchant.offer.model._
import com.merchant.offer.repository.OfferRepository
import org.scalatest.{Matchers, PrivateMethodTester, PropSpec}
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import play.api.Logger
import shapeless.ops.coproduct.Inject

import scala.language.postfixOps

class TestOfferServiceSpec extends PropSpec with ScalaCheckDrivenPropertyChecks with Matchers with PrivateMethodTester {
  private val logger = Logger(this.getClass)

  import com.merchant.offer.fixtures._

  property("doCreateOffer should report errors when validation fails") {

    val repo = new OfferRepository()
    val handler = new OfferServiceImpl(repo)

    forAll(failAmountRequestGen) {
      t => assert(handler.doCreateOffer(t).errors.nonEmpty)
    }
  }

  property("doCreateOffer should return OfferResponse when validation succeeds") {

    val repo = new OfferRepository()
    val handler = new OfferServiceImpl(repo)

    forAll(offerRequestGen) {
      t => {
        val offerResponse = handler.doCreateOffer(t)
        assert(offerResponse.errors.isEmpty)
        assert(offerResponse.offer.isDefined)
      }
    }
  }

  //Reduced function's access to private - but keep test
  property("doValidate should enforce validation") {
    val repo = new OfferRepository()
    val handler = new OfferServiceImpl(repo)

    val accessValidate = PrivateMethod[RestOfferDTO[Offer, String]](Symbol("doValidate"))

    forAll(offerRequestGen) {
      t => (handler invokePrivate accessValidate(t.offer) errors) should equal(List())
    }
  }

  //Reduced function's access to private - but keep test
  property("doValidate should report amount failures") {

    val repo = new OfferRepository()
    val handler = new OfferServiceImpl(repo)
    val accessValidate = PrivateMethod[RestOfferDTO[Offer, String]](Symbol("doValidate"))

    forAll(failAmountRequestGen) {
      t => (handler invokePrivate accessValidate(t.offer) errors).length should equal(1)
    }
  }

  //Reduced function's access to private - but keep test
  property("doValidate should report description failures") {

    val repo = new OfferRepository()
    val handler = new OfferServiceImpl(repo)
    val accessValidate = PrivateMethod[RestOfferDTO[Offer, String]](Symbol("doValidate"))

    forAll(failDescriptionOfferGen) {
      t => (handler invokePrivate accessValidate(t) errors).length should equal(1)
    }
  }

  //Reduced function's access to private - but keep test
  property("doValidate should report date failures") {
    val dateTime = "2020/12/01 13:00:01"

    val genFailDateRequest = for {
      description <- stringGen(10).map(Description(_))
      currency <- currencyCodeGen
      amount <- amountGen.map(Amount(_))
    } yield OfferRequest(Offer(description, Price(amount, currency), OfferDate(dateTime)))


    val repo = new OfferRepository()
    val handler = new OfferServiceImpl(repo)
    val accessValidate = PrivateMethod[RestOfferDTO[Offer, String]](Symbol("doValidate"))

    forAll(genFailDateRequest) {
      t => {
        (handler invokePrivate accessValidate(t.offer) errors).length should equal(1)
      }
    }
  }

  //Reduced function's access to private - but keep test
  property("transform should not create an Offer from an OfferRequest when errors exist ") {
    val repo = new OfferRepository()
    val handler = new OfferServiceImpl(repo)
    val accessTransform = PrivateMethod[RestOfferDTO[Option[OfferData], String]](Symbol("doTransformRequest"))

    forAll(failDTOGen) {
      t => {
        val result = (handler invokePrivate accessTransform(t)).data
        logger.trace(s"transform :$result")

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

    val repo = new OfferRepository()
    val handler = new OfferServiceImpl(repo)
    val accessTransform = PrivateMethod[RestOfferDTO[Option[OfferData], String]](Symbol("doTransformRequest"))

    forAll(succeedDTOGen) {
      t => (handler invokePrivate accessTransform(t)).data.isInstanceOf[Option[OfferData]]
    }
  }

}
