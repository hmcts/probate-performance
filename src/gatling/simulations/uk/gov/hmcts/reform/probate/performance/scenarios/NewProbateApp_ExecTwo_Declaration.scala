package uk.gov.hmcts.reform.probate.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.probate.performance.scenarios.checks.{CsrfCheck, CurrentPageUrl}
import uk.gov.hmcts.reform.probate.performance.scenarios.utils.Environment

import scala.concurrent.duration._

object NewProbateApp_ExecTwo_Declaration {

  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader
  val GetHeader = Environment.getHeader
  val PostHeader = Environment.postHeader

  val ProbateDeclaration = group("Probate_NewApp_ExecTwo_Declaration") {

    //inviteIdList was invoked prior to the first executor logging out, to retrieve the invite id for the
    //second executor

    //The following calls are required to bypass the manual email and text message portion of the flow.
    //Ordinarily, the second executor is sent an email with a link. When they click on this link, a text
    //message is sent to their mobile phone with a code. They submit this code into the next page.

    //Simulate clicking the email link to display the page with a pin input box

    exec(http("Probate_050_005_InviteId")
      .get(BaseURL + "/executors/invitation/${inviteId}")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(CsrfCheck.save)
      .check(regex("Before making an application for probate")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    //Retrieve the PIN code that was sent to the mobile phone by text message

    .exec(http("Probate_050_010_RetrievePinCode")
      .get(BaseURL + "/pin")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(jsonPath("$.pin").saveAs("pin")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    //Simulate clicking the email link to display the page with a pin input box

    .exec(http("Probate_050_015_PinCodeSubmit")
      .post(BaseURL + "/sign-in")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("pin", "${pin}")
      .check(regex("been named as an executor")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_050_020_CoApplicantDeclaration")
      .get(BaseURL + "/co-applicant-declaration")
      .headers(CommonHeader)
      .headers(GetHeader)
      .check(CsrfCheck.save)
      .check(regex("Check legal statement and make declaration")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

    .exec(http("Probate_050_025_CoApplicantDeclarationSubmit")
      .post(BaseURL + "/co-applicant-declaration")
      .headers(CommonHeader)
      .headers(PostHeader)
      .formParam("_csrf", "${csrf}")
      .formParam("agreement", "optionYes")
      .check(regex("made your legal declaration")))

    .pause(MinThinkTime seconds, MaxThinkTime seconds)

  }

}