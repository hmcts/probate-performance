package uk.gov.hmcts.reform.probate.performance.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.cmc.performance.utils.Environment
import uk.gov.hmcts.reform.probate.performance.caveat.CaveatJourney.thinktime
import uk.gov.hmcts.reform.probate.performance.simulations.checks.CsrfCheck

class IntestacyJourney_Payment extends Simulation {

  val userFeeder = csv("probate_executors3.csv").queue

  val httpProtocol = http
    .baseUrl(Environment.intestacyURL)
    .proxy(Proxy("proxyout.reform.hmcts.net", 8080).httpsPort(8080))
    .doNotTrackHeader("1")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")

  val uri1 = "https://idam-web-public.aat.platform.hmcts.net"
  val uri2 = "https://www.payments.service.gov.uk"

  val headers_0 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "none",
    "Upgrade-Insecure-Requests" -> "1",
    "User-Agent" -> "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36")

  val headers_1 = Map(
    "Accept" -> "text/css,*/*;q=0.1",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Mode" -> "no-cors",
    "Sec-Fetch-Site" -> "same-origin",
    "User-Agent" -> "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36")

  val headers_2 = Map(
    "Accept" -> "*/*",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Mode" -> "no-cors",
    "Sec-Fetch-Site" -> "same-origin",
    "User-Agent" -> "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36")

  val headers_5 = Map(
    "Accept" -> "*/*",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Origin" -> "https://probate-frontend-aat.service.core-compute-aat.internal",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "same-origin",
    "User-Agent" -> "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36")

  val headers_8 = Map("User-Agent" -> "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36")

  val probscreeningStart =

    exec(http("PROBATEThree_010_StartEligibility")
      .get("/start-eligibility")
      .check(CsrfCheck.save))

      .pause(1)

  val probscreeningDeathEligibility =

    exec(http("PROBATEThree_020_DeathCertificate")
      .get("/death-certificate")
      .check(CsrfCheck.save))
      .pause(1)

  val probscreeningDeceaseDomicile =

    exec(http("PROBATEThree_030_Domicile")
      .post("/deceased-domicile")
      .formParam("_csrf", "${csrf}")
      .formParam("domicile", "Yes")
      .check(CsrfCheck.save))
      .pause(1)

  val ihtCompleted =

    exec(http("PROBATEThree_040_IHT_Completed")
      .post("/iht-completed")
      .formParam("_csrf", "${csrf}")
      .formParam("completed", "Yes")
      .check(CsrfCheck.save))
      .pause(1)

  val willLeft =

    exec(http("PROBATEThree_050_Is_Will_Left")
      .post("/will-left")
      .formParam("left", "No")
      .formParam("_csrf", "${csrf}")
      .check(CsrfCheck.save))
      .pause(1)

  val diedAfterOctober2014 =

    exec(http("PROBATEThree_060_Is_Died_After_2014")
      .post("/died-after-october-2014")
      .formParam("diedAfter", "Yes")
      .formParam("_csrf", "${csrf}")
      .check(CsrfCheck.save))
      .pause(1)

  val relatedToDecesed =

    exec(http("PROBATEThree_070_Is_Related_To_Deceased")
      .post("/related-to-deceased")
      .formParam("related", "Yes")
      .formParam("_csrf", "${csrf}")
      .check(CsrfCheck.save))
      .pause(1)

  val otherApplicants =

    exec(http("PROBATEThree_080_Is_Other_Applicants")
      .post("/other-applicants")
      .formParam("otherApplicants", "No")
      .formParam("_csrf", "${csrf}")
      .check(CsrfCheck.save))
      .pause(1)

  /*val dashBoard =

    exec(http("PROBATEThree_080_DashBoard")
      .get("/dashboard")
      .check(CsrfCheck.save))
      .pause(1)*/

  val dashBoard =

    exec(http("PROBATEThree_080_DashBoard")
      .get("/dashboard")
      .check(CsrfCheck.save)
      .check(css(".form-group>input[name='client_id']", "value").saveAs("clientId"))
      .check(css(".form-group>input[name='state']", "value").saveAs("state"))
      .check(css(".form-group>input[name='redirect_uri']", "value").saveAs("redirectUri"))
      .check(css(".form-group>input[name='continue']", "value").saveAs("continue"))
      .check(regex("Email address")))

      .pause(1)

  val probUserFeed = feed(userFeeder)

  val probLogin =
