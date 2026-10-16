package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.CsrfCheck
import utils.Environment

import scala.concurrent.duration._

object Homepage {

  val BaseURL = Environment.baseURL
  val IdamURL = Environment.idamURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader

  val ProbateHomepage =

    exec(flushHttpCache)
    .exec(flushCookieJar)

    .group("Probate_001_HomePage") {

      exec(http("Probate_001_005_HomePage")
        .get(BaseURL + "/")
        .headers(CommonHeader)
        .header("sec-fetch-site", "none")
        .check(substring("Sign in or create an account")))

    }

     .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

     .group("Probate_002_ClickSignIn") {
       exec(http("Probate_001_010_ClickSignIn")
         .get(IdamURL + "/sign-in-or-create")
         .headers(CommonHeader)
         .header("sec-fetch-site", "same-origin")
         .check(status.is(200)))
     }

     .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

}