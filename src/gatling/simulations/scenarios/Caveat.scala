package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{CsrfCheck, Environment}
import utilities.{DateUtils, StringUtils}

import scala.concurrent.duration._

object Caveat {

  val BaseURL = Environment.baseURL
  val PaymentURL = Environment.paymentURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader
  val PostHeader = Environment.postHeader

  val postcodeFeeder = csv("postcodes.csv").random

  val LodgeCaveat =

    exec(_.setAll("randomString" -> StringUtils.randomString(5),
      "dobDay" -> DateUtils.getRandomDayOfMonth(),
      "dobMonth" -> DateUtils.getRandomMonthOfYear(),
      "dobYear" -> DateUtils.getDatePastRandom("yyyy", minYears = 25, maxYears = 70),
      "dodDay" -> DateUtils.getRandomDayOfMonth(),
      "dodMonth" -> DateUtils.getRandomMonthOfYear(),
      "dodYear" -> DateUtils.getDatePastRandom("yyyy", minYears = 1, maxYears = 2),
      "cardExpiryYear" -> DateUtils.getDateFuture("yy", years = 2)))

    .feed(postcodeFeeder)

    .group("Caveat_010_Homepage") {

      exec(http("Homepage")
        .get(BaseURL + "/caveats/start-apply")
        .headers(CommonHeader)
        .header("sec-fetch-site", "none")
        .header("accept-language", "en-GB,en;q=0.9")
        .check(substring("Stop an application for probate")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Caveat_020_ApplicantName") {

      exec(http("ApplicantName")
        .get(BaseURL + "/caveats/applicant-name")
        .headers(CommonHeader)
        .check(CsrfCheck.save)
        .check(substring("What is your full name")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Caveat_030_ApplicantNameSubmit") {

      exec(http("ApplicantNameSubmit")
        .post(BaseURL + "/caveats/applicant-name")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("firstName", "Perf#{randomString}")
        .formParam("lastName", "Test#{randomString}")
        .check(CsrfCheck.save)
        .check(substring("What is your email address")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Caveat_040_EmailAddressSubmit") {

      exec(http("EmailAddressSubmit")
        .post(BaseURL + "/caveats/applicant-email")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("email", "caveat@perftest#{randomString}.com")
        .check(CsrfCheck.save)
        .check(substring("What is your address")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Caveat_050_AddressSubmit") {

      exec(http("AddressSubmit")
        .post(BaseURL + "/caveats/applicant-address")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("addressLine1", "1 Perf#{randomString} Road")
        .formParam("addressLine2", "")
        .formParam("addressLine3", "")
        .formParam("postTown", "Perf #{randomString} Town")
        .formParam("newPostCode", "#{postcode}")
        .formParam("country", "United Kingdom")
        .check(CsrfCheck.save)
        .check(substring("full name of the person")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Caveat_060_DeceasedNameSubmit") {

      exec(http("DeceasedNameSubmit")
        .post(BaseURL + "/caveats/deceased-name")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("firstName", "Perf#{randomString}")
        .formParam("lastName", "Tester#{randomString}")
        .check(CsrfCheck.save)
        .check(substring("What was the date that they died")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Caveat_070_DeceasedDodSubmit") {

      exec(http("DeceasedDodSubmit")
        .post(BaseURL + "/caveats/deceased-dod")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("dod-day", "#{dodDay}")
        .formParam("dod-month", "#{dodMonth}")
        .formParam("dod-year", "#{dodYear}")
        .check(CsrfCheck.save)
        .check(substring("known by any other names")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Caveat_100_DeceasedAliasSubmit") {

      exec(http("DeceasedAliasSubmit")
        .post(BaseURL + "/caveats/deceased-alias")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("alias", "optionNo")
        .check(CsrfCheck.save)
        .check(substring("What was the permanent address")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Caveat_110_DeceasedAddressSubmit") {

      exec(http("DeceasedAddressSubmit")
        .post(BaseURL + "/caveats/deceased-address")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("addressLine1", "2 Perf#{randomString} Road")
        .formParam("addressLine2", "")
        .formParam("addressLine3", "")
        .formParam("postTown", "Perf #{randomString} Town")
        .formParam("newPostCode", "#{postcode}")
        .formParam("country", "United Kingdom")
        .check(CsrfCheck.save)
        .check(substring("English and Welsh")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Caveat_120_BilingualSubmit") {

      exec(http("BilingualSubmit")
        .post(BaseURL + "/caveats/bilingual-gop")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("bilingual", "optionNo")
        .check(regex("Check your answers|Equality and diversity questions")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Caveat_130_PaymentBreakdown") {

      exec(http("PaymentBreakdown")
        .get(BaseURL + "/caveats/payment-breakdown")
        .headers(CommonHeader)
        .check(CsrfCheck.save)
        .check(substring("Application fee")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Caveat_140_PaymentBreakdownSubmit") {

      exec(http("PaymentBreakdownSubmit")
        .post(BaseURL + "/caveats/payment-breakdown")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .check(substring("Enter card details"))
        .check(css("input[name='csrfToken']", "value").saveAs("csrf"))
        .check(css("input[name='chargeId']", "value").saveAs("ChargeId")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Caveat_150_CheckCard") {

      exec(http("CheckCard")
        .post(PaymentURL + "/check_card/#{ChargeId}")
        .headers(PostHeader)
        .formParam("cardNo", "4444333322221111")
        .check(jsonPath("$.accepted").is("true")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Caveat_160_CardDetailsSubmit") {

      exec(http("CardDetailsSubmit")
        .post(PaymentURL + "/card_details/#{ChargeId}")
        .headers(PostHeader)
        .formParam("chargeId", "#{ChargeId}")
        .formParam("csrfToken", "#{csrf}")
        .formParam("cardNo", "4444333322221111")
        .formParam("expiryMonth", "01")
        .formParam("expiryYear", "#{cardExpiryYear}")
        .formParam("cardholderName", "Perf Tester #{randomString}")
        .formParam("cvc", "123")
        .formParam("addressCountry", "GB")
        .formParam("addressLine1", "1 Perf#{randomString} Road")
        .formParam("addressLine2", "")
        .formParam("addressCity", "Perf #{randomString} Town")
        .formParam("addressPostcode", "#{postcode}")
        .formParam("email", "caveat@perftest#{randomString}.com")
        .check(substring("Confirm your payment"))
        .check(css("input[name='csrfToken']", "value").saveAs("csrf")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Caveat_170_CardDetailsConfirmSubmit") {

      exec(http("CardDetailsConfirmSubmit")
        .post(PaymentURL + "/card_details/#{ChargeId}/confirm")
        .headers(PostHeader)
        .formParam("chargeId", "#{ChargeId}")
        .formParam("csrfToken", "#{csrf}")
        .check(substring("Application complete"))
        .check(regex("Your reference number is</span><br>(?s).*?<strong class=.govuk-!-font-weight-bold. aria-label=..>([0-9|-]*)<").saveAs("referenceNo")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

}