package utils

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Environment {

  val baseURL = "https://probate.#{env}.platform.hmcts.net"
  val idamURL = "https://idam-web-public.#{env}.platform.hmcts.net"
  val idamAPIURL = "https://idam-api.#{env}.platform.hmcts.net"
  val paymentURL = "https://card.payments.service.gov.uk"

  val minThinkTime = 5
  val maxThinkTime = 7

  val HttpProtocol = http

  val commonHeader = Map(
    "accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-GB,en;q=0.9",
    "sec-fetch-dest" -> "document",
    "sec-fetch-mode" -> "navigate",
    "sec-fetch-site" -> "same-origin",
    "sec-fetch-user" -> "?1",
    "upgrade-insecure-requests" -> "1"
  )

  val postHeader = Map(
    "content-type" -> "application/x-www-form-urlencoded"
  )

}