package cn.heima.myutilslibrary.contacts;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.heima.myutilslibrary.R;

public class ContactsActivity extends AppCompatActivity {

    private Context mContext;
    private ArrayList<ContactInfo> contactInfoList = new ArrayList<>();
    private ArrayList<String> contactInfoListTelephone = new ArrayList<>();
    private ArrayList<String> contactInfoListTelephoneRelation = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        mContext = this;
        initPermission();
    }


    private void initPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (mContext.checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                    mContext.checkSelfPermission(Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED ||
                    mContext.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS,
                        Manifest.permission.GET_ACCOUNTS,
                        Manifest.permission.READ_CONTACTS}, 1);
            } else {
                initPhotoData();
            }
        } else {
            initPhotoData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                boolean isAllGranted1 = true;
                // 判断是否所有的权限都已经授予了
                for (int grant : grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        isAllGranted1 = false;
                        break;
                    }
                }
                if (isAllGranted1) {
                    initPhotoData();
                } else {
                    getAppDetailSettingIntent();
                    finish();
                }
                break;
        }
    }

    /**
     * 跳转到权限设置界面
     */
    protected void getAppDetailSettingIntent() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivity(intent);
    }


    /*初始化通讯录*/
    private void initPhotoData() {
        getPhoneContacts1();
        getPhoneContacts2();
    }

    private void getPhoneContacts1() {
        //访问raw_contacts表的URI，我们需要先从该表中查出联系人ID。
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        //访问data表(应该是视图)的URI，从该表中获取我们想要的数据。
        Uri dataUri = Uri.parse("content://com.android.contacts/data");
        ContentResolver mContentResolver = mContext.getContentResolver();
        //查看数据表，进行连表查询，确定数据。
        Cursor cursor = mContentResolver.query(uri, null, null, null, null);
        if (cursor == null) {
            return;
        }
        while (cursor.moveToNext()) {
            ContactInfo contactsInfo = new ContactInfo();
            String id = cursor.getString(cursor.getColumnIndex("_id"));
            Cursor data = mContentResolver.query(dataUri, null, "raw_contact_id = ?", new String[]{id}, null);
            if (data == null) {
                return;
            }

            while (data.moveToNext()) {
                //对数据进行筛选，每一次循环结束，筛选出一条用户的数据。
                String data1 = data.getString(data.getColumnIndex("data1"));
                String minetype = data.getString(data.getColumnIndex("mimetype"));
                String data2 = data.getString(data.getColumnIndex("data2"));
                if (minetype.equals("vnd.android.cursor.item/name")) {
                    contactsInfo.setName(data1 + " ");
                }
                if (minetype.equals("vnd.android.cursor.item/phone_v2")) {
                    if (data2.equals("1") || data2.equals("2") || data2.equals("3")) {
                        String str2 = data1.replaceAll(" ", "");
                        //手机号。
                        if (validatePhoneNumber(str2)) {
                            if (str2.contains("+86")) {
                                str2 = str2.substring(3);
                            }
                            contactsInfo.setTelephone(str2);
                            contactInfoList.add(contactsInfo);
                            contactInfoListTelephone.add(str2);
                        }
                    }
                }
            }
            data.close();
        }
        cursor.close();
    }


    private void getPhoneContacts2() {
        String[] projection22 = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.CONTACT_ID};
        // Consider using CursorLoader to perform the query.
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection22, null, null, null);
        if (cursor == null) {
            return;
        }
        while (cursor.moveToNext()) {
            ContactInfo contactsInfo = new ContactInfo();
            //获取联系人的唯一标示ID
            int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String number = cursor.getString(column);
            int column_name = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            String name = cursor.getString(column_name);
            if (validatePhoneNumber(number)) {
                if (number.contains("+86")) {
                    number = number.substring(3);
                }
                contactsInfo.setTelephone(number);
                contactsInfo.setName(name + " ");

                if (!contactInfoListTelephone.contains(number)) {
                    contactInfoList.add(contactsInfo);
                }
            }
        }
        cursor.close();
    }


    /**
     * 验证手机号码是否合法
     */
    public boolean validatePhoneNumber(String mobiles) {
        Pattern p = Pattern.compile("^((\\+86)|(86))?[1][123456789][0-9]{9}$");
        Matcher m = p.matcher(mobiles);
        boolean matches = m.matches();
        return matches;
    }

}
