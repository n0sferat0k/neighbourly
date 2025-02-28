package com.neighbourly.app.a_device.iot

import com.neighbourly.app.d_entity.data.TopicMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

object PahoMqttIot : MqttCallback {
    val clientId: String = "neighbourlyapp_"
    val topics = mutableSetOf<String>()

    private var client: PahoMqttClient? = null
    private val messageDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    private val _connectedFlow = MutableSharedFlow<Boolean>()
    val connectedFlow: Flow<Boolean> = _connectedFlow.asSharedFlow()

    private val _messageFlow = MutableSharedFlow<TopicMessage>()
    val messageFlow: Flow<TopicMessage> = _messageFlow.asSharedFlow()

    suspend fun connect(brokerUrl: String, username: String, password: String) {
        withContext(Dispatchers.IO) {
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
                client?.setCallback(this@PahoMqttIot)
                topics.forEach {
                    client.takeIf { it?.isConnected ?: false }?.subscribe(it)
                }
            }
            withContext(messageDispatcher) {
                _connectedFlow.emit(client?.isConnected ?: false)
            }
        }
    }

    fun disConnect() {
        client?.disconnect()
        client = null
        CoroutineScope(messageDispatcher).launch {
            _connectedFlow.emit(false)
        }
    }

    override fun connectionLost(cause: Throwable?) {
        CoroutineScope(Dispatchers.IO).launch {
            client?.reconnect()
            withContext(messageDispatcher) {
                _connectedFlow.emit(client?.isConnected ?: false)
                topics.forEach {
                    client.takeIf { it?.isConnected ?: false }?.subscribe(it)
                }
            }
        }
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        if (topic.isNullOrEmpty() || message == null) return
        CoroutineScope(messageDispatcher).launch {
            _messageFlow.emit(TopicMessage(topic, message.toString()))
        }
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {}

    fun publish(topic: String, payload: String) {
        CoroutineScope(messageDispatcher).launch {
            client.takeIf { it?.isConnected ?: false }
                ?.publish(topic, MqttMessage(payload.toByteArray()))
        }
    }

    fun subscribe(topic: String) {
        CoroutineScope(messageDispatcher).launch {
            if (!topics.contains(topic)) {
                topics.add(topic)
                client.takeIf { it?.isConnected ?: false }?.subscribe(topic)
            }
        }
    }

    fun unsubscribe(topic: String) {
        CoroutineScope(messageDispatcher).launch {
            if (topics.contains(topic)) {
                topics.remove(topic)
                client.takeIf { it?.isConnected ?: false }?.unsubscribe(topic)
            }
        }
    }
}
