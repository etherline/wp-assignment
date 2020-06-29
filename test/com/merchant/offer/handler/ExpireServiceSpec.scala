package com.merchant.offer.handler

import java.util.UUID

import com.merchant.offer.model
import com.merchant.offer.model.{OfferData, OfferDate}
import com.merchant.offer.repository.OfferRepository
import org.scalatest.{Matchers, PrivateMethodTester, PropSpec}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.collection.mutable

class ExpireServiceSpec extends PropSpec with ScalaCheckPropertyChecks with Matchers with PrivateMethodTester {

  import com.merchant.offer.fixtures._


  property("ExpireService.expireOffer expire offer date for existing OfferData") {

    forAll(offerDataGen) {
      offer =>

        val repo = new OfferRepository()
        val service = new ExpireServiceImpl(repo)
        val accessPrivateDataStore = PrivateMethod[mutable.HashMap[UUID, OfferData]](Symbol("offerMap"))
        val fromPrivateDataStore = repo invokePrivate accessPrivateDataStore()

        repo.save(offer)

        assert(fromPrivateDataStore.size == 1)
        val persistedOfferId = fromPrivateDataStore.values.toSeq(0).uid

        val expireResult: model.ExpireResponse = service.expireOffer(persistedOfferId.toString)
        val expiryDate = OfferDate.toDateTimeZone(expireResult.offer.get.expiryDateTime.strValue)

        assert(expiryDate.isBeforeNow || expiryDate.isEqualNow)
        assert(expireResult.isConfirmed)
    }

  }

  property("return an empty value for non-existent OfferData") {

    forAll(offerDataGen) {
      offer =>

        val repo = new OfferRepository()
        val service = new ExpireServiceImpl(repo)
        val accessPrivateDataStore = PrivateMethod[mutable.HashMap[UUID, OfferData]](Symbol("offerMap"))
        val fromPrivateDataStore = repo invokePrivate accessPrivateDataStore()

        assert(fromPrivateDataStore.size == 0)
        val persistedOfferId = offer.uid
        val expireResult: model.ExpireResponse = service.expireOffer(persistedOfferId.toString)

        assertResult(None)(expireResult.offer)
        assert(!expireResult.isConfirmed)
    }
  }

  property("return an error where the UUID is invalid") {

    val repo = new OfferRepository()
    val service = new ExpireServiceImpl(repo)

    val expireResult: model.ExpireResponse = service.expireOffer("blah123")

    assertResult(None)(expireResult.offer)
    assert(!expireResult.isConfirmed)
  }

}
