package com.sadpumpkin.farm2table.game.forage;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.type.LatLng;
import com.sadpumpkin.farm2table.R;
import com.sadpumpkin.farm2table.util.BaseFragment;
import com.sadpumpkin.farm2table.util.SeedDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ForageFragment extends BaseFragment {

    private static final int ACCESS_LOCATION_REQ_ID = 107;

    private boolean _useFineLocation = false;
    private boolean _permissionsGranted = false;
    private LocationManager _locationManager = null;

    private LatLng _location = null;
    private String _locationString = null;

    private ForageData _forageData = null;

    private View _loadingDetailsParent = null;
    private View _loadedDetailsParent = null;
    private TextView _locationLabel = null;
    private TextView _fertilityLabel = null;
    private ProgressBar _regrowthBar = null;
    private TextView _regrowthLabel = null;
    private GridLayout _resourcesGrid = null;
    private Button _forageButton = null;

    private DocumentReference _forageDocRef = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        _loadingDetailsParent = view.findViewById(R.id.loadingLayout);
        _loadedDetailsParent = view.findViewById(R.id.loadedLayout);
        _locationLabel = view.findViewById(R.id.locationLabel);
        _fertilityLabel = view.findViewById(R.id.fertilityLabel);
        _regrowthBar = view.findViewById(R.id.regrowthBar);
        _regrowthLabel = view.findViewById(R.id.regrowthLabel);
        _resourcesGrid = view.findViewById(R.id.resourceIconsGrid);
        _forageButton = view.findViewById(R.id.forageButton);

        _loadingDetailsParent.setVisibility(View.VISIBLE);
        _loadedDetailsParent.setVisibility(View.GONE);
        _forageButton.setEnabled(false);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(_activity.getApplicationContext());

        _useFineLocation = preferences.getBoolean("USE_FINE_LOCATION", true);
        _locationManager = _activity.getApplicationContext().getSystemService(LocationManager.class);

        getPermissions();
        getLocation();
    }

    private void getPermissions() {
        Context context = _activity.getApplicationContext();

        _permissionsGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (!_permissionsGranted) {
            if (_useFineLocation) {
                ActivityCompat.requestPermissions(_activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQ_ID);
            } else {
                ActivityCompat.requestPermissions(_activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_LOCATION_REQ_ID);
            }
        }
    }

    private void getLocation() {
        if (_permissionsGranted &&
                (_locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || _locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))) {
            Criteria providerCriteria = new Criteria();
            providerCriteria.setAccuracy(_useFineLocation ? Criteria.ACCURACY_FINE : Criteria.ACCURACY_COARSE);
            providerCriteria.setPowerRequirement(_useFineLocation ? Criteria.POWER_HIGH : Criteria.POWER_LOW);

            String bestProvider = _locationManager.getBestProvider(providerCriteria, true);
            if (TextUtils.isEmpty(bestProvider)) {
                getBackupLocation();
                return;
            }

            try {
                _locationManager.requestLocationUpdates(bestProvider, 1000, 3, location -> {
                    if (location == null) {
                        getBackupLocation();
                        return;
                    }

                    double lat = location.getLatitude();
                    double lon = location.getLongitude();

                    onLocationUpdated(
                            LatLng.newBuilder().setLatitude(lat).setLongitude(lon).build(),
                            String.format(Locale.getDefault(Locale.Category.FORMAT), "%.2f-%.2f", lat, lon));
                });
            } catch (SecurityException e) {
                Log.e("GPS", e.getMessage(), e.getCause());
                getBackupLocation();
            }
        } else {
            getBackupLocation();
        }
    }

    private void getBackupLocation() {
        Criteria providerCriteria = new Criteria();
        providerCriteria.setAccuracy(_useFineLocation ? Criteria.ACCURACY_FINE : Criteria.ACCURACY_COARSE);
        providerCriteria.setCostAllowed(false);
        providerCriteria.setAltitudeRequired(false);
        providerCriteria.setBearingRequired(false);
        providerCriteria.setSpeedRequired(false);

        String bestProvider = _locationManager.getBestProvider(providerCriteria, true);
        if (TextUtils.isEmpty(bestProvider)) {
            getFallbackLocation();
            return;
        }

        Location location;
        try {
            location = _locationManager.getLastKnownLocation(bestProvider);
        } catch (SecurityException e) {
            Log.e("GPS", e.getMessage(), e.getCause());
            getFallbackLocation();
            return;
        }

        if (location == null) {
            getFallbackLocation();
            return;
        }

        double lat = location.getLatitude();
        double lon = location.getLongitude();

        onLocationUpdated(
                LatLng.newBuilder().setLatitude(lat).setLongitude(lon).build(),
                String.format(Locale.getDefault(Locale.Category.FORMAT), "%.2f-%.2f", lat, lon));
    }

    private void getFallbackLocation() {
        onLocationUpdated(
                null,
                String.format(Locale.getDefault(Locale.Category.FORMAT), "%.2f-%.2f-%s", 0d, 0d, _userData.user().getUid()));
    }

    private void onLocationUpdated(LatLng newLatLng, String newLocationString) {
        if (newLatLng != _location || !newLocationString.equals(_locationString)) {
            _location = newLatLng;
            _locationString = newLocationString;

            getFertilityInformation();
        }
    }

    private void getFertilityInformation() {
        _forageDocRef = _firebase.getForageDocRef(_locationString);

        _forageDocRef.get().addOnCompleteListener(docSnapTask -> {
            View view = getView();
            if (view == null) {
                return;
            }

            if (docSnapTask.isSuccessful()) {
                DocumentSnapshot docSnapshot = docSnapTask.getResult();

                ForageData forageData = null;
                if (docSnapshot.exists()) {
                    forageData = docSnapshot.toObject(ForageData.class);
                } else {
                    forageData = ForageData.BuildDefault(_gameData.getAllSeeds());

                    _forageDocRef.set(forageData);
                }

                final ForageData finalForageData = forageData;
                view.post(() -> updateUiWithForageData(finalForageData));
            } else {
                Log.e("ERR", docSnapTask.getException().getMessage());
                view.post(() -> updateUiWithForageData(null));
            }
        });
    }

    private void updateUiWithForageData(ForageData forageData) {
        _forageData = forageData;

        if (_forageData != null) {
            // Set all the fields
            _resourcesGrid.removeAllViewsInLayout();
            _resourcesGrid.setMinimumWidth(30);
            _resourcesGrid.setMinimumHeight(30);
            for (String resourceId : _forageData.getResourceIds().keySet()) {
                SeedDefinition seedDefinition = _gameData.getSeedDefinition(resourceId);

                ImageView resourceIcon = new ImageView(getContext());
                resourceIcon.setScaleType(ImageView.ScaleType.FIT_XY);
                resourceIcon.setMinimumWidth(30);
                resourceIcon.setMinimumHeight(30);
                _resourcesGrid.addView(resourceIcon);

                Glide.with(_resourcesGrid)
                        .load(_gameData.getImageReference(seedDefinition.getPath()))
                        .into(resourceIcon);
            }

            _locationLabel.setText(_location == null
                    ? getText(R.string.label_the_void)
                    : String.format(Locale.getDefault(), "%.2f x %.2f", _location.getLatitude(), _location.getLongitude()));
            _fertilityLabel.setText(String.format(Locale.getDefault(), "%.0f%%", _forageData.getFertility() * 100));

            CountDownTimer timer = new CountDownTimer(_forageData.getRegrownInSeconds() * 1000, 1000) {
                @Override
                public void onTick(long l) {
                    onFinish();
                }

                @Override
                public void onFinish() {
                    Long remaining = _forageData.getRegrownInSeconds();
                    StringBuilder remainingBuilder = new StringBuilder();
                    if (remaining / (60 * 60) >= 1) {
                        Long hours = remaining / (60 * 60);
                        remaining %= 60 * 60;
                        remainingBuilder.append(String.format("%1$2s", hours).replace(' ', '0')).append(":");
                    }
                    if (remaining / 60 >= 1 || remainingBuilder.length() > 0) {
                        Long minutes = remaining / 60;
                        remaining %= 60;
                        remainingBuilder.append(String.format("%1$2s", minutes).replace(' ', '0')).append(":");
                    }
                    if (remaining >= 1 || remainingBuilder.length() > 0) {
                        Long seconds = remaining;
                        remainingBuilder.append(String.format("%1$2s", seconds).replace(' ', '0'));
                    }

                    _regrowthBar.setMin(0);
                    _regrowthBar.setMax(100);
                    _regrowthBar.setProgress((int) Math.round(100 * _forageData.getRegrowthPercent()));
                    _regrowthLabel.setText(_forageData.getRegrowthPercent() >= 1d
                            ? getString(R.string.label_regrown_complete)
                            : getString(R.string.label_regrown_in, remainingBuilder.toString()));
                    _forageButton.setEnabled(_forageData.getRegrowthPercent() > 0.25d);
                }
            }.start();

            _loadingDetailsParent.setVisibility(View.GONE);
            _loadedDetailsParent.setVisibility(View.VISIBLE);
            _forageButton.setEnabled(_forageData.getRegrowthPercent() > 0.25d);

            _forageButton.setOnClickListener(clickedView -> {
                timer.cancel();

                // Determine Forage Results
                List<String> forageResults = _forageData.computeForageResults();
                // Set Forage Time to Now
                _forageData.setLastForage();
                // Send Forage Data to Server
                _forageDocRef.set(_forageData);

                // Update User Data with Forage Results
                _userData.farm().addForage(forageResults, _gameData);

                // Send User Data to Server
                _firebase.getUserDocRef(_userData.user()).set(_userData.farm()).addOnFailureListener(ex ->{
                    _firebase.getUserDocRef(_userData.user()).set(_userData.farm());
                });

                Map<String, Long> seedCounts = new HashMap<>();
                for(String seedId : forageResults) {
                    seedCounts.put(seedId, seedCounts.getOrDefault(seedId, 0L) + 1);
                }
                StringBuilder resultBuilder = new StringBuilder();
                for (String seedType: seedCounts.keySet()) {
                    SeedDefinition seedDefinition = _gameData.getSeedDefinition(seedType);
                    resultBuilder
                            .append("    ")
                            .append(seedDefinition.getName())
                            .append(" x")
                            .append(seedCounts.get(seedType))
                            .append("\n");
                }

                // Display Results to User
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.title_forage_success)
                        .setMessage(getString(R.string.body_forage_success, resultBuilder.toString()))
                        .show();

                updateUiWithForageData(_forageData);
            });

        } else {
            // Hide and show some error stuff
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.title_forage_error)
                    .setMessage(R.string.body_forage_error)
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ACCESS_LOCATION_REQ_ID: {
                _permissionsGranted = grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED;
                getLocation();
            }
        }
    }
}
