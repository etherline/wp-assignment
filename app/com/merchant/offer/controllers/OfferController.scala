package com.merchant.offer.controllers

import com.merchant.offer.component.{OfferControllerBase, OfferControllerComponents}
import com.merchant.offer.model.{ListRequest, OfferRequest}
import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.Future

@Singleton
class OfferController @Inject() (cc: OfferControllerComponents) extends OfferControllerBase(cc) {
    val logger: Logger = Logger(this.getClass())

    def createOffer() = Action.async { implicit request =>
        logger.debug(s"createOffer: request : ${request.body}")
        val json = request.body.asJson.get
        val offerRequest = json.as[OfferRequest]
        val response = cc.offerService.doCreateOffer(offerRequest)
        logger.debug(s"createOffer: response = ${response}")
        Future.successful(Ok(Json.toJson(response)))
    }

    def listOffers: Action[AnyContent] = Action.async {
        implicit request =>
            logger.debug(s"listOffers: request : ${request.body}")
            val json = request.body.asJson.get
            val listRequest = json.as[ListRequest]
        val response = cc.listService.listOffers(listRequest)
            logger.debug(s"listOffers: response = ${response}")
        Future.successful(Ok(Json.toJson(response)))
    }

    def expireOffer(id:String): Action[AnyContent] = ???

}
