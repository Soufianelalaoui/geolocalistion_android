package fr.ccm.m1.android.projet.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import fr.ccm.m1.android.projet.service.UpdateAvatarLocalisationService;
import fr.ccm.m1.android.projet.service.UpdateUtilisateurLocalsation;

public class Restarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Broadcast Listened", "Service tried to stop");
        Toast.makeText(context, "Service restarted", Toast.LENGTH_SHORT).show();
        if(intent.getBooleanExtra("serviceUtilisateur",true)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, UpdateUtilisateurLocalsation.class));
            } else {
                context.startService(new Intent(context, UpdateUtilisateurLocalsation.class));
            }
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, UpdateAvatarLocalisationService.class));
            } else {
                context.startService(new Intent(context, UpdateAvatarLocalisationService.class));
            }
        }

    }
}
