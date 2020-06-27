package com.merchant.offer.model

import com.merchant.config.MerchantConfigs
import org.scalatest.{Matchers, PropSpec}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Logger

class TestAmountSpec extends PropSpec with ScalaCheckPropertyChecks with Matchers {
  private val logger = Logger(this.getClass)

  import com.merchant.offer.fixtures._


  property("Verify Amount.validator validates decimal places correctly") {
    val minScale = MerchantConfigs.getDecimalConstraints.scaleMin
    val maxScale = MerchantConfigs.getDecimalConstraints.scaleMax

    forAll(amountGen) {
      amt => {
        logger.trace(s"testValue amt: $amt")

        val resultOption = Amount.validator.apply(Amount(amt))
        logger.trace(s"resultOption: $resultOption")

        val isSuccessExpected = amt.scale <= maxScale && amt.scale >= minScale
        logger.trace(s"$isSuccessExpected  ${amt.scale.toString}")

        val isCorrectResult = resultOption match {
          case Some(_) if isSuccessExpected => false
          case None if isSuccessExpected => true
          case _ => false
        }
        logger.trace("------------------")

        assertResult(isSuccessExpected)(isCorrectResult)
      }
    }
  }
}
