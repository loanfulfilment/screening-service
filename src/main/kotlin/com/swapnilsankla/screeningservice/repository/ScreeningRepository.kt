package com.swapnilsankla.screeningservice.repository

import com.swapnilsankla.screeningservice.model.Screening
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface ScreeningRepository: ReactiveMongoRepository<Screening, String> {
    fun findByNameAndPan(name: String, pan: String): Mono<Screening?>
}