package com.example.staysafe;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {

    private static final String TAG = "" ;
    private FirebaseAuth mAuth;
    Button signup;
    EditText fname,lname,email,passsword, from,area;
    FirebaseAuth mFirebaseAuth;
    String lang,lat,address;
    String uemail,upass,ufname,ulname,ufrom, uarea;
    private FirebaseFirestore db;
    LatLng latlang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        String apiKey="AIzaSyDzkhBJZpa16X7NMsbveeggrcSGfT-IsH0";
        signup=findViewById(R.id.signup);
        fname=findViewById(R.id.fname);
        lname=findViewById(R.id.lname);
        email=findViewById(R.id.semail);
        passsword=findViewById(R.id.spassword);
        from =findViewById(R.id.rfrom);
        area =findViewById(R.id.area);
        signup.setOnClickListener(sign);
        Places.initialize(getApplicationContext(), apiKey);
        mFirebaseAuth = FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

// Create a new Places client instance
        PlacesClient placesClient = Places.createClient(this);
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

// Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

// Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                  latlang =place.getLatLng();
                    lang= ""+place.getLatLng().longitude;
                    lat=""+place.getLatLng().latitude;
                    address=place.getName();
                Toast.makeText(getApplicationContext(),""+place.getLatLng(),Toast.LENGTH_LONG).show();

            }



            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

    }


    View.OnClickListener sign=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            uemail=email.getText().toString();
            upass=passsword.getText().toString();
            ufname=fname.getText().toString();
            ulname=lname.getText().toString();
            ufrom=from.getText().toString();
            uarea=area.getText().toString();

            Toast.makeText(getApplicationContext(),"clicked"+uemail+""+upass,Toast.LENGTH_LONG).show();
            if(uemail.isEmpty()){
                email.setError("Please enter email id");
                email.requestFocus();
            }
            else  if(upass.isEmpty()){
                passsword.setError("Please enter your password");
                passsword.requestFocus();
            }
            else  if(uemail.isEmpty() && upass.isEmpty()){
                Toast.makeText(Signup.this,"Fields Are Empty!",Toast.LENGTH_SHORT).show();
            }
            else  if(!(uemail.isEmpty() && upass.isEmpty())){

                mFirebaseAuth.createUserWithEmailAndPassword(uemail, upass).addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){

                            Toast.makeText(Signup.this,"SignUp Unsuccessful, Please Try Again",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Map<String,Object> usermap=new HashMap<>();
                            usermap.put("Email",uemail);
                            usermap.put("Fname",ufname);
                            usermap.put("Lname",ulname);
                            usermap.put("From",ufrom);
                            usermap.put("Lat",lat);
                            usermap.put("Long",lang);
                            usermap.put("LatLong",latlang);
                            usermap.put("area", uarea);
                            LocalDate l_date = LocalDate.now();
                            String date=(String) l_date.toString();
                            LocalDate edate =  l_date.plusDays(14);
                            String uedate=(String) edate.toString();
                            usermap.put("Qdate",date);
                            usermap.put("edate",uedate);
                            usermap.put("address",address);
                            db.collection("User").document(mFirebaseAuth.getCurrentUser().getUid()).set(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getApplicationContext().getApplicationContext(),"Register Success!",Toast.LENGTH_LONG).show();
                                        Toast.makeText(getApplicationContext().getApplicationContext(),"Please varify your Email address!",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                          //  mFirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(Signup.this,MainActivity.class));

                        }
                    }
                });
            }
            else{
                Toast.makeText(Signup.this,"Error Occurred!",Toast.LENGTH_SHORT).show();

            }
        }
    };

}




