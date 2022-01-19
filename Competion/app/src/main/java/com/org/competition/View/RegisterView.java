package com.org.competition.View;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.org.competition.Base.BaseActivity;
import com.org.competition.Firebase.Firebase;
import com.org.competition.Helper.StringHelper;
import com.org.competition.Helper.Utils;
import com.org.competition.Model.User;
import com.org.competition.R;
import com.org.competition.databinding.ActivityLoginBinding;
import com.org.competition.databinding.ActivityRegisterBinding;

import org.apache.commons.validator.routines.EmailValidator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterView extends BaseActivity {
    ActivityRegisterBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        initView();
    }
    private void initView(){
        bind.birthday.setOnClickListener(this);
        bind.registerBtn.setOnClickListener(this);
        bind.closeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == bind.closeBtn){
            finish();
        }else if(view == bind.birthday){
            setBirthday();
        }else if(view == bind.registerBtn){
            createAccount();
        }
    }
    private void setBirthday(){
        final Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
                bind.birthday.setText(format.format(newDate.getTime()));
            }

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private User getUser(){
        String name = bind.name.getText().toString().trim();
        String email = bind.email.getText().toString().trim();
        String phone = bind.phone.getText().toString().trim();
        String birth = bind.birthday.getText().toString().trim();
        String gender = "male";
        if(bind.femaleOption.isChecked()){
            gender = "female";
        }
        String password = bind.password.getText().toString().trim();
        String confirm_password = bind.confirmPassword.getText().toString().trim();

        //validate user input
        if (StringHelper.isEmpty(name)) {
            bind.name.setError("Please enter name.");
            return null;
        }
        if (StringHelper.isEmpty(email)) {
            bind.email.setError("Please enter email.");
            return null;
        }
        if (!EmailValidator.getInstance().isValid(email)) {
            bind.email.setError("Invalid email format.");
            return null;
        }
        if (StringHelper.isEmpty(phone)) {
            bind.phone.setError("Please enter phone number.");
            return null;
        }
        if (StringHelper.isEmpty(birth)) {
            bind.birthday.setError("Please select birthday.");
            return null;
        }
        if (StringHelper.isEmpty(password)) {
            bind.password.setError("Please enter password.");
            return null;
        }
        if(password.length() < 6){
            bind.password.setError("Please enter more than 6 length.");
            return null;
        }
        if (!password.equals(confirm_password)) {
            bind.password.setError("Password is not matched.");
            return null;
        }
        User model = new User();
        model.setEmail(email);
        model.setName(name);
        model.setPhone(phone);
        model.setGender(gender);
        model.setBirth(birth);

        return model;
    }
    //create firebase account
    private void createAccount() {
        if(!Utils.isNetworkAvailable(context)){
            showMessage(getString(R.string.no_network));
            return;
        }
        User user = getUser();
        if(user == null) return;
        String pass = bind.password.getText().toString().trim();

        dialogHelper.showProgressDialog();
        //create firebase email account
        mAuth.createUserWithEmailAndPassword(user.getEmail(), pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    currentUser = mAuth.getCurrentUser();
                    String uid = currentUser.getUid();
                    user.setId(uid);
                    //add user to db
                    addUser(user);
                } else {
                    // account creation failed
                    dialogHelper.closeDialog();
                    String error = task.getException().getMessage();
                    showMessage(error);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage("Fail to register !");
                dialogHelper.closeDialog();
            }
        });
    }
    //add user information to firebase user table
    private void addUser(User model) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Firebase.USER).child(model.getId());
        // add post data to firebase database
        myRef.setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                dialogHelper.closeDialog();
                showMessage("Success to register !");
                startIntentAsCleanMode(UploadProfileView.class);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage("Fail to register !");
                dialogHelper.closeDialog();
            }
        });
    }
}
