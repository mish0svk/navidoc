package com.example.navidoc.utils;

import com.kontakt.sdk.android.ble.device.BeaconDevice;

import java.util.HashMap;
import java.util.Map;

public class BeaconUtility
{

    private static Map<String, String> setUniqueIds()
    {
        Map<String, String> result = new HashMap<>();
        result.put("C2:7E:8A:C4:27:2F", "UuaJiX");
        result.put("FC:74:76:7C:5F:2E", "UutvWt");
        result.put("FB:5A:65:C8:66:B5", "Uujp66");
        result.put("C4:A6:40:07:67:FD", "UuGhGx");
        result.put("F3:26:2A:C2:DD:2B", "UuehLL");

        return result;
    }

    public static String getUniqueId(String mac)
    {

        return setUniqueIds().getOrDefault(mac, null);
    }

    public static String getUniqueId(BeaconDevice beacon)
    {
        return setUniqueIds().getOrDefault(beacon.getAddress(), null);
    }
}
