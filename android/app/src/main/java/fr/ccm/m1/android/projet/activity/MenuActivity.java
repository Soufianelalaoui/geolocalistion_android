package fr.ccm.m1.android.projet.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.ccm.m1.android.projet.R;
import fr.ccm.m1.android.projet.databinding.ActivityMenuBinding;
import fr.ccm.m1.android.projet.model.Avatar;
import fr.ccm.m1.android.projet.model.Localisation;
import fr.ccm.m1.android.projet.model.Utilisateur;
import fr.ccm.m1.android.projet.model.Voyage;
import fr.ccm.m1.android.projet.service.UpdateAvatarLocalisationService;
import fr.ccm.m1.android.projet.service.UpdateUtilisateurLocalsation;


public class MenuActivity extends AppCompatActivity {
    private static final String TAG = "MENU ACTIVITY";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Avatar avatar = new Avatar();
    private Localisation localisation = new Localisation();
    public final ObservableField<String>  localisationAdresse = new ObservableField<>();
    public final ObservableField<String>  enVoyage = new ObservableField<>();
    private UpdateUtilisateurLocalsation updateUtilisateurLocalsation;
    private UpdateAvatarLocalisationService updateAvatarLocalisationService;
    private Intent utilisateurServiceIntent;
    private Intent avatarServiceIntent;



    public MenuActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        if(mAuth.getCurrentUser() == null){
            goToLogin();
        }else {
            ActivityMenuBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_menu);
            binding.setActivity(this);
            binding.setUsername(mAuth.getCurrentUser().getEmail());
            updateUtilisateurLocalsation = new UpdateUtilisateurLocalsation();
            utilisateurServiceIntent = new Intent(this, updateUtilisateurLocalsation.getClass());
            utilisateurServiceIntent.putExtra("utilisateurId",mAuth.getCurrentUser().getUid());
            if (!isMyServiceRunning(updateUtilisateurLocalsation.getClass())) {
                startService(utilisateurServiceIntent);
            }

            updateAvatarLocalisationService = new UpdateAvatarLocalisationService();
            avatarServiceIntent = new Intent(this, updateAvatarLocalisationService.getClass());
            avatarServiceIntent.putExtra("utilisateurId",mAuth.getCurrentUser().getUid());
            if (!isMyServiceRunning(updateAvatarLocalisationService.getClass())) {
                startService(avatarServiceIntent);
            }


        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }


    @Override
    public void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() == null){
            goToLogin();
        }else{
            createAvatarListener();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    public void goToAvatarSurMonTel(){
        Intent menuActivity = new Intent(MenuActivity.this, AvatarsSurMonTelActivity.class);
        startActivity(menuActivity);
        finish();
    }

    public void goToConfigAvatar(){
        Intent menuActivity = new Intent(MenuActivity.this, ConfigAvatarActivity.class);
        startActivity(menuActivity);
        finish();
    }

    public void goToHistoriqueVoyages(){
        Intent menuActivity = new Intent(MenuActivity.this, HistoriqueVoyagesActivity.class);
        startActivity(menuActivity);
        finish();
    }

    public void goToLogin() {
        FirebaseAuth.getInstance().signOut();
        Intent loginActivity = new Intent(MenuActivity.this, LoginActivity.class);
        startActivity(loginActivity);
        finish(); //Stops the current activity
    }

    public void createAvatarListener(){
        final DocumentReference docRef = db.collection("avatars").document(mAuth.getCurrentUser().getUid());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w( "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    setAvatar(snapshot.toObject(Avatar.class));
                    enVoyage.set(getAvatar().isEnVoyage() ? "Oui" : "Non");
                    recupereAvatarLoCalisation(avatar.getDerniereLocalisationId());
                }
            }
        });
    }
    public void recupereAvatarLoCalisation(String localisationId){
        DocumentReference docRef = db.collection("localisation").document(localisationId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        localisation = document.toObject(Localisation.class);
                        localisationAdresse.set(localisation.getAdresseDetail());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void envoyerAvatar(){
        if(avatar.isEnVoyage()){
            Toast.makeText(MenuActivity.this,"l'avatar est déjà en voyage!",Toast.LENGTH_LONG).show();
        }else {
            db.collection("utilisateurs").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<String> localisationIdList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            localisationIdList.add((String) document.get("derniereLocalisationId"));
                        }
                        db.collection("localisation").whereIn("localisationId",localisationIdList).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    List<Localisation> localisationList = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        localisationList.add(document.toObject(Localisation.class));
                                    }
                                    createVoyage(localisation.localisationDuTelephoneLePlusProche(localisationList));
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        }
    }

    public void createVoyage(Localisation localisation){
        DocumentReference documentReference = db.collection("voyage").document();
        String voyageId = documentReference.getId();
        Voyage voyage = new Voyage();
        voyage.setNumero(String.valueOf(avatar.getNbVoyage() +1));
        voyage.setVoyageId(documentReference.getId());
        List<String> localisationIdList = new ArrayList<>();
        localisationIdList.add(localisation.getLocalisationId());
        voyage.setTrajet(localisationIdList);
        List<String> voyageIdList = new ArrayList<>();
        voyageIdList.add(documentReference.getId());
        avatar.setEnVoyage(true);
        avatar.setHistoriqueDesVoyagesId(voyageIdList);
        avatar.setVoyageEnCoursId(documentReference.getId());
        avatar.setDerniereLocalisationId(localisation.getLocalisationId());
        db.collection("avatars").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                .update("derniereLocalisationId", localisation.getLocalisationId(),
                                "enVoyage", true,
                        "historiqueDesVoyagesId", FieldValue.arrayUnion(voyageId),
                        "nbVoyage",avatar.getNbVoyage()+1,
                        "voyageEnCoursId", documentReference.getId());
        documentReference.set(voyage);
        db.collection("utilisateurs").document(localisation.getReferenceUtilisateurId()).update("avatarInviteListe", FieldValue.arrayUnion(mAuth.getCurrentUser().getUid()));
    }

    public void ramenerAvatar(){
        if(!avatar.isEnVoyage()){
            Toast.makeText(MenuActivity.this,"l'avatar n'est pas en voyage!",Toast.LENGTH_LONG).show();
        }else {
            db.collection("utilisateurs").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Utilisateur utilisateur = document.toObject(Utilisateur.class);
                            assert utilisateur != null;
                            db.collection("avatars").document(mAuth.getCurrentUser().getUid()).update(
                                    "derniereLocalisationId",utilisateur.getDerniereLocalisationId(),
                                    "enVoyage", false,
                                    "voyageEnCoursId", null,
                                    "tempsParcouruSurTelephone",0,
                                    "tempsTotalParcourru",0);

                            db.collection("utilisateurs").document(localisation.getReferenceUtilisateurId()).update("avatarInviteListe", FieldValue.arrayRemove(mAuth.getCurrentUser().getUid()));
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
    }

    //region getters and setters

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }





    //endregion

}

