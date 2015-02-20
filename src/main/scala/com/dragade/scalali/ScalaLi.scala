package com.dragade.scalali

import org.scribe.builder.ServiceBuilder
import org.scribe.builder.api.LinkedInApi
import org.scribe.model._
import org.scribe.oauth.OAuthService

/**
 * The main entry point for using the LinkedIn API.
  */
class ScalaLi(apiKey: String, secretKey: String, callbackUrl: Option[String] = None) {
  val oauthService = buildOathService(apiKey, secretKey, callbackUrl)

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
      new Verifier(oauthVerifier))
    AccessToken(accessToken.getToken, accessToken.getSecret)
  }

  def makeClient(accessToken: AccessToken): Client = {
    new Client(oauthService, accessToken)
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
