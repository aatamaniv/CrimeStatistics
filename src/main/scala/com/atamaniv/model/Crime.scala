package com.atamaniv.model

import java.util.UUID

case class Crime (crimeId: UUID,
                  coordinate: Coordinates,
                  reportedBy: String,
                  fallsWithin: String,
                  Location: String,
                  lsoaCode: String,
                  lsodName: String,
                  crimeType: String,
                  lastOutcome: String
                 )
