package com.symeonchen.wakeupscreen.services

import android.os.PowerManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.symeonchen.wakeupscreen.data.NotifyItem
import com.symeonchen.wakeupscreen.services.notification.ConditionParam
import com.symeonchen.wakeupscreen.services.notification.ConditionState
import com.symeonchen.wakeupscreen.services.notification.ListenerManager
import com.symeonchen.wakeupscreen.services.notification.conditions.*
import com.symeonchen.wakeupscreen.utils.DataInjection
import com.symeonchen.wakeupscreen.utils.NotifyDataUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by SymeonChen on 2019-10-27.
 */
@Suppress("DEPRECATION")
class ScNotificationListenerService : NotificationListenerService() {

    companion object {
        private const val TAG_WAKE = "symeonchen:wakeupscreen"
    }

    init {
        ListenerManager.register(PocketModeCondition())
            .register(InteractiveCondition())
            .register(FilterListCondition())
            .register(OnGoingNotificationCondition())
            .register(SleepModeCondition())
            .register(DndCondition())
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn ?: return

        //Pre check for better performance
        if (ConditionState.BLOCK == preCheckStatusOpen()) return

        val pm = getSystemService(POWER_SERVICE) as PowerManager

        //Record log here
        recordDebugModeLog(sbn)

        if (ConditionState.BLOCK == ListenerManager.provideState(
                ConditionParam(
                    sbn,
                    pm,
                    application
                )
            )
        ) return

        val wl = pm.newWakeLock(
            PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
            TAG_WAKE
        )
        val sec = DataInjection.milliSecondOfWakeUpScreen

        wl.acquire((sec))
        wl.release()

    }

    /**
     * Check if service switch is open
     */
    private fun preCheckStatusOpen(): ConditionState? {
        val status = DataInjection.switchOfApp
        if (!status) {
            return ConditionState.BLOCK
        }
        return ConditionState.SUCCESS
    }

    /**
     * Check if open debug mode
     */
    private fun recordDebugModeLog(sbn: StatusBarNotification) {
        if (DataInjection.switchOfDebugMode) {
            CoroutineScope(Dispatchers.IO).launch {
                val time = System.currentTimeMillis()
                val notifyItem = NotifyItem()
                notifyItem.id = time
                notifyItem.time = time
                notifyItem.appName = sbn.packageName ?: ""
                NotifyDataUtils.addData(notifyItem, application)
            }
        }
    }
}