//need to create the test data for probate
    feed(userFeeder)

    .exec(http("PROBATEThree_100_Login")
      .post(uri1 + "/login?response_type=code&state=${state}&client_id=probate&redirect_uri=https%3A%2F%2Fprobate-frontend-aat.service.core-compute-aat.internal%2Foauth2%2Fcallback")
      .formParam("username", "${email}") //pt.probate0151@perftest.uk.gov
      .formParam("password", "${password}")
      .formParam("selfRegistrationEnabled", "true")
      .formParam("_csrf", "${csrf}")
      .formParam("save", "Sign in")
      .check(regex("Apply for probate")))

    .pause(1)

  val getIntestacyCase =

    exec(http("PROBATEThree_110_Is_Other_Applicants")
//case id should be retrieved from login page
        .get("/get-case/1578008682174357?probateType=INTESTACY")
      .check(CsrfCheck.save))
      .pause(1)

  val deceasedDetails_Get =

    exec(http("PROBATEThree_120_deceasedDetails_Get")
      //case id should be retrieved from login page
      .get("/deceased-details")
      .check(CsrfCheck.save))
      .pause(1)
  val deceasedDetails_Post =

    exec(http("PROBATEThree_140_deceasedDetails")
      //case id should be retrieved from login page
      .post("/deceased-details")
      .formParam("_csrf", "${csrf}")
      .formParam("firstName", "asasasas")
      .formParam("lastName", "hhjhjhjhjh")
      .formParam("dob-day", "01")
      .formParam("dob-month", "08")
      .formParam("dob-year", "1946")
      .formParam("dod-day", "01")
      .formParam("dod-month", "08")
      .formParam("dod-year", "2019")
      .check(CsrfCheck.save))
      .pause(1)

  val addressLookup =
    exec(http("PROBATEThree_150_AddressLookup")
      .post("/find-address")
      .formParam("_csrf", "${csrf}")
      .formParam("postcode", "tw3 1jx")
      .formParam("referrer", "DeceasedAddress")
      .formParam("addressFound", "none")
      // .headers(headers_75)
      .check(CsrfCheck.save))
      .pause(1)

  val deceasedAddress =

    exec(http("PROBATEThree_140_deceasedDetails")
      //case id should be retrieved from login page
      .post("/deceased-address")

      .formParam("_csrf", "${csrf}")
      .formParam("addressLine1", "6 Balfour Road")
      .formParam("addressLine2", "")
      .formParam("addressLine3", "")
      .formParam("postTown", "Hounslow")
      .formParam("newPostCode", "TW3 1JX")
      .formParam("country", "United Kingdom")
      .check(CsrfCheck.save))
      .pause(1)

  val fileUpload =

    exec(http("PROBATEThree_150_FileUpload")
      //case id should be retrieved from login page
      .post("/document-upload")
      .check(CsrfCheck.save))
      .pause(1)
      .exec(http("request_623")
        .post("/document-upload")
        .formParam("_csrf", "${csrf}")
        .check(CsrfCheck.save))
      .pause(1)

  val ihtmethod =

    exec(http("PROBATEThree_160_IHTMethod")
      //case id should be retrieved from login page
      .post("/iht-method")
      .formParam("_csrf", "${csrf}")
      .formParam("method", "Through the HMRC online service")
        .check(CsrfCheck.save))
      .pause(1)

  val ihtidentifier =

    exec(http("PROBATEThree_170_IHTIdentifier")
      //case id should be retrieved from login page
      .post("/iht-identifier")
      .formParam("identifier", "34321345")
      .formParam("_csrf", "${csrf}")
      .check(CsrfCheck.save))
      .pause(1)

  val ihtvalue =

    exec(http("PROBATEThree_170_IHTValue")
      //case id should be retrieved from login page
      .post("/iht-value")
      .formParam("_csrf", "${csrf}")
      .formParam("grossValueField", "100000")
      .formParam("netValueField", "80000")
      .check(CsrfCheck.save))
      .pause(1)

  val assetsOutside =

    exec(http("PROBATEThree_180_assetsOutside")
      //case id should be retrieved from login page
      .post("/assets-outside-england-wales")
      .formParam("assetsOutside", "Yes")
      .formParam("_csrf", "${csrf}")
      .check(CsrfCheck.save))
      .pause(1)

  val assetsOutsideValue =

    exec(http("PROBATEThree_180_assetsOutsideValue")
      //case id should be retrieved from login page
      .post("/value-assets-outside-england-wales")
      .formParam("_csrf", "${csrf}")
      .formParam("netValueAssetsOutsideField", "100000")
      .check(CsrfCheck.save))
      .pause(1)

  val deceasedAlias =

    exec(http("PROBATEThree_190_DeceasedAlias")
      //case id should be retrieved from login page
      .post("/deceased-alias")
      .formParam("_csrf", "${csrf}")
      .formParam("alias", "Yes")
      .check(CsrfCheck.save))
      .pause(1)

  val deceasedAlias =

    exec(http("PROBATEThree_190_DeceasedAlias")
      //case id should be retrieved from login page
      .post("/deceased-alias")
      .formParam("_csrf", "${csrf}")
      .formParam("alias", "Yes")
      .check(CsrfCheck.save))
      .pause(1)

  val otherNamesAdd =

    exec(http("PROBATEThree_190_OtherNamesAdd")
      //case id should be retrieved from login page
      .post("/other-names/add")
      .formParam("_csrf", "${csrf}")
      .formParam("otherNames[name_0][firstName]", "ddddd")
      .formParam("otherNames[name_0][lastName]", "ddddd")
      .check(CsrfCheck.save))
      .pause(1)

  val otherNamesAddFull =

    exec(http("PROBATEThree_190_OtherNamesAddFull")
      //case id should be retrieved from login page
      .post("/other-names")
      .formParam("_csrf", "${csrf}")
      .formParam("otherNames[name_0][firstName]", "ddddd")
      .formParam("otherNames[name_0][lastName]", "ddddd")
      .formParam("otherNames[name_1][firstName]", "ghtte")
      .formParam("otherNames[name_1][lastName]", "bfwsdda")
      .formParam("otherNames[name_0][firstName]", "ddddd")
      .formParam("otherNames[name_0][lastName]", "ddddd")
      .check(CsrfCheck.save))
      .pause(1)


  val deceasedMaritalStatus =

    exec(http("PROBATEThree_190_DeceasedMarital")
      //case id should be retrieved from login page
      .post("/deceased-marital-status")
      .formParam("_csrf", "${csrf}")
      .formParam("maritalStatus", "Divorced or their civil partnership was dissolved")
      .check(CsrfCheck.save))
      .pause(1)

  val deceasedDivorcePlace =

    exec(http("PROBATEThree_200_DeceasedMaritalPlace")
      //case id should be retrieved from login page
      .post("/deceased-divorce-or-separation-place")
      .formParam("_csrf", "${csrf}")
      .formParam("divorcePlace", "Yes")
      .check(CsrfCheck.save))
      .pause(1)

  val relationShipToDeceased =

    exec(http("PROBATEThree_210_RelationShipToDeceased")
      //case id should be retrieved from login page
      .get("/relationship-to-deceased")
      .formParam("divorcePlace", "Yes")
      .check(CsrfCheck.save))
      .pause(1)
      .exec(http("request_1070")
        .get("/relationship-to-deceased")
  val probDeclaration =

    exec(http("PROBATEThree_030_DeclarationSummary")
      .get("/summary/declaration")
      //.check(CsrfCheck.save)
      .headers(headers_1)
      .check(regex("Check the information below carefully")))
    .pause(1)

  val relation =

    exec(http("PROBATEThree_030_DeclarationSummary")
      .post("/relationship-to-deceased")
      .formParam("_csrf", "${csrf}")
      .formParam("relationshipToDeceased", "Child (this does not include stepchildren)"))
      .pause(1)

  val children =

    exec(http("PROBATEThree_030_DeclarationSummary")
        .post("/any-other-children")
        .formParam("_csrf", "U8DPk0nC-edxk-YNNkJ0H6L8pJ6sqqVLuw2E")
        .formParam("anyOtherChildren", "Yes")

      val declaration=
    exec(http("PROBATEThree_040_Declaration")
      .get("/declaration")
      .check(CsrfCheck.save)
      .headers(headers_1))

    .exec(http("PROBATEThree_050_DeclarationConfirmAndNotify")
      .post("/declaration")
      .headers(headers_1)
      .formParam("_csrf", "${csrf}") //eVpbq0IQ-L0dRAe--WuW-BDXWPnF4YuTCz30
      .formParam("declarationCheckbox", "true")
      .check(regex("Task list - Apply for probate")))

    .pause(1)

    .exec(http("PROBATEThree_060_TasklistPage")
      .get("/tasklist")
      .headers(headers_1)
      .check(regex("Complete these steps to get the legal right to deal with the property and belongings")))

    .pause(1)

  val probCopies =

    exec(http("PROBATEThree_070_CopiesPage")
      .get("/copies-uk")
      .check(CsrfCheck.save)
      .headers(headers_0))

    .pause(1)

    .exec(http("PROBATEThree_080_RequestNoCopies")
      .post("/copies-uk")
      .check(CsrfCheck.save)
      .headers(headers_0)
      .formParam("_csrf", "${csrf}") //A5z8lOKd-McRFFdw2eANPDiF9H2al8ZzDYCA
      .formParam("uk", "0"))

    .pause(1)

    .exec(http("PROBATEThree_090_NoOverseasAssets")
      .post("/assets-overseas")
      .headers(headers_0)
      .formParam("_csrf", "${csrf}") //pSuQi8tQ-rzdZDRarEXqArBkUFsp7VmwqLJc
      .formParam("assetsoverseas", "No"))

    .pause(1)

    /*.exec(http("request_18")
      .get("/tasklist")
      .headers(headers_0))

    .pause(1)*/

  val probPayment =

    exec(http("PROBATEThree_100_PaymentBreakdown")
      .get("/payment-breakdown")
      .check(CsrfCheck.save)
      .headers(headers_1)
      .check(regex("Application fee")))

      .pause(1)

      .exec(http("PROBATEThree_110_ConfirmPayment")
        .post("/payment-breakdown")
        .check(CsrfCheck.save)
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //w7Ngjz5W-OyH5db3nnydgOhAlKj6RP8-8qDw
        .check(regex("Before your application can be processed, you need to send your documents by post")))

      .pause(1)

      .exec(http("PROBATEThree_120_ConfirmationPage")
        .post("/payment-status")
        .check(CsrfCheck.save)
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //cFMKpcKi-K8sSIGFR9hibg7P_NpD1veVidGs
        .check(regex("I understand that I need to sign the will")))

      .pause(1)

      .exec(http("PROBATEThree_130_ApplicationSubmitted")
        .post("/documents")
        .headers(headers_1)
        .formParam("_csrf", "${csrf}") //AiOSAfFT-dxn99IGzxOglfadx3dBsmd07cPc
        .formParam("sentDocuments", "true")
        .check(regex("Application complete"))
        .check(regex("Your reference number is")))

      .pause(1)

      .exec(http("PROBATEThree_140_SignOut")
        .get("/sign-out")
        .headers(headers_1)
        .check(regex("signed out")))

  val scn = scenario("ProbateJourney_IntesticyPayment").exec(
    probscreeningStart,
    probscreeningDeathEligibility,
    probscreeningDeceaseDomicile,
    probLogin,
    probDeclaration,
    probCopies,
    probPayment
  )

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)

}