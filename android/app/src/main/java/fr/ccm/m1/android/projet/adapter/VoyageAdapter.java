package fr.ccm.m1.android.projet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fr.ccm.m1.android.projet.activity.HistoriqueVoyagesActivity;
import fr.ccm.m1.android.projet.databinding.ItemVoyageBinding;
import fr.ccm.m1.android.projet.model.Voyage;

public class VoyageAdapter extends RecyclerView.Adapter<VoyageAdapter.UserViewHolder>{

    private static final String TAG = "VoyageAdapter";

    List<Voyage> voyageList;
    private HistoriqueVoyagesActivity mContext;

    public VoyageAdapter(List<Voyage> voyageList, Context context) {

        this.voyageList = voyageList;
        this.mContext = (HistoriqueVoyagesActivity) context;
    }

    @NonNull
    @Override
    public VoyageAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemVoyageBinding itemVoyageBinding = ItemVoyageBinding.inflate(layoutInflater, parent, false);
        return new UserViewHolder(itemVoyageBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull VoyageAdapter.UserViewHolder holder, int position) {
        final Voyage voyage = voyageList.get(position);
        holder.itemVoyageBinding.setVoyage(voyage);
        holder.itemVoyageBinding.setActivity(mContext);
        holder.itemVoyageBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return voyageList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        ItemVoyageBinding itemVoyageBinding;

        public UserViewHolder(@NonNull ItemVoyageBinding itemVoyageBinding) {
            super(itemVoyageBinding.getRoot());
            this.itemVoyageBinding = itemVoyageBinding;
        }
    }

    public void setItems(List<Voyage> voyageList) {
        this.voyageList = voyageList;
        notifyDataSetChanged();

    }
}
