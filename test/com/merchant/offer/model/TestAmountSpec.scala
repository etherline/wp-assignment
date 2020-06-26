package com.merchant.offer.model

import org.scalatest.{Matchers, PropSpec}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Logger

class TestAmountSpec extends PropSpec with ScalaCheckPropertyChecks with Matchers {
  val logger = Logger(this.getClass())

  import com.merchant.testgens._


  property("Verify Amount.validator validates decimal places correctly") {

    forAll(amountGen) {
      amt => {
        logger.trace(s"testValue amt: ${amt}")

        val resultOption = Amount.validator.apply(Amount(amt))
        logger.trace(s"resultOption: ${resultOption}")

        val isSuccessExpected = amt.scale <= maxScale && amt.scale >= minScale
        logger.trace(s"${isSuccessExpected}  ${amt.scale.toString}")

        val isCorrectResult = resultOption match {
          case Some(e) if !isSuccessExpected == false => false
          case None if isSuccessExpected => true
          case _ => false
        }
        logger.trace("------------------")

        assertResult(isSuccessExpected)(isCorrectResult)
      }
    }
  }
}
