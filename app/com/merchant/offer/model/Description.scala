package com.merchant.offer.model

import com.merchant.config.MerchantConfigs
import play.api.Logger
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.Format

case class Description(val value: String) extends AnyVal

object Description {
  val logger = Logger("Description")
  val constraints = MerchantConfigs.getDescriptionConstraints()
  logger.debug(s"Description<init>: constraints:${constraints}")

  val validator: Description => Option[String] = (description: Description) => {
    description match {
      case Description(v) if v == null => Some(s" Description.value is null")
      case Description(v) if (v.length <= constraints.maxLength && v.length >= constraints.minLength) => None
      case _: Description => Some(s"Invalid Description.value: ${description.value}")
    }
  }
  implicit val format: Format[Description] = SingleFieldFormat.format(Description.apply, unlift(Description.unapply))
}
