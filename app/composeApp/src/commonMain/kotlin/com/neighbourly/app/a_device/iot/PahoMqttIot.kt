package com.neighbourly.app.a_device.iot

import com.neighbourly.app.d_entity.data.TopicMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.util.UUID
import java.util.concurrent.Executors
import org.eclipse.paho.client.mqttv3.MqttClient as PahoMqttClient

object PahoMqttIot : MqttCallback {
    val clientId: String = "neighbourlyapp_"

    private var client: PahoMqttClient? = null
    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    private val _messageFlow = MutableSharedFlow<TopicMessage>()
    val messageFlow: Flow<TopicMessage> = _messageFlow.asSharedFlow()

    fun connect(brokerUrl: String, username: String, password: String) {
        if (client?.serverURI != brokerUrl) {
            disConnect()
            client = PahoMqttClient(
                brokerUrl,
                clientId + UUID.randomUUID(),
                MemoryPersistence()
            )
            client?.connect(MqttConnectOptions().apply {
                this.userName = username
                this.password = password.toCharArray()
            })
            client?.setCallback(this)
        }
    }

    fun disConnect() {
        client?.disconnect()
        client = null
    }

    override fun connectionLost(cause: Throwable?) {
        CoroutineScope(dispatcher).launch {
            client?.reconnect()
        }
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        if (topic.isNullOrEmpty() || message == null) return
        CoroutineScope(dispatcher).launch {
            _messageFlow.emit(TopicMessage(topic, message.toString()))
        }
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {}

    fun publish(topic: String, payload: String) {
        client?.publish(topic, MqttMessage(payload.toByteArray()))
    }

    fun subscribe(topic: String) {
        client?.subscribe(topic)
    }

    fun unsubscribe(topic: String) {
        client?.unsubscribe(topic)
    }
}
