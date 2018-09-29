package oscar.riksdagskollen.Util.Helper;

/**
 * Created by gustavaaro on 2018-09-28.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings.Secure;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class DeviceUuidFactory {

    private static UUID getUniquePsuedoID() {

        String m_szDevIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);

        String serial = null;
        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode());
        } catch (Exception exception) {
            serial = "serial"; // some value
        }
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode());
    }

    @SuppressLint("HardwareIds")
    public static String getDeviceUuid(Context context) {
        final String androidId = Secure.getString(
                context.getContentResolver(), Secure.ANDROID_ID);
        try {
            if (!"9774d56d682e549c".equals(androidId)) {
                return UUID.nameUUIDFromBytes(androidId.getBytes("utf8")).toString();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return getUniquePsuedoID().toString();
    }
}