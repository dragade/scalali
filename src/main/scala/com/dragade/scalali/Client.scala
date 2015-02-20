package com.dragade.scalali

import com.dragade.scalali.LinkedIn.API_SERVER
import org.scribe.model.{Response, Token, Verb, OAuthRequest}
import org.scribe.oauth.OAuthService

class Client(oauthService: OAuthService, accessToken: AccessToken) {
  /**
   * Queries for your profile and returns a Person object if it gets back profile data for you
   */
  def myProfile: Option[Person] = {
    val restUrl = "%s/people/~:(%s)".format(API_SERVER, Person.DEFAULT_PROFILE_FIELDS)
    parsePeople(restUrl).headOption
  }

  /**
   * Queries for your connections and returns a Seq of people
   */
  def myConnections: Seq[Person] = {
    val restUrl = "%s/people/~/connections:(%s)".format(API_SERVER, Person.DEFAULT_PROFILE_FIELDS)
    parsePeople(restUrl)
  }

  def peopleSearch: PeopleSearch = {
    PeopleSearch(this)
  }

  /**
   * Calls the API with the URL and parsers the resulting xml into a Seq of People
   */
  private def parsePeople(restUrl: String) : Seq[Person] = {
    if (restUrl.endsWith("json")) { throw new Exception("Sorry but json responses are not supported") }
    val apiResponse = makeApiCall(restUrl)
    Person.parsePeopleXml(apiResponse)
  }

  /**
   * Recreates the access token, and signs a request for the given resource, returns the raw response body.
   * This method is exposed so you can directly use things from the linkedin REST API not covered by ScalaLi
   */
  def makeApiCall(restUrl: String) : String = {
    val orequest: OAuthRequest = new OAuthRequest(Verb.GET, restUrl)
    oauthService.signRequest(new Token(accessToken.token, accessToken.secret), orequest)
    val oresponse: Response = orequest.send()
    oresponse.getBody()
  }

  /**
   * Makes a voldemort API call for the given store and set of keys and format.
   * The format must be xml or json.
   * Only works if you have permissions to use the API
   */
  def voldemort(storeName: String, keys: Set[String], format : String = "json") = {
    val restFormat = if (format != null && format.toLowerCase.equals("xml")) { "xml" } else { "json" }
    val csvKeys = keys.mkString(",")
    val restUrlPattern = "%s/voldemort/stores/%s/values::(%s)?format=%s"
    val restUrl = restUrlPattern.format(API_SERVER, storeName, csvKeys, restFormat)
    makeApiCall(restUrl)
  }
}
