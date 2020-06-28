package com.merchant.offer.repository

import java.util.UUID

import com.merchant.offer.model.OfferData
import org.scalatest.{Matchers, PrivateMethodTester, PropSpec}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.collection.mutable

class OfferRepositorySpec extends PropSpec with ScalaCheckPropertyChecks with Matchers with PrivateMethodTester {

  import com.merchant.offer.fixtures._

  property("Verify OfferRepository save succeeds") {
    val repo = new OfferRepository()
    val accessPrivateDataStore = PrivateMethod[mutable.HashMap[UUID, OfferData]](Symbol("offerMap"))
    val fromPrivateDataStore = repo invokePrivate accessPrivateDataStore()

    forAll(offerDataGen) {
      offerData =>
        val result = repo.save(offerData)
        assertResult(true)(result)
        assertResult(offerData)(fromPrivateDataStore.getOrElse(offerData.uid, None))
    }
  }


  property("Verify OfferRepository list succeeds") {

    forAll(listOfOfferDataGen) {
      offerList =>
        val repo = new OfferRepository()
        val accessPrivateDataStore = PrivateMethod[mutable.HashMap[UUID, OfferData]](Symbol("offerMap"))
        val fromPrivateDataStore = repo invokePrivate accessPrivateDataStore()
        offerList.foreach(repo.save)
        val extra = fromPrivateDataStore filter (element => offerList.contains(element))
        assert(fromPrivateDataStore.size == offerList.size)
        assert(extra.isEmpty)
    }
  }

  property("Verify OfferRepository empty list param succeeds") {
    val repo = new OfferRepository()
    val accessPrivateDataStore = PrivateMethod[mutable.HashMap[UUID, OfferData]](Symbol("offerMap"))
    val fromPrivateDataStore = repo invokePrivate accessPrivateDataStore()

    val offerList = List()
    offerList.foreach(repo.save(_))
    val extra = fromPrivateDataStore filter (offerList.contains(_))
    assertResult(fromPrivateDataStore.size) (offerList.size)
    assert(extra.isEmpty)
  }


  property("Verify OfferRepository expire succeeds") {

    def isExpiryFulfilled(offerData:OfferData) = {
      offerData.expiry.isBeforeNow || offerData.expiry.isEqualNow
    }

    forAll(listOfOfferDataGen) {
      offerList =>
        val repo = new OfferRepository()
        val accessPrivateDataStore = PrivateMethod[mutable.HashMap[UUID, OfferData]](Symbol("offerMap"))
        val fromPrivateDataStore = repo invokePrivate accessPrivateDataStore()
        offerList.foreach(repo.save)
        assertResult(offerList.size)(fromPrivateDataStore.size)

        offerList.map(offerData => offerData.uid).foreach(
          id => {
            val result = repo.expire(id)
            val isCorrect = result match {
              case Some(offer) => offer.uid == id && isExpiryFulfilled(offer)
              case None => false
            }
            assert(isCorrect)
          }
        )
    }
  }

}
