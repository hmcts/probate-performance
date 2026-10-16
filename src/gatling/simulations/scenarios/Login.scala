package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.Environment

import scala.concurrent.duration._

object Login {

  val BaseURL = Environment.baseURL
  val IdamURL = Environment.idamURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader
  val PostHeader = Environment.postHeader

  val ProbateLogin =


      group("Probate_000_Login") {
        exec(http("Probate Login_Email")
          .post(IdamURL + "/enter-email")
          .headers(CommonHeader)
          .formParam("email", "#{emailAddress}")
          .formParam("_csrf", "#{csrf}")
          .check(substring("Enter your password")))
      }
      .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

      group("Probate_001_Login_Password") {
        exec(http("Probate Login_Password")
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
