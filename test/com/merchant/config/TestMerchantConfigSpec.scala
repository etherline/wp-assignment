package com.merchant.config

import org.scalatest.{FunSuite, Matchers}


class TestMerchantConfigSpec extends FunSuite with Matchers{

  test("verify application.conf loads description constraints"){
    val constraints = MerchantConfigs.getDescriptionConstraints
    assert(constraints != null)
  }

  test("verify application.conf loads date constraints"){
    val constraints = MerchantConfigs.getDateConstraints
    assert(constraints != null)
  }

  test("verify application.conf loads decimal constraints"){
    val constraints = MerchantConfigs.getDecimalConstraints
    assert(constraints != null)
  }
}
