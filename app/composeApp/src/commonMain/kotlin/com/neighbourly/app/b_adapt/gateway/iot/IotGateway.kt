package com.neighbourly.app.b_adapt.gateway.iot

import com.neighbourly.app.a_device.iot.PahoMqttIot
import com.neighbourly.app.d_entity.data.BoxStateUpdate
import com.neighbourly.app.d_entity.interf.Iot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class IotGateway(val pahoMqttIot: PahoMqttIot) : Iot {
    override val boxStateFlow: Flow<BoxStateUpdate> = pahoMqttIot.messageFlow.map { iotMessage ->
            val regex = Regex("(?<=neighbourlybox/)[^/]+(?=/)")
            regex.find(iotMessage.topic)?.value?.let { id ->
                when {
                    iotMessage.topic.contains("status") -> BoxStateUpdate(
                        id = id,
                        online = iotMessage.message == "ONLINE"
                    )

                    iotMessage.topic.contains("triggered") -> BoxStateUpdate(
                        id = id,
                        triggered = iotMessage.message == "TRUE"
                    )

                    iotMessage.topic.contains("locked") -> BoxStateUpdate(
                        id = id,
                        unlocked = iotMessage.message == "FALSE"
                    )

                    iotMessage.topic.contains("lit") -> BoxStateUpdate(
                        id = id,
                        lit = iotMessage.message == "TRUE"
                    )

                    iotMessage.topic.contains("hello") -> BoxStateUpdate(
                        id = id,
                        ssd = iotMessage.message.split("/")[0],
                        signal = iotMessage.message.split("/")[1].toIntOrNull()
                    )

                    else -> null
                }
            }
        }.filterNotNull()

    override suspend fun monitorBoxes(boxIds: List<String>) {
        if (boxIds.isEmpty()) {
            pahoMqttIot.disConnect()
        } else {
            pahoMqttIot.connect(brokerUrl, username, password)
        }

        boxIds.forEach {
            pahoMqttIot.subscribe("neighbourlybox/$it/status")
            pahoMqttIot.subscribe("neighbourlybox/$it/triggered")
            pahoMqttIot.subscribe("neighbourlybox/$it/locked")
            pahoMqttIot.subscribe("neighbourlybox/$it/lit")
            pahoMqttIot.subscribe("neighbourlybox/$it/hello")
        }
    }

    companion object {
        val brokerUrl: String = "tcp://neighbourlybox.com:1883"
        val username: String = "neighbourlyapp"
        val password: String = "qwerty1234"
    }
}