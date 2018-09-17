package com.davromalc.demotwitterkafkamysql.repository

import com.davromalc.demotwitterkafkamysql.model.Influencer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface InfluencerRepository : JpaRepository<Influencer, String> {

    fun findTop10ByOrderByLikesDesc() : List<Influencer>

    fun findTop10ByOrderByTweetsDesc() : List<Influencer>

    @Query("select sum(tweets) from Influencer")
    fun findTotalTweets() : Int

}