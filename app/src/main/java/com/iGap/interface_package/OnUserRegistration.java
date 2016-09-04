package com.iGap.interface_package;

import com.iGap.proto.ProtoUserRegister;

import java.util.List;

public interface OnUserRegistration {

    void onRegister(String userName, long userId, ProtoUserRegister.UserRegisterResponse.Method methodValue, List<Long> smsNumbers, String regex);

}
