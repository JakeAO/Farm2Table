package com.sadpumpkin.farm2table.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sadpumpkin.farm2table.util.callback.ICallback1;
import com.sadpumpkin.farm2table.util.factory.*;
import com.sadpumpkin.farm2table.util.factory.definition.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AsyncLoginBase implements Runnable {

    protected WeakReference<Handler> _handler = null;
    protected WeakReference<Activity> _context = null;
    protected WeakReference<FirebaseWrapper> _firebase = null;
    protected WeakReference<UserDataWrapper> _userData = null;
    protected WeakReference<GameDataWrapper> _gameData = null;
    protected ICallback1<Boolean> _callback = null;

    protected ProgressDialog _progressDialog = null;

    public AsyncLoginBase(Handler handler,
                          Activity context,
                          FirebaseWrapper firebase,
                          UserDataWrapper userData,
                          GameDataWrapper gameData,
                          ICallback1<Boolean> onDoneCallback) {
        _handler = new WeakReference<>(handler);
        _context = new WeakReference<>(context);
        _firebase = new WeakReference<>(firebase);
        _userData = new WeakReference<>(userData);
        _gameData = new WeakReference<>(gameData);
        _callback = onDoneCallback;
    }

    @Override
    public void run() {
        _handler.get().post(this::onPreExecute_mainThread);

        boolean success = false;
        try {
            success = onExecute_backgroundThread();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        boolean finalSuccess = success;
        _handler.get().post(() -> onPostExecute_mainThread(finalSuccess));
    }

    protected void onPreExecute_mainThread() {
        _progressDialog = new ProgressDialog(_context.get());
        _progressDialog.setCancelable(false);
        _progressDialog.setCanceledOnTouchOutside(false);
        _progressDialog.setMessage("");
        _progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        _progressDialog.show();
    }

    protected boolean onExecute_backgroundThread() throws InterruptedException {
        _handler.get().post(() -> onProgress_mainThread("Logging In"));
        FirebaseUser user = onLogin();
        if (user == null)
            return false;

        Thread.sleep(100);

        _handler.get().post(() -> onProgress_mainThread("Syncing Game Data"));
        boolean gameDataSuccess = onSyncGameData();
        if (!gameDataSuccess)
            return false;

        Thread.sleep(100);

        _handler.get().post(() -> onProgress_mainThread("Syncing User Data"));
        FarmData farm = onSyncUserData(user);
        if (farm == null)
            return false;

        _userData.get().setUser(user);
        _userData.get().setFarm(farm);

        Thread.sleep(100);

        _handler.get().post(() -> onProgress_mainThread("Loading Farm"));

        Thread.sleep(100);

        return true;
    }

    protected void onProgress_mainThread(String status) {
        _progressDialog.setMessage(status);
    }

    protected void onPostExecute_mainThread(boolean success) {
        _progressDialog.dismiss();
        if (_callback != null) {
            _callback.onInvoke(success);
        }
    }

    protected abstract FirebaseUser onLogin() throws InterruptedException;

    protected boolean onSyncGameData() {

        FirebaseStorage storage = _firebase.get().storage();
        Gson gson = new Gson();

        // Get Primary Manifest
        File manifestFile = downloadOrUpdateFileAtPath(storage, "manifest.json");

        AssetManifest assetManifest = null;
        try {
            assetManifest = gson.fromJson(new FileReader(manifestFile), AssetManifest.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        // Get Seeds Manifest
        File seedsFile = downloadOrUpdateFileAtPath(storage, assetManifest.getSeedManifest());
        Type seedsTokenType = new TypeToken<ArrayList<SeedDefinition>>() {
        }.getType();
        List<SeedDefinition> seeds = null;
        try {
            seeds = gson.fromJson(new FileReader(seedsFile), seedsTokenType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Get Resources Manifest
        File resourcesFile = downloadOrUpdateFileAtPath(storage, assetManifest.getResourceManifest());
        Type resourcesTokenType = new TypeToken<ArrayList<ResourceDefinition>>() {
        }.getType();
        List<ResourceDefinition> resources = null;
        try {
            resources = gson.fromJson(new FileReader(resourcesFile), resourcesTokenType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Get Producers Manifest
        File producersFile = downloadOrUpdateFileAtPath(storage, assetManifest.getProducerManifest());
        Type producersTokenType = new TypeToken<ArrayList<ProducerDefinition>>() {
        }.getType();
        List<ProducerDefinition> producers = null;
        try {
            producers = gson.fromJson(new FileReader(producersFile), producersTokenType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Get Converters Manifest
        File convertersFile = downloadOrUpdateFileAtPath(storage, assetManifest.getConverterManifest());
        Type convertersTokenType = new TypeToken<ArrayList<ConverterDefinition>>() {
        }.getType();
        List<ConverterDefinition> converters = null;
        try {
            converters = gson.fromJson(new FileReader(convertersFile), convertersTokenType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Get Consumers Manifest
        File consumersFile = downloadOrUpdateFileAtPath(storage, assetManifest.getConsumerManifest());
        Type consumersTokenType = new TypeToken<ArrayList<ConsumerDefinition>>() {
        }.getType();
        List<ConsumerDefinition> consumers = null;
        try {
            consumers = gson.fromJson(new FileReader(consumersFile), consumersTokenType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (seeds == null ||
                resources == null ||
                producers == null ||
                converters == null ||
                consumers == null) {
            return false;
        }

        GameDataWrapper gameDataWrapper = _gameData.get();
        gameDataWrapper.setFirebaseWrapper(_firebase.get());
        gameDataWrapper.setSeedData(seeds);
        gameDataWrapper.setResourceData(resources);
        gameDataWrapper.setProducerData(producers);
        gameDataWrapper.setConverterData(converters);
        gameDataWrapper.setConsumerData(consumers);

        return true;
    }

    private File downloadOrUpdateFileAtPath(FirebaseStorage storage, String fileName) {

        try {
            File localFile = new File(_context.get().getFilesDir(), fileName);
            StorageReference remoteRef = storage.getReference(fileName);
            Task<StorageMetadata> remoteMetadataTask = remoteRef.getMetadata();
            Tasks.await(remoteMetadataTask, 5, TimeUnit.SECONDS);
            StorageMetadata remoteMetadata = remoteMetadataTask.getResult();
            if (!localFile.exists() || localFile.lastModified() < remoteMetadata.getUpdatedTimeMillis()) {
                FileDownloadTask remoteDataTask = remoteRef.getFile(localFile);
                Tasks.await(remoteDataTask, 15, TimeUnit.SECONDS);
            }
            if (localFile.exists())
                return localFile;
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected FarmData onSyncUserData(FirebaseUser user) throws InterruptedException {
        GameDataWrapper gameDataWrapper = _gameData.get();
        FirebaseWrapper fb = _firebase.get();
        DocumentReference docRef = fb.getUserDocRef(user);

        Task<DocumentSnapshot> getTask = docRef.get();
        try {
            Tasks.await(getTask, 10, TimeUnit.SECONDS);
        } catch (ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }

        if (!getTask.isSuccessful()) {
            return null;
        }

        DocumentSnapshot docSnapshot = null;
        try {
            docSnapshot = getTask.getResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        FarmData farmData = null;
        if (docSnapshot.exists()) {
            farmData = docSnapshot.toObject(FarmData.class);
        } else {
            farmData = FarmData.BuildDefault(gameDataWrapper.getAllSeeds(), gameDataWrapper.getAllProducers());

            Task<Void> setTask = docRef.set(farmData);
            while (!setTask.isComplete()) {
                Thread.sleep(100);
            }
        }

        if(farmData == null){
            return null;
        }

        for (ProducerInstance producer : farmData.getProducers()) {
            producer.setDefinition(gameDataWrapper.getProducerDefinition(producer.getFactoryType()));
            producer.init();
        }
        for (ConverterInstance converter : farmData.getConverters()) {
            converter.setDefinition(gameDataWrapper.getProducerDefinition(converter.getFactoryType()));
            converter.init();
        }
        for (ConsumerInstance consumer : farmData.getConsumers()) {
            consumer.setDefinition(gameDataWrapper.getProducerDefinition(consumer.getFactoryType()));
            consumer.init();
        }

        return farmData;
    }
}
