/*
LinphoneContact.java
Copyright (C) 2016  Belledonne Communications, Grenoble, France

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

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.MediaStore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import cn.heima.myutilslibrary.R;

public class LinphoneContact implements Serializable, Comparable<LinphoneContact> {
	/**
	 *
	 */
	private static final long serialVersionUID = 9015568163905205244L;

	private String fullName, firstName, lastName, androidId, androidRawId, androidTagId, organization;
	private transient Uri photoUri, thumbnailUri;
	private transient ArrayList<ContentProviderOperation> changesToCommit;
	private transient ArrayList<ContentProviderOperation> changesToCommit2;
	private transient Bitmap photoBitmap, thumbnailBitmap;

	public LinphoneContact() {
		androidId = null;
		thumbnailUri = null;
		photoUri = null;
		changesToCommit = new ArrayList<ContentProviderOperation>();
		changesToCommit2 = new ArrayList<ContentProviderOperation>();
	}

	@Override
	protected void finalize() throws Throwable {
		if (photoBitmap != null) {
			photoBitmap.recycle();
			photoBitmap = null;
		}
		if (thumbnailBitmap != null) {
			thumbnailBitmap.recycle();
			thumbnailBitmap = null;
		}
		super.finalize();
	}

	@Override
	public int compareTo(LinphoneContact contact) {
		String fullName = getFullName() != null ? getFullName() : "";
		String contactFullName = contact.getFullName() != null ? contact.getFullName() : "";
		/*String firstLetter = fullName == null || fullName.isEmpty() ? "" : fullName.substring(0, 1).toUpperCase(Locale.getDefault());
		String contactfirstLetter = contactFullName == null || contactFullName.isEmpty() ? "" : contactFullName.substring(0, 1).toUpperCase(Locale.getDefault());*/
		return fullName.compareTo(contactFullName);
	}

	public void setFullName(String name) {
		fullName = name;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFirstNameAndLastName(String fn, String ln) {
		if (fn != null && fn.length() == 0 && ln != null && ln.length() == 0) return;

		if (isAndroidContact()) {
			if (firstName != null || lastName != null) {
				String select = ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "='" + CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'";
				String[] args = new String[]{ getAndroidId() };

				changesToCommit.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
					.withSelection(select, args)
					.withValue(ContactsContract.Data.MIMETYPE, CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
					.withValue(CommonDataKinds.StructuredName.GIVEN_NAME, fn)
					.withValue(CommonDataKinds.StructuredName.FAMILY_NAME, ln)
					.build()
				);
			} else {
				changesToCommit.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
			        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
			        .withValue(ContactsContract.Data.MIMETYPE, CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
			        .withValue(CommonDataKinds.StructuredName.GIVEN_NAME, fn)
			        .withValue(CommonDataKinds.StructuredName.FAMILY_NAME, ln)
			        .build());
			}
		}

		firstName = fn;
		lastName = ln;
		if (firstName != null && lastName != null && firstName.length() > 0 && lastName.length() > 0) {
			fullName = firstName + " " + lastName;
		} else if (firstName != null && firstName.length() > 0) {
			fullName = firstName;
		} else if (lastName != null && lastName.length() > 0) {
			fullName = lastName;
		}
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String org) {
		if (isAndroidContact()) {
			if (androidRawId != null) {
				String select = ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "='" + CommonDataKinds.Organization.CONTENT_ITEM_TYPE + "'";
				String[] args = new String[]{ getAndroidId() };

				if (organization != null) {
					changesToCommit.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
						.withSelection(select, args)
						.withValue(ContactsContract.Data.MIMETYPE, CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
						.withValue(CommonDataKinds.Organization.COMPANY, org)
						.build());
				} else {
					changesToCommit.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValue(ContactsContract.Data.RAW_CONTACT_ID, androidRawId)
						.withValue(ContactsContract.Data.MIMETYPE, CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
						.withValue(CommonDataKinds.Organization.COMPANY, org)
						.build());
				}
			} else {
				changesToCommit.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
			        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
			        .withValue(ContactsContract.Data.MIMETYPE, CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
			        .withValue(CommonDataKinds.Organization.COMPANY, org)
			        .build());
			}
		}

		organization = org;
	}

	public boolean hasPhoto() {
		return photoUri != null;
	}

	public void setPhotoUri(Uri uri) {
		if (uri.equals(photoUri)) return;
		photoUri = uri;

		if (photoBitmap != null) {
			photoBitmap.recycle();
		}
		try {
			photoBitmap = MediaStore.Images.Media.getBitmap(ContactsManager.getInstance().getContentResolver(), photoUri);
		} catch (FileNotFoundException e) {
			// Let's not say anything if the picture doesn't exist, it will pollute the logs
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Uri getPhotoUri() {
		return photoUri;
	}

	public Bitmap getPhotoBitmap() {
		return photoBitmap;
	}

	public void setThumbnailUri(Uri uri) {
		if (uri.equals(thumbnailUri)) return;
		thumbnailUri = uri;

		if (thumbnailBitmap != null) {
			thumbnailBitmap.recycle();
		}
		try {
			thumbnailBitmap = MediaStore.Images.Media.getBitmap(ContactsManager.getInstance().getContentResolver(), thumbnailUri);
		} catch (FileNotFoundException e) {
			// Let's not say anything if the picture doesn't exist, it will pollute the logs
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Uri getThumbnailUri() {
		return thumbnailUri;
	}

	public Bitmap getThumbnailBitmap() {
		return thumbnailBitmap;
	}

	public Bitmap getPhoto() {
		if (photoBitmap != null) {
			return photoBitmap;
		} else if (thumbnailBitmap != null) {
			return thumbnailBitmap;
		}
		return null;
	}

	public void setPhoto(byte[] photo) {
		if (photo != null) {
			if (isAndroidContact()) {
				if (androidRawId != null) {
					changesToCommit.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
						.withValue(ContactsContract.Data.RAW_CONTACT_ID, androidRawId)
						.withValue(ContactsContract.Data.MIMETYPE, CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
						.withValue(CommonDataKinds.Photo.PHOTO, photo)
						.withValue(ContactsContract.Data.IS_PRIMARY, 1)
						.withValue(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
						.build());
				} else {
					changesToCommit.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
				        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
						.withValue(ContactsContract.Data.MIMETYPE, CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
						.withValue(CommonDataKinds.Photo.PHOTO, photo)
						.build());
				}
			}
		}
	}


	public void setAndroidId(String id) {
		androidId = id;
	}

	public String getAndroidId() {
		return androidId;
	}


	public void save() {
		if (isAndroidContact() && ContactsManager.getInstance().hasContactsAccess() && changesToCommit.size() > 0) {
			try {
				ContactsManager.getInstance().getContentResolver().applyBatch(ContactsContract.AUTHORITY, changesToCommit);
				createLinphoneTagIfNeeded();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				changesToCommit = new ArrayList<ContentProviderOperation>();
				changesToCommit2 = new ArrayList<ContentProviderOperation>();
			}
		}
	}

	public void delete() {
		if (isAndroidContact()) {
			String select = ContactsContract.Data.CONTACT_ID + " = ?";
			String[] args = new String[] { getAndroidId() };
			changesToCommit.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI).withSelection(select, args).build());
			save();
		}
	}


	public void minimalRefresh() {

		if (isAndroidContact()) {
			getContactNames();
			setThumbnailUri(getContactThumbnailPictureUri());
			setPhotoUri(getContactPictureUri());
			getNativeContactOrganization();
		}
	}

	public void refresh() {
		if (isAndroidContact()) {
			getContactNames();
			setThumbnailUri(getContactThumbnailPictureUri());
			setPhotoUri(getContactPictureUri());
			androidRawId = findRawContactID();
		}
	}

	public boolean isAndroidContact() {
		return androidId != null;
	}


	private Uri getContactThumbnailPictureUri() {
		Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(getAndroidId()));
		return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
	}

	private Uri getContactPictureUri() {
		Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(getAndroidId()));
		return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
	}

	private void getContactNames() {
		ContentResolver resolver = ContactsManager.getInstance().getContentResolver();
		String[] proj = new String[]{ CommonDataKinds.StructuredName.GIVEN_NAME, CommonDataKinds.StructuredName.FAMILY_NAME, ContactsContract.Contacts.DISPLAY_NAME };
		String select = ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=?";
		String[] args = new String[]{ getAndroidId(), CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE };
		Cursor c = resolver.query(ContactsContract.Data.CONTENT_URI, proj, select, args, null);
		if (c != null) {
			if (c.moveToFirst()) {
				firstName = c.getString(c.getColumnIndex(CommonDataKinds.StructuredName.GIVEN_NAME));
				lastName = c.getString(c.getColumnIndex(CommonDataKinds.StructuredName.FAMILY_NAME));
	        	fullName = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			}
			c.close();
		}
	}

	private void getNativeContactOrganization() {
		ContentResolver resolver = ContactsManager.getInstance().getContentResolver();
		String[] proj = new String[]{ CommonDataKinds.Organization.COMPANY };
		String select = ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=?";
		String[] args = new String[]{ getAndroidId(), CommonDataKinds.Organization.CONTENT_ITEM_TYPE };
		Cursor c = resolver.query(ContactsContract.Data.CONTENT_URI, proj, select, args, null);
		if (c != null) {
			if (c.moveToFirst()) {
				organization = c.getString(c.getColumnIndex(CommonDataKinds.Organization.COMPANY));
			}
			c.close();
		}
	}

	private String findRawContactID() {
		ContentResolver resolver = ContactsManager.getInstance().getContentResolver();
		String result = null;
		String[] projection = { ContactsContract.RawContacts._ID };

		String selection = ContactsContract.RawContacts.CONTACT_ID + "=?";
		Cursor c = resolver.query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, new String[]{ getAndroidId() }, null);
		if (c != null) {
			if (c.moveToFirst()) {
				result = c.getString(c.getColumnIndex(ContactsContract.RawContacts._ID));
			}
			c.close();
		}
		return result;
	}

	private static LinphoneContact createAndroidContact() {
		LinphoneContact contact = new LinphoneContact();

		contact.changesToCommit.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
	        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
	        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
	        .withValue(ContactsContract.RawContacts.AGGREGATION_MODE, ContactsContract.RawContacts.AGGREGATION_MODE_DEFAULT)
	        .build());
		contact.setAndroidId("0");

		return contact;
	}

	private String findLinphoneRawContactId() {
		ContentResolver resolver = ContactsManager.getInstance().getContentResolver();
		String result = null;
		String[] projection = { ContactsContract.RawContacts._ID };

		String selection = ContactsContract.RawContacts.CONTACT_ID + "=? AND " + ContactsContract.RawContacts.ACCOUNT_TYPE + "=?";
		Cursor c = resolver.query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, new String[] { getAndroidId(), ContactsManager.getInstance().getString(R.string.sync_account_type) }, null);
		if (c != null) {
			if (c.moveToFirst()) {
				result = c.getString(c.getColumnIndex(ContactsContract.RawContacts._ID));
			}
			c.close();
		}
		return result;
	}

	private void createLinphoneTagIfNeeded() {
		if (androidTagId == null && findLinphoneRawContactId() == null) {
			createLinphoneContactTag();
		}
	}

	private void createLinphoneContactTag() {
		ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

		batch.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
			.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, ContactsManager.getInstance().getString(R.string.sync_account_type))
			.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, ContactsManager.getInstance().getString(R.string.sync_account_name))
			.withValue(ContactsContract.RawContacts.AGGREGATION_MODE, ContactsContract.RawContacts.AGGREGATION_MODE_DEFAULT)
			.build());

		batch.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
			.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
			.withValue(ContactsContract.Data.MIMETYPE, CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
			.withValue(CommonDataKinds.StructuredName.DISPLAY_NAME, getFullName())
			.build());

		batch.add(ContentProviderOperation.newUpdate(ContactsContract.AggregationExceptions.CONTENT_URI)
			.withValue(ContactsContract.AggregationExceptions.TYPE, ContactsContract.AggregationExceptions.TYPE_KEEP_TOGETHER)
			.withValue(ContactsContract.AggregationExceptions.RAW_CONTACT_ID1, androidRawId)
			.withValueBackReference(ContactsContract.AggregationExceptions.RAW_CONTACT_ID2, 0)
			.build());

		if (changesToCommit2.size() > 0) {
			for(ContentProviderOperation cpo : changesToCommit2) {
				batch.add(cpo);
			}
		}

		try {
			ContactsManager.getInstance().getContentResolver().applyBatch(ContactsContract.AUTHORITY, batch);
			androidTagId = findLinphoneRawContactId();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
