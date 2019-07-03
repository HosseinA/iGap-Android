package net.iGap.helper;

import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmContactsFields;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class ContactManager {
    public static final int LOAD_AVG = 30;
    public static final int CONTACT_LIMIT = 5000;

    public static final String FIRST = "FIRST";
    public static final String OVER_LOAD = "OVERLOAD";
    private static RealmResults<RealmContacts> results;

    private static int first = 0;
    private static int loadMore = LOAD_AVG;
    private static int contactSize;

    private ContactManager() {

    }

    public static RealmList<RealmContacts> getContactList(String mode) {
        if (mode.equals(FIRST)) {
            first = 0;
            loadMore = LOAD_AVG;
            return overLoadContact();
        } else if (mode.equals(OVER_LOAD))
            return overLoadContact();
        else
            return new RealmList<>();
    }

    private static RealmList<RealmContacts> overLoadContact() {
        RealmList<RealmContacts> contacts = new RealmList<>();

        if (results == null)
            getIgapContact();

        if (loadMore < contactSize)
            contacts.addAll(results.subList(first, loadMore));
        else if (first < contactSize)
            contacts.addAll(results.subList(first, contactSize));

        first = loadMore;
        loadMore = loadMore + LOAD_AVG;
        return contacts;
    }

    private static void getIgapContact() {


        if (results == null) {
            Realm realm = Realm.getDefaultInstance();
//            realm.executeTransaction(realm1 -> {
//                RealmContacts contacts = realm1.where(RealmContacts.class).sort(RealmContactsFields.DISPLAY_NAME).findFirst();
//                for (int i = 0; i < 5000; i++) {
//                    RealmContacts object = realm1.createObject(RealmContacts.class);
//                    object.setDisplay_name(contacts.getDisplay_name());
//                    object.setId(contacts.getId());
//                    object.setPhone(contacts.getPhone());
//                    object.setAvatar(contacts.getAvatar());
//                    Log.i(TAG, "getIgapContact: " + contacts.getDisplay_name() + " " + i);
//                }
//            });
            realm.executeTransaction(realm1 -> {
                results = realm1.where(RealmContacts.class).limit(CONTACT_LIMIT).sort(RealmContactsFields.DISPLAY_NAME).findAll();
                contactSize = results.size();
            });
        }
    }

}