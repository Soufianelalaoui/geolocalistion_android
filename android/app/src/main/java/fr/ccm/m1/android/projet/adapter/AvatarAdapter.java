package fr.ccm.m1.android.projet.adapter;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import fr.ccm.m1.android.projet.activity.AvatarsSurMonTelActivity;
import fr.ccm.m1.android.projet.databinding.ItemAvatarBinding;
import fr.ccm.m1.android.projet.model.Avatar;

public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.UserViewHolder> {

    private static final String TAG = "AvatarAdapter";

    List<Avatar> avatarList;
    private AvatarsSurMonTelActivity mContext;

    public AvatarAdapter(List<Avatar> avatarList,Context context) {

        this.avatarList = avatarList;
        this.mContext = (AvatarsSurMonTelActivity)context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemAvatarBinding itemAvatarBinding = ItemAvatarBinding.inflate(layoutInflater, parent, false);
        return new UserViewHolder(itemAvatarBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        final Avatar avatar = avatarList.get(position);
        holder.itemAvatarBinding.setAvatar(avatar);
        holder.itemAvatarBinding.setActivity(mContext);
        holder.itemAvatarBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return avatarList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        ItemAvatarBinding itemAvatarBinding;

        public UserViewHolder(@NonNull ItemAvatarBinding itemAvatarBinding) {
            super(itemAvatarBinding.getRoot());
            this.itemAvatarBinding = itemAvatarBinding;
        }
    }

    public void setItems(List<Avatar> avatarList) {
        this.avatarList = avatarList;
        notifyDataSetChanged();

    }
}


