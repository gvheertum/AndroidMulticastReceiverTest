package com.occ.occpingtester.DeviceMetrics;

import android.content.Context
import android.media.AudioManager
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.telephony.*
import android.telephony.TelephonyManager.*
import android.text.TextUtils


class DeviceStatusHelper {

    companion object {

        fun getCellularSignalStrength(context: Context): Int? {
            val telephonyManager = context.applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            //This will give info of all sims present inside your mobile
            val cellularData = telephonyManager.allCellInfo ?: return null

            for (i in cellularData.indices) {
                if (!cellularData[i].isRegistered)
                    continue

                when {
                    cellularData[i] is CellInfoLte -> { //4G
                        val cellInfoLte = cellularData[i] as CellInfoLte
                        val cellSignalStrengthLte = cellInfoLte.cellSignalStrength
                        return cellSignalStrengthLte.dbm
                    }

                    cellularData[i] is CellInfoWcdma -> { //3G
                        val cellInfoWcdma = cellularData[i] as CellInfoWcdma
                        val cellSignalStrengthWcdma = cellInfoWcdma.cellSignalStrength
                        return cellSignalStrengthWcdma.dbm
                    }

                    cellularData[i] is CellInfoCdma -> { //3G
                        val cellInfoCdma = cellularData[i] as CellInfoCdma
                        val cellSignalStrengthCdma = cellInfoCdma.cellSignalStrength
                        return cellSignalStrengthCdma.dbm
                    }

                    cellularData[i] is CellInfoGsm -> { //2G
                        val cellInfoGsm = cellularData[i] as CellInfoGsm
                        val cellSignalStrengthGsm = cellInfoGsm.cellSignalStrength
                        return cellSignalStrengthGsm.dbm
                    }
                }
            }

            return null
        }

        fun getCellularSignalType(context: Context): String?
        {
            val telephonyManager = context.applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            //This will give info of all sims present inside your mobile
            val cellularData = telephonyManager.allCellInfo ?: return null;

            for (i in cellularData.indices) {
                if (!cellularData[i].isRegistered)
                    continue

                when {
                    cellularData[i] is CellInfoLte -> { //4G
                        return "4G";
                    }

                    cellularData[i] is CellInfoWcdma -> { //3G
                        return "3G";
                    }

                    cellularData[i] is CellInfoCdma -> { //3G
                        return "3G";
                    }

                    cellularData[i] is CellInfoGsm -> { //2G
                        return "2G";
                    }
                }
            }

            return null;
        }

        fun getSimOperatorName(context: Context): String {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return when (telephonyManager.simState) {
                SIM_STATE_ABSENT -> "No Sim Available"
                SIM_STATE_NETWORK_LOCKED -> "Sim Locked"
                SIM_STATE_PIN_REQUIRED -> "Sim pin required"
                SIM_STATE_PUK_REQUIRED -> "Sim puk required"
                SIM_STATE_READY -> telephonyManager.networkOperatorName.substringBeforeLast("#")
                SIM_STATE_UNKNOWN -> "Sim state unknown"
                else -> "Sim state unknown"
            }
        }

        fun getWifiSignalStrength(context: Context): Int {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiConnectionInfo = wifiManager.connectionInfo

            return wifiConnectionInfo.rssi
        }

        fun getWifiSSID(context: Context): String {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiConnectionInfo = wifiManager.connectionInfo

            return wifiConnectionInfo.ssid
        }

        fun getBatteryPercentage(context: Context): Int {
            val batteryManager = context.applicationContext.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

            return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        }

        fun getAudioVolumePercentage(context: Context): Int {
            val audioManager = context.applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

            return 100 * currentVolume / maxVolume
        }

        fun getDeviceName(): String {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.startsWith(manufacturer)) {
                capitalize(model)
            } else capitalize(manufacturer) + " " + model
        }

        private fun capitalize(str: String): String {
            if (TextUtils.isEmpty(str)) {
                return str
            }
            val arr = str.toCharArray()
            var capitalizeNext = true
            val phrase = StringBuilder()
            for (c in arr) {
                if (capitalizeNext && Character.isLetter(c)) {
                        phrase.append(Character.toUpperCase(c))
                        capitalizeNext = false
                    continue
                } else if (Character.isWhitespace(c)) {
                    capitalizeNext = true
                }
                phrase.append(c)
            }
            return phrase.toString()
        }
    }

}