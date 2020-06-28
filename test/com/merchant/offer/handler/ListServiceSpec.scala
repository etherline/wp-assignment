package com.merchant.offer.handler


import java.util.UUID

import com.merchant.offer.model.{ListRequest, Offer, OfferData, OfferDate}
import com.merchant.offer.repository.OfferRepository
import org.scalatest.{Matchers, PrivateMethodTester, PropSpec}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Logger

import scala.collection.mutable

class ListServiceSpec extends PropSpec with ScalaCheckPropertyChecks with Matchers with PrivateMethodTester {

  import com.merchant.offer.fixtures._

  private val logger = Logger(getClass)


  property("Verify ListService listOffers with filter `all` succeeds") {

    forAll(listOfOfferDataGen) {
      offerList =>
        val repo = new OfferRepository()
        val listService = new ListServiceImpl(repo)

        offerList.foreach(repo.save)

        val filteredOffers = listService listOffers ListRequest("all")
        val evaluated = filteredOffers.size == offerList.size
        assert(evaluated)
    }
  }

  property("Verify ListService listOffers with filter `expired` succeeds") {

    forAll(listOfOfferDataGen) {
      offerList =>
        val repo = new OfferRepository()
        val listService = new ListServiceImpl(repo)

        offerList.foreach(repo.save)

        val filteredOffers = listService listOffers ListRequest("expired")
        val evaluated =
          filteredOffers.forall(offer => OfferDate.toDateTimeZone(offer.expiryDateTime.strValue).isBeforeNow)

        assert(evaluated)
    }
  }

  property("Verify ListService listOffers with filter `current` succeeds") {

    forAll(listOfOfferDataGen) {
      offerList =>
        val repo = new OfferRepository()
        val listService = new ListServiceImpl(repo)

        offerList.foreach(repo.save)

        val filteredOffers = listService listOffers ListRequest("current")
        val evaluated =
          filteredOffers.foldLeft(true)((accum, offer) =>
            accum && isBeforeNow(offer.expiryDateTime.strValue) || isEqualNow(offer.expiryDateTime.strValue))

        assert(evaluated)
    }
  }


  //Reduced function's access to private - but test is still relevant
  property("Verify ListService extractParams succeeds") {
    val listService = new ListServiceImpl(null)
    val accessExtractParams = PrivateMethod[String](Symbol("doExtractParams"))

    forAll(filterGen) {
      filterParam =>
        val params = listService invokePrivate accessExtractParams(ListRequest(filterParam))
        assert(filterParam == params)
    }
  }

  //Reduced function's access to private - but test is still relevant
  property("Verify ListService transform from OfferData to Offers succeeds") {
    val repository = new OfferRepository()
    val listService = new ListServiceImpl(repository)
    val accessTransform = PrivateMethod[Seq[Offer]](Symbol("doTransformResults"))

    forAll(listOfOfferDataGen) {
      offerList: Seq[OfferData] =>
        val resultList = listService invokePrivate accessTransform(offerList)
        logger.trace(s"$resultList")
        assert(offerList.isInstanceOf[List[OfferData]])
        assert(resultList.isInstanceOf[List[Offer]])
        assert(resultList.size == offerList.size)
    }
  }

  //Reduced function's access to private - but test is still relevant
  property("Verify ListService retrieveOffers gets correct OfferData for `all` filter") {
    forAll(listOfOfferDataGen, "all") {
      (offerList, filter) =>
        val repo = new OfferRepository()
        val accessPrivateDataStore = PrivateMethod[mutable.HashMap[UUID, OfferData]](Symbol("offerMap"))
        val fromPrivateDataStore = repo invokePrivate accessPrivateDataStore()
        val listService = new ListServiceImpl(repo)
        val accessRetrieveOffers = PrivateMethod[Seq[OfferData]](Symbol("doRetrieveOffers"))

        offerList.foreach(repo.save)
        val extra = fromPrivateDataStore filter (element => offerList.contains(element))
        assert(fromPrivateDataStore.size == offerList.size)
        assert(extra.isEmpty)

        val filteredOffers = listService invokePrivate accessRetrieveOffers(filter)
        val evaluated = filteredOffers.size == offerList.size
        assert(evaluated)
    }
  }


  //Reduced function's access to private - but test is still relevant
  property("Verify ListService retrieveOffers gets correct OfferData for `current` filter") {
    forAll(listOfOfferDataGen, "current") {
      (offerList, filter) =>
        val repo = new OfferRepository()
        val accessPrivateDataStore = PrivateMethod[mutable.HashMap[UUID, OfferData]](Symbol("offerMap"))
        val fromPrivateDataStore = repo invokePrivate accessPrivateDataStore()
        val listService = new ListServiceImpl(repo)
        val accessRetrieveOffers = PrivateMethod[Seq[OfferData]](Symbol("doRetrieveOffers"))

        offerList.foreach(repo.save)
        val extra = fromPrivateDataStore filter (element => offerList.contains(element))
        assert(fromPrivateDataStore.size == offerList.size)
        assert(extra.isEmpty)

        val filteredOffers = listService invokePrivate accessRetrieveOffers(filter)
        val evaluated = filteredOffers.forall(offer => offer.expiry.isEqualNow || offer.expiry.isAfterNow)
        assert(evaluated)
    }
  }

  //Reduced function's access to private - but test is still relevant
  property("Verify ListService retrieveOffers gets correct OfferData for `expired` filter") {
    forAll(listOfOfferDataGen, "expired") {
      (offerList, filter) =>
        val repo = new OfferRepository()
        val accessPrivateDataStore = PrivateMethod[mutable.HashMap[UUID, OfferData]](Symbol("offerMap"))
        val fromPrivateDataStore = repo invokePrivate accessPrivateDataStore()
        val listService = new ListServiceImpl(repo)
        val accessRetrieveOffers = PrivateMethod[Seq[OfferData]](Symbol("doRetrieveOffers"))

        offerList.foreach(repo.save)
        val extra = fromPrivateDataStore filter (element => offerList.contains(element))
        assert(fromPrivateDataStore.size == offerList.size)
        assert(extra.isEmpty)

        val filteredOffers = listService invokePrivate accessRetrieveOffers(filter)
        val evaluated = filteredOffers.forall(offer => offer.expiry.isBeforeNow)
        assert(evaluated)
    }
  }

}
