package uk.gov.hmcts.reform.probate.performance.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import uk.gov.hmcts.reform.probate.performance.scenarios.utils.{Environment, Common}

package object CreateUser {

  val IdamAPIURL = Environment.idamAPIURL

  val newUserFeeder = Iterator.continually(Map(
    "emailAddress" -> ("perftest" + Common.getDate() + "@perftest-" + Common.randomString(10) + ".com"),
    "password" -> "Pa55word11",
    "role" -> "citizen"
  ))

  val CreateCitizen =
    feed(newUserFeeder)
      .exec(http("Probate_000_CreateCitizen")
      .post(IdamAPIURL + "/testing-support/accounts")
      .body(ElFileBody("bodies/NewUserTemplate.json")).asJson
      .check(status.is(201)))

      .exec {
        session =>
          println("EMAIL: " + session("emailAddress").as[String])
          println("PASSWORD: " + session("password").as[String])
          println("ROLE: " + session("role").as[String])
          session
      }

}
