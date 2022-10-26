package com.hangzhou.me.floatview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

/**
 * @Author: edison qian
 * @Email: edison.qian@applovin.com
 * @CreateDate: 2022/10/25 21:26
 * @Description:
 */
public class TelephonyUtils {

    /**
     * 获取国家码
     * 1.https://www.runoob.com/w3cnote/android-tutorial-telephonymanager.html
     * 2.MCC：Mobile Country Code，移动国家码，MCC的资源由国际电联（ITU）统一分配和管理，唯一识别移动用户所属的国家，共3位，中国为460;
     * 3.MNC:Mobile Network Code，移动网络码，共2位，中国移动TD系统使用00，中国联通GSM系统使用01，中国移动GSM系统使用02，中国电信CDMA系统使用03
     * 4.https://www.jianshu.com/p/4317d60a90fd
     * 5.Android国际化总结：https://ansuote.github.io/2017/08/24/Android%20%E5%9B%BD%E9%99%85%E5%8C%96%E6%80%BB%E7%BB%93/
     * 6.Google的开源库：https://github.com/google/libphonenumber
     *
     * @param context
     * @return
     */
    public static String getCountryCode(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        // 如果插卡，全部都正常返回。但是开飞行模式后，Network的三个方法返回空，其余都正常。
        // 如果没插卡，只有两个locale.getCountry()和getNetworkCountryIso()方法正常返回，其余都返回空。其中通过local获取的方式，只跟系统设置有关。
        Log.d("edison", "getNetworkCountryIso=" + telephonyManager.getNetworkCountryIso()); //cn
        Log.d("edison", "getNetworkOperator=" + telephonyManager.getNetworkOperator()); //46000
        Log.d("edison", "getNetworkOperatorName=" + telephonyManager.getNetworkOperatorName()); //中国移动
        Log.d("edison", "getSimCountryIso=" + telephonyManager.getSimCountryIso()); //cn
        Log.d("edison", "getSimOperator=" + telephonyManager.getSimOperator()); //46001
        Log.d("edison", "getSimOperatorName=" + telephonyManager.getSimOperatorName()); //中国移动
        Log.d("edison", "getConfiguration().mcc=" + context.getResources().getConfiguration().mcc); //460
        Log.d("edison", "getConfiguration().mnc=" + context.getResources().getConfiguration().mnc); //7
        Log.d("edison", "locale.getCountry()=" + context.getResources().getConfiguration().getLocales().get(0).getCountry()); //CN
        Log.d("edison", "locale.getCountry()=" + context.getResources().getConfiguration().locale.getCountry()); //CN
        // getCDMACountryIso方法，不论插没插卡，通过反射获取到的homeOperator都为空
        Log.d("edison", "getCDMACountryIso=" + getCDMACountryIso()); //空

        String countryCode = telephonyManager.getSimCountryIso();
        if (!TextUtils.isEmpty(countryCode)) {
            return countryCode.toUpperCase(Locale.ENGLISH);
        }

        // 网络模式是CDMA时，getNetworkCountryIso方法不可靠，因此要分别处理
        if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
            countryCode = getCDMACountryIso();
        } else {
            countryCode = telephonyManager.getNetworkCountryIso();
        }
        if (!TextUtils.isEmpty(countryCode)) {
            return countryCode.toUpperCase(Locale.ENGLISH);
        }

        countryCode = MccTable.countryCodeForMcc(context.getResources().getConfiguration().mcc);
        if (!TextUtils.isEmpty(countryCode)) {
            return countryCode.toUpperCase(Locale.ENGLISH);
        }

        // 取决于手机里的语言设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            countryCode = context.getResources().getConfiguration().getLocales().get(0).getCountry();
        } else {
            countryCode = context.getResources().getConfiguration().locale.getCountry();
        }
        return countryCode.toUpperCase(Locale.ENGLISH);
    }

    @SuppressLint("PrivateApi")
    private static String getCDMACountryIso() {
        try {
            // try to get country code from SystemProperties private class
            Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            Method get = systemProperties.getMethod("get", String.class);

            // get homeOperator that contain MCC + MNC
            String homeOperator = (String) get.invoke(systemProperties, "ro.cdma.home.operator.numeric");
            if (TextUtils.isEmpty(homeOperator)) {
                return "";
            }

            // first 3 chars (MCC) from homeOperator represents the country code
            String mcc = homeOperator.substring(0, 3);

            // mapping just countries that actually use CDMA networks
            return MccTable.countryCodeForMcc(mcc);
        } catch (ClassNotFoundException ignored) {
            ignored.printStackTrace();
        } catch (NoSuchMethodException ignored) {
            ignored.printStackTrace();
        } catch (IllegalAccessException ignored) {
            ignored.printStackTrace();
        } catch (InvocationTargetException ignored) {
            ignored.printStackTrace();
        } catch (NullPointerException ignored) {
            ignored.printStackTrace();
        } catch (StringIndexOutOfBoundsException ignored) {
            ignored.printStackTrace();
        }
        return "";
    }
}
