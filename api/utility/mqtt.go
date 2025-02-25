package utility

import (
	"log"

	mqtt "github.com/eclipse/paho.mqtt.golang"
)

var MqttClient mqtt.Client

func ConnectMQTT() {
	// Create an MQTT client options
	opts := mqtt.NewClientOptions()
	opts.AddBroker("mqtt://localhost:1883")
	opts.SetClientID("go_mqtt_client")
	opts.SetUsername("neighbourly")
	opts.SetPassword("localpass")

	// Create an MQTT client
	MqttClient = mqtt.NewClient(opts)

	// Connect to the MQTT broker
	if token := MqttClient.Connect(); token.Wait() && token.Error() != nil {
		log.Fatalf("Failed to connect to MQTT broker: %v", token.Error())
	}
}
