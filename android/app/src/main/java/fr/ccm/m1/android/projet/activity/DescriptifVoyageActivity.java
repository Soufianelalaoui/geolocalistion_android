package fr.ccm.m1.android.projet.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fr.ccm.m1.android.projet.R;
import fr.ccm.m1.android.projet.adapter.LocalisationAdapter;
import fr.ccm.m1.android.projet.adapter.VoyageAdapter;
import fr.ccm.m1.android.projet.databinding.ActivityDescriptifVoyageBinding;
import fr.ccm.m1.android.projet.model.Avatar;
import fr.ccm.m1.android.projet.model.Localisation;
import fr.ccm.m1.android.projet.model.Voyage;

public class DescriptifVoyageActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Localisation> localisationList = new ArrayList<>();
    ArrayList<String> localisations;
    LocalisationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localisations = getIntent().getStringArrayListExtra("localisations");
        ActivityDescriptifVoyageBinding activityDescriptifVoyageBinding = DataBindingUtil.setContentView(this, R.layout.activity_descriptif_voyage);
        adapter = new LocalisationAdapter(localisationList, this);
        activityDescriptifVoyageBinding.recyclerView.setAdapter(adapter);
        activityDescriptifVoyageBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        activityDescriptifVoyageBinding.recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        if (localisations.isEmpty()) {
            Toast.makeText(DescriptifVoyageActivity.this, "pas de localisation", Toast.LENGTH_LONG).show();
        }
    }


    public void recupereLocalisationList() {
        if(!localisations.isEmpty()){
            db.collection("localisation").whereIn(FieldPath.documentId(), localisations).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        localisationList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            localisationList.add(document.toObject(Localisation.class));
                        }
                        adapter.setItems(localisationList);
                    }
                }
            });
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        recupereLocalisationList();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goToMenu();
    }

    public void goToMenu() {
        Intent menuActivity = new Intent(DescriptifVoyageActivity.this, MenuActivity.class);
        startActivity(menuActivity);
        finish();
    }
}