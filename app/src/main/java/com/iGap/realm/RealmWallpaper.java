package com.iGap.realm;

import com.iGap.module.TimeUtils;
import com.iGap.proto.ProtoGlobal;
import io.realm.Realm;
import io.realm.RealmObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by android3 on 2/14/2017.
 */

public class RealmWallpaper extends RealmObject {

    private long lastTimeGetList;
    private byte[] wallPaperList;
    private byte[] lockalList;

    public List<ProtoGlobal.Wallpaper> getWallPaperList() {
        return wallPaperList == null ? null : (List<ProtoGlobal.Wallpaper>) SerializationUtils.deserialize(wallPaperList);
    }

    public void setWallPaperList(List<ProtoGlobal.Wallpaper> wallpaperListProto) {
        this.wallPaperList = SerializationUtils.serialize(wallpaperListProto);
    }

    public ArrayList<String> getLockalList() {
        return lockalList == null ? null : ((ArrayList<String>) SerializationUtils.deserialize(lockalList));
    }

    public void setLockalList(ArrayList<String> list) {
        this.lockalList = SerializationUtils.serialize(list);
    }

    public long getLastTimeGetList() {
        return lastTimeGetList;
    }

    public void setLastTimeGetList(long lastTimeGetList) {
        this.lastTimeGetList = lastTimeGetList;
    }

    public static void updateField(final List<ProtoGlobal.Wallpaper> protoList, final String lockaPath) {

        Realm realm = Realm.getDefaultInstance();

        final RealmWallpaper realmWallpaper = realm.where(RealmWallpaper.class).findFirst();

        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {

                RealmWallpaper item;

                if (realmWallpaper == null) {
                    final RealmWallpaper rw = new RealmWallpaper();
                    item = realm.copyToRealm(rw);
                } else {
                    item = realmWallpaper;
                }

                if (protoList != null) {
                    item.setWallPaperList(protoList);
                    item.setLastTimeGetList(TimeUtils.currentLocalTime());
                }

                if (lockaPath.length() > 0) {

                    ArrayList<String> lockalList = item.getLockalList();

                    if (lockalList == null) {

                        lockalList = new ArrayList<String>();
                        lockalList.add(lockaPath);
                        item.setLockalList(lockalList);
                    } else if (lockalList.indexOf(lockaPath) == -1) {
                        lockalList.add(lockaPath);
                        item.setLockalList(lockalList);
                    }
                }
            }
        });

        realm.close();
    }

    //****************************************************************************************************

    public static class SerializationUtils {

        /**
         * @param obj - object to serialize to a byte array
         * @return byte array containing the serialized obj
         */
        public static byte[] serialize(Object obj) {
            byte[] result = null;
            ByteArrayOutputStream fos = null;

            try {
                fos = new ByteArrayOutputStream();
                ObjectOutputStream o = new ObjectOutputStream(fos);
                o.writeObject(obj);
                result = fos.toByteArray();
            } catch (IOException e) {
                System.err.println(e);
            } finally {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return result;
        }

        /**
         * @param arr - the byte array that holds the serialized object
         * @return the deserialized object
         */
        public static Object deserialize(byte[] arr) {
            InputStream fis = null;

            try {
                fis = new ByteArrayInputStream(arr);
                ObjectInputStream o = new ObjectInputStream(fis);
                return o.readObject();
            } catch (IOException e) {
                System.err.println(e);
            } catch (ClassNotFoundException e) {
                System.err.println(e);
            } finally {
                try {
                    fis.close();
                } catch (Exception e) {
                }
            }

            return null;
        }

        /**
         * @param obj - object to be cloned
         * @return a clone of obj
         */
        @SuppressWarnings("unchecked") public static <T> T cloneObject(T obj) {
            return (T) deserialize(serialize(obj));
        }
    }
}
