package com.sadpumpkin.farm2table.util;

import android.app.Activity;
import android.os.Handler;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.sadpumpkin.farm2table.util.callback.ICallback1;

public class LoginAnonymous extends AsyncLoginBase {

    public LoginAnonymous(Handler handler,
                          Activity context,
                          FirebaseWrapper firebase,
                          UserDataWrapper userData,
                          GameDataWrapper gameData,
                          ICallback1<Boolean> onDoneCallback) {
        super(handler, context, firebase, userData, gameData, onDoneCallback);
    }

    @Override
    protected FirebaseUser onLogin() throws InterruptedException {
        Task<AuthResult> task = _firebase.get().auth().signInAnonymously();
        while (!task.isComplete()) {
            Thread.sleep(100);
        }
        if (!task.isSuccessful()) {
            return null;
        }

        AuthResult result = task.getResult();
        FirebaseUser user = result.getUser();
        return user;
    }
}
