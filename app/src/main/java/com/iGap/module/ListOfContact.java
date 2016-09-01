package com.iGap.module;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.iGap.G;
import com.iGap.adapter.ContactNamesAdapter;
import com.iGap.realm.RealmUserContactsGetListResponse;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by molareza on 9/6/95.
 */
public class ListOfContact {

    public static ArrayList<ContactNamesAdapter.LineItem> Retrive(String s) {


        ArrayList<ContactNamesAdapter.LineItem> mItems = new ArrayList<>();
        RealmResults<RealmUserContactsGetListResponse> items = null;

        G.realm = Realm.getInstance(G.realmConfig);

        if (s.equals("")) {
            items = G.realm.where(RealmUserContactsGetListResponse.class).findAll();
        } else {
            items = G.realm
                    .where(RealmUserContactsGetListResponse.class)
                    .contains("display_name", s)
                    .findAll();
        }

        //Insert headers into list of items.
        String lastHeader = "";
        int sectionManager = -1;
        int headerCount = 0;
        int sectionFirstPosition = 0;

        for (int i = 0; i < items.size(); i++) {
            try {
                String header = items.get(i).getDisplay_name();

                if (!TextUtils.equals(lastHeader.toLowerCase(), header.toLowerCase())) {
                    // Insert new header view and update section data.
                    sectionManager = (sectionManager + 1) % 2;
                    sectionFirstPosition = i + headerCount;
                    lastHeader = header.toUpperCase();
                    headerCount += 1;
                    mItems.add(new ContactNamesAdapter.LineItem(header.toUpperCase(), "", true, sectionManager, sectionFirstPosition));
                }
                mItems.add(new ContactNamesAdapter.LineItem(items.get(i).getDisplay_name(), "Last seen recently", false, sectionManager, sectionFirstPosition));

            } catch (Exception e) {
            }
        }

        return mItems;
    }

    public static void getListOfContact() {

        final ArrayList<StructListOfContact> itemList = new ArrayList<>();

        String whereName = ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereNameParams = new String[]{ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME
                , ContactsContract.CommonDataKinds.Phone.NUMBER
                , ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME
                , ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME};
        Cursor nameCur = G.context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, whereNameParams, null, null, null);

        while (nameCur.moveToNext()) {
            StructListOfContact item = new StructListOfContact();
            String display = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));// get family and name togater
            item.setFirst_name(nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME)));
            item.setLast_name(nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)));
            item.setPhone(nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
//            item.setLast_name(nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME)));
            Log.i("CCC", "getPhone: " + display);
            itemList.add(item);
        }
        nameCur.close();

        for (int i = 0; i < itemList.size(); i++) {

            Log.i("CCC", "getPhone: " + itemList.get(i).getPhone());
            Log.i("CCC", "getFirst_name: " + itemList.get(i).getFirst_name());
            Log.i("CCC", "getLast_name: " + itemList.get(i).getLast_name());

        }

//        RequestUserContactImport t = new RequestUserContactImport();
//        t.contactImport(itemList);

        Log.i("TAGTAGTTT", "Phone : ");

    }

}
