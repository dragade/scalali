package com.dragade.scalali

import xml.XML

/**
 * Simple class to hold person info
 */
case class Person(id:String, firstName: String, lastName: String, picture: Option[String], headline: Option[String])

object Person {
  val DEFAULT_PROFILE_FIELDS = "id,first-name,last-name,picture-url,headline"

  /**
   * Parse the XML response from the API and return a sequence of Person
   */
  def parsePeopleXml(apiResponse: String) : Seq[Person] = {
    val xml = XML.loadString(apiResponse)
    val people = xml \\ "person"

    people.map(p => {
      val id = (p \ "id").text
      val firstName = (p \ "first-name").text
      val lastName = (p \ "last-name").text
      val picture = maybe((p \ "picture-url").text)
      val headline = maybe((p \ "headline").text)
      Person(id, firstName, lastName, picture, headline)
    })
  }

  /**
   * If the string is empty or null, makes it an Option
   */
  private def maybe(s:String) : Option[String] = if (s == null || s.isEmpty) None else Some(s)
}
