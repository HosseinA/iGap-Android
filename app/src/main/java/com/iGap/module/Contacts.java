package com.iGap.module;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.iGap.G;
import com.iGap.realm.RealmContacts;
import com.iGap.realm.RealmContactsFields;
import com.iGap.realm.RealmInviteFriend;
import com.iGap.realm.RealmInviteFriendFields;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.request.RequestUserContactImport;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * work with saved contacts in database
 */
public class Contacts {

    /**
     * retrieve contacts from database
     *
     * @param filter filter contacts
     * @return List<StructContactInfo>
     */
    public static List<StructContactInfo> retrieve(String filter) {
        ArrayList<StructContactInfo> items = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();

        RealmResults<RealmContacts> contacts;
        if (filter == null) {
            contacts =
                    realm.where(RealmContacts.class).findAllSorted(RealmContactsFields.DISPLAY_NAME);
        } else {
            contacts = realm.where(RealmContacts.class)
                    .contains(RealmContactsFields.DISPLAY_NAME, filter)
                    .findAllSorted(RealmContactsFields.DISPLAY_NAME);
        }

        String lastHeader = "";
        for (int i = 0; i < contacts.size(); i++) {
            String header = contacts.get(i).getDisplay_name();
            long peerId = contacts.get(i).getId();

            //TODO [Saeed Mozaffari] [2016-11-09 5:26 PM] - Clear This Code nabayad ba RegisteredUserInfo gerfte beshe

            RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, contacts.get(i).getId()).findFirst();

            // new header exists
            if (lastHeader.isEmpty() || (!lastHeader.isEmpty()
                    && !header.isEmpty()
                    && lastHeader.toLowerCase().charAt(0) != header.toLowerCase().charAt(0))) {
                // TODO: 9/5/2016 [Alireza Eskandarpour Shoferi] implement contact last seen
                StructContactInfo structContactInfo =
                        new StructContactInfo(peerId, header, "", true, false, "");
                structContactInfo.initials = contacts.get(i).getInitials();
                structContactInfo.color = contacts.get(i).getColor();
                //structContactInfo.avatar = contacts.get(i).getAvatar(); //TODO [Saeed Mozaffari] [2016-11-09 5:28 PM] - in code nabayd comment beshe va khat paiin ejrabeshe
                structContactInfo.avatar = realmRegisteredInfo.getLastAvatar();
                items.add(structContactInfo);
            } else {
                StructContactInfo structContactInfo =
                        new StructContactInfo(peerId, header, "", false, false, "");
                structContactInfo.initials = contacts.get(i).getInitials();
                structContactInfo.color = contacts.get(i).getColor();
                //structContactInfo.avatar = contacts.get(i).getAvatar(); // //TODO [Saeed Mozaffari] [2016-11-09 5:28 PM] - in code nabayd comment beshe va khat paiin ejrabeshe
                structContactInfo.avatar = realmRegisteredInfo.getLastAvatar();
                items.add(structContactInfo);
            }
            lastHeader = header;
        }

        realm.close();
        return items;
    }

    public static ArrayList<StructListOfContact> getListOfContact(
            boolean sendToServer) { //get List Of Contact

        ArrayList<StructListOfContact> contactList = new ArrayList<>();
        ContentResolver cr = G.context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        assert cur != null;
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                StructListOfContact itemContact = new StructListOfContact();
                itemContact.setDisplayName(
                        cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                if (Integer.parseInt(
                        cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)))
                        > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{
                                    id
                            }, null);
                    assert pCur != null;
                    while (pCur.moveToNext()) {


                        itemContact.setPhone(pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER)));
                        Log.i("BBBBB", "getListOfContact: " + pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER)));

                        /**
                         * this part filter phone contact
                         * and get just mobile number
                         */
//                        int phoneType = pCur.getInt(
//                                pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

