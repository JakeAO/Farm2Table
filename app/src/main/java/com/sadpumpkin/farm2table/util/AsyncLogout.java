package com.sadpumpkin.farm2table.util;

import android.app.Activity;
import android.os.Handler;

import com.sadpumpkin.farm2table.util.callback.ICallback;

import java.lang.ref.WeakReference;

public class AsyncLogout implements Runnable {

    private WeakReference<Handler> _handler = null;
    private WeakReference<Activity> _context = null;
    private WeakReference<FirebaseWrapper> _firebase = null;
    private WeakReference<UserDataWrapper> _userData = null;
    private ICallback _callback = null;

    public AsyncLogout(Handler handler, Activity context, FirebaseWrapper firebase, UserDataWrapper userData, ICallback onDoneCallback) {
        _handler = new WeakReference<>(handler);
        _context = new WeakReference<>(context);
        _firebase = new WeakReference<>(firebase);
        _userData = new WeakReference<>(userData);
        _callback = onDoneCallback;
    }

    @Override
    public void run() {
        // Sign Out
        _firebase.get().auth().signOut();

        // Clear Data & Report
        _handler.get().post(() -> {
            _userData.clear();

            if (_callback != null) {
                _callback.onInvoke();
            }
        });
    }
}
