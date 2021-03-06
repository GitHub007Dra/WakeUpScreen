package com.symeonchen.wakeupscreen.states

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import com.symeonchen.wakeupscreen.services.ScProximitySensor

/**
 * Created by SymeonChen on 2019-10-27.
 */
class ProximitySensorState {
    companion object {
        private var proximityListener = ScProximitySensor()
        private var proximitySensor: Sensor? = null
        private var sensorManager: SensorManager? = null
        fun registerListener(context: Context?) {
            if (context == null) {
                return
            }
            if (sensorManager == null) {
                sensorManager =
                    context.applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            }

            if (isRegistered()) {
                sensorManager?.unregisterListener(proximityListener)
            }
            proximitySensor = sensorManager?.getDefaultSensor(Sensor.TYPE_PROXIMITY)
            sensorManager?.registerListener(
                proximityListener,
                proximitySensor, SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        fun unRegisterListener(context: Context?) {
            if (context == null) {
                return
            }
            if (sensorManager == null) {
                sensorManager =
                    context.applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            }
            sensorManager?.unregisterListener(proximityListener)
            proximitySensor = null
        }

        fun isRegistered(): Boolean {
            return proximitySensor != null
        }
    }

}