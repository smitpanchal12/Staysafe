package com.example.staysafe;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.SmsManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Home extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;
    private FirebaseAuth auth;
    String latitude,longitude;
    Toolbar toolbar;
    LatLng l;
    Date oldDate,newdate;
    private FirebaseUser curUser;
    DocumentSnapshot doc;
    FirebaseFirestore db;
    int cirlecounter=0;
    Date date1;
    int diff;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        curUser=auth.getCurrentUser();
        latitude=getIntent().getStringExtra("latitude");
        longitude=getIntent().getStringExtra("longitude");


        toolbar=findViewById(R.id.toolBar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent=new Intent(Home.this,MainActivity.class);
                startActivity(intent);
            }
        });
      //  readDatabase();
setdate();
if(diff==0){
    Intent i=new Intent(Home.this, Finish.class);
    startActivity(i);
finish();
}
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setdate() {
        String qdate=getIntent().getStringExtra("qdate");
        String edate=getIntent().getStringExtra("edate");

     //   SimpleDateFormat formatter = new SimpleDateFormat("yyyy/mm/dd");

        LocalDate date = LocalDate.parse(qdate);
        LocalDate date2 = LocalDate.parse(edate);
       // Toast.makeText(getApplicationContext(),""+date,Toast.LENGTH_LONG).show();
        Period period = Period.between(date, date2);
        diff = period.getDays();




        Button p1_button = (Button)findViewById(R.id.datebtn);
        p1_button.setText(""+diff+" Days left");
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
//        if (mFusedLocationClient != null) {
//            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
//        }
    }



    public void readDatabase(){

//        DocumentReference docref=db.collection("User").document(curUser.getUid());
//        docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                  doc=task.getResult();
//                    if(doc.exists()){
//                        System.out.println("Document Snapshot :"+doc.getData());
//                        latitude=(String) doc.get("Lat").toString();
//                        longitude=(String) doc.get("Long").toString();
//                   // Double la,lg;
//                   // la=doc.get("User/LatLong/latitude/").toString()
//                      Toast.makeText(getApplicationContext(),""+latitude+""+longitude,Toast.LENGTH_LONG).show();
//                      //  textName.setText("Welcome "+doc.get("Name"));
//
//                    }
//                }
//            }
//        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setMinZoomPreference(16);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(30000); // two minute interval
        mLocationRequest.setFastestInterval(30000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {


    }


    LocationCallback mLocationCallback = new LocationCallback() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();


            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

                //move map camera
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                String  area=getIntent().getStringExtra("area");
                int radius= Integer.parseInt(area);
                int minboundary=radius+100;
                int maxboundary=radius+250;
               if(cirlecounter==0){
                   Location loc2 = new Location("");
                   Double haddresslat=Double.parseDouble(latitude);
                   double haddresslong=Double.parseDouble(longitude);
                   LatLng hlatlong = new LatLng(haddresslat, haddresslong);
                   cirlecounter=cirlecounter+1;

                   Circle circle = mGoogleMap.addCircle(new CircleOptions()
                                    .center(hlatlong)
                                    .radius(minboundary)
                                    .strokeColor(Color.YELLOW)

                            );
                   Circle circle2 = mGoogleMap.addCircle(new CircleOptions()
                           .center(hlatlong)
                           .radius(maxboundary)
                           .strokeColor(Color.RED)

                   );
                }


               chekDistance(latLng,minboundary,maxboundary);
            }
        }
    };
    int counter=0;
    boolean alertinformed =false;
    boolean policeinformed=false;
    boolean onetime=false;
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void chekDistance(LatLng latLng, int min, int max) {

        Location loc1 = new Location("");
        loc1.setLatitude(latLng.latitude);
        loc1.setLongitude(latLng.longitude);
        Location loc2 = new Location("");
        loc2.setLatitude(Double.parseDouble(latitude));
        loc2.setLongitude(Double.parseDouble(longitude));
        LocalTime currentitme;

        float distance = loc1.distanceTo(loc2);
        Date Current=new Date();
        Toast.makeText(getApplicationContext(), "" + distance, Toast.LENGTH_LONG).show();
        if(distance<min && counter==1){
            onetime=true;
        }
        if (distance > min && distance < max && counter == 0 && alertinformed == false) {
            alertinformed = true;
            counter = counter + 1;



            oldDate= new Date(); // oldDate == current time
            final long hoursInMillis = 60L * 60L * 1000L;
            newdate = new Date(oldDate.getTime() +
                    (2L * hoursInMillis));


            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("You have crossed minmum Boundary first time, so we are just alerting you. At this time if you " +
                    "cross maximum boundary that is 250 meter then we will directly inform to police authority." +
                    "Next time if you cross minimum boundary then also we will inform to police authority.");
            alertDialogBuilder.setPositiveButton("ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Toast.makeText(Home.this, "You clicked ok button", Toast.LENGTH_LONG).show();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        } else if (distance > min && distance < max && counter == 1 && policeinformed == false && onetime) {
            policeinformed();
        } else if (distance > max && policeinformed == false) {
            policeinformed();
        }
    }

    public void policeinformed() {
        policeinformed=true;
        String fname=getIntent().getStringExtra("fname");
        String lname=getIntent().getStringExtra("lname");
        String address=getIntent().getStringExtra("address");
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("433551995", null, fname+" "+lname+" has broke the rule and address is "+address, null, null);
            Toast.makeText(getApplicationContext(), "Informed to authority",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(Home.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}

