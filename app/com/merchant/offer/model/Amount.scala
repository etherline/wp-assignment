package com.merchant.offer.model

import com.merchant.config.MerchantConfigs
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.Format

case class Amount(value: BigDecimal) extends AnyVal

object Amount {

  private val constraints = MerchantConfigs.getDecimalConstraints
  private val nullMessage = constraints.nullMessage
  private val outOfBoundsMessage = constraints.outOfBoundsMessage

  val validator: Amount => Option[String] = {
    case Amount(v) if v == null => Some(s"$nullMessage")
    case Amount(v) if (v.scale > constraints.scaleMax) || (v.scale < constraints.scaleMin) => Some(s"$outOfBoundsMessage: $v")
    case Amount(v) if (v.scale <= constraints.scaleMax) && (v.scale >= constraints.scaleMin) => None
  }

  implicit val format: Format[Amount] = SingleFieldFormat.format(Amount.apply, unlift(Amount.unapply))
}
