package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.core.controller.inject.open.OpenInjectionStep
import io.gatling.commons.stats.assertion.Assertion
import io.gatling.core.pause.PauseType
import scenarios._
import utils.Environment

import scala.util.Random
import scala.concurrent.duration._

class Probate_Simulation extends Simulation {

  val BaseURL = Environment.baseURL

  /* TEST TYPE DEFINITION */
  /* pipeline = nightly pipeline against the AAT environment (see the Jenkins_nightly file) */
  /* perftest (default) = performance test against the perftest environment */
  val testType = scala.util.Properties.envOrElse("TEST_TYPE", "perftest")

  //set the environment based on the test type
  val environment = testType match{
    case "perftest" => "perftest"
    case "pipeline" => "perftest" //updated pipeline to run against perftest - change to aat to run against AAT
    case _ => "**INVALID**"
  }
  /* ******************************** */

  /* ADDITIONAL COMMAND LINE ARGUMENT OPTIONS */
  val debugMode = System.getProperty("debug", "off") //runs a single user e.g. ./gradle gatlingRun -Ddebug=on (default: off)
  val env = System.getProperty("env", environment) //manually override the environment aat|perftest e.g. ./gradle gatlingRun -Denv=aat
  /* ******************************** */

  /* PERFORMANCE TEST CONFIGURATION */
  val rampUpDurationMins = 5
  val rampDownDurationMins = 5
  val testDurationMins = 60

  //Must be doubles to ensure the calculations result in doubles not rounded integers
  val probateHourlyTarget:Double = 100
  val intestacyHourlyTarget:Double = 16
  val caveatHourlyTarget:Double = 60

  val continueAfterEligibilityPercentage = 58

  val probateRatePerSec = probateHourlyTarget / 3600
  val intestacyRatePerSec = intestacyHourlyTarget / 3600
  val caveatRatePerSec = caveatHourlyTarget / 3600

  val randomFeeder = Iterator.continually( Map( "perc" -> Random.nextInt(100)))

  //If running in debug mode, disable pauses between steps
  val pauseOption:PauseType = debugMode match{
    case "off" => constantPauses
    case _ => disabledPauses
  }
  /* ******************************** */

  /* PIPELINE CONFIGURATION */
  val numberOfPipelineUsers:Double = 5
  /* ******************************** */

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(BaseURL)
    .doNotTrackHeader("1")
    .inferHtmlResources(BlackList("https://www.payments.service.gov.uk/.*", "https://webchat-client.training.ctsc.hmcts.net/.*"), WhiteList())
    .silentResources

  before{
    println(s"Test Type: ${testType}")
    println(s"Test Environment: ${env}")
    println(s"Debug Mode: ${debugMode}")
  }

  val ProbateGoR = scenario( "ProbateGoR")
    .feed(randomFeeder)
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
      .exec(
        CreateUser.CreateCitizen,
        Homepage.ProbateHomepage,
        Login.ProbateLogin,
        ProbateApp_ExecOne_Apply.ProbateEligibility)
      .doIf(session => session("perc").as[Int] < continueAfterEligibilityPercentage || debugMode != "off" || testType == "pipeline") {
        exec(
          ProbateApp_ExecOne_Apply.ProbateApplicationSection1,
          ProbateApp_ExecOne_Apply.ProbateApplicationSection2,
          ProbateApp_ExecOne_Apply.ProbateApplicationSection3,
          Logout.ProbateLogout)
        .exec(flushHttpCache)
        .exec(
          ProbateApp_ExecTwo_Declaration.ProbateDeclaration)
        .exec(flushHttpCache)
        .exec(
          Homepage.ProbateHomepage,
          Login.ProbateLogin,
          ProbateApp_ExecOne_Submit.ProbateSubmit,
          Logout.ProbateLogout,
          ProbateApp_CWIssueGrant.IssueGrant)
      }
    }
    .doIf("${emailAddress.exists()}") {
      exec(DeleteUser.DeleteCitizen)
    }

    .exec {
      session =>
        println(session)
        session
    }

  val ProbateIntestacy = scenario( "ProbateIntestacy")
    .feed(randomFeeder)
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
        .exec(
          CreateUser.CreateCitizen,
          Homepage.ProbateHomepage,
          Login.ProbateLogin,
          ProbateApp_Intestacy.IntestacyEligibility)
        .doIf(session => session("perc").as[Int] < continueAfterEligibilityPercentage || debugMode != "off" || testType == "pipeline") {
          exec(
            ProbateApp_Intestacy.IntestacyApplicationSection1,
            ProbateApp_Intestacy.IntestacyApplicationSection2,
            ProbateApp_Intestacy.IntestacyApplicationSection3,
            ProbateApp_Intestacy.IntestacyApplicationSection4,
            ProbateApp_Intestacy.IntestacyApplicationSection5,
            Logout.ProbateLogout)
        }
    }
    .doIf("${emailAddress.exists()}") {
      exec(DeleteUser.DeleteCitizen)
    }

    .exec {
      session =>
        println(session)
        session
    }

  val ProbateCaveat = scenario( "ProbateCaveat")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
      .exec(
        ProbateApp_Caveat.ProbateCaveat
      )
    }

    .exec {
      session =>
        println(session)
        session
    }


  //defines the Gatling simulation model, based on the inputs
  def simulationProfile(simulationType: String, userPerSecRate: Double, numberOfPipelineUsers: Double): Seq[OpenInjectionStep] = {
    simulationType match {
      case "perftest" =>
        if (debugMode == "off") {
          Seq(
            rampUsersPerSec(0.00) to (userPerSecRate) during (rampUpDurationMins minutes),
            constantUsersPerSec(userPerSecRate) during (testDurationMins minutes),
            rampUsersPerSec(userPerSecRate) to (0.00) during (rampDownDurationMins minutes)
          )
        }
        else{
          Seq(atOnceUsers(1))
        }
      case "pipeline" =>
        Seq(rampUsers(numberOfPipelineUsers.toInt) during (2 minutes))
      case _ =>
        Seq(nothingFor(0))
    }
  }

  //defines the test assertions, based on the test type
  def assertions(simulationType: String): Seq[Assertion] = {
    simulationType match {
      case "perftest" | "pipeline" => //currently using the same assertions for a performance test and the pipeline
        if (debugMode == "off") {
          Seq(global.successfulRequests.percent.gte(95),
            details("CCD_000_CCDEvent-boIssueGrantForCaseMatching").successfulRequests.percent.gte(80),
            details("Intestacy_420_DownloadDeclarationPDF").successfulRequests.percent.gte(80),
            details("Caveat_170_CardDetailsConfirmSubmit").successfulRequests.percent.gte(80))
        }
        else {
          Seq(global.successfulRequests.percent.is(100))
        }
      case _ =>
        Seq()
    }
  }

  setUp(
    ProbateGoR.inject(simulationProfile(testType, probateRatePerSec, numberOfPipelineUsers)).pauses(pauseOption),
    ProbateIntestacy.inject(simulationProfile(testType, intestacyRatePerSec, numberOfPipelineUsers)).pauses(pauseOption),
    ProbateCaveat.inject(simulationProfile(testType, caveatRatePerSec, numberOfPipelineUsers)).pauses(pauseOption)
  ).protocols(httpProtocol)
    .assertions(assertions(testType))

}
