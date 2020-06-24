package com.merchant.offer.model

import com.merchant.config.MerchantConfigs
import org.scalacheck.Gen
import org.scalatest.{Matchers, PropSpec}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Logger

class TestDescriptionSpec extends PropSpec with ScalaCheckPropertyChecks with Matchers {
  val logger = Logger(this.getClass())

  val stringGen = (n: Int) => Gen.listOfN(n, Gen.alphaNumChar).map(_.mkString)
  val singleNumGen = Gen.chooseNum(0, 300)

  val descriptionGen = for {
    charCount <- singleNumGen
    value <- stringGen(charCount)
  } yield Description(value)

  /*val failCondition = (description: Description) => description.value.length > 250

  val checkCondition: (Option[_], Description => Boolean) => (Description, Description) => Boolean = (resultOption: Option[_], failCheck: Description => Boolean) => resultOption match {
    case Some(e) if failCheck(_:Description) == false => false
    case None if failCheck(_:Description) => true
    case _ => false
  }

  val isCorrect = checkCondition(_, failCondition) */

  property("Verify Description.validator checks description length") {
    val minLength = MerchantConfigs.getDescriptionConstraints().minLength
    val maxLength = MerchantConfigs.getDescriptionConstraints().maxLength

    forAll(descriptionGen) {
      sentence => {
        val resultOption = Description.validator.apply(sentence)
        logger.info(s"resultOption: ${resultOption}")

        val condition =
          sentence.value.length <= maxLength && sentence.value.length >= minLength

        val isSuccessExpected = condition
        logger.info(s"isSuccessExpected: ${isSuccessExpected} :  ${condition}")

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
