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

  /**
   * Before using ScalaLI, you need to call intialize(), then go to that URL to authenticate, then pass
   * the oauth token and oauth_verifier you get back into the verify call.
   * Returns the url, requestToken, and requestTokenSecret
   */
  def initialize() : (String, String, String) = {
    val requestToken = oauthService.getRequestToken()
    val url = oauthService.getAuthorizationUrl(requestToken)
    (url, requestToken.getToken, requestToken.getSecret)
  }

  /**
   * The client needs to hit the authorization URL and then enter in the verifier code that is returned
   * along with the requestToken and requestTokenSecret that were returned from initialize()
   */
  def verify(requestToken: String, requestTokenSecret: String, oauthVerifier: String) : (String,String) = {
    val accessToken = oauthService.getAccessToken(
      new Token(requestToken, requestTokenSecret),
      new Verifier(oauthVerifier));
    (accessToken.getToken, accessToken.getSecret)
  }

  /**
   * Queries for your profile and returns a Person object if it gets back profile data for you
   */
  def myProfile(accessToken:String, accessTokenSecret:String) : Option[Person] = {
    val restUrl = "http://api.linkedin.com/v1/people/~:(id,first-name,last-name,picture-url)"
    val apiResponse = makeApiCall(accessToken, accessTokenSecret, restUrl)
    val people = Person.parsePeopleXml(apiResponse)
    people.headOption
  }

  /**
   * Recreates the access token, and signs a request for the given resource, returns the raw response body.
   * This method is exposed so you can directly use things from the linkedin REST API not covered by ScalaLi
   */
  def makeApiCall(accessToken:String, accessTokenSecret:String, restUrl: String) : String = {
    val orequest: OAuthRequest = new OAuthRequest(Verb.GET, restUrl);
    oauthService.signRequest(new Token(accessToken, accessTokenSecret), orequest);
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
