package fr.ccm.m1.android.projet.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;


import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import fr.ccm.m1.android.projet.R;
import fr.ccm.m1.android.projet.databinding.ActivityLoginBinding;

import fr.ccm.m1.android.projet.model.Avatar;
import fr.ccm.m1.android.projet.model.Localisation;
import fr.ccm.m1.android.projet.model.Login;
import fr.ccm.m1.android.projet.model.Utilisateur;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LOGIN ACTIVITY";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private static final int PERMS_CALL_ID = 200;
    private Location location;
    private Geocoder geocoder;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        geocoder = new Geocoder(this, Locale.getDefault());
        mAuth = FirebaseAuth.getInstance();
        ActivityLoginBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setActivity(this);
        binding.setUserLogin(new Login());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                setLocation(locationResult.getLastLocation());
                fusedLocationProviderClient.removeLocationUpdates(locationCallback);

            }
        };
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, PERMS_CALL_ID);
        } else {
            checkSettingsAndStartLocationUpdates();
        }
    }

    private void checkSettingsAndStartLocationUpdates() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, PERMS_CALL_ID);
                }
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            }
        });
        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(LoginActivity.this, 1001);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkPermission();
    }

    @Override
    public void onStart() {
        super.onStart();
        checkPermission();
    }

    private void goToMenu(FirebaseUser user) {
        if(user != null){
            Intent menuActivity = new Intent(LoginActivity.this, MenuActivity.class);
            startActivity(menuActivity);
        }
    }

    public void connection(String email,String password){
        if(email == null || password == null || email.isEmpty() || password.isEmpty()){
            Log.d(TAG, "createUserWithEmail:failure");
            Toast.makeText(LoginActivity.this,"email ou mot de passe vide",Toast.LENGTH_LONG).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(Objects.requireNonNull(email), password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        goToMenu(mAuth.getCurrentUser());
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Email ou mot de passe incorrect",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void enregistrement(String email, String password){
        if(getLocation() == null){
            Toast.makeText(LoginActivity.this,"Veuillez activer vos données de localisation",Toast.LENGTH_LONG).show();
            return;
        }
        if(email == null || password == null || email.isEmpty() || password.isEmpty()){
            Log.d(TAG, "createUserWithEmail:failure");
            Toast.makeText(LoginActivity.this,"email ou mot de passe vide",Toast.LENGTH_LONG).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        if (mAuth.getCurrentUser() != null) {
                            Localisation localisation = createLocalisation(mAuth.getCurrentUser(),getLocation());
                            Avatar avatar = createAvatar(mAuth.getCurrentUser(),localisation);
                            createUser(mAuth.getCurrentUser(),localisation,avatar);
                        }
                        goToMenu(mAuth.getCurrentUser());
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "syntaxe email incorrect email déjà utilisée ou mot de passe inférieur à 6 caractères",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
    public Avatar createAvatar(FirebaseUser user, Localisation localisation) {
        Avatar avatar = new Avatar();
        avatar.setNomUtilisateur(user.getEmail());
        avatar.setUtilisateurId(user.getUid());
        avatar.setEnVoyage(false);
        avatar.setTempsSurUnTelephone(4);
        avatar.setTempsDuVoyage(8);
        avatar.setFrequenceCollecteLocalisation(2);
        avatar.setNbVoyage(0);
        avatar.setDerniereLocalisationId(localisation.getLocalisationId());
        db.collection("avatars").document(user.getUid()).set(avatar);
        return avatar;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Localisation createLocalisation(FirebaseUser user, Location location) {
        Localisation localisation = new Localisation();
        localisation.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        localisation.setReferenceUtilisateurId(user.getUid());
        if (location != null){
            localisation.setLatitude(location.getLatitude());
            localisation.setLongitude(location.getLatitude());
            try {
                List<Address> adresseList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                localisation.setAdresseDetail(adresseList.get(0).getAddressLine(0));
            } catch (Exception e) {
                localisation.setAdresseDetail("Latitude : " + location.getLatitude() + ", Longitude : " + location.getLongitude());
                e.printStackTrace();
            }

        }
        DocumentReference newLocalisationRef = db.collection("localisation").document();
        localisation.setLocalisationId(newLocalisationRef.getId());
        newLocalisationRef.set(localisation);
        return localisation;
    }

    public Utilisateur createUser(FirebaseUser user, Localisation localisation, Avatar avatar) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(user.getEmail());
        utilisateur.setDerniereLocalisationId(localisation.getLocalisationId());
        utilisateur.setAvatarInviteListe( new ArrayList<>());
        utilisateur.setFrequenceUpdateLocalisation(2);
        db.collection("utilisateurs").document(user.getUid()).set(utilisateur);
        return utilisateur;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }



}