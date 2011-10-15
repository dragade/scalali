package com.dragade.scalali

import xml.XML

/**
 * Simple class to hold person info
 */
case class Person(val firstName: String, val lastName: String, val picture: String, val headline: String) {

  def hasPicture = hasX(picture)

  def hasHeadline = hasX(headline)

  private def hasX(x: String) = x != null && ! x.isEmpty
}

object Person {

  val DEFAULT_PROFILE_FIELDS = "id,first-name,last-name,picture-url,headline"

  /**
   * Parse the XML response from the API and return a sequence of Person
   */
  def parsePeopleXml(apiResponse: String) : Seq[Person] = {
    val xml = XML.loadString(apiResponse)
    val people = xml \\ "person"
    people.map(p => {
      val firstName = (p \ "first-name").text
      val lastName = (p \ "last-name").text
      val picture = (p \ "picture-url").text
      val headline = (p \ "headline").text
      Person(firstName, lastName, picture, headline)
    })
  }
}
