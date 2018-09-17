package com.davromalc.demotwitterkafkamysql

import com.davromalc.demotwitterkafkamysql.service.InfluencerHandler
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import java.util.*


@SpringBootApplication
class DemoTwitterKafkaMysqlApplication {

    @Bean
    fun influencerRoute(handler: InfluencerHandler) = router {
        ("/influencer" and accept(MediaType.APPLICATION_JSON))
                .nest {
                    GET("/count", handler::getTotalTweets)
                    GET("/stats", handler::getRanking)
                    GET("/active", handler::getMoreActive)
                }
    }

    @Bean
    fun corsWebFilter(): CorsWebFilter {
        val corsConfig = CorsConfiguration()
        corsConfig.allowedOrigins = Arrays.asList("*")
        corsConfig.maxAge = 8000L
        corsConfig.addAllowedMethod("GET")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfig)
        return CorsWebFilter(source)
    }

}

fun main(args: Array<String>) {
    runApplication<DemoTwitterKafkaMysqlApplication>(*args)
}
