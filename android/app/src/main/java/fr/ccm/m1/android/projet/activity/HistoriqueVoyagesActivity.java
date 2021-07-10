package fr.ccm.m1.android.projet.activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.ccm.m1.android.projet.R;
import fr.ccm.m1.android.projet.adapter.VoyageAdapter;
import fr.ccm.m1.android.projet.databinding.ActivityHistoriqueVoyagesBinding;
import fr.ccm.m1.android.projet.model.Avatar;
import fr.ccm.m1.android.projet.model.Utilisateur;
import fr.ccm.m1.android.projet.model.Voyage;

public class HistoriqueVoyagesActivity extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    VoyageAdapter adapter;
    List<Voyage> voyageList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    Avatar avatar;
    ActivityHistoriqueVoyagesBinding activityHistoriqueVoyagesBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHistoriqueVoyagesBinding = DataBindingUtil.setContentView(this,R.layout.activity_historique_voyages);
        adapter = new VoyageAdapter(voyageList,this);
        activityHistoriqueVoyagesBinding.recyclerView.setAdapter(adapter);
        activityHistoriqueVoyagesBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        activityHistoriqueVoyagesBinding.recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

    }
    @Override
    public void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() == null){
            goToLogin();
        }else {
            recupereVoyageList();
        }
    }



    public void goToVoyageItineraire(Voyage voyage){
        Intent descriptifVoyageActivity = new Intent(HistoriqueVoyagesActivity.this, DescriptifVoyageActivity.class);

        descriptifVoyageActivity.putStringArrayListExtra("localisations", (ArrayList<String>) voyage.getTrajet());
        startActivity(descriptifVoyageActivity);
        finish();
    }

    public void recupereVoyageList(){
        db.collection("avatars").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        avatar = document.toObject(Avatar.class);
                        assert avatar != null;
                        if(avatar != null && avatar.getHistoriqueDesVoyagesId()!= null && !avatar.getHistoriqueDesVoyagesId().isEmpty()){
                            db.collection("voyage").whereIn(FieldPath.documentId(), avatar.getHistoriqueDesVoyagesId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        voyageList.clear();
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            voyageList.add(document.toObject(Voyage.class));
                                        }
                                        adapter.setItems(voyageList);
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(HistoriqueVoyagesActivity.this,"Aucun voyage",Toast.LENGTH_LONG).show();

                        }
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
        Intent menuActivity = new Intent(HistoriqueVoyagesActivity.this, MenuActivity.class);
        startActivity(menuActivity);
        finish();
    }

    public void goToLogin() {
        FirebaseAuth.getInstance().signOut();
        Intent loginActivity = new Intent(HistoriqueVoyagesActivity.this, LoginActivity.class);
        startActivity(loginActivity);
        finish();
    }
}