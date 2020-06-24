package com.merchant.config

import pureconfig.ConfigObjectSource

/**
  * Core trait providing access to HOCON configuration files.
  */
trait Configs {
  import pureconfig.generic.auto._

  val getConfig: ConfigObjectSource

  def getConfig(path:String):ConfigObjectSource

  def getDateConstraints(): DateConstraints

  def getDecimalConstraints(): DecimalConstraints

  def getDescriptionConstraints(): DescriptionConstraints

  case class OfferConstraints(dateConstraints: DateConstraints,
                              decimalConstraints: DecimalConstraints,
                              descriptionConstraints: DescriptionConstraints)

  case class DateConstraints(pattern: String, message: String)

  case class DescriptionConstraints(maxLength: Int, minLength: Int)

  case class DecimalConstraints(scaleMax: Int, scaleMin: Int)
}