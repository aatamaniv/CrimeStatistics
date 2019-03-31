package com.atamaniv.model

object ColumnNames extends Enumeration {
  type ColumnNames = Value

  val ID = Value("Crime ID")
  val MONTH = Value("Month")
  val REPORTED_BY = Value("Reported by")
  val FALLS_WITHIN = Value("Falls within")
  val LONGITUDE = Value("Longitude")
  val LATITUDE = Value("Latitude")
  val LOCATION = Value("Location")
  val LSOA_CODE = Value("LSOA code")
  val LSOA_NAME = Value("LSOA name")
  val CRIME_TYPE = Value("Crime type")
  val LAST_OUTCOME = Value("Last outcome category")
  val CONTEXT = Value("Context")
}