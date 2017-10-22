package za.co.riggaroo.workshopmotionsensingcamera

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback
import com.google.android.things.pio.PeripheralManagerService


class MainActivity : Activity() {

    private val MOTION_SENSOR_PIN = "GPIO_35"
    private lateinit var gpioMotionSensor: Gpio

    private var ledGpio: Gpio? = null
    private val LED_GPIO_PIN: String = "GPIO_174"


    private lateinit var camera: CustomCamera
    private lateinit var motionImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeLed()
        initializeMotionSensor()
        setupCamera()
        setupUI()
    }

    private fun setupUI() {
        motionImageView = findViewById(R.id.image_view_motion_sense)
    }

    private fun initializeLed() {
        ledGpio = PeripheralManagerService().openGpio(LED_GPIO_PIN)
        ledGpio?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
    }

    private fun initializeMotionSensor() {
        gpioMotionSensor = PeripheralManagerService().openGpio(MOTION_SENSOR_PIN)
        gpioMotionSensor.setDirection(Gpio.DIRECTION_IN)
        gpioMotionSensor.setActiveType(Gpio.ACTIVE_HIGH)
        gpioMotionSensor.setEdgeTriggerType(Gpio.EDGE_BOTH)
        gpioMotionSensor.registerGpioCallback(object : GpioCallback() {
            override fun onGpioEdge(gpio: Gpio): Boolean {
                ledGpio?.value = gpio.value
                if (gpio.value) {
                    camera.takePicture()
                    Log.d("MainActivity", "onGpioEdge: motion detected")
                } else {
                    Log.d("MainActivity", "onGpioEdge: NO MOTION detected")
                }
                return true
            }
        })

    }

    private fun setupCamera() {
        camera = CustomCamera.getInstance()
        camera.initializeCamera(this, Handler(), imageAvailableListener)
    }

    private val imageAvailableListener = object : CustomCamera.ImageCapturedListener {
        override fun onImageCaptured(bitmap: Bitmap) {
            motionImageView.setImageBitmap(bitmap)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        gpioMotionSensor.close()
        ledGpio?.close()
    }
}
