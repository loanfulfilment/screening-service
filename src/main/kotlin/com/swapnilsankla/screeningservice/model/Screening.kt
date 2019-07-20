package com.swapnilsankla.screeningservice.model

import org.springframework.data.mongodb.core.mapping.Document

@Document("screening")
data class Screening(val customerId: String, val fraudStatus: FraudStatus)

enum class FraudStatus {
    CLEAR,
    FRAUDULENT
}