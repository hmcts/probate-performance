package uk.gov.hmcts.reform.probate.performance.simulations.checks

import io.gatling.core.Predef._
import io.gatling.core.check.CheckBuilder
import io.gatling.core.check.extractor.css.CssCheckType
import jodd.lagarto.dom.NodeSelector

object PaymentSessionToken {
  def save: CheckBuilder[CssCheckType, NodeSelector, String] = css("input[name='chargeId']", "value").saveAs("chargeId")

  def chargeIdParameter: String = "chargeId"
  def chargeIdTemplate: String = "${chargeId}"
}

