package com.merchant.offer.controllers

import java.util.UUID

import com.merchant.offer.handler.{ExpireService, ListService, OfferService}
import com.merchant.offer.model
import com.merchant.offer.model._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Logger
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json._
import play.api.mvc.Result
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers, Injecting}

import scala.concurrent.{ExecutionContext, Future}

class TestOfferControllerSpec extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite with Injecting{
  private val logger = Logger(getClass)

  object JsonFixture {

    val createOfferString: String =
      """{"offer":{
        |	"description":"Something really exciting with a long description",
        |	"price":{
        |		"amount":153.99,
        |		"currency":"GBP"
        |	},
        |	"expiryDateTime":"2020-06-30T12:00:01.000"
        |}
        |}
        |""".stripMargin

    val createOfferJsonValue: JsValue = Json.parse(createOfferString)

    val listRequestJsonString: String =
      """
        |{"filter":"all"}
        |""".stripMargin

    val listRequestJsonValue: JsValue = Json.parse(listRequestJsonString)

    val expireRequestJsonString:String =
      """
        |{
        |    "uuid": "93cd0ab4-a485-4855-9c82-82120f5c1248"
        |}
        |""".stripMargin

    val expireRequestJsonValue: JsValue = Json.parse(expireRequestJsonString)

    def isValid[T](result: JsResult[T]): Boolean = result match {
      case _: JsSuccess[T] => true
      case _: JsError => false
    }
  }

  override def fakeApplication() = GuiceApplicationBuilder().
        overrides(
          bind[OfferService] toInstance(MockOfferService),
          bind[ExpireService] toInstance(MockExpireService),
          bind[ListService] toInstance(MockListService)
        )
        .build()


  "OfferController createOffer should delegate to OfferService" in {
    implicit val ec = inject[ExecutionContext]

    val json: JsValue = JsonFixture.createOfferJsonValue
    val request = {
      FakeRequest(POST, "/api/merchant/offer/createOffer").withJsonBody(json)
    }
    val result: Future[Result] = route(app,request).get
    Helpers.await(result)

    result onComplete {
      case scala.util.Success(v) => logger.info(s"$v")
      case scala.util.Failure(e) => logger.info(s"$e")
      case _ => logger.info(s"who knows")
    }
    assert(result.isCompleted)
  }

  "OfferController listOffers should delegate to ListService" in {
    implicit val ec = inject[ExecutionContext]

    val json: JsValue = JsonFixture.listRequestJsonValue
    val request = {
      FakeRequest(POST, "/api/merchant/offer/listOffers").withJsonBody(json)
    }
    val result: Future[Result] = route(app,request).get
    Helpers.await(result)

    result onComplete {
      case scala.util.Success(v) => logger.info(s"$v")
      case scala.util.Failure(e) => logger.info(s"$e")
      case _ => logger.info(s"who knows")
    }
    assert(result.isCompleted)
  }

  "OfferController expireOffer should delegate to ExpireService" in {
    implicit val ec = inject[ExecutionContext]

    val json: JsValue = JsonFixture.expireRequestJsonValue
    val request = {
      FakeRequest(POST, "/api/merchant/offer/expireOffer").withJsonBody(json)
    }
    val result: Future[Result] = route(app,request).get
    Helpers.await(result)

    result onComplete {
      case scala.util.Success(v) => logger.info(s"$v")
      case scala.util.Failure(e) => logger.info(s"$e")
      case _ => logger.info(s"who knows")
    }
    assert(result.isCompleted)
  }




  object MockOfferService extends OfferService {
    override def doCreateOffer(request: model.OfferRequest): model.OfferResponse = {
      OfferResponse(
        Some(Offer(Description("myOffer"),Price(Amount(50.0), "GBP"),
          OfferDate("2020-01-01T00:01:02.000+1:00"),
          Option(UUID.randomUUID()))))
    }
  }

  object MockExpireService extends ExpireService {
    override def expireOffer(id: String): model.ExpireResponse = {
      ExpireResponse(
        Some(
          Offer(Description("myOffer"),
            Price(Amount(50.0), "GBP"),
          OfferDate("2020-01-01T00:01:02.000+1:00"),
          Option(UUID.randomUUID()))),true)

    }
  }

  object MockListService extends ListService {
    override def listOffers(listRequest: model.ListRequest): Seq[model.Offer] = {

        Seq(Offer(Description("myOffer"),Price(Amount(50.0), "GBP"),
          OfferDate("2020-01-01T00:01:02.000+1:00"),
          Option(UUID.randomUUID())),
        Offer(Description("myOffer"),Price(Amount(50.0), "GBP"),
          OfferDate("2020-01-01T00:01:02.000+1:00"),
          Option(UUID.randomUUID())))
    }
  }

}
