package fr.ccm.m1.android.projet.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fr.ccm.m1.android.projet.broadcastReceiver.Restarter;
import fr.ccm.m1.android.projet.model.Avatar;
import fr.ccm.m1.android.projet.model.Localisation;
import fr.ccm.m1.android.projet.model.Utilisateur;

public class UpdateAvatarLocalisationService extends Service {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String utilisateurId;
    private Avatar avatar;
    private List<Localisation> localisationList = new ArrayList<>();
    public UpdateAvatarLocalisationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        utilisateurId = intent.getStringExtra("utilisateurId");
        startTimer();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stoptimertask();

        Intent broadcastIntent = new Intent();
        broadcastIntent.putExtra("serviceUtilisateur", false);
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);
    }



    private Timer timer;
    private TimerTask timerTask;
    public void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
               update();
            }
        };
        timer.schedule(timerTask, 1000*60*2, 1000*60*2); //
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public void update(){
        db.collection("avatars").document(utilisateurId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        avatar= document.toObject(Avatar.class);
                        if(avatar.isEnVoyage()){
                            if(avatar.getTempsTotalParcourru() > avatar.getTempsDuVoyage()){
                                ramener();
                            }
                            else if(avatar.getTempsParcouruSurTelephone() > avatar.getTempsSurUnTelephone()){
                                chandeDeTelephone();
                            }else {
                                updateLocalisation();

                            }
                        }else {
                            updateLocalisation();
                        }
                    }
                }
            }
        });
    }

    public void updateLocalisation(){
        if(!avatar.isEnVoyage()){
            db.collection("utilisateurs").document(utilisateurId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Utilisateur utilisateur = document.toObject(Utilisateur.class);
                            db.collection("avatars").document(utilisateurId).update("derniereLocalisationId",utilisateur.getDerniereLocalisationId());
                        }
                    }
                }
            });
        }
        else {
            db.collection("localisation").document(avatar.getDerniereLocalisationId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Localisation localisation = document.toObject(Localisation.class);
                            assert localisation != null;
                            db.collection("utilisateurs").document(localisation.getReferenceUtilisateurId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Utilisateur utilisateur = document.toObject(Utilisateur.class);
                                            assert utilisateur != null;
                                            db.collection("voyage").document(avatar.getVoyageEnCoursId()).update("trajet", FieldValue.arrayUnion(utilisateur.getDerniereLocalisationId()));
                                            db.collection("avatars").document(utilisateurId).update(
                                                    "derniereLocalisationId",utilisateur.getDerniereLocalisationId(),
                                                    "tempsTotalParcourru",FieldValue.increment(2),
                                                    "tempsParcouruSurTelephone",FieldValue.increment(2)
                                            );
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    public void chandeDeTelephone(){

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
                                localisationList.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    localisationList.add(document.toObject(Localisation.class));
                                }

                                db.collection("localisation").document(avatar.getDerniereLocalisationId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                Localisation localisation = document.toObject(Localisation.class);
                                                assert localisation != null;
                                                Localisation nouvelleLocalisation = localisation.localisationDuTelephoneLePlusProche(localisationList);
                                                db.collection("voyage").document(avatar.getVoyageEnCoursId()).update("trajet", FieldValue.arrayUnion(nouvelleLocalisation.getLocalisationId()));
                                                db.collection("avatars").document(utilisateurId).update(
                                                        "derniereLocalisationId",nouvelleLocalisation.getLocalisationId(),
                                                        "tempsTotalParcourru",FieldValue.increment(2),
                                                        "tempsParcouruSurTelephone",0
                                                );
                                                db.collection("utilisateurs").document(localisation.getReferenceUtilisateurId()).update("avatarInviteListe", FieldValue.arrayRemove(utilisateurId));
                                                db.collection("utilisateurs").document(nouvelleLocalisation.getReferenceUtilisateurId()).update("avatarInviteListe", FieldValue.arrayUnion(utilisateurId));

                                            }
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

    }

    public void ramener(){
        db.collection("localisation").document(avatar.getDerniereLocalisationId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Localisation localisation = document.toObject(Localisation.class);
                        db.collection("utilisateurs").document(localisation.getReferenceUtilisateurId()).update("avatarInviteListe", FieldValue.arrayRemove(utilisateurId));
                    }
                }
            }
        });

        db.collection("utilisateurs").document(utilisateurId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Utilisateur utilisateur = document.toObject(Utilisateur.class);
                        assert utilisateur != null;
                        db.collection("voyage").document(avatar.getVoyageEnCoursId()).update("trajet", FieldValue.arrayUnion(utilisateur.getDerniereLocalisationId()));
                        db.collection("avatars").document(utilisateurId).update(
                                "enVoyage",false,
                                "voyageEnCoursId",null,
                                "derniereLocalisationId",utilisateur.getDerniereLocalisationId(),
                                "tempsTotalParcourru",0,
                                "tempsParcouruSurTelephone",0);
                    }
                }
            }
        });


    }
}