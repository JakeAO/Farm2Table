package com.sadpumpkin.farm2table.util;

import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class FirebaseWrapper extends ViewModel {
    private FirebaseAuth _auth;
    private FirebaseStorage _storage;
    private FirebaseFirestore _database;

    public FirebaseWrapper() {
        _auth = FirebaseAuth.getInstance();
        _storage = FirebaseStorage.getInstance();
        _database = FirebaseFirestore.getInstance();

        _database.enableNetwork();
    }

    public FirebaseAuth auth() {
        return _auth;
    }

    public FirebaseStorage storage() {
        return _storage;
    }

    public FirebaseFirestore database() {
        return _database;
    }

    public DocumentReference getUserDocRef(FirebaseUser user) {
        return database()
                .collection("userData")
                .document(user.getUid());
    }

    public DocumentReference getForageDocRef(String location) {
        return database()
                .collection("forageData")
                .document(location);
    }
}