package cn.heima.myutilslibrary.contacts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.heima.myutilslibrary.R;
import cn.heima.myutilslibrary.adapter.ContactAdapter;
import cn.krvision.toolmodule.utils.LogUtils;

public class Contacts2Activity extends AppCompatActivity {

    private ListView listView;
    private ContactAdapter contactAdapter;
    private List<LinphoneContact> Contacts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contacts2);
        listView = findViewById(R.id.listview);


        ContactsManager.getInstance().initializeSyncAccount(getApplicationContext(), getContentResolver());
        ContactsManager.getInstance().setContactsUpdatedListener(new ContactsManager.ContactsUpdatedListener() {
            @Override
            public void onContactsUpdated() {
                Contacts = ContactsManager.getInstance().getContacts();
                for (LinphoneContact contact : Contacts) {
                    LogUtils.e("111111111 ", contact.getFullName());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        contactAdapter = new ContactAdapter(Contacts2Activity.this, Contacts);
                        listView.setAdapter(contactAdapter);
                    }
                });
            }
        });
    }
}
