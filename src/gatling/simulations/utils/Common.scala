package utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import scala.util.Random

object Common {

  val rnd = new Random()
  val now = LocalDate.now()
  val patternDay = DateTimeFormatter.ofPattern("dd")
  val patternMonth = DateTimeFormatter.ofPattern("MM")
  val patternYear = DateTimeFormatter.ofPattern("yyyy")
  val patternExpiryYear = DateTimeFormatter.ofPattern("yy")
  val patternDate = DateTimeFormatter.ofPattern("yyyyMMdd")

  def randomString(length: Int) = {
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  def getDate(): String = {
    now.format(patternDate)
  }

  def getDay(): String = {
    (1 + rnd.nextInt(28)).toString.format(patternDay).reverse.padTo(2, '0').reverse //pads single-digit dates with a leading zero
  }

  def getMonth(): String = {
    (1 + rnd.nextInt(12)).toString.format(patternMonth).reverse.padTo(2, '0').reverse //pads single-digit dates with a leading zero
  }

  //Dob >= 25 years
  def getDobYear(): String = {
    now.minusYears(25 + rnd.nextInt(70)).format(patternYear)
  }
  //Dod <= 2 years
  def getDodYear(): String = {
    now.minusYears(1 + rnd.nextInt(2)).format(patternYear)
  }

  def getCardExpiryYear(): String = {
    now.plusYears(1 + rnd.nextInt(2)).format(patternExpiryYear)
  }

  def getPostcode(): String = {
    randomString(2).toUpperCase() + rnd.nextInt(10).toString + " " + rnd.nextInt(10).toString + randomString(2).toUpperCase()
  }

}