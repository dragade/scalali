package com.dragade.scalali

import xml.XML

/**
 * Simple class to hold person info
 */
case class Person(val firstName: String, val lastName: String, val picture: String) {

  def hasPicture = (picture != null && ! picture.isEmpty)
}

object Person {
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
      Person(firstName, lastName, picture)
    })
  }

}
