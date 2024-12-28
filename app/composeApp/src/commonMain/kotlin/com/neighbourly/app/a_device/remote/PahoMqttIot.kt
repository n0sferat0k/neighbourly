package com.neighbourly.app.a_device.remote

import com.neighbourly.app.d_entity.interf.Iot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.MqttClient as PahoMqttClient

class PahoMqttIot : Iot, MqttCallback {
    private lateinit var client: PahoMqttClient
    private val _messageFlow = MutableSharedFlow<Iot.TopicMessage>()
    override val messageFlow: Flow<Iot.TopicMessage> = _messageFlow.asSharedFlow()

    override fun connect() {
        client = PahoMqttClient(brokerUrl, clientId)
        val options = MqttConnectOptions().apply {
            this.userName = username
            this.password = PahoMqttIot.password.toCharArray()
        }
        client.connect(options)
        client.setCallback(this)
    }

    override fun connectionLost(cause: Throwable?) {}
    override fun messageArrived(topic: String?, message: MqttMessage?) {
        _messageFlow.tryEmit(Iot.TopicMessage(topic, message?.payload.toString()))
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {}

    override fun publish(topic: String, payload: String) {
        val message = MqttMessage(payload.toByteArray())
        client.publish(topic, message)
    }

    override fun subscribe(topic: String) =
        client.subscribe(topic)


    override fun unsubscribe(topic: String) {
        client.unsubscribe(topic)
    }

    override fun disconnect() {
        client.disconnect()
    }

    companion object {
        val brokerUrl: String = "tcp://neighbourly.go.ro:1883"
        val clientId: String = "neighbourlyapp"
        val username: String = "neighbourly"
        val password: String = "localpass"
    }
}
