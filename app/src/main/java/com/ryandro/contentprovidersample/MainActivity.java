package com.ryandro.contentprovidersample;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    String TabhleNAme = "StudentData";
    String _id;

    private String[] mColumnProjection = new String[]{ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.CONTACT_STATUS,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
    };
    private boolean isFirstTimeLounch = false;

    private String mSelectionCluse = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " = ?";

    private String[] mSelectionArguments = new String[]{"Kranthi Y", "Sanjay"};

    private String mOrderBy = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY;

    private TextView tv_Contact;
    private EditText et_ContactName;
    private Button btn_AddData, btn_loadData, btn_updateData, btn_DeletData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_Contact = (TextView) findViewById(R.id.tv_Contact);
        btn_loadData = findViewById(R.id.btn_loadData);
        btn_updateData = findViewById(R.id.btn_updateData);
        btn_DeletData = findViewById(R.id.btn_DeletData);
        btn_AddData = findViewById(R.id.btn_AddData);
        et_ContactName = findViewById(R.id.et_ContactName);


        btn_AddData.setOnClickListener(this);
        btn_loadData.setOnClickListener(this);
        btn_DeletData.setOnClickListener(this);
        btn_updateData.setOnClickListener(this);

    }

    public void loadData() {

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, mColumnProjection,
                null,
                null,
                null);


        if (cursor != null && cursor.getCount() > 0) {
            StringBuilder stringBuilderQueryResult = new StringBuilder("");
            while (cursor.moveToNext()) {
                String str1 = cursor.getString(0);
                String str2 = cursor.getString(1);
                String str3 = cursor.getString(2);
                stringBuilderQueryResult.append(str1 + " , " + str2 + " , " + str3 + "\n");
            }
            tv_Contact.setText(stringBuilderQueryResult.toString());
        } else {

            tv_Contact.setText("No Contacts in device");
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == 1) {
            return new CursorLoader(this, ContactsContract.Contacts.CONTENT_URI, mColumnProjection, null, null, null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            StringBuilder stringBuilderQueryResult = new StringBuilder("");
            while (cursor.moveToNext()) {
                stringBuilderQueryResult.append(cursor.getString(0) + " , " + cursor.getString(1) + " , " + cursor.getString(2) + "\n");
            }
            tv_Contact.setText(stringBuilderQueryResult.toString());
        } else {
            tv_Contact.setText("No Contacts in device");
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_AddData:
                addData();
                break;
            case R.id.btn_updateData:
                updateData();
                break;
            case R.id.btn_loadData:
                loadData();
                break;
            case R.id.btn_DeletData:
                deletData();
                break;
        }
    }

    private void addData() {

        ArrayList<ContentProviderOperation> cops = new ArrayList<ContentProviderOperation>();

        cops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, "accountname@gmail.com")
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, "com.google")
                .build());
        cops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, et_ContactName.getText().toString())
                .build());

        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, cops);
        } catch (Exception exception) {
            Log.i("MainActivity ", exception.getMessage());
        }
    }

    private void deletData() {
        String whereClause = ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY + " = '" + et_ContactName.getText().toString() + "'";
        getContentResolver().delete(ContactsContract.RawContacts.CONTENT_URI, whereClause, null);
    }

    private void updateData() {
        String[] updateValue = et_ContactName.getText().toString().split(" ");
        ContentProviderResult[] result = null;

        String targetString = null;
        String newString = null;
        if (updateValue.length == 2) {

            targetString = updateValue[0];
            newString = updateValue[1];

            String where = ContactsContract.RawContacts._ID + " = ? ";
            String[] params = new String[]{targetString};

            ContentResolver contentResolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY, newString);
            contentResolver.update(ContactsContract.RawContacts.CONTENT_URI, contentValues, where, params);
        }
    }

  /*  private void loadData() {
        if (isFirstTimeLounch == false) {
            getLoaderManager().initLoader(1, null, MainActivity.this);
            isFirstTimeLounch = true;
        } else {
            getLoaderManager().restartLoader(1, null, MainActivity.this);
        }
    }*/
}
