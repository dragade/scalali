package com.dragade.scalali

/**
 * Creating two separate classes to wrap the token and secret parts for both
 * request tokens and access tokens.
 * Scribe uses the same Token class for both and this can cause confusion.
 * Plus we don't want to expose Scribe through the ScalaLi API
 */
case class RequestToken(token:String,secret:String)
