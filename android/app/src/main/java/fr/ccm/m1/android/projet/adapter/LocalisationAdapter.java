package fr.ccm.m1.android.projet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fr.ccm.m1.android.projet.activity.AvatarsSurMonTelActivity;
import fr.ccm.m1.android.projet.activity.DescriptifVoyageActivity;
import fr.ccm.m1.android.projet.databinding.ItemAvatarBinding;
import fr.ccm.m1.android.projet.databinding.ItemLocalisationBinding;
import fr.ccm.m1.android.projet.model.Avatar;
import fr.ccm.m1.android.projet.model.Localisation;

public class LocalisationAdapter extends RecyclerView.Adapter<LocalisationAdapter.UserViewHolder> {

    List<Localisation> localisationList;
    private DescriptifVoyageActivity mContext;

    public LocalisationAdapter(List<Localisation> localisationList, Context context) {

        this.localisationList = localisationList;
        this.mContext = (DescriptifVoyageActivity)context;
    }

    @NonNull
    @Override
    public LocalisationAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemLocalisationBinding itemLocalisationBinding = ItemLocalisationBinding.inflate(layoutInflater, parent, false);
        return new LocalisationAdapter.UserViewHolder(itemLocalisationBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalisationAdapter.UserViewHolder holder, int position) {
        final Localisation localisation = localisationList.get(position);
        holder.itemLocalisationBinding.setLocalisation(localisation);
        holder.itemLocalisationBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return localisationList.size();
    }



    class UserViewHolder extends RecyclerView.ViewHolder {

        ItemLocalisationBinding itemLocalisationBinding;

        public UserViewHolder(@NonNull ItemLocalisationBinding itemLocalisationBinding) {
            super(itemLocalisationBinding.getRoot());
            this.itemLocalisationBinding = itemLocalisationBinding;
        }
    }

    public void setItems(List<Localisation> localisationList) {
        this.localisationList = localisationList;
        notifyDataSetChanged();

    }
}
