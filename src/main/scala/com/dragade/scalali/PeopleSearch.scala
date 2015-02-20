package com.dragade.scalali

import java.net.{URLEncoder, URL}

/**
 * https://apigee.com/console/linkedin
 */
case class PeopleSearch(client: Client,
                        keywords: Option[Seq[String]] = None,
                        firstName: Option[String] = None,
                        lastName: Option[String] = None,
                        companyName: Option[String] = None,
                        currentCompany: Option[Boolean] = None,
                        title: Option[String] = None,
                        currentTitle: Option[Boolean] = None,
                        schoolName: Option[String] = None,
                        currentSchool: Option[Boolean] = None,
                        countryCode: Option[CountryCode] = None,
                        postalCode: Option[String] = None,
                        distance: Option[Int] = None,
                        start: Option[Int] = None,
                        count: Option[Int] = None) {
  def withKeyword(ks: String)          = { copy(keywords = Some(Seq(ks))) }
  def withKeywords(ks: Seq[String])    = { copy(keywords = Some(ks)) }
  def withFirstName(fn: String)        = { copy(firstName = Some(fn)) }
  def withLastName(ln: String)         = { copy(lastName = Some(ln)) }
  def withCompanyName(cn: String)      = { copy(companyName = Some(cn)) }
  def withCurrentCompany(cc: Boolean)  = { copy(currentCompany = Some(cc)) }
  def withTitle(t: String)             = { copy(title = Some(t)) }
  def withCurrentTitle(ct: Boolean)    = { copy(currentTitle = Some(ct)) }
  def withSchoolName(sn: String)       = { copy(schoolName = Some(sn)) }
  def withCurrentSchool(cs: Boolean)   = { copy(currentSchool = Some(cs)) }
  def withCountryCode(cc: CountryCode) = { copy(countryCode = Some(cc)) }
  def withPostalCode(pc: String)       = { copy(postalCode = Some(pc)) }
  def withDistance(d: Int)             = { copy(distance = Some(d)) }
  def withStart(s: Int)                = { copy(start = Some(s)) }
  def withCount(c: Int)                = { copy(count = Some(c)) }

  private lazy val params: Seq[(String, Option[String])] = Seq(
    "keywords"        -> keywords.map(_.mkString(" ")),
    "first-name"      -> firstName,
    "last-name"       -> lastName,
    "company-name"    -> companyName,
    "current-company" -> currentCompany.map(boolToString),
    "title"           -> title,
    "current-title"   -> currentTitle.map(boolToString),
    "school-name"     -> schoolName,
    "current-school"  -> currentSchool.map(boolToString),
    "country-code"    -> countryCode.map(_.code),
    "postal-code"     -> postalCode,
    "distance"        -> distance.map(_.toString),
    "start"           -> start.map(_.toString),
    "count"           -> count.map(_.toString)
  ).filter(_._2.isDefined)

  private def boolToString(b: Boolean): String = {
    if (b) { "true" } else { "false" }
  }

  def requestUrl: URL = {
    val paramStr = params.map { case (k,v) => s"${k}=${URLEncoder.encode(v.get)}" }.mkString("&")
    new URL(s"${LinkedIn.API_SERVER}/people-search?$paramStr")
  }

  def result: String = {
    println(requestUrl)
    client.makeApiCall(requestUrl.toString)
  }
}
