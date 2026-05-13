package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{CsrfCheck, Environment}

import scala.concurrent.duration._

object Intestacy_03_ExecOne_Submit {

  val BaseURL = Environment.baseURL
  val PaymentURL = Environment.paymentURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader
  val PostHeader = Environment.postHeader

  val postcodeFeeder = csv("postcodes.csv").random

  val IntestacySubmit =

    feed(postcodeFeeder)

    .group("Intestacy_289_GetCase") {

      exec(http("GetCase")
        .get(BaseURL + "/get-case/#{caseId}?probateType=INTESTACY")
        .headers(CommonHeader)
        .check(substring("Complete these steps"))
        .check(regex("Check your answers and make your legal declaration(?s).*?govuk-task-list__status\">(.+?)</div>").is("Completed")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_290_SectionFourStart") {

      exec(http("SectionFourStart")
        .get(BaseURL + "/intestacy/copies-uk")
        .headers(CommonHeader)
        .check(CsrfCheck.save)
        .check(substring("How many extra official copies")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_300_ExtraCopiesSubmit") {

      exec(http("ExtraCopiesSubmit")
        .post(BaseURL + "/intestacy/copies-uk")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("uk", "0")
        .check(CsrfCheck.save)
        .check(substring("have assets outside the UK")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_310_AssetsOverseasSubmit") {

      exec(http("AssetsOverseasSubmit")
        .post(BaseURL + "/intestacy/assets-overseas")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("assetsoverseas", "optionNo")
        .check(substring("Check your answers")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_330_PaymentBreakdown") {

      exec(http("PaymentBreakdown")
        .get(BaseURL + "/payment-breakdown")
        .headers(CommonHeader)
        .check(CsrfCheck.save)
        .check(substring("Application fee")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_340_PaymentBreakdownSubmit") {

      exec(http("PaymentBreakdownSubmit")
        .post(BaseURL + "/payment-breakdown")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .check(substring("Enter card details"))
        .check(css("input[name='csrfToken']", "value").saveAs("csrf"))
        .check(css("input[name='chargeId']", "value").saveAs("ChargeId")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_350_CheckCard") {

      exec(http("CheckCard")
        .post(PaymentURL + "/check_card/#{ChargeId}")
        .headers(PostHeader)
        .formParam("cardNo", "4444333322221111")
        .check(jsonPath("$.accepted").is("true")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_360_CardDetailsSubmit") {

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
        .formParam("email", "intestacy@perftest#{randomString}.com")
        .check(substring("Confirm your payment"))
        .check(css("input[name='csrfToken']", "value").saveAs("csrf")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_370_CardDetailsConfirmSubmit") {

      exec(http("CardDetailsConfirmSubmit")
        .post(PaymentURL + "/card_details/#{ChargeId}/confirm")
        .headers(PostHeader)
        .formParam("chargeId", "#{ChargeId}")
        .formParam("csrfToken", "#{csrf}")
        .check(substring("Application submitted")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_400_DownloadCoverSheetPDF") {

      exec(http("DownloadCoverSheetPDF")
        .get(BaseURL + "/cover-sheet-pdf")
        .headers(CommonHeader)
        .check(bodyString.transform(_.size > 10000).is(true)))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_410_DownloadCheckAnswersPDF") {

      exec(http("DownloadCheckAnswersPDF")
        .get(BaseURL + "/check-answers-pdf")
        .headers(CommonHeader)
        .check(bodyString.transform(_.size > 3000).is(true)))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_420_DownloadDeclarationPDF") {

      exec(http("DownloadDeclarationPDF")
        .get(BaseURL + "/declaration-pdf")
        .headers(CommonHeader)
        .check(bodyString.transform(_.size > 15000).is(true)))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

}
