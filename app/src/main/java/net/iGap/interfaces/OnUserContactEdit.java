/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the Kianiranian Company - www.kianiranian.com
* All rights reserved.
*/

package net.iGap.interfaces;

public interface OnUserContactEdit {

    void onContactEdit(String firstName, String lastName, String initials);

    void onContactEditTimeOut();

    void onContactEditError(int majorCode, int minorCode);
}
