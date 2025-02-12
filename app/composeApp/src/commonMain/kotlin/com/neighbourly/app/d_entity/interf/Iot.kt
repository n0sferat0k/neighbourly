package com.neighbourly.app.d_entity.interf

import kotlinx.coroutines.flow.Flow

interface Iot {
    val messageFlow: Flow<TopicMessage?>

    suspend fun requireConnect()
    suspend fun publish(topic: String, payload: String)
    suspend fun subscribe(topic: String)
    suspend fun unsubscribe(topic: String)
    suspend fun requireDisconnect()

    data class TopicMessage(val topic: String, val message: String)
}