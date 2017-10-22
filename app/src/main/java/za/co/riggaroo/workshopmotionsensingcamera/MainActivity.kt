package za.co.riggaroo.workshopmotionsensingcamera

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback
import com.google.android.things.pio.PeripheralManagerService


class MainActivity : Activity() {

    private val MOTION_SENSOR_PIN = "GPIO_35"
    private lateinit var gpioMotionSensor: Gpio


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeMotionSensor()

    }

    private fun initializeMotionSensor() {
        gpioMotionSensor = PeripheralManagerService().openGpio(MOTION_SENSOR_PIN)
        gpioMotionSensor.setDirection(Gpio.DIRECTION_IN)
        gpioMotionSensor.setActiveType(Gpio.ACTIVE_HIGH)
        gpioMotionSensor.setEdgeTriggerType(Gpio.EDGE_BOTH)
        gpioMotionSensor.registerGpioCallback(object : GpioCallback() {
            override fun onGpioEdge(gpio: Gpio): Boolean {
                if (gpio.value) {
                    Log.d("MainActivity", "onGpioEdge: motion detected")
                } else {
                    Log.d("MainActivity", "onGpioEdge: NO MOTION detected")
                }
                return true
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        gpioMotionSensor.close()
    }
}
