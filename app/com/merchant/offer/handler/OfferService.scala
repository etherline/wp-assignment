package com.merchant.offer.handler

import java.util.UUID
import com.merchant.offer.model._
import com.merchant.offer.repository.Repository
import javax.inject.Inject
import play.api.Logger

class OfferService @Inject()(repository: Repository[OfferData]) {
  val logger: Logger = Logger(this.getClass())

  def doCreateOffer(request: OfferRequest) = {
    logger.debug(s"doCreateOffer: request : ${request}")
    (validate _ andThen transform _ andThen persist _) (request.offer)
  }


  private def validate(offer: Offer) = {
    logger.debug(s"doValidate: request = ${offer}")

    val results = List(
      getDescriptionError(offer.description),
      getAmountError(offer.price.amount),
      getDateError(offer.expiryDateTime.strValue)
    )

    val errors = results collect {
      x: Option[String] =>
        x match {
          case Some(message) => message
        }
    }
    logger.debug(s"doValidate: errors = ${errors} ")
    RestOfferDTO(offer, errors)
  }

  private def transform(dto: OfferDTO[Offer, String]) = {
    val reqData = dto.data
    val validatedOffer: Option[OfferData] = dto.errors match {
      case Seq() => Some(
        OfferData(UUID.randomUUID(),
          reqData.description.value,
          reqData.price.amount, reqData.price.currency,
          OfferDate.toDateTime(reqData.expiryDateTime.strValue)))
      case _ => None
    }

    RestOfferDTO(validatedOffer, dto.errors)
  }

  private def persist(dto: OfferDTO[Option[OfferData], String]) = {
    dto.data match {
      case None => OfferResponse(None, dto.errors)
      case Some(offer) => {
        repository.save(offer) match { //side effect
          case true => {
            OfferResponse(
              Some(
                Offer(
                  Description(offer.description),
                  Price(offer.price, offer.currency),
                  OfferDate(offer.expiry.toString()),
                  Some(offer.uid))))
          }
          case false => OfferResponse(None) //TODO:error handling
        }
      }
    }
  }


  private def validator[R](value: R)(f: (R) => Option[String]) = f(value)

  private def getDescriptionError = validator(_: Description)(Description.validator)

  private def getAmountError = validator(_: Amount)(Amount.validator)

  private def getDateError = validator(_: String)(OfferDate.validator)

}
