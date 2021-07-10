package fr.ccm.m1.android.projet.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import fr.ccm.m1.android.projet.BR;

public class Login extends BaseObservable {

    private String email;
    private String password;


    @Bindable
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
    }
    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }
}
