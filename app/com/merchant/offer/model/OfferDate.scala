package com.merchant.offer.model

import com.merchant.config.MerchantConfigs
import org.joda.time.format.DateTimeFormat
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.Format

import scala.util.{Failure, Success, Try}

case class OfferDate(val strValue: String) extends AnyVal

object OfferDate {
  val constraints = MerchantConfigs.getDateConstraints()

  val validator = (dateTime: String) => {
    val t = Try {
      toDateTime(dateTime)
    }

    t match {
      case Success(_) => None
      case Failure(e: Throwable) => Some(s"${e.getMessage}. ${constraints.message}")
    }
  }

  def toDateTime(dateTime: String) = {
    val formatter = DateTimeFormat forPattern constraints.pattern
    formatter parseDateTime dateTime
  }

  implicit val format: Format[OfferDate] = SingleFieldFormat.format(OfferDate.apply, unlift(OfferDate.unapply))
}
