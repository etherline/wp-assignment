package com.merchant.offer.controllers

import com.merchant.offer.component.{OfferControllerBase, OfferControllerComponents}
import com.merchant.offer.handler.{ExpireService, ListService}
import com.merchant.offer.model.{ExpireRequest, ListRequest, OfferRequest}
import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.Future

@Singleton
class OfferController @Inject()(cc: OfferControllerComponents) extends OfferControllerBase(cc) {
  val logger: Logger = Logger(this.getClass)

  private val offerService = cc.offerService
  val listService: ListService = cc.listService
  val expireService: ExpireService = cc.expireService

  def createOffer(): Action[AnyContent] = Action { implicit request =>

    logger.debug(s"createOffer: request : $request.body")
    val json: JsValue = request.body.asJson.get
    val offerRequest = json.as[OfferRequest]
    val response = offerService.doCreateOffer(offerRequest)
    logger.debug(s"createOffer: response = $response")
    Ok(Json.toJson(response))
  }

  def listOffers: Action[AnyContent] = Action.async {
    implicit request =>

      logger.debug(s"listOffers: request : $request.body")
      val json = request.body.asJson.get
      val listRequest = json.as[ListRequest]
      val response = listService.listOffers(listRequest)
      logger.debug(s"listOffers: response = $response")
      Future.successful(Ok(Json.toJson(response)))
  }

  def expireOffer: Action[AnyContent] = Action.async {
    implicit request =>

      logger.debug(s"expireOffer: request : $request.body")
      val json: Option[JsValue] = request.body.asJson
      json match {
        case None => Future.successful(BadRequest)
        case Some(v) => Future.successful(Ok(
          Json.toJson(
            expireService.expireOffer(v.as[ExpireRequest].uuid)))
        )
      }
  }

}
