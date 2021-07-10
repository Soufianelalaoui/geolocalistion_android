package fr.ccm.m1.android.projet.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import fr.ccm.m1.android.projet.R;
import fr.ccm.m1.android.projet.databinding.ActivityConfigAvatarBinding;
import fr.ccm.m1.android.projet.model.Avatar;
import fr.ccm.m1.android.projet.model.Localisation;

public class ConfigAvatarActivity extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static final String TAG = "CONFIGURATION ACTIVITY";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public final ObservableField<Integer> tempsVoyage = new ObservableField<>();
    public final ObservableField<Integer> tempsTelephone = new ObservableField<>();
    public final ObservableField<Integer> frequenceCollecte = new ObservableField<>();
    private boolean enVoyage = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_avatar);
        if(mAuth.getCurrentUser() == null){
            goToLogin();
        }else {
            ActivityConfigAvatarBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_config_avatar);
            binding.setActivity(this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() == null){
            goToLogin();
        }else{
            recupereAvatar();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goToMenu();
    }

    public void goToMenu() {
        Intent menuActivity = new Intent(ConfigAvatarActivity.this, MenuActivity.class);
        startActivity(menuActivity);
        finish();
    }

    public void goToLogin() {
        FirebaseAuth.getInstance().signOut();
        Intent loginActivity = new Intent(ConfigAvatarActivity.this, LoginActivity.class);
        startActivity(loginActivity);
        finish();
    }

    public void recupereAvatar(){
        DocumentReference docRef = db.collection("avatars").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Avatar avatar = document.toObject(Avatar.class);
                        assert avatar != null;
                        enVoyage=avatar.isEnVoyage();
                        tempsVoyage.set(avatar.getTempsDuVoyage());
                        tempsTelephone.set(avatar.getTempsSurUnTelephone());
                        frequenceCollecte.set(avatar.getFrequenceCollecteLocalisation());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void modifier(){
        if(enVoyage){
            Toast.makeText(ConfigAvatarActivity.this,"ERREUR : l'avatar est en voyage, on ne peu pas modifier sa configuration",Toast.LENGTH_LONG).show();
            return;
        }
        if(tempsTelephone.get() <= 0 || tempsVoyage.get() <= 0 || frequenceCollecte.get() <= 0){
            Toast.makeText(ConfigAvatarActivity.this,"ERREUR : les valeurs doivent être supérieur à 0",Toast.LENGTH_LONG).show();
            return;
        }
        db.collection("avatars").document(mAuth.getCurrentUser().getUid())
                .update(
                        "frequenceCollecteLocalisation",frequenceCollecte.get(),
                        "tempsDuVoyage",tempsVoyage.get(),
                        "tempsSurUnTelephone",tempsTelephone.get()
                ).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ConfigAvatarActivity.this,"configuration validé",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ConfigAvatarActivity.this,"Erreur serveur",Toast.LENGTH_LONG).show();
                    }
                });
    }
}