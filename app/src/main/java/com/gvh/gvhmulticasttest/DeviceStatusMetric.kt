package com.gvh.gvhmulticasttest;

import android.content.Context

data class DeviceStatusMetric(
        var cellularSignalStrength: Int?,
        var cellularType: String?,
        var wifiSignalStrength: Int,
        var batteryPercentage: Int,
        var audioVolumePercentage: Int,
        var wifiSSID: String,
        var networkOperatorName: String
) {
    companion object {
        fun build(context: Context): DeviceStatusMetric {
            return DeviceStatusMetric(
                    DeviceStatusHelper.getCellularSignalStrength(context),
                    DeviceStatusHelper.getCellularSignalType(context),
                    DeviceStatusHelper.getWifiSignalStrength(context),
                    DeviceStatusHelper.getBatteryPercentage(context),
                    DeviceStatusHelper.getAudioVolumePercentage(context),
                    DeviceStatusHelper.getWifiSSID(context),
                    DeviceStatusHelper.getSimOperatorName(context)
            )
        }
    }
}