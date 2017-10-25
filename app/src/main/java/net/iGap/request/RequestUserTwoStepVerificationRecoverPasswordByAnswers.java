/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.request;

import net.iGap.proto.ProtoUserTwoStepVerificationRecoverPasswordByAnswers;

public class RequestUserTwoStepVerificationRecoverPasswordByAnswers {

    public void RecoveryPasswordByAnswer(String answerOne, String answerTwo) {

        ProtoUserTwoStepVerificationRecoverPasswordByAnswers.UserTwoStepVerificationRecoverPasswordByAnswers.Builder builder = ProtoUserTwoStepVerificationRecoverPasswordByAnswers.UserTwoStepVerificationRecoverPasswordByAnswers.newBuilder();
        builder.setAnswerOne(answerOne);
        builder.setAnswerTwo(answerTwo);

        RequestWrapper requestWrapper = new RequestWrapper(140, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
