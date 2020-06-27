package com.merchant.offer.model

import com.merchant.config.MerchantConfigs
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.Format

case class Description(value: String) extends AnyVal

object Description {

  private val constraints = MerchantConfigs.getDescriptionConstraints
  private val nullMessage = constraints.nullMessage
  private val outOfBoundsMessage = constraints.outOfBoundsMessage

  val validator: Description => Option[String] = {
    case Description(v) if v == null => Some(s"$nullMessage")
    case Description(v) if v.length <= constraints.maxLength && v.length >= constraints.minLength => None
    case description@(_: Description) => Some(s"$outOfBoundsMessage: ${description.value}")
  }

  implicit val format: Format[Description] = SingleFieldFormat.format(Description.apply, unlift(Description.unapply))
}
