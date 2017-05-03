/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.response;

import java.security.GeneralSecurityException;
import net.iGap.AESCrypt;
import net.iGap.G;
import net.iGap.helper.HelperNumerical;
import net.iGap.helper.HelperUnpackMessage;

public class HandleResponse extends Thread {

    byte[] binary;

    public HandleResponse(byte[] binary) {
        this.binary = binary;
    }

    @Override public void run() {
        super.run();
        if (G.isSecure) {
            byte[] iv = HelperNumerical.getIv(binary, G.ivSize);
            byte[] binaryDecode = HelperNumerical.getMessage(binary);

            try {
                binaryDecode = AESCrypt.decrypt(G.symmetricKey, iv, binaryDecode);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
            HelperUnpackMessage.unpack(binaryDecode);
        } else {
            HelperUnpackMessage.unpack(binary);
        }
    }
}
