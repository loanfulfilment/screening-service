package com.swapnilsankla.screeningservice.publisher

import com.fasterxml.jackson.databind.ObjectMapper
import com.swapnilsankla.screeningservice.model.ScreeningResult
import com.swapnilsankla.tracestarter.CustomKafkaTemplate
import com.swapnilsankla.tracestarter.Trace
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
class ScreeningDataAvailableEventPublisher(@Autowired val kafkaTemplate: CustomKafkaTemplate,
                                           @Autowired val objectMapper: ObjectMapper) {

    fun publish(screening: ScreeningResult, trace: Trace) {
        Logger.getLogger(ScreeningDataAvailableEventPublisher::class.simpleName).info("raising event $screening")

        kafkaTemplate.publish(
                topic = "screeningDataAvailableForLoanProcessing",
                data = screening,
                trace = trace
        )
    }
}