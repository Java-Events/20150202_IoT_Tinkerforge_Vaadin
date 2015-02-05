package org.rapipdm.iot.vaadin.tinkerforge.demos.weatherstation.cml;

import com.tinkerforge.*;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.rapipdm.iot.vaadin.tinkerforge.mqtt.MqttBuffer;
import org.rapipdm.iot.vaadin.tinkerforge.mqtt.MqttClientBuilder;
import org.rapipdm.iot.vaadin.tinkerforge.v002.LCD20x4;

import java.io.IOException;
import java.time.LocalDateTime;

import static java.lang.System.out;

/**
 * Created by sven on 02.02.15.
 */
public class WeatherstationCML {


    public static final String TOPIC = "TinkerForge/Wetterstation/";

    public static final String TOPIC_Light = "TinkerForge/Wetterstation/Light";
    public static final String TOPIC_Temp = "TinkerForge/Wetterstation/Temp";
    public static final String TOPIC_Hum = "TinkerForge/Wetterstation/Hum";
    public static final String TOPIC_AirPressure = "TinkerForge/Wetterstation/Air";


    public static final String HOST = "127.0.0.1";  //wetterstation
    public static final String BROKER = "127.0.0.1";  //wetterstation
    public static final int PORT = 4223;
    private static int callbackPeriod = 1_000;

    private static MqttClientBuilder builder = new MqttClientBuilder();

    public static void main(String[] args) throws AlreadyConnectedException, IOException {
        IPConnection ipConnection = new IPConnection();
        LCD20x4 lcd20x4 = new LCD20x4("jvX", ipConnection);
        ipConnection.connect(HOST, PORT);
        lcd20x4.init();

        MqttClient sender = builder
                .uri("tcp://" + BROKER + ":1883")
                .clientUIDGenerated()
                .build();
        try {
            initAmbientLight(ipConnection, lcd20x4, sender);
            initTemp(ipConnection, lcd20x4, sender);
            iniHumidity(ipConnection, lcd20x4, sender);
            iniAirPressure(ipConnection, lcd20x4, sender);

            sender.connect();

            out.println("Press key to exit");
            System.in.read();
            ipConnection.disconnect();
        } catch (MqttException | IOException | NotConnectedException | TimeoutException e) {
            e.printStackTrace();
        }

    }

    private static void iniAirPressure(IPConnection ipConnection, LCD20x4 lcd20x4, MqttClient sender) throws TimeoutException, NotConnectedException {
        MqttBuffer buffer = new MqttBuffer()
                .client(sender).topic(TOPIC_AirPressure).qos(1).retained(true);

        BrickletBarometer barometer = new BrickletBarometer("jY4", ipConnection);
        barometer.setAirPressureCallbackPeriod(callbackPeriod);
        barometer.addAirPressureListener(sensorvalue -> {
            final double v = sensorvalue / 1000.0;
            final String text = LocalDateTime.now() + ":" + v;
            System.out.println("mBar = " + v);
            lcd20x4.printLine(3, "mBar = " + v);
            buffer.sendAsync(text);
        });
    }


    private static void iniHumidity(IPConnection ipConnection, LCD20x4 lcd20x4, MqttClient sender) throws TimeoutException, NotConnectedException {
        MqttBuffer buffer = new MqttBuffer()
                .client(sender).topic(TOPIC_Hum).qos(1).retained(true);

        BrickletHumidity humidity = new BrickletHumidity("kfd", ipConnection);
        humidity.setHumidityCallbackPeriod(callbackPeriod);
        humidity.addHumidityListener(sensorvalue -> {
            final double v = sensorvalue / 10.0;
            final String text = LocalDateTime.now() + ":" + v;
            System.out.println("%RH = " + v);
            lcd20x4.printLine(2, "%RH  = " + v);
            buffer.sendAsync(text);
        });
    }

    private static void initTemp(IPConnection ipConnection, LCD20x4 lcd20x4, MqttClient sender) throws TimeoutException, NotConnectedException {
        MqttBuffer buffer = new MqttBuffer()
                .client(sender).topic(TOPIC_Temp).qos(1).retained(true);
        BrickletTemperature temperature = new BrickletTemperature("dXj", ipConnection);
        temperature.setTemperatureCallbackPeriod(callbackPeriod);
        temperature.addTemperatureListener(sensorvalue -> {
            final double v = sensorvalue / 100.0;
            final String text = LocalDateTime.now() + ":" + v;
            System.out.println("C    = " + v);
            lcd20x4.printLine(1, "C    = " + v);
            buffer.sendAsync(text);
        });
    }

    private static void initAmbientLight(IPConnection ipConnection, LCD20x4 lcd20x4, MqttClient sender) throws TimeoutException, NotConnectedException {
        MqttBuffer buffer = new MqttBuffer()
                .client(sender).topic(TOPIC_Light).qos(1).retained(true);

        BrickletAmbientLight ambientLight = new BrickletAmbientLight("jy2", ipConnection);
        ambientLight.setIlluminanceCallbackPeriod(callbackPeriod);
        ambientLight.addIlluminanceListener(sensorvalue -> {
            final double v = sensorvalue / 10.0;

            final String text = LocalDateTime.now() + ":" + v;
            System.out.println("Lux  = " + v);
            lcd20x4.printLine(0, "Lux  = " + v);
            buffer.sendAsync(text);

        });
    }
}
