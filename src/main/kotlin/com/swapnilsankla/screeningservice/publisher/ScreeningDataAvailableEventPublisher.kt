package com.swapnilsankla.screeningservice.publisher

import com.fasterxml.jackson.databind.ObjectMapper
import com.swapnilsankla.screeningservice.model.Screening
import com.swapnilsankla.screeningservice.model.ScreeningResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
class ScreeningDataAvailableEventPublisher(@Autowired val kafkaTemplate: KafkaTemplate<String, String>,
                                           @Autowired val objectMapper: ObjectMapper) {

    fun publish(screening: ScreeningResult) {
        Logger.getLogger(ScreeningDataAvailableEventPublisher::class.simpleName).info("raising event $screening")

            kafkaTemplate.send("screeningDataAvailableForLoanProcessing",
                objectMapper.writeValueAsString(screening))
    }
}