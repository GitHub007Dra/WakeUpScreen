package com.symeonchen.wakeupscreen.states

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import com.symeonchen.wakeupscreen.services.ScNotificationListenerService

/**
 * Created by SymeonChen on 2019-10-27.
 */
class NotificationState {
    companion object {

        fun isNotificationServiceOpen(context: Context?): Boolean {
            if (context == null) {
                return false
            }
            val appContext = context.applicationContext
            val pm = appContext.packageManager
            val state = pm.getComponentEnabledSetting(
                ComponentName(
                    appContext, ScNotificationListenerService::class.java
                )
            )
            if (state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                return true
            }
            return false
        }

        fun closeNotificationService(context: Context?) {
            if (context == null) {
                return
            }
            val appContext = context.applicationContext
            val pm = appContext.packageManager
            pm.setComponentEnabledSetting(
                ComponentName(
                    appContext, ScNotificationListenerService::class.java
                ),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
            )
        }

        fun openNotificationService(context: Context?) {
            if (context == null) {
                return
            }
            val appContext = context.applicationContext
            val pm = appContext.packageManager
            pm.setComponentEnabledSetting(
                ComponentName(
                    appContext, ScNotificationListenerService::class.java
                ),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
            )
        }
    }
}