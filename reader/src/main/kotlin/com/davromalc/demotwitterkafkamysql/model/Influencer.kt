package com.davromalc.demotwitterkafkamysql.model

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Influencer (
        @Id val username : String,
        val tweets : Long,
        val content : String,
        val likes : Long
)