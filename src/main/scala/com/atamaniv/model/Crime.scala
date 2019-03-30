package com.atamaniv.model

case class Crime (crimeId: String,
                  coordinates: Coordinates,
                  reportedBy: Option[String] = None,
                  fallsWithin: Option[String] = None,
                  location: Option[String] = None,
                  lsoaCode: Option[String] = None,
                  lsoaName: Option[String] = None,
                  crimeType: Option[String] = None,
                  lastOutcome: Option[String] = None,
                  context: Option[String] = None
                 ) extends Serializable
