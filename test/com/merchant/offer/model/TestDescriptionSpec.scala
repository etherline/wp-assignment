package com.merchant.offer.model

import com.merchant.config.MerchantConfigs
import org.scalatest.{Matchers, PropSpec}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Logger

class TestDescriptionSpec extends PropSpec with ScalaCheckPropertyChecks with Matchers {
  val logger = Logger(this.getClass())

  import com.merchant.testgens._

  property("Verify Description.validator checks description length") {
    val minLength = MerchantConfigs.getDescriptionConstraints().minLength
    val maxLength = MerchantConfigs.getDescriptionConstraints().maxLength

    forAll(descriptionGen) {
      sentence => {
        val resultOption = Description.validator.apply(sentence)
        logger.trace(s"resultOption: ${resultOption}")

        val condition =
          sentence.value.length <= maxLength && sentence.value.length >= minLength

        val isSuccessExpected = condition
        logger.trace(s"isSuccessExpected: ${isSuccessExpected} :  ${condition}")

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
