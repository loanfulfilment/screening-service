package com.swapnilsankla.screeningservice.publisher

import com.fasterxml.jackson.databind.ObjectMapper
import com.swapnilsankla.screeningservice.model.Screening
import com.swapnilsankla.screeningservice.model.ScreeningResult
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
class ScreeningDataAvailableEventPublisher(@Autowired val kafkaTemplate: KafkaTemplate<String, String>,
                                           @Autowired val objectMapper: ObjectMapper) {

    fun publish(screening: ScreeningResult, traceId: ByteArray) {
        Logger.getLogger(ScreeningDataAvailableEventPublisher::class.simpleName).info("raising event $screening")

        kafkaTemplate.send(buildMessage(screening, traceId))
    }

    private fun buildMessage(screening: ScreeningResult, traceId: ByteArray): ProducerRecord<String, String> {
        val message = ProducerRecord<String, String>(
                "screeningDataAvailableForLoanProcessing",
                objectMapper.writeValueAsString(screening)
        )
        message.headers().remove("uber-trace-id")
        message.headers().add("uber-trace-id", traceId)

        return message
    }
}