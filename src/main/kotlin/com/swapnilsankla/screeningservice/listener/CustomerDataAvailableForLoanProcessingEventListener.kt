package com.swapnilsankla.screeningservice.listener

import com.fasterxml.jackson.databind.ObjectMapper
import com.swapnilsankla.screeningservice.model.FraudStatus
import com.swapnilsankla.screeningservice.model.Screening
import com.swapnilsankla.screeningservice.model.ScreeningResult
import com.swapnilsankla.screeningservice.publisher.ScreeningDataAvailableEventPublisher
import com.swapnilsankla.screeningservice.repository.ScreeningRepository
import com.swapnilsankla.tracestarter.TraceIdExtractor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Headers
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.logging.Logger

@Component
class CustomerDataAvailableForLoanProcessingEventListener(@Autowired val repository: ScreeningRepository,
                                                          @Autowired val objectMapper: ObjectMapper,
                                                          @Autowired val screeningDataAvailableEventPublisher: ScreeningDataAvailableEventPublisher,
                                                          @Autowired val traceIdExtractor: TraceIdExtractor) {

    private val logger = Logger.getLogger(CustomerDataAvailableForLoanProcessingEventListener::class.simpleName)

    @KafkaListener(topics = ["customerDataAvailableForLoanProcessing"])
    fun listen(customerDataAvailableForLoanProcessingEventString: String, @Headers headers: Map<String, Any>) {
        val customerData = objectMapper.readValue(customerDataAvailableForLoanProcessingEventString, Customer::class.java)
        logger.info("customerDataAvailableForLoanProcessing event received for customer ${customerData.customerId}")

        val trace = traceIdExtractor.fromKafkaHeaders(headers)

        customerData.pan ?: return screeningDataAvailableEventPublisher.publish(ScreeningResult(customerData.customerId, FraudStatus.UNKNOWN), trace)

        repository
                .findByNameAndPan(customerData.name, customerData.pan)
                .switchIfEmpty(Mono.error(ScreeningDataNotFound(customerData.customerId)))
                .map { mapToScreeningResult(customerData.customerId, it) }
                .doOnError { mapToScreeningResult(customerData.customerId, null) }
                .doOnSuccess { screeningDataAvailableEventPublisher.publish(it, trace) }
                .subscribe()
    }

    fun mapToScreeningResult(customerId: String, screening: Screening?) = ScreeningResult(customerId, screening?.fraudStatus ?: FraudStatus.UNKNOWN)
}

class ScreeningDataNotFound(customerId: String): Throwable("Screening data not found for customer id $customerId")