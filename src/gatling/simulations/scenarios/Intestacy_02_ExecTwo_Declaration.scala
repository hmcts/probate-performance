package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{CsrfCheck, Environment}

import scala.concurrent.duration._

object Intestacy_02_ExecTwo_Declaration {

  val BaseURL = Environment.baseURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader
  val PostHeader = Environment.postHeader

  val IntestacyDeclaration = {

    //inviteIdList was invoked prior to the first executor logging out, to retrieve the invite id for the
    //second executor

    //The following calls are required to bypass the manual email portion of the flow.
    //Ordinarily, the second executor is sent an email with a link.

    //Simulate clicking the email link

    /*
    exec {
      session =>
        println("INVITE ID: " + session("inviteId").as[String])
        session
    }
     */

    group("Intestacy_283_InviteId") {

      exec(http("InviteId")
        .get(BaseURL + "/executors/intestacy-invitation/#{inviteId}")
        .headers(CommonHeader)
        .check(substring("started an application for a grant of probate")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_284_StartVerify") {

      exec(http("StartVerify")
        .get(BaseURL + "/verify-dod")
        .headers(CommonHeader)
        .check(CsrfCheck.save)
        .check(substring("What was the date that")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_285_VerifyDodSubmit") {

      exec(http("VerifyDodSubmit")
        .post(BaseURL + "/verify-dod")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("dod-day", "#{dodDay}")
        .formParam("dod-month", "#{dodMonth}")
        .formParam("dod-year", "#{dodYear}")
        .check(substring("Check legal statement and make declaration")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_286_ExecTwoDeclarationSubmit") {

      exec(http("ExecTwoDeclarationSubmit")
        .post(BaseURL + "/intestacy/co-applicant-declaration")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("agreement", "optionYes")
        .check(substring("made your legal declaration")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .exec(flushHttpCache)
  }

}