package com.merchant.config

import pureconfig.{ConfigObjectSource, ConfigSource}
import pureconfig.generic.auto._

object MerchantConfigs extends Configs {

  override val getConfig: ConfigObjectSource = ConfigSource.resources("application.conf")

  lazy val constraints: MerchantConfigs.OfferConstraints = getConfig.at("constraints").load[OfferConstraints] match {
    case Right(x) => x
    case Left(x) => throw new Exception(x.toString)
  }


  override def getDateConstraints: DateConstraints = constraints.dateConstraints

  override def getDecimalConstraints: DecimalConstraints = constraints.decimalConstraints

  override def getDescriptionConstraints: DescriptionConstraints = constraints.descriptionConstraints

}
