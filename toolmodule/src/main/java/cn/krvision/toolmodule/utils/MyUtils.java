package cn.krvision.toolmodule.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by gaoqiong on 2018/11/13 15:39
 */
public class MyUtils {
    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        int versioncode;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            versioncode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
//            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }


    /**
     * 获取时间戳
     */
    public static int getTimetemp() {
        Date dt = new Date();
        Long time = dt.getTime();
        float v = time / 1000f;
        return (int) v;
    }

    /**
     * 获取时间
     */
    public static String getTimeString() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return year + "年" + month + "月" + day + "日";
    }

    /**
     * 获取手机系统版本号
     */
    public static String getPhoneSystem() {
        String carrier = android.os.Build.MANUFACTURER;
        String model = android.os.Build.MODEL;
        String mobile_system_version = android.os.Build.VERSION.RELEASE;
        String phoneSystemInfo = carrier + model + mobile_system_version;
        return phoneSystemInfo;
    }

    /**
     * 从文件中读取数据
     *
     * @return 从文件中读取的数据
     */
    public static String readAssetsTxt(Context context, String filename) {
        String result = "";
        try {
            //Return an AssetManager instance for your application's package
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            result = new String(buffer, "utf-8");
            // Finally stick the string into the text view.
        } catch (IOException e) {
            // Should never happen!
//            throw new RuntimeException(e);
            e.printStackTrace();
        }
        return result;
    }

    private static String judgeProvider(LocationManager locationManager) {
        List<String> prodiverlist = locationManager.getProviders(true);
        if (prodiverlist.contains(LocationManager.NETWORK_PROVIDER)) {
            return LocationManager.NETWORK_PROVIDER;//
        } else if (prodiverlist.contains(LocationManager.GPS_PROVIDER)) {
            return LocationManager.GPS_PROVIDER;
        }
        return null;
    }

    @SuppressLint("MissingPermission")
    public static String beginLocation(Activity mContext) {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        String provider = judgeProvider(locationManager);
        Location location = null;
        if (provider != null) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            location = locationManager.getLastKnownLocation(provider);
        }


        List<Address> addList = null;
        try {
            Geocoder ge = new Geocoder(mContext);
            addList = ge.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addList != null && addList.size() > 0) {
            for (int i = 0; i < addList.size(); i++) {
                Address ad = addList.get(i);

                return ad.getLocality();
            }
        }

        return null;
    }

    public static void Toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /*
     * 将秒数转为时分秒
     * */
    public static String secondToMinute(int second) {
        String timeString = "";
        if (second > 0) {
            int d;
            int s;
            if (second > 60) {
                d = second / 60;
                s = (second - d * 60);
                String dStr;
                String sStr;
                if (d <= 9) {
                    dStr = "0" + d;
                } else {
                    dStr = "" + d;
                }
                if (s <= 9) {
                    sStr = "0" + s;
                } else {
                    sStr = "" + s;
                }
                timeString = dStr + ":" + sStr;
            } else {

                String sStr;

                if (second <= 9) {
                    sStr = "00:0" + second;
                } else {
                    sStr = "00:" + second;
                }
                timeString = sStr;
            }

        } else {
            timeString = "00:00";
        }

        return timeString;
    }


    public static String getJson(String fileName, Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static String convertValue(float value) {
        DecimalFormat df = new DecimalFormat("##0.000");
        return df.format(value);
    }

    public static String convertValue2(float value) {
        DecimalFormat df = new DecimalFormat("##0.000000");
        return df.format(value);
    }


    /**
     * 验证手机号码是否合法
     */
    public static boolean validatePhoneNumber(String mobiles) {
        String telRegex = "^1[0-9]{10}$";
        return !TextUtils.isEmpty(mobiles) && mobiles.matches(telRegex);
    }


    /**
     * 检测是否有emoji表情
     *
     * @param source
     * @return
     */
    public static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是Emoji
     *
     * @param codePoint 比较的单个字符
     * @return
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
                (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
                && (codePoint <= 0x10FFFF));
    }

    /*2.0.4 变为 204*/
    public static int stringToInt(String str) {
        String strNew = str.replace(".", "");
        return Integer.parseInt(strNew);
    }

    public static double doubleFunction(double price) {
        DecimalFormat df = new DecimalFormat("#.00");
        String format = df.format(price);
        double v = Double.parseDouble(format);
        return v;
    }


    /**
     * dp转换成px
     */
    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转换成dp
     */
    public static int px2dp(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * sp转换成px
     */
    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * px转换成sp
     */
    public static int px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }


    /**
     * 跳转APP
     */
    public static void openApp(Activity context, String packageName) {
        PackageInfo pi = null;
        try {
            pi = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (pi==null){
            Uri uri = Uri.parse("http://a.app.qq.com/o/simple.jsp?pkgname=cn.krvision.litekrnavi");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
            return;
        }

        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(pi.packageName);

        List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(resolveIntent, 0);

        ResolveInfo ri = apps.iterator().next();
        if (ri != null) {
            packageName = ri.activityInfo.packageName;
            String className = ri.activityInfo.name;

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            context.startActivity(intent);
        }else {
            Uri uri = Uri.parse("http://a.app.qq.com/o/simple.jsp?pkgname=cn.krvision.litekrnavi");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        }
    }


    /**
     * 判断App是否是Debug版本
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isAppDebug(Context context) {
        if (StringUtils.isSpace(context.getPackageName())) return false;
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(), 0);
            return ai != null && (ai.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static <T> T checkNotNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }

}
