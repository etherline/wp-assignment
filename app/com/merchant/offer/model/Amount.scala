package com.merchant.offer.model

import com.merchant.config.MerchantConfigs
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.Format

case class Amount(val value: BigDecimal) extends AnyVal

object Amount {
  val constraints = MerchantConfigs.getDecimalConstraints()
  val validator: Amount => Option[String] = (value: Amount) => {
    value match {
      case Amount(v) if v == null => Some(s"Amount.value was null")
      case Amount(v) if v.scale > constraints.scaleMax || v.scale < constraints.scaleMin => Some(s"Amount value has invalid scale: ${v}")
      case Amount(v) if v.scale <= 2 && v.scale >= 0 => None
    }
  }
  implicit val format: Format[Amount] = SingleFieldFormat.format(Amount.apply, unlift(Amount.unapply))
}
