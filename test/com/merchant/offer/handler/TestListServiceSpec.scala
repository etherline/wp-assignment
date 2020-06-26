package com.merchant.offer.handler


import java.util.UUID

import com.merchant.offer.model.{ListRequest, Offer, OfferData}
import com.merchant.offer.repository.OfferRepository
import org.scalatest.{Matchers, PrivateMethodTester, PropSpec}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Logger

import scala.collection.mutable

class TestListServiceSpec extends PropSpec with ScalaCheckPropertyChecks with Matchers with PrivateMethodTester {

  import com.merchant.testgens._

  val logger = Logger(getClass())


  property("Verify ListService listOffers succeeds") {
    fail("not yet implemented")
  }


  //Reduced function's access to private - but test is still relevant
  property("Verify ListService extractParams succeeds") {
    val listService = new ListService(null)
    val accessExtractParams = PrivateMethod[String](Symbol("extractParams"))

    forAll(filterGen) {
      filterParam =>
        val params = listService invokePrivate accessExtractParams(ListRequest(filterParam))
        assert(filterParam == params)
    }
  }

  //Reduced function's access to private - but test is still relevant
  property("Verify ListService transform from OfferData to Offers succeeds") {
    val repository = new OfferRepository()
    val listService = new ListService(repository)
    val accessTransform = PrivateMethod[Seq[Offer]](Symbol("transform"))

    forAll(listOfOfferDataGen) {
      offerList: Seq[OfferData] =>
        val resultList = listService invokePrivate accessTransform(offerList)
        logger.trace(s"${resultList}")
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
        val listService = new ListService(repo)
        val accessRetrieveOffers = PrivateMethod[Seq[OfferData]](Symbol("retrieveOffers"))

        offerList.foreach(repo.save(_))
        val extra = fromPrivateDataStore filter (element => offerList.contains(element))
        assert(fromPrivateDataStore.size == offerList.size)
        assert(extra.isEmpty)

        val filteredOffers = listService invokePrivate accessRetrieveOffers(filter)
        val evaluated = filteredOffers.size == offerList.size
        assert(evaluated == true)
    }
  }


  //Reduced function's access to private - but test is still relevant
  property("Verify ListService retrieveOffers gets correct OfferData for `current` filter") {
    forAll(listOfOfferDataGen, "current") {
      (offerList, filter) =>
        val repo = new OfferRepository()
        val accessPrivateDataStore = PrivateMethod[mutable.HashMap[UUID, OfferData]](Symbol("offerMap"))
        val fromPrivateDataStore = repo invokePrivate accessPrivateDataStore()
        val listService = new ListService(repo)
        val accessRetrieveOffers = PrivateMethod[Seq[OfferData]](Symbol("retrieveOffers"))

        offerList.foreach(repo.save(_))
        val extra = fromPrivateDataStore filter (element => offerList.contains(element))
        assert(fromPrivateDataStore.size == offerList.size)
        assert(extra.isEmpty)

        val filteredOffers = listService invokePrivate accessRetrieveOffers(filter)
        val evaluated = filteredOffers.foldLeft(true)((accum, offer) => accum && (offer.expiry.isEqualNow || offer.expiry.isAfterNow))
        assert(evaluated == true)
    }
  }

  //Reduced function's access to private - but test is still relevant
  property("Verify ListService retrieveOffers gets correct OfferData for `expired` filter") {
    forAll(listOfOfferDataGen, "expired") {
      (offerList, filter) =>
        val repo = new OfferRepository()
        val accessPrivateDataStore = PrivateMethod[mutable.HashMap[UUID, OfferData]](Symbol("offerMap"))
        val fromPrivateDataStore = repo invokePrivate accessPrivateDataStore()
        val listService = new ListService(repo)
        val accessRetrieveOffers = PrivateMethod[Seq[OfferData]](Symbol("retrieveOffers"))

        offerList.foreach(repo.save(_))
        val extra = fromPrivateDataStore filter (element => offerList.contains(element))
        assert(fromPrivateDataStore.size == offerList.size)
        assert(extra.isEmpty)

        val filteredOffers = listService invokePrivate accessRetrieveOffers(filter)
        val evaluated = filteredOffers.foldLeft(true)((accum, offer) => accum && offer.expiry.isBeforeNow)
        assert(evaluated == true)
    }
  }

}
