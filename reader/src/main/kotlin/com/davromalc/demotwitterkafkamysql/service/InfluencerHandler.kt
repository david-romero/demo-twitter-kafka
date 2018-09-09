package com.davromalc.demotwitterkafkamysql.service


import com.davromalc.demotwitterkafkamysql.repository.InfluencerRepository
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Flux

@Service
class InfluencerHandler (val influencerRepository: InfluencerRepository) {

    fun findAll(request: ServerRequest) = ok().body(Flux.fromIterable(influencerRepository.findAll()))

    fun getRanking(request: ServerRequest) = ok().body(Flux.fromIterable(influencerRepository.findTop10ByOrderByLikesDesc()))

}