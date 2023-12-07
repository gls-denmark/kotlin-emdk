package dk.gls.kemdk.model

import android.net.Uri
import dk.gls.kemdk.model.OEMInfo.Companion.BASE_SECURE_INFO_URI

enum class OEMInfo(val api: String) {
    RO_PRODUCT_MODEL("ro_product_model"),
    BUILD_SERIAL("build_serial"),
    IDENTITY_DEVICE_ID("identity_device_id"),
    MANIFEST_VERIFY_INFO("manifest_verify_info"),
    BT_MAC("bt_mac"),
    PERSIST_SYS_REBOOT_COUNT("persist.sys.reboot_count"),
    WIFI_MAC("wifi_mac"),
    WIFI_AP_MAC("wifi_ap_mac"),
    WIFI_SSID("wifi_ssid"),
    ETHERNET_MAC("ethernet_mac"),
    RO_BLUETOOTH_BT_FILTER("ro.bluetooth.bt_filter");

    companion object {
        const val BASE_SECURE_INFO_URI = "content://oem_info/oem.zebra.secure/"
    }
}

fun OEMInfo.getApiUri(): Uri {
    return Uri.parse(BASE_SECURE_INFO_URI + this.api)
}