package com.davromalc.demotwitterkafkamysql

import com.davromalc.demotwitterkafkamysql.service.InfluencerHandler
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router

@SpringBootApplication
class DemoTwitterKafkaMysqlApplication {

    @Bean
    fun influencerRoute(handler: InfluencerHandler) = router {
        ("/influencer" and accept(MediaType.APPLICATION_JSON))
                .nest {
                    GET("/", handler::findAll)
                    GET("/stats", handler::getRanking)
                }
    }

}

fun main(args: Array<String>) {
    runApplication<DemoTwitterKafkaMysqlApplication>(*args)
}
