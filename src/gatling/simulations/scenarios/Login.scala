package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{CsrfCheck, Environment}

import scala.concurrent.duration._

object Login {

  val BaseURL = Environment.baseURL
  val IdamURL = Environment.idamURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader
  val PostHeader = Environment.postHeader

  val ProbateLogin =

    group("Probate_001_LoadLoginPage") {
      exec(http("Probate Load Login Page")
        .get(IdamURL + "/enter-email")
        .headers(CommonHeader)
        .check(CsrfCheck.save)
        .check(substring("Enter your email address")))
    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_002_Login_EnterEmail") {
      exec(http("Probate Login Enter Email")
        .post(IdamURL + "/enter-email")
        .headers(CommonHeader)
        .formParam("email", "#{emailAddress}")
        .formParam("_csrf", "#{csrf}")
        .check(CsrfCheck.save)
        .check(substring("Enter your password")))
    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Probate_003_Login_EnterPassword") {
      exec(http("Probate Login Enter Password")
        .post(IdamURL + "/enter-password")
        .headers(CommonHeader)
        .formParam("action", "_submit")
        .formParam("password", "#{password}")
        .formParam("_csrf", "#{csrf}")
        .check(regex("Apply for probate|Probate applications"))
        .check(regex("a href=./get-case/([0-9]+).probateType=(?:PA|INTESTACY)").optional.saveAs("caseId")))
    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)
}
