package com.merchant.offer.model

import com.merchant.config.MerchantConfigs
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.Format

import scala.util.{Failure, Success, Try}

case class OfferDate(strValue: String) extends AnyVal

object OfferDate {

  private val constraints = MerchantConfigs.getDateConstraints

  val validator: String => Option[String] = (dateTime: String) => {
    val t = Try {
      toDateTime(dateTime)
    }

    t match {
      case Success(_) => None
      case Failure(e: Throwable) => Some(s"${e.getMessage}. ${constraints.message}")
    }
  }

  def toDateTime(dateTime: String) :DateTime = {
    val formatter = DateTimeFormat forPattern constraints.pattern
    formatter parseDateTime dateTime
  }

  def fromDateTimeZone(dateTime: DateTime):String = {
    val dt = toDateTimeZone(dateTime.toString)
    val formatter = DateTimeFormat forPattern constraints.pattern
    val parsedDt = formatter parseDateTime dt.toString
    parsedDt.toString
  }

  def toDateTimeZone(dateWithTimeZone: String): DateTime = {
    val formatter = DateTimeFormat forPattern constraints.timeZonePattern
    formatter parseDateTime dateWithTimeZone
  }

  implicit val format: Format[OfferDate] = SingleFieldFormat.format(OfferDate.apply, unlift(OfferDate.unapply))
}
