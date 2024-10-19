package com.pollub.awpfoc.data.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class CustomerInfo(val id:Int, val name: String, val surname: String, val phone:String, val pesel:String, val email:String, val account_deleted:Boolean, val protection_expiration_date: LocalDateTime?=null, var token:String?=null)
