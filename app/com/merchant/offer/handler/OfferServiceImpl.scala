package com.merchant.offer.handler

import java.util.UUID

import com.merchant.offer.model.{Amount, Description, Offer, OfferData, OfferDate, OfferRequest, OfferResponse, Price}
import com.merchant.offer.repository.Repository
import javax.inject.Inject
import play.api.Logger

class OfferServiceImpl @Inject()(repository: Repository[OfferData]) extends OfferService {
  private val logger: Logger = Logger(this.getClass)


  def doCreateOffer(request: OfferRequest): OfferResponse = {
    (validate andThen transform andThen persist) (request.offer)
  }


  private val validate = doValidate _
  private val transform = doGatedTransform _
  private val persist = doConditionalPersist _

  private def validator[R](value: R)(f: R => Option[String]) = f(value)

  private def getDescriptionError = validator(_: Description)(Description.validator)

  private def getAmountError = validator(_: Amount)(Amount.validator)

  private def getDateError = validator(_: String)(OfferDate.validator)


  private def doValidate(offer: Offer) = {
    logger.debug(s"doValidate: request = $offer")

    val results = List(
      getDescriptionError(offer.description),
      getAmountError(offer.price.amount),
      getDateError(offer.expiryDateTime.strValue)
    )

    val errors = results.flatMap(x => x.collect { case s: String => s })

    logger.debug(s"doValidate: errors = $errors ")
    RestOfferDTO(offer, errors)
  }

  private def doGatedTransform(dto: OfferDTO[Offer, String]) = {
    val offer = dto.data
    val validatedOffer: Option[OfferData] = dto.errors match {
      case Seq() => Some(
        OfferData(UUID.randomUUID(),
          offer.description.value,
          offer.price.amount, offer.price.currency,
          OfferDate.toDateTime(offer.expiryDateTime.strValue)))
      case _ => None
    }

    RestOfferDTO(validatedOffer, dto.errors)
  }

  private def doConditionalPersist(dto: OfferDTO[Option[OfferData], String]) = {

    dto.data match {
      case None => OfferResponse(None, dto.errors)
      case Some(offer) =>
        if (repository.save(offer)) {
          OfferResponse(
            Some(
              Offer(
                Description(offer.description),
                Price(offer.price, offer.currency),
                OfferDate(offer.expiry.toString()),
                Some(offer.uid))))
        } else {
          OfferResponse(None)
        }
    }
  }

}
