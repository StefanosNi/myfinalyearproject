package com.org.trophy.admin.View.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.org.trophy.admin.Adapter.SpinAdapter;
import com.org.trophy.admin.Adapter.SportAdapter;
import com.org.trophy.admin.Base.BaseActivity;
import com.org.trophy.admin.Firebase.Firebase;
import com.org.trophy.admin.Helper.StringHelper;
import com.org.trophy.admin.Model.EventModel;
import com.org.trophy.admin.Model.SportModel;
import com.org.trophy.admin.Model.SportParams;
import com.org.trophy.admin.View.Dialog.LocationDialog;
import com.org.trophy.admin.databinding.ActivityAddEventBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EventAddView extends BaseActivity {
    ActivityAddEventBinding bind;
    SportAdapter adapter;
    SpinAdapter week_adapter;
    LatLng location;
    String weekname_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityAddEventBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        initView();
    }
    //initialize view
    private void initView(){
        //initialize sport dropdown
        adapter = new SportAdapter(context, SportParams.SPORTS);
        bind.sport.setAdapter(adapter);
        bind.sport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try{

                }catch (Exception e){
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        //set weekday name dropdown
        //It gets weekday name like SUN, MON, TUE, .... SAT
        weekname_2 = SportParams.WEEKS_2.get(0);
        week_adapter = new SpinAdapter(context, SportParams.WEEKS);
        bind.onceWeek.setAdapter(week_adapter);
        //add dropdown selection event
        bind.onceWeek.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try{
                    //get weekday name
                    weekname_2 = SportParams.WEEKS_2.get(position);
                }catch (Exception e){
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        //add touch event
        bind.address.setOnClickListener(this);
        bind.time.setOnClickListener(this);
        bind.startAt.setOnClickListener(this);
        bind.deadline.setOnClickListener(this);
        bind.addEvent.setOnClickListener(this);
        bind.backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == bind.addEvent){
            addEvent();
        }else if(view == bind.backBtn){
            finish();
        }else if(view == bind.time){
            showTimeSelector(bind.time);
        }else if(view == bind.startAt){
            showDateSelector(bind.startAt);
        }else if(view == bind.deadline){
            showDateSelector(bind.deadline);
        }else if(view == bind.address){
            showAddressDlg();
        }
    }
    //show date selection popup
    private void showDateSelector(final TextView date_view){
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        //Once data is selected, it gets calendar and show date with reasonable format
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                        date_view.setText(format.format(calendar.getTime()));
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
    //show time selection popup
    private void showTimeSelector(final TextView date_view){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR, selectedHour);
                calendar.set(Calendar.MINUTE, selectedMinute);
                SimpleDateFormat format = new SimpleDateFormat("HH:mm aa", Locale.getDefault());
                date_view.setText( format.format(calendar.getTime()));
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }
    //show location and address selection dialog with google map
    private void showAddressDlg(){
        LocationDialog dlg = new LocationDialog();
        dlg.setListener(new LocationDialog.LocationListener() {
            @Override
            public void onLocation(String address, LatLng latlng) {
                bind.address.setText(address);
                location = latlng;
            }
        });
        dlg.show(getSupportFragmentManager(), "Location_Dlg");
    }
    //get event model from user input
    private EventModel getEvent(){
        //get all user's input
        String title = bind.title.getText().toString().trim();
        String address = bind.address.getText().toString().trim();
        String price = bind.price.getText().toString().trim();
        String time = bind.time.getText().toString().trim();
        String duration = bind.duration.getText().toString().trim();
        String once_week = SportParams.WEEKS.get(bind.onceWeek.getSelectedItemPosition());
        String start_at = bind.startAt.getText().toString().trim();
        String deadline = bind.deadline.getText().toString().trim();

        //validate user input
        if(StringHelper.isEmpty(title)){
            bind.title.setError("Please enter title.");
            return null;
        }
//        if(StringHelper.isEmpty(address)){
//            bind.address.setError("Please select address.");
//            return null;
//        }
        if(StringHelper.isEmpty(price)){
            bind.price.setError("Please enter price.");
            return null;
        }
        if(StringHelper.isEmpty(time)){
            bind.time.setError("Please select time.");
            return null;
        }
        if(StringHelper.isEmpty(duration)){
            bind.duration.setError("Please enter duration.");
            return null;
        }
        if(StringHelper.isEmpty(start_at)){
            bind.startAt.setError("Please select start date.");
            return null;
        }
        if(StringHelper.isEmpty(deadline)){
            bind.deadline.setError("Please select deadline.");
            return null;
        }
        if(location == null){
            bind.address.setError("Please select address.");
        }
        SportModel sport = SportParams.SPORTS.get(bind.sport.getSelectedItemPosition());

        //initialize event and set values
        EventModel event = new EventModel();
        event.setTitle(title);
        event.setAddress(address);
        event.setSport(sport);
        event.setSportKey(String.valueOf(sport.getKey()));
        event.setPrice(price);
        event.setTime(time);
        event.setDuration(duration);
        event.setOnceWeek(once_week);
        event.setWeekName(weekname_2);
        event.setStartAt(start_at);
        event.setDeadline(deadline);
        event.setLat(location.latitude);
        event.setLon(location.longitude);

        return event;
    }
    //post new event to firebase
    private void addEvent(){
        EventModel event = getEvent();
        if(event == null){
            return;
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //connect to firebase event table
        DatabaseReference myRef = database.getReference(Firebase.EVENT).push();
        String key = myRef.getKey();
        event.setId(key);
        dialogHelper.showProgressDialog();
        //post new event to firebase
        myRef.setValue(event).addOnSuccessListener(new OnSuccessListener<Void>() {
            //if it is success, this callback is called
            public void onSuccess(Void aVoid) {
                dialogHelper.closeDialog();
                showMessage("Success to create event !");
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            //fail
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage("Fail to create event !");
                dialogHelper.closeDialog();
            }
        });
    }
}
