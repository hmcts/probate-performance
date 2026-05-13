package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.Environment

import scala.concurrent.duration._

object CitizenHub {

  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader


  val ProbateCitizenHub =

    group("Probate_600_LoadCitizenHub") {

      exec(http("LoadCitizenHub")
        .get(BaseURL + "/get-case/#{caseId}?probateType=PA")
        .headers(CommonHeader)
        .check(substring("hmcts-progress-bar__icon--complete").count.in(1, 4))) //Application received or Grant Issued

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

  val IntestacyCitizenHub =

    group("Intestacy_600_LoadCitizenHub") {

      exec(http("LoadCitizenHub")
        .get(BaseURL + "/get-case/#{caseId}?probateType=INTESTACY")
        .headers(CommonHeader)
        .check(substring("hmcts-progress-bar__icon--complete").count.in(1, 3))) //Application received or Grant Issued

    }

      .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

}