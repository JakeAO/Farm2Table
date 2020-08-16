package com.sadpumpkin.farm2table.util;

import android.app.Activity;
import android.os.Handler;

import com.google.firebase.auth.FirebaseUser;
import com.sadpumpkin.farm2table.util.callback.ICallback1;

public class LoginReturning extends AsyncLoginBase {

    public LoginReturning(Handler handler,
                          Activity context,
                          FirebaseWrapper firebase,
                          UserDataWrapper userData,
                          GameDataWrapper gameData,
                          ICallback1<Boolean> onDoneCallback) {
        super(handler, context, firebase, userData, gameData, onDoneCallback);
    }

    @Override
    protected FirebaseUser onLogin() {
        return _firebase.get().auth().getCurrentUser();
    }
}
