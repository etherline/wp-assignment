package com.merchant.offer.model

import java.util.{Calendar, UUID}

import org.joda.time.LocalDateTime
import org.junit.runner.RunWith
import org.scalatest.{FunSuite, Matchers}
import org.scalatestplus.junit.JUnitRunner
import org.scalatestplus.scalacheck.Checkers
import play.api.libs.json._

import scala.language.postfixOps

@RunWith(classOf[JUnitRunner])
class TestModelFormatSpec extends FunSuite with Matchers with Checkers {

  object JsonFixture {

    val createOfferRequestString: String =
      """{
        | "offer":{
        | "description" : "A wonderful offer to buy, hurry don't wait!",
        | "price" : {
        | "amount" : 98.76,
        | "currency" : "GBP"},
        | "expiryDateTime" : "01-09-2020T23:59:59.000"
        |   }
        |}
        |""".stripMargin

    val createOfferJsonValue: JsValue = Json.parse(createOfferRequestString)

    def isValid[T](result: JsResult[T]): Boolean = result match {
      case _: JsSuccess[T] => true
      case _: JsError => false
    }
  }

  test("CreateOfferRequest JSON is correctly formed") {
    assert((JsonFixture.createOfferJsonValue \"offer"\ "description").as[String] == "A wonderful offer to buy, hurry don't wait!")
    assert((JsonFixture.createOfferJsonValue \"offer"\ "expiryDateTime").as[String] == "01-09-2020T23:59:59.000")
    assert((JsonFixture.createOfferJsonValue \"offer"\ "price" \ "amount").as[BigDecimal] == BigDecimal(98.76))
    assert((JsonFixture.createOfferJsonValue \"offer"\ "price" \ "currency").as[String] == "GBP")
  }

  test("CreateOfferRequest JSON validates") {
    val validatedDescription = (JsonFixture.createOfferJsonValue \"offer"\ "description").validate[String]
    assert(JsonFixture.isValid(validatedDescription))
    val validatedExpiryDateTime = (JsonFixture.createOfferJsonValue \ "offer"\"expiryDateTime").validate[String]
    assert(JsonFixture.isValid(validatedExpiryDateTime))
    val validatedPriceAmount = (JsonFixture.createOfferJsonValue \"offer"\ "price" \ "amount").validate[BigDecimal]
    assert(JsonFixture.isValid(validatedPriceAmount))
    val validatedPriceCurrency = (JsonFixture.createOfferJsonValue \"offer"\ "price" \ "currency").validate[String]
    assert(JsonFixture.isValid(validatedPriceCurrency))
  }

  test("CreateOfferRequest JSON is parseable to case class with implicit Format from companion object") {
    val request = Json.fromJson[OfferRequest](JsonFixture.createOfferJsonValue).get

    assertResult(Description("A wonderful offer to buy, hurry don't wait!"))(request.offer.description)
    assertResult(Amount(BigDecimal(98.76)))(request.offer.price.amount)
    assertResult("GBP")(request.offer.price.currency)
    assertResult(OfferDate("01-09-2020T23:59:59.000"))(request.offer.expiryDateTime)
  }

  test("CreateOfferResponse case class is parseable to JSON") {
    val json: JsValue = Json.toJson(OfferResponse(Some(Offer(Description("description"), Price(Amount(125.0),"GBP"),OfferDate("01-09-2020T23:59:59.000"),Some(UUID.randomUUID())))))
    assert(json.isInstanceOf[JsValue])
  }

  test("localDateTime info only") {
    println(LocalDateTime.fromCalendarFields(Calendar.getInstance()).withMillisOfSecond(0))
  }


}
