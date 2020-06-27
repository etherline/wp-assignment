package com.merchant.config

import pureconfig.ConfigObjectSource

/**
 * Core trait providing access to HOCON configuration files.
 */
trait Configs {

  import pureconfig.generic.auto._

  val getConfig: ConfigObjectSource

  def getDateConstraints: DateConstraints

  def getDecimalConstraints: DecimalConstraints

  def getDescriptionConstraints: DescriptionConstraints

  case class OfferConstraints(dateConstraints: DateConstraints,
                              decimalConstraints: DecimalConstraints,
                              descriptionConstraints: DescriptionConstraints)

  case class DateConstraints(pattern: String, timeZonePattern: String, message: String)

  case class DescriptionConstraints(maxLength: Int, minLength: Int, nullMessage: String, outOfBoundsMessage: String)

  case class DecimalConstraints(scaleMax: Int, scaleMin: Int, nullMessage: String, outOfBoundsMessage: String)

}