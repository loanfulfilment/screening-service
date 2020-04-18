package com.swapnilsankla.screeningservice.listener

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.swapnilsankla.screeningservice.model.FraudStatus
import com.swapnilsankla.screeningservice.model.Screening
import com.swapnilsankla.screeningservice.model.ScreeningResult
import com.swapnilsankla.screeningservice.publisher.ScreeningDataAvailableEventPublisher
import com.swapnilsankla.screeningservice.repository.ScreeningRepository
import com.swapnilsankla.tracestarter.TraceIdExtractor
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import reactor.core.publisher.Mono

internal class CustomerDataAvailableForLoanProcessingEventListenerTest {

    private lateinit var repository: ScreeningRepository
    private lateinit var publisher: ScreeningDataAvailableEventPublisher

    @Before
    fun setUp() {
        repository = mockk()
        publisher = mockk()
    }

    @Test
    fun `should publish clear result if status is clear`() {
        val objectMapper = ObjectMapper().registerModule(KotlinModule())
        val listener = CustomerDataAvailableForLoanProcessingEventListener(repository,
                objectMapper,
                publisher,
                TraceIdExtractor())

        every { repository.findByNameAndPan(any(), any()) } returns
                Mono.just(Screening("Swapnil Sankla", "BJKHGLDA", FraudStatus.CLEAR))

        every { publisher.publish(ScreeningResult("1", FraudStatus.CLEAR), any()) } returns Unit

        val customer = Customer("1", "Swapnil Sankla", "BJKHGLDA", "Pune")
        listener.listen(objectMapper.writeValueAsString(customer), mapOf("uber-trace-id" to "trace-id".toByteArray()))
    }

    @Test
    fun `should publish fraud result if status is fraud`() {
        val objectMapper = ObjectMapper().registerModule(KotlinModule())
        val listener = CustomerDataAvailableForLoanProcessingEventListener(repository,
                objectMapper,
                publisher,
                TraceIdExtractor())

        every { repository.findByNameAndPan(any(), any()) } returns
                Mono.just(Screening("Swapnil Sankla", "BJKHGLDA", FraudStatus.FRAUDULENT))

        every { publisher.publish(ScreeningResult("1", FraudStatus.FRAUDULENT), any()) } returns Unit

        val customer = Customer("1", "Swapnil Sankla", "BJKHGLDA", "Pune")
        listener.listen(objectMapper.writeValueAsString(customer), mapOf("uber-trace-id" to "trace-id".toByteArray()))
    }

    @Test
    fun `should publish unknown result if customer does not have PAN`() {
        val objectMapper = ObjectMapper().registerModule(KotlinModule())
        val listener = CustomerDataAvailableForLoanProcessingEventListener(repository,
                objectMapper,
                publisher,
                TraceIdExtractor())

        every { repository.findByNameAndPan(any(), any()) } returns
                Mono.just(Screening("Swapnil Sankla", "BJKHGLDA", FraudStatus.CLEAR))

        every { publisher.publish(ScreeningResult("1", FraudStatus.UNKNOWN), any()) } returns Unit

        val customer = Customer("1", "Swapnil Sankla", null, "Pune")
        listener.listen(objectMapper.writeValueAsString(customer), mapOf("uber-trace-id" to "trace-id".toByteArray()))
    }

    @Test
    fun `should publish unknown result if customer is absent`() {
        val objectMapper = ObjectMapper().registerModule(KotlinModule())
        val listener = CustomerDataAvailableForLoanProcessingEventListener(repository,
                objectMapper,
                publisher,
                TraceIdExtractor())

        every { repository.findByNameAndPan(any(), any()) } returns Mono.empty()

        every { publisher.publish(ScreeningResult("1", FraudStatus.UNKNOWN), any()) } returns Unit

        val customer = Customer("1", "Swapnil Sankla", "BJKHGLDA", "Pune")
        listener.listen(objectMapper.writeValueAsString(customer), mapOf("uber-trace-id" to "trace-id".toByteArray()))
    }
}