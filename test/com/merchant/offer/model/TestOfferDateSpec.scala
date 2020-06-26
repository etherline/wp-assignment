package com.merchant.offer.model

import org.scalatest.{Matchers, PropSpec}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Logger

class TestOfferDateSpec extends PropSpec with ScalaCheckPropertyChecks with Matchers {
  val logger = Logger(this.getClass())

  import com.merchant.testgens._

  property("OfferDate.validator should return no errors for valid formatted date") {

    forAll(dateTimeGen) {
      dateStr => {
        val hasError = OfferDate.validator.apply(OfferDate(dateStr).strValue) match {
          case Some(_) => true
          case None => false
        }
        assertResult(false)(hasError)
      }
    }
  }

  property("OfferDate.validator should return an error for an invalidly formatted date") {
    //val formatter = DateTimeFormat forPattern "yyyy/MM/dd HH:mm:ss"
    val dateTime = "2020/12/01T13:00:01"

    val resultOption = OfferDate.validator.apply(dateTime)
    val hasError = resultOption match {
      case Some(_) => true
      case None => false
    }
    assertResult(true)(hasError)
  }

}
