package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import ccd._

object CW_IssueGrant {

  val CaseWorkerUserFeeder = csv("CWUserData.csv").circular

  val IssueGrant = {

    feed(CaseWorkerUserFeeder)

    //Upload Document (Will)
    .exec(CcdHelper.uploadDocumentToCdam(
      "#{cw-user}",
      "#{cw-password}",
      CcdCaseTypes.PROBATE_GrantOfRepresentation,
      "2MB.pdf",
      additionalChecks = Seq(
        jsonPath("$.documents[0]._links.self.href").saveAs("DocumentURL")
    )))

    .pause(1)

    //Link uploaded document to case
    .exec(CcdHelper.addCaseEvent("#{cw-user}", "#{cw-password}", CcdCaseTypes.PROBATE_GrantOfRepresentation, "#{caseId}", "boUploadDocumentsAwaitingDoc", "bodies/CCD_boUploadDocumentsAwaitingDoc.json"))

    .pause(1)

    //Print the case
    .exec(CcdHelper.addCaseEvent("#{cw-user}", "#{cw-password}", CcdCaseTypes.PROBATE_GrantOfRepresentation, "#{caseId}", "boGenerateGrantPreviewForExamining", "bodies/CCD_boGenerateGrantPreviewForExamining.json"))

    .pause(1)

    //Find matches (Issue grant)
    .exec(CcdHelper.addCaseEvent("#{cw-user}", "#{cw-password}", CcdCaseTypes.PROBATE_GrantOfRepresentation, "#{caseId}", "boFindMatchesNoQA", "bodies/CCD_FindMatchesNoQA.json"))

    .pause(1)

    //Issue grant
    .exec(CcdHelper.addCaseEvent("#{cw-user}", "#{cw-password}", CcdCaseTypes.PROBATE_GrantOfRepresentation, "#{caseId}", "boIssueGrantForCaseMatching", "bodies/CCD_IssueGrantForCaseMatching.json"))

    .pause(1)
  }

}