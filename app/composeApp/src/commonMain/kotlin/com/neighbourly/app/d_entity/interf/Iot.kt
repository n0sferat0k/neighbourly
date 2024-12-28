package com.neighbourly.app.d_entity.interf

import kotlinx.coroutines.flow.Flow

interface Iot {
    fun connect()
    val messageFlow: Flow<TopicMessage>
    fun publish(topic: String, payload: String)
    fun subscribe(topic: String)
    fun unsubscribe(topic: String)
    fun disconnect()

    data class TopicMessage(val topic: String?, val message: String?)
}