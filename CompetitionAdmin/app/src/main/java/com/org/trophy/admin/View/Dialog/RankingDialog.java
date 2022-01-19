package com.org.trophy.admin.View.Dialog;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.org.trophy.admin.Base.BaseDialog;
import com.org.trophy.admin.Helper.HttpHelper;
import com.org.trophy.admin.Helper.LocationHelper;
import com.org.trophy.admin.R;
import com.org.trophy.admin.databinding.DialogRankingBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class RankingDialog extends BaseDialog {
    DialogRankingBinding bind;
    RankingListener mListener;
    List<RadioButton> radioButtons;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        bind();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(bind.getRoot());
        return builder.create();
    }
    private void bind(){
        bind = DialogRankingBinding.inflate(Objects.requireNonNull(getActivity()).getLayoutInflater());
        bind.closeBtn.setOnClickListener(this);
        bind.updateBtn.setOnClickListener(this);
        radioButtons = new ArrayList<>();
        radioButtons.add(bind.bronze);
        radioButtons.add(bind.silver);
        radioButtons.add(bind.gold);
        radioButtons.add(bind.platinum);
        radioButtons.add(bind.diamond);
        for (RadioButton button : radioButtons){
            button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) processRadioButtonClick(buttonView);
                }
            });

        }
    }
    public interface RankingListener{
        void onSelect(int option);
    }
    public void setListener(RankingListener listener){
        this.mListener = listener;
    }
    @Override
    public void onClick(View view){
        if(view == bind.closeBtn){
            dismiss();
        }else if(view == bind.updateBtn){
            if(mListener != null){
                int option = 0;
                if(bind.bronze.isChecked()){
                    option = 1;
                }else if(bind.silver.isChecked()){
                    option = 2;
                }else if(bind.gold.isChecked()){
                    option = 3;
                }else if(bind.platinum.isChecked()){
                    option = 4;
                }else if(bind.diamond.isChecked()){
                    option = 5;
                }
                mListener.onSelect(option);
            }
            dismiss();
        }
    }

    private void processRadioButtonClick(CompoundButton buttonView){
        for (RadioButton button : radioButtons){
            if (button != buttonView ) button.setChecked(false);
        }
    }
}
