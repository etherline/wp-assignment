package com.merchant.offer.component

import javax.inject.Inject
import play.api.mvc.{BaseController, ControllerComponents}

class OfferControllerBase @Inject()(mcc : OfferControllerComponents) extends BaseController {
  override protected def controllerComponents: ControllerComponents = mcc
}
