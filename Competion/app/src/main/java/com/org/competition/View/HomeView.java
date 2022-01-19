package com.org.competition.View;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.org.competition.Base.BaseActivity;
import com.org.competition.Firebase.Firebase;
import com.org.competition.Model.User;
import com.org.competition.R;
import com.org.competition.View.Fragment.EventFragment;
import com.org.competition.View.Fragment.MapFragment;
import com.org.competition.View.Fragment.ProfileFragment;
import com.org.competition.View.Fragment.UsersFragment;
import com.org.competition.databinding.ActivityHomeBinding;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeView extends BaseActivity{
    ActivityHomeBinding bind;
    List<Fragment> fragment_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        initView();
    }
    private void initView(){
        bind.logout.setOnClickListener(this);
        initFragments();
        initBottom();
    }

    @Override
    public void onClick(View view) {
        if(view == bind.logout){
            mAuth.signOut();
            startIntentAsCleanMode(LoginView.class);
        }
    }
    //initialize 4 fragments
    private void initFragments(){
        fragment_list = new ArrayList<>();
        if(fragment_list.isEmpty()){
            fragment_list.add(new EventFragment());
            fragment_list.add(new MapFragment());
            fragment_list.add(new ProfileFragment());
            fragment_list.add(new UsersFragment());
        }
        for(int i = 0; i < fragment_list.size(); i++){
            if(i == 0){
                getSupportFragmentManager().beginTransaction().add(R.id.container, fragment_list.get(i)).show(fragment_list.get(i)).commit();
            }else{
                getSupportFragmentManager().beginTransaction().add(R.id.container, fragment_list.get(i)).hide(fragment_list.get(i)).commit();
            }
        }
    }
    //initialize bottom menus
    private void initBottom(){
        bind.bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                Fragment fragment = null;
                switch (item.getItemId()) {
                    //add action event to each bottom menu
                    case R.id.my_product:
                        bind.bottomBar.getMenu().getItem(0).setChecked(true);
                        fragment = fragment_list.get(0);
                        break;
                    case R.id.my_order:
                        bind.bottomBar.getMenu().getItem(1).setChecked(true);
                        fragment = fragment_list.get(1);
                        break;
                    case R.id.my_profile:
                        bind.bottomBar.getMenu().getItem(2).setChecked(true);
                        fragment = fragment_list.get(2);
                        break;
                    case R.id.users:
                        bind.bottomBar.getMenu().getItem(3).setChecked(true);
                        fragment = fragment_list.get(3);
                        break;
                }
                if (fragment != null) {
                    if(!fragment.isHidden()){
                        return true;
                    }
                    hideAllFragments();
                    fragmentTransaction.show(fragment).commit();
                }
                return true;
            }
        });
    }
    //hide all fragments
    private void hideAllFragments(){
        for(Fragment page : fragment_list){
            if(!page.isHidden()){
                getSupportFragmentManager().beginTransaction().hide(page).commit();
            }
        }
    }

    @Override
    public void onBackPressed() {

    }

}
