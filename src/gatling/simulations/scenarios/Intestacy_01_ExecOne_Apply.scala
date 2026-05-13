package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{CsrfCheck, Environment}
import utilities.{DateUtils, StringUtils}

import scala.concurrent.duration._

object Intestacy_01_ExecOne_Apply {

  val BaseURL = Environment.baseURL
  val PaymentURL = Environment.paymentURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader
  val PostHeader = Environment.postHeader

  val postcodeFeeder = csv("postcodes.csv").random

  val IntestacyEligibility =

    exec(_.setAll("randomString" -> StringUtils.randomString(5),
      "dobDay" -> DateUtils.getRandomDayOfMonth(),
      "dobMonth" -> DateUtils.getRandomMonthOfYear(),
      "dobYear" -> DateUtils.getDatePastRandom("yyyy", minYears = 25, maxYears = 70),
      "dodDay" -> DateUtils.getRandomDayOfMonth(),
      "dodMonth" -> DateUtils.getRandomMonthOfYear(),
      "dodYear" -> DateUtils.getDatePastRandom("yyyy", minYears = 1, maxYears = 2),
      "cardExpiryYear" -> DateUtils.getDateFuture("yy", years = 2)))

    .feed(postcodeFeeder)

    .group("Intestacy_010_StartEligibility") {

      exec(http("StartEligibility")
        .get(BaseURL + "/death-certificate")
        .headers(CommonHeader)
        .check(CsrfCheck.save)
        .check(substring("Do you have the death certificate")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_020_DeathCertificateSubmit") {

      exec(http("DeathCertificateSubmit")
        .post(BaseURL + "/death-certificate")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("deathCertificate", "optionYes")
        .check(CsrfCheck.save)
        .check(substring("Is the original death certificate in English")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_025_DeathCertEnglishSubmit") {

      exec(http("DeathCertEnglishSubmit")
        .post(BaseURL + "/death-certificate-english")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("deathCertificateInEnglish", "optionYes")
        .check(CsrfCheck.save)
        .check(substring("Did the person who died live permanently")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_030_DomicileSubmit") {

      exec(http("DomicileSubmit")
        .post(BaseURL + "/deceased-domicile")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("domicile", "optionYes")
        .check(CsrfCheck.save)
        .check(substring("1 January 2022")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_035_ExceptedEstatesDodSubmit") {

      exec(http("EEDodSubmit")
        .post(BaseURL + "/ee-deceased-dod")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("eeDeceasedDod", "optionYes")
        .check(CsrfCheck.save)
        .check(substring("Have you worked out")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_040_ExceptedEstatesValuedSubmit") {

      exec(http("ExceptedEstatesValuedSubmit")
        .post(BaseURL + "/ee-estate-valued")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("eeEstateValued", "optionYes")
        .check(CsrfCheck.save)
        .check(substring("Did the person who died leave a will")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_050_WillLeftSubmit") {

      exec(http("WillLeftSubmit")
        .post(BaseURL + "/will-left")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("left", "optionNo")
        .check(CsrfCheck.save)
        .check(substring("What is your relationship to the person")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_070_RelatedSubmit") {

      exec(http("RelatedSubmit")
        .post(BaseURL + "/related-to-deceased")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("related", "optionYes")
        .check(substring("Complete these steps")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

  //Update May 2024: The user is no longer shown the dashboard, but will now be taken directly to the task-list

  val IntestacyApplicationSection1 =

    group("Intestacy_100_SectionOneStart") {

      exec(http("SectionOneStart")
        .get(BaseURL + "/intestacy/bilingual-gop")
        .headers(CommonHeader)
        .check(CsrfCheck.save)
        .check(substring("Do you require a bilingual grant")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_110_BilingualGrantSubmit") {

      exec(http("BilingualGrantSubmit")
        .post(BaseURL + "/intestacy/bilingual-gop")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("bilingual", "optionNo")
        .check(CsrfCheck.save)
        .check(substring("What is the full name of the person")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_120_DeceasedNameSubmit") {

      exec(http("DeceasedNameSubmit")
        .post(BaseURL + "/intestacy/deceased-name")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("firstName", "Perf#{randomString}")
        .formParam("lastName", "Test#{randomString}")
        .check(CsrfCheck.save)
        .check(substring("date of birth")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_125_DeceasedDOBSubmit") {

      exec(http("DeceasedDOBSubmit")
        .post(BaseURL + "/intestacy/deceased-dob")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("dob-day", "#{dobDay}")
        .formParam("dob-month", "#{dobMonth}")
        .formParam("dob-year", "#{dobYear}")
        .check(CsrfCheck.save)
        .check(substring("Use the date from the death certificate")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_127_DeceasedDODSubmit") {

      exec(http("DeceasedDODSubmit")
        .post(BaseURL + "/intestacy/deceased-dod")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("dod-day", "#{dodDay}")
        .formParam("dod-month", "#{dodMonth}")
        .formParam("dod-year", "#{dodYear}")
        .check(CsrfCheck.save)
        .check(substring("permanent address at the time of their death")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_130_DeceasedAddressSubmit") {

      exec(http("DeceasedAddressSubmit")
        .post(BaseURL + "/intestacy/deceased-address")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("addressLine1", "1 Perf#{randomString} Road")
        .formParam("addressLine2", "")
        .formParam("addressLine3", "")
        .formParam("postTown", "Perf #{randomString} Town")
        .formParam("newPostCode", "#{postcode}")
        .formParam("country", "United Kingdom")
        .check(CsrfCheck.save)
        .check(substring("die in England or Wales")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_140_DiedEngOrWalesSubmit") {

      exec(http("DiedEngOrWalesSubmit")
        .post(BaseURL + "/intestacy/died-eng-or-wales")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("diedEngOrWales", "optionYes")
        .check(CsrfCheck.save)
        .check(substring("Do you have a death certificate")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_150_CertificateInterimSubmit") {

      exec(http("CertificateInterimSubmit")
        .post(BaseURL + "/intestacy/certificate-interim")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("deathCertificate", "optionDeathCertificate")
        .check(CsrfCheck.save)
        .check(substring("report the estate value to HMRC")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_155_CalcCheckSubmit") {

      exec(http("CalcCheckSubmit")
        .post(BaseURL + "/intestacy/calc-check")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("calcCheckCompleted", "optionYes")
        .check(CsrfCheck.save)
        .check(substring("Did you need to submit form IHT400")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_160_HMRCFormsSubmit") {

      exec(http("HMRCFormsSubmit")
        .post(BaseURL + "/intestacy/new-submitted-to-hmrc")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("estateValueCompleted", "optionYes")
        .check(CsrfCheck.save)
        .check(substring("Have you received a letter or email from HMRC")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_165_HMRCLetterSubmit") {

      exec(http("HMRCLetterSubmit")
        .post(BaseURL + "/intestacy/hmrc-letter")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("hmrcLetterId", "optionYes")
        .check(CsrfCheck.save)
        .check(substring("Enter the unique probate code")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_170_HMRCCodeSubmit") {

      exec(http("HMRCCodeSubmit")
        .post(BaseURL + "/intestacy/unique-probate-code")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("uniqueProbateCodeId", "CTS 040523 1104 3tpp s8e9")
        .check(CsrfCheck.save)
        .check(substring("What are the values of assets")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_180_EstateValuesSubmit") {

      exec(http("EstateValuesSubmit")
        .post(BaseURL + "/intestacy/probate-estate-values")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("grossValueField", "900000")
        .formParam("netValueField", "800000")
        .check(CsrfCheck.save)
        .check(substring("assets outside of England")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_185_AssetsOutsideUKSubmit") {

      exec(http("AssetsOutsideUKSubmit")
        .post(BaseURL + "/intestacy/assets-outside-england-wales")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("assetsOutside", "optionNo")
        .check(CsrfCheck.save)
        .check(substring("assets in another name")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_190_DeceasedAliasSubmit") {

      exec(http("DeceasedAliasSubmit")
        .post(BaseURL + "/intestacy/deceased-alias")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("alias", "optionNo")
        .check(CsrfCheck.save)
        .check(substring("marital status")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_200_MaritalStatusSubmit") {

      exec(http("MaritalStatusSubmit")
        .post(BaseURL + "/intestacy/deceased-marital-status")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("maritalStatus", "optionWidowed")
        .check(substring("Complete these steps"))
        .check(regex("Tell us about the person who has died(?s).*?govuk-task-list__status\">(.+?)</div>").is("Completed")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

  val IntestacyApplicationSection2 =

    group("Intestacy_210_SectionTwoStart") {

      exec(http("SectionTwoStart")
        .get(BaseURL + "/intestacy/relationship-to-deceased")
        .headers(CommonHeader)
        .check(CsrfCheck.save)
        .check(substring("What is your relationship")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_220_RelationshipSubmit") {

      exec(http("RelationshipSubmit")
        .post(BaseURL + "/intestacy/relationship-to-deceased")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("relationshipToDeceased", "optionChild")
        .check(CsrfCheck.save)
        .check(substring("legally adopt you into their family")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_222_AdoptedInSubmit") {

      exec(http("AdoptedInSubmit")
        .post(BaseURL + "/intestacy/main-applicant-adopted-in")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("adoptedIn", "optionNo")
        .check(CsrfCheck.save)
        .check(substring("egally adopt you out of their family")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_223_AdoptedOutSubmit") {

      exec(http("AdoptedOutSubmit")
        .post(BaseURL + "/intestacy/main-applicant-adopted-out")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("adoptedOut", "optionNo")
        .check(CsrfCheck.save)
        .check(substring("any other children")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_225_AnyOtherChildrenSubmit") {

      exec(http("AnyOtherChildrenSubmit")
        .post(BaseURL + "/intestacy/any-other-children")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("anyOtherChildren", "optionYes")
        .check(CsrfCheck.save)
        .check(substring("Did any of these children die before")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_227_PredeceasedChildrenSubmit") {

      exec(http("PredeceasedChildrenSubmit")
        .post(BaseURL + "/intestacy/any-predeceased-children")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("anyPredeceasedChildren", "optionNo")
        .check(CsrfCheck.save)
        .check(substring("18 or older")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_228_ChildrenOver18Submit") {

      exec(http("ChildrenOver18")
        .post(BaseURL + "/intestacy/all-children-over-18")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("allChildrenOver18", "optionYes")
        .check(CsrfCheck.save)
        .check(substring("What is your full name")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_230_ApplicantNameSubmit") {

      exec(http("ApplicantNameSubmit")
        .post(BaseURL + "/intestacy/applicant-name")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("firstName", "Perf#{randomString}")
        .formParam("lastName", "ExecOne#{randomString}")
        .check(CsrfCheck.save)
        .check(substring("What is your phone number")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_240_ApplicantPhoneSubmit") {

      exec(http("ApplicantPhoneSubmit")
        .post(BaseURL + "/intestacy/applicant-phone")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("phoneNumber", "07000000000")
        .check(CsrfCheck.save)
        .check(substring("What is your address")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_250_ApplicantAddressSubmit") {

      exec(http("ApplicantAddressSubmit")
        .post(BaseURL + "/intestacy/applicant-address")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("addressLine1", "2 Perf#{randomString} Road")
        .formParam("addressLine2", "")
        .formParam("addressLine3", "")
        .formParam("postTown", "Perf #{randomString} Town")
        .formParam("newPostCode", "#{postcode}")
        .formParam("country", "United Kingdom")
        .check(substring("Do you want to apply with anyone else")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_251_JointApplicationSubmit") {

      exec(http("JointApplicationSubmit")
        .post(BaseURL + "/intestacy/joint-application")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("hasCoApplicant", "optionYes")
        .formParam("hasCoApplicantChecked", "true")
        .check(substring("What is the other applicant")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_252_RelationshipSubmit") {

      exec(http("RelationshipSubmit")
        .post(BaseURL + "/coapplicant-relationship-to-deceased/1")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("coApplicantRelationshipToDeceased", "optionChild")
        .check(substring("The name you give us will be written on the grant of probate")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_253_CoapplicantNameSubmit") {

      exec(http("CoapplicantNameSubmit")
        .post(BaseURL + "/intestacy/coapplicant-name/1")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("fullName", "Perf#{randomString} ExecTwo#{randomString}")
        .check(substring("legally adopted into")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_254_CoapplicantAdoptedInSubmit") {

      exec(http("CoapplicantAdoptedInSubmit")
        .post(BaseURL + "/intestacy/coapplicant-adopted-in/1")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("adoptedIn", "optionNo")
        .check(substring("legally adopted out")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_255_CoapplicantAdoptedOutSubmit") {

      exec(http("CoapplicantAdoptedOutSubmit")
        .post(BaseURL + "/intestacy/coapplicant-adopted-out/1")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("adoptedOut", "optionNo")
        .check(substring("email address")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_256_CoapplicantEmailSubmit") {

      exec(http("CoapplicantEmailSubmit")
        .post(BaseURL + "/intestacy/coapplicant-email/1")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("email", "Perf#{randomString}@perftest.com")
        .check(substring("s address")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_257_CoapplicantAddressSubmit") {

      exec(http("CoapplicantAddressSubmit")
        .post(BaseURL + "/intestacy/executor-address/1")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("addressLine1", "3 Perf#{randomString} Road")
        .formParam("addressLine2", "")
        .formParam("addressLine3", "")
        .formParam("postTown", "Perf #{randomString} Town")
        .formParam("newPostCode", "#{postcode}")
        .formParam("country", "United Kingdom")
        .check(substring("Do you want to apply with anyone else")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_258_JointApplicationSubmit") {

      exec(http("JointApplicationSubmit")
        .post(BaseURL + "/intestacy/joint-application")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("hasCoApplicant", "optionNo")
        .formParam("hasCoApplicantChecked", "true")
        .check(regex("Give details about the people applying(?s).*?<span class=.govuk-tag task-completed.>Completed</span>|Equality and diversity questions")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

  val IntestacyApplicationSection3 =

    group("Intestacy_260_SectionThreeStart") {

      exec(http("SectionThreeStart")
        .get(BaseURL + "/summary/declaration")
        .headers(CommonHeader)
        .check(substring("Check your answers")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_270_Declaration") {

      exec(http("Declaration")
        .get(BaseURL + "/declaration")
        .headers(CommonHeader)
        .check(CsrfCheck.save)
        .check(substring("Check the legal statement and make your declaration")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_280_DeclarationSubmit") {

      exec(http("DeclarationSubmit")
        .post(BaseURL + "/declaration")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .formParam("declarationCheckbox", "true")
        .check(substring("Notify the other applicants")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_281_NotifyApplicantsSubmit") {

      exec(http("NotifyApplicantsSubmit")
        .post(BaseURL + "/intestacy/executors-invite")
        .headers(CommonHeader)
        .headers(PostHeader)
        .formParam("_csrf", "#{csrf}")
        .formParam("isSaveAndClose", "false")
        .check(substring("notified the other executors who are applying for probate")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    .group("Intestacy_282_ReturnToTaskList") {

      exec(http("ReturnToTaskList")
        .get(BaseURL + "/task-list")
        .headers(CommonHeader)
        .check(substring("Complete these steps"))
        .check(substring("All applicants must make their legal declaration"))
        .check(regex("Check your answers and make your legal declaration(?s).*?govuk-task-list__status\">(.+?)</div>").is("Completed")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    //Get the invite ID associated with the second executor

    .group("Probate_Util_InviteIdList") {

      exec(http("InviteIdList")
        .get(BaseURL + "/inviteIdList")
        .headers(CommonHeader)
        .check(regex("\\\"ids\\\":\\[\\\"(.+?)\\\"").saveAs("inviteId")))

    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

}