//                        if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) { //
//                            itemContact.setPhone(pCur.getString(pCur.getColumnIndex(
//                                    ContactsContract.CommonDataKinds.Phone.NUMBER)));
//                        }
                    }
                    pCur.close();
                }
                contactList.add(itemContact);
            }
        }
        cur.close();
        ArrayList<StructListOfContact> resultContactList = new ArrayList<>();
        for (int i = 0; i < contactList.size(); i++) {

            if (contactList.get(i).getPhone() != null) {

                StructListOfContact itemContact = new StructListOfContact();
                String[] sp = contactList.get(i).getDisplayName().split(" ");
                if (sp.length == 1) {

                    itemContact.setFirstName(sp[0]);
                    itemContact.setLastName("");
                    itemContact.setPhone(contactList.get(i).getPhone());
                    itemContact.setDisplayName(contactList.get(i).displayName);
                } else if (sp.length == 2) {
                    itemContact.setFirstName(sp[0]);
                    itemContact.setLastName(sp[1]);
                    itemContact.setPhone(contactList.get(i).getPhone());
                    itemContact.setDisplayName(contactList.get(i).displayName);
                } else if (sp.length == 3) {
                    itemContact.setFirstName(sp[0]);
                    itemContact.setLastName(sp[1] + sp[2]);
                    itemContact.setPhone(contactList.get(i).getPhone());
                    itemContact.setDisplayName(contactList.get(i).displayName);
                } else if (sp.length >= 3) {
                    itemContact.setFirstName(contactList.get(i).getDisplayName());
                    itemContact.setLastName("");
                    itemContact.setPhone(contactList.get(i).getPhone());
                    itemContact.setDisplayName(contactList.get(i).displayName);
                }

                resultContactList.add(itemContact);
            }
        }
        if (sendToServer) {
            RequestUserContactImport listContact = new RequestUserContactImport();
            listContact.contactImport(resultContactList, false);
        }
        return resultContactList;
    }

    public static void FillRealmInviteFriend() {

        final ArrayList<StructListOfContact> contactList = getListOfContact(false);
        final int size = contactList.size();

        if (size > 0) {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.delete(
                            RealmInviteFriend.class);  // delete all item in invite friend database
                    for (int i = 0; i < size; i++) {
                        RealmInviteFriend item = realm.createObject(RealmInviteFriend.class);
                        item.setDisplayName(contactList.get(i).getDisplayName());
                        item.setFirstName(contactList.get(i).getFirstName());
                        item.setLastName(contactList.get(i).getLastName());
                        item.setPhone(contactList.get(i).getPhone().replaceAll(" ", ""));
                    }
                }
            });

            //*****************************************************************************************************

            final RealmResults<RealmContacts> results = realm.where(RealmContacts.class).findAll();
            if (!results.isEmpty()) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        for (int i = 0; i < results.size(); i++) {
                            if (results.get(i).isValid()) {
                                long phone = results.get(i).getPhone();
                                String str = Long.toString(phone).replaceAll(" ", "");
                                if (str.length() > 10) {
                                    str = str.substring(str.length() - 10, str.length());
                                }

                                realm.where(RealmInviteFriend.class)
                                        .contains(RealmInviteFriendFields.PHONE, str)
                                        .findAll()
                                        .deleteAllFromRealm();
                            }
                        }
                    }
                });
            }
            realm.close();
        } else {
            // you can delete all item in realm contact  if there was no item
        }
    }

    public static ArrayList<StructContactInfo> getInviteFriendList() {

        ArrayList<StructContactInfo> list = new ArrayList<>();

        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmInviteFriend> results = realm.where(RealmInviteFriend.class)
                .findAllSorted(RealmInviteFriendFields.DISPLAY_NAME);

        if (results != null) {
            String lastHeader = "";

            for (int i = 0; i < results.size(); i++) {
                String header = results.get(i).getDisplayName();

                StructContactInfo item;

                // new header exists
                if (lastHeader.isEmpty() || (!lastHeader.isEmpty()
                        && !header.isEmpty()
                        && lastHeader.toLowerCase().charAt(0) != header.toLowerCase().charAt(0))) {
                    item =
                            new StructContactInfo(0, results.get(i).getDisplayName(), "", true, false,
                                    results.get(i).getPhone());
                } else {
                    item =
                            new StructContactInfo(0, results.get(i).getDisplayName(), "", false, false,
                                    results.get(i).getPhone());
                }
                lastHeader = header;

                list.add(item);
            }
        }
        realm.close();

        return list;
    }
}
