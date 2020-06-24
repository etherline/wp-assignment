package com.merchant.offer.component

import com.merchant.offer.handler.{ExpireService, ListService, OfferService}
import javax.inject.Inject
import play.api.http.FileMimeTypes
import play.api.i18n.{Langs, MessagesApi}
import play.api.mvc.{ControllerComponents, DefaultActionBuilder, PlayBodyParsers}

case class OfferControllerComponents @Inject()(
                                                   offerService:OfferService,
                                                   listService:ListService,
                                                   expireService:ExpireService,
                                                   actionBuilder: DefaultActionBuilder,
                                                   parsers: PlayBodyParsers,
                                                   messagesApi: MessagesApi,
                                                   langs: Langs,
                                                   fileMimeTypes: FileMimeTypes,
                                                   executionContext: scala.concurrent.ExecutionContext)
  extends ControllerComponents
