package com.dragade.scalali

import org.scribe.builder.ServiceBuilder
import org.scribe.builder.api.{Api, LinkedInApi}
import org.scribe.oauth.OAuthService
import org.scribe.model._

/**
 * The main entry point for using the LinkedIn API.
 *
 * This class is stateless, so the client needs to remember the keys and hand them back on every call.
 */
class ScalaLi(apiKey: String, secretKey: String, callbackUrl: Option[String] = None) {

  val oauthService = buildOathService(apiKey, secretKey, callbackUrl)

  private val API_SERVER = "http://api.linkedin.com/v1"

  /**
   * Before using ScalaLI, you need to call intialize(), then go to that URL to authenticate, then pass
   * the oauth token and oauth_verifier you get back into the verify call.
   * Returns the url and requestToken
   */
  def initialize() : (String, RequestToken) = {
    val requestToken = oauthService.getRequestToken()
    val url = oauthService.getAuthorizationUrl(requestToken)
    (url, RequestToken(requestToken.getToken, requestToken.getSecret))
  }

  /**
   * The client needs to hit the authorization URL and then enter in the verifier code that is returned
   * along with the requestToken that was returned from initialize()
   */
  def verify(requestToken: RequestToken, oauthVerifier: String) : AccessToken = {
    val accessToken = oauthService.getAccessToken(
      new Token(requestToken.token, requestToken.secret),
      new Verifier(oauthVerifier));
    AccessToken(accessToken.getToken, accessToken.getSecret)
  }

  /**
   * Queries for your profile and returns a Person object if it gets back profile data for you
   */
  def myProfile(accessToken: AccessToken) : Option[Person] = {
    val restUrl = "%s/people/~:(%s)".format(API_SERVER, Person.DEFAULT_PROFILE_FIELDS)
    parsePeople(accessToken, restUrl).headOption
  }

  /**
   * Queries for your connections and returns a Seq of people
   */
  def myConnections(accessToken: AccessToken) : Seq[Person] = {
    val restUrl = "%s/people/~/connections:(%s)".format(API_SERVER, Person.DEFAULT_PROFILE_FIELDS)
    parsePeople(accessToken, restUrl)
  }

  /**
   * Calls the API with the URL and parsers the resulting xml into a Seq of People
   */
  def parsePeople(accessToken: AccessToken, restUrl:String) : Seq[Person] = {
    if (restUrl.endsWith("json")) { throw new Exception("Sorry but json responses are not supported") }
    val apiResponse = makeApiCall(accessToken, restUrl)
    Person.parsePeopleXml(apiResponse)
  }

  /**
   * Recreates the access token, and signs a request for the given resource, returns the raw response body.
   * This method is exposed so you can directly use things from the linkedin REST API not covered by ScalaLi
   */
  def makeApiCall(accessToken:AccessToken, restUrl: String) : String = {
    val orequest: OAuthRequest = new OAuthRequest(Verb.GET, restUrl);
    oauthService.signRequest(new Token(accessToken.token, accessToken.secret), orequest);
    val oresponse: Response = orequest.send();
    oresponse.getBody();
  }

  /**
   * Builds up a builder and then uses it to create the oauth service
   */
  private def buildOathService(apiKey: String, secretKey: String, callbackUrl: Option[String] = None): OAuthService = {
    var builder = new ServiceBuilder().provider(classOf[LinkedInApi])
    builder = builder.apiKey(apiKey).apiSecret(secretKey)
    builder = if (callbackUrl.isDefined) {
      builder.callback(callbackUrl.get)
    } else {
      builder
    }
    builder.build()
  }
}

/**
 * Creating two separate classes to wrap the token and secret parts for both
 * request tokens and access tokens.
 * Scribe uses the same Token class for both and this can cause confusion.
 * Plus we don't want to expose Scribe through the ScalaLi API
 */
case class RequestToken(token:String,secret:String)
case class AccessToken(token:String,secret:String)