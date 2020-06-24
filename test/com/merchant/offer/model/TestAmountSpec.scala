package com.merchant.offer.model

import com.merchant.config.MerchantConfigs
import org.scalacheck.Gen
import org.scalatest.{Matchers, PropSpec}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Logger

class TestAmountSpec extends PropSpec with ScalaCheckPropertyChecks with Matchers {
  val logger = Logger(this.getClass())
  
  val amountGen = for {
    amt <- Gen.chooseNum(0, 100000000)
    amtDecimal = BigDecimal.valueOf(amt)
  } yield amtDecimal / 1000


  property("Verify Amount.validator validates decimal places correctly") {
    val minScale = MerchantConfigs.getDecimalConstraints().scaleMin
    val maxScale = MerchantConfigs.getDecimalConstraints().scaleMax

    forAll(amountGen) {
      amt => {
        logger.info(s"testValue amt: ${amt}")

        val resultOption = Amount.validator.apply(Amount(amt))
        logger.info(s"resultOption: ${resultOption}")

        val isSuccessExpected = amt.scale <= maxScale && amt.scale >= minScale
        logger.info(isSuccessExpected + amt.scale.toString)

        val isCorrectResult = resultOption match {
          case Some(e) if !isSuccessExpected == false => false
          case None if isSuccessExpected => true
          case _ => false
        }
        logger.info("------------------")

        assertResult(isSuccessExpected)(isCorrectResult)
      }
    }
  }
}
