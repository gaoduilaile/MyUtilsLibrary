/*
ContactsManager.java
Copyright (C) 2015  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package cn.heima.myutilslibrary.contacts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ContactsManager extends ContentObserver {
    private static ContactsManager instance;
    private List<LinphoneContact> contacts;
    private boolean preferLinphoneContacts = false, isContactPresenceDisabled = true, hasContactAccess = false;
    private ContentResolver contentResolver;
    private Context context;
    private ContactsFetchTask contactsFetchTask;

    private ContactsUpdatedListener listener;
//    private CustomProgressDialog customProgressDialog;

    interface ContactsUpdatedListener {
        void onContactsUpdated();
    }

    @SuppressLint("HandlerLeak")
    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };

    private ContactsManager(Handler handler) {
        super(handler);
        contacts = new ArrayList<LinphoneContact>();

    }

    public void setContactsUpdatedListener(ContactsUpdatedListener listener) {
        this.listener = listener;

    }

    public void destroy() {
        if (contactsFetchTask != null && !contactsFetchTask.isCancelled()) {
            contactsFetchTask.cancel(true);
        }
        instance = null;
    }

    public ContentResolver getContentResolver() {
        return contentResolver;
    }

    public static final synchronized ContactsManager getInstance() {
        if (instance == null) instance = new ContactsManager(handler);
        return instance;
    }

    /**
     * 获取通讯录
     *
     * @return
     */
    public synchronized List<LinphoneContact> getContacts() {
        return contacts;
    }

    /**
     * 搜索通讯录
     *
     * @return
     */
    public synchronized List<LinphoneContact> getContacts(String search) {
        search = search.toLowerCase(Locale.getDefault());
        List<LinphoneContact> searchContactsBegin = new ArrayList<LinphoneContact>();
        List<LinphoneContact> searchContactsContain = new ArrayList<LinphoneContact>();
        for (LinphoneContact contact : contacts) {
            if (contact.getFullName() != null) {
                if (contact.getFullName().toLowerCase(Locale.getDefault()).startsWith(search)) {
                    searchContactsBegin.add(contact);
                } else if (contact.getFullName().toLowerCase(Locale.getDefault()).contains(search)) {
                    searchContactsContain.add(contact);
                }
            }
        }
        searchContactsBegin.addAll(searchContactsContain);
        return searchContactsBegin;
    }

    public void enableContactsAccess() {
        hasContactAccess = true;
    }

    public boolean hasContactsAccess() {
        if (context == null)
            return false;
        int contacts = context.getPackageManager().checkPermission(android.Manifest.permission.READ_CONTACTS, context.getPackageName());
        return contacts == context.getPackageManager().PERMISSION_GRANTED;
    }


    public void initializeContactManager(Context context, ContentResolver contentResolver) {
        this.context = context;
        this.contentResolver = contentResolver;
    }

    public void initializeSyncAccount(Context context, ContentResolver contentResolver) {
        initializeContactManager(context, contentResolver);
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account[] accounts = accountManager.getAccountsByType(context.getPackageName());

        if (accounts.length == 0) {
            Account newAccount = new Account("sync_account_name", context.getPackageName());
            try {
                accountManager.addAccountExplicitly(newAccount, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        initializeContactManager(context, contentResolver);

//        customProgressDialog = new CustomProgressDialog(context, "加载中...", false);

        fetchContactsAsync();
    }


    public synchronized void fetchContactsAsync() {
        if (contactsFetchTask != null && !contactsFetchTask.isCancelled()) {
            contactsFetchTask.cancel(true);
        }
        contactsFetchTask = new ContactsFetchTask();
        contactsFetchTask.execute();
    }

    private class ContactsFetchTask extends AsyncTask<Void, List<LinphoneContact>, List<LinphoneContact>> {
        @SuppressWarnings("unchecked")
        protected List<LinphoneContact> doInBackground(Void... params) {
            List<LinphoneContact> contacts = new ArrayList<LinphoneContact>();

            if (hasContactsAccess()) {
                Cursor c = getContactsCursor(contentResolver);
                if (c != null) {
                    while (c.moveToNext()) {
                        String id = c.getString(c.getColumnIndex(Data.CONTACT_ID));
                        LinphoneContact contact = new LinphoneContact();
                        contact.setAndroidId(id);
                        contacts.add(contact);
                    }
                    c.close();
                }
            }

            for (LinphoneContact contact : contacts) {
                // This will only get name & picture informations to be able to quickly display contacts list
                contact.minimalRefresh();
            }
            Collections.sort(contacts);

            // Public the current list of contacts without all the informations populated
            publishProgress(contacts);

            return contacts;
        }

        protected void onProgressUpdate(List<LinphoneContact>... result) {
//            customProgressDialog.show();
        }

        protected void onPostExecute(List<LinphoneContact> result) {
//            customProgressDialog.dismiss();
            if (result != null && result.size() > 0) {
                contacts = result;
                listener.onContactsUpdated();
            }
        }
    }

    public static String getAddressOrNumberForAndroidContact(ContentResolver resolver, Uri contactUri) {
        // Phone Numbers
        String[] projection = new String[]{CommonDataKinds.Phone.NUMBER};
        Cursor c = resolver.query(contactUri, projection, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
                int numberIndex = c.getColumnIndex(CommonDataKinds.Phone.NUMBER);
                String number = c.getString(numberIndex);
                c.close();
                return number;
            }
        }

        // SIP addresses
        projection = new String[]{CommonDataKinds.SipAddress.SIP_ADDRESS};
        c = resolver.query(contactUri, projection, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
                int numberIndex = c.getColumnIndex(CommonDataKinds.SipAddress.SIP_ADDRESS);
                String address = c.getString(numberIndex);
                c.close();
                return address;
            }
            c.close();
        }
        return null;
    }

    public void delete(String id) {
        ArrayList<String> ids = new ArrayList<String>();
        ids.add(id);
        deleteMultipleContactsAtOnce(ids);
    }

    public void deleteMultipleContactsAtOnce(List<String> ids) {
        String select = Data.CONTACT_ID + " = ?";
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        for (String id : ids) {
            String[] args = new String[]{id};
            ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI).withSelection(select, args).build());
        }

        ContentResolver cr = ContactsManager.getInstance().getContentResolver();
        try {
            cr.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getString(int resourceID) {
        return context.getString(resourceID);
    }

    private Cursor getContactsCursor(ContentResolver cr) {
        String req = "(" + Data.MIMETYPE + " = '" + CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                + "' AND " + CommonDataKinds.Phone.NUMBER + " IS NOT NULL "
                + " OR (" + Data.MIMETYPE + " = '" + CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE
                + "' AND " + CommonDataKinds.SipAddress.SIP_ADDRESS + " IS NOT NULL))";
        String[] projection = new String[]{Data.CONTACT_ID, Data.DISPLAY_NAME};
        String query = Data.DISPLAY_NAME + " IS NOT NULL AND (" + req + ")";

        Cursor cursor = cr.query(Data.CONTENT_URI, projection, query, null, " lower(" + Data.DISPLAY_NAME + ") COLLATE UNICODE ASC");
        if (cursor == null) {
            return cursor;
        }

        MatrixCursor result = new MatrixCursor(cursor.getColumnNames());
        Set<String> groupBy = new HashSet<String>();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(Data.DISPLAY_NAME));
            if (!groupBy.contains(name)) {
                groupBy.add(name);
                Object[] newRow = new Object[cursor.getColumnCount()];

                int contactID = cursor.getColumnIndex(Data.CONTACT_ID);
                int displayName = cursor.getColumnIndex(Data.DISPLAY_NAME);

                newRow[contactID] = cursor.getString(contactID);
                newRow[displayName] = cursor.getString(displayName);

                result.addRow(newRow);
            }
        }
        cursor.close();
        return result;
    }
}
