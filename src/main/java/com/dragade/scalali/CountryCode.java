package com.dragade.scalali;

/**
 * http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2#Officially_assigned_code_elements
 */
public enum CountryCode {
  CANADA("CA"),
  UNITED_STATES("US");

  public final String code;

  CountryCode(String code) {
    this.code = code;
  }
}
