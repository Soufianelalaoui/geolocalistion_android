package fr.ccm.m1.android.projet.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.ccm.m1.android.projet.R;
import fr.ccm.m1.android.projet.adapter.AvatarAdapter;
import fr.ccm.m1.android.projet.databinding.ActivityAvatarsSurMonTelBinding;
import fr.ccm.m1.android.projet.model.Avatar;
import fr.ccm.m1.android.projet.model.Localisation;
import fr.ccm.m1.android.projet.model.Utilisateur;

public class AvatarsSurMonTelActivity extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    AvatarAdapter adapter;
    List<Avatar> avatarList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    Utilisateur utilisateur;
    ActivityAvatarsSurMonTelBinding activityAvatarsSurMonTelBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAvatarsSurMonTelBinding = DataBindingUtil.setContentView(this,R.layout.activity_avatars_sur_mon_tel);
        adapter = new AvatarAdapter(avatarList,this);
        activityAvatarsSurMonTelBinding.recyclerView.setAdapter(adapter);
        activityAvatarsSurMonTelBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        activityAvatarsSurMonTelBinding.recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

    }
    @Override
    public void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() == null){
            goToLogin();
        }else{
            createUtilisateurListener();
        }
    }
    public void createUtilisateurListener(){
         db.collection("utilisateurs").document(mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w( "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    utilisateur = snapshot.toObject(Utilisateur.class);
                    if(utilisateur != null && utilisateur.getAvatarInviteListe()!= null && !utilisateur.getAvatarInviteListe().isEmpty()){
                        db.collection("avatars").whereIn(FieldPath.documentId(),utilisateur.getAvatarInviteListe()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    avatarList.clear();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        avatarList.add(document.toObject(Avatar.class));
                                    }
                                    adapter.setItems(avatarList);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public void renvoyerAvatar(String avatarId){
        db.collection("utilisateurs").document(avatarId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Utilisateur utilisateur = document.toObject(Utilisateur.class);
                        assert utilisateur != null;
                        db.collection("avatars").document(avatarId).update(
                                "derniereLocalisationId",utilisateur.getDerniereLocalisationId(),
                                "enVoyage", false,
                                "voyageEnCoursId", null,
                                "disanceSurTelephoneParcouru",0,
                                "distanceTotalParcouru",0,
                                "tempsParcouruSurTelephone",0,
                                "tempsTotalParcourru",0);

                        db.collection("utilisateurs").document(mAuth.getCurrentUser().getUid()).update("avatarInviteListe", FieldValue.arrayRemove(avatarId));
                    }
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goToMenu();
    }

    public void goToMenu() {
        Intent menuActivity = new Intent(AvatarsSurMonTelActivity.this, MenuActivity.class);
        startActivity(menuActivity);
        finish();
    }

    public void goToLogin() {
        FirebaseAuth.getInstance().signOut();
        Intent loginActivity = new Intent(AvatarsSurMonTelActivity.this, LoginActivity.class);
        startActivity(loginActivity);
        finish();
    }
}