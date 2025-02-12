package com.neighbourly.app.a_device.remote

import com.neighbourly.app.d_entity.interf.Iot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.util.UUID
import java.util.concurrent.Executors
import org.eclipse.paho.client.mqttv3.MqttClient as PahoMqttClient

class PahoMqttIot : Iot, MqttCallback {
    private var client: PahoMqttClient? = null
    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    private val _messageFlow = MutableSharedFlow<Iot.TopicMessage?>()
    override val messageFlow: Flow<Iot.TopicMessage?> = _messageFlow.asSharedFlow()

    override suspend fun requireConnect() {
        withContext(dispatcher) {
            if (client == null) {
                client = PahoMqttClient(brokerUrl, clientId +  UUID.randomUUID(), MemoryPersistence()).apply {
                    connect(MqttConnectOptions().apply {
                        this.userName = username
                        this.password = PahoMqttIot.password.toCharArray()
                    })
                    setCallback(this@PahoMqttIot)
                }
            } else if(client?.isConnected == false) {
                client?.reconnect()
            }
        }
    }

    override suspend fun requireDisconnect() =
        withContext(dispatcher) {
            client?.disconnect()
            client = null
        }

    override fun connectionLost(cause: Throwable?) {
        CoroutineScope(dispatcher).launch {
            client?.reconnect()
            _messageFlow.emit(null)
        }
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        if (topic.isNullOrEmpty() || message == null) return

        CoroutineScope(dispatcher).launch {
            _messageFlow.emit(Iot.TopicMessage(topic, message.toString()))
        }
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {}

    override suspend fun publish(topic: String, payload: String) {
        withContext(dispatcher) {
            val message = MqttMessage(payload.toByteArray())
            client?.publish(topic, message)
        }
    }

    override suspend fun subscribe(topic: String) {
        withContext(dispatcher) {
            client?.subscribe(topic)
        }
    }

    override suspend fun unsubscribe(topic: String) {
        withContext(dispatcher) {
            client?.unsubscribe(topic)
        }
    }

    companion object {
        val brokerUrl: String = "tcp://neighbourly.go.ro:1883"
        val clientId: String = "neighbourlyapp_"
        val username: String = "neighbourly"
        val password: String = "localpass"
    }
}
