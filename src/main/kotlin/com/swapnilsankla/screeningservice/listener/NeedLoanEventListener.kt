package com.swapnilsankla.screeningservice.listener

import com.fasterxml.jackson.databind.ObjectMapper
import com.swapnilsankla.screeningservice.publisher.ScreeningDataAvailableEventPublisher
import com.swapnilsankla.screeningservice.repository.ScreeningRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
class NeedLoanEventListener(@Autowired val repository: ScreeningRepository,
                            @Autowired val objectMapper: ObjectMapper,
                            @Autowired val screeningDataAvailableEventPublisher: ScreeningDataAvailableEventPublisher) {

    @KafkaListener(topics = ["needLoanEvent"])
    fun listen(needLoanEventString: String) {
        val needLoanEvent = objectMapper.readValue(needLoanEventString, NeedLoanEvent::class.java)
        Logger.getLogger(NeedLoanEventListener::class.simpleName)
                .info("needLoanEvent event received for customer ${needLoanEvent.customerId}")

        repository
                .findByCustomerId(needLoanEvent.customerId)
                .doOnSuccess(screeningDataAvailableEventPublisher::publish)
                .subscribe()
    }
}