package com.example.staysafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
Button login;
EditText email,pass;
TextView signup;
String lemail,lpass;
CheckBox checkBox;
   // private FirebaseAuth auth;
    String latitude,longitude;
    private FirebaseAuth auth;
    private FirebaseUser curUser;
    DocumentSnapshot doc;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth=FirebaseAuth.getInstance();
        login=findViewById(R.id.login);
        email=findViewById(R.id.email);
        pass=findViewById(R.id.password);
        signup=findViewById(R.id.LsignUp);
        checkBox=findViewById(R.id.isadmin);
        login.setOnClickListener(log);
        signup.setOnClickListener(sign);
        //auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
    }


    View.OnClickListener sign=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(MainActivity.this,Signup.class);
            startActivity(intent);
        }
    };



    View.OnClickListener log=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            lemail=email.getText().toString();
            lpass=pass.getText().toString();

            if(checkBox.isChecked()){
                adminLogin(lemail,lpass);
            }else{
                firebaseLogin(lemail,lpass);
            }

        }
    };

    private void firebaseLogin(String lemail, String lpass) {
        auth.signInWithEmailAndPassword(lemail,lpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    curUser=auth.getCurrentUser();
                    Toast.makeText(getApplicationContext(),"Login Success!",Toast.LENGTH_LONG).show();
                    updateUI(curUser);
                }else{
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        Toast.makeText(getApplicationContext(),"Email not exist!",Toast.LENGTH_LONG).show();
                        email.getText().clear();
                        pass.getText().clear();
                        email.setError("Email not exist!");
                        email.requestFocus();
                        return;
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        Toast.makeText(getApplicationContext(),"Wrong Password!",Toast.LENGTH_LONG).show();
                        pass.getText().clear();
                        pass.setError("Wrong Password!");
                        pass.requestFocus();
                        return;
                    }catch (Exception e ){
                        Toast.makeText(getApplicationContext(),"Login Failed!",Toast.LENGTH_LONG).show();
                    }
                }


            }
        });

    }

    private void adminLogin(String lemail, String lpass) {
        if(lemail.equals("saumik")&& lpass.equals("1234")){
            Intent intent =new Intent(MainActivity.this,Adminhome.class);
            startActivity(intent);
        }else {
            Toast.makeText(getApplicationContext(),"Wrong Credentials",Toast.LENGTH_LONG).show();

        }

    }

    public void updateUI(FirebaseUser fUser){
        DocumentReference docref=db.collection("User").document(curUser.getUid());
        docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    doc=task.getResult();
                    if(doc.exists()){
                        System.out.println("Document Snapshot :"+doc.getData());
                        latitude=(String) doc.get("Lat").toString();
                        longitude=(String) doc.get("Long").toString();
                        String date= (String) doc.get("Qdate");
                        String edate= (String) doc.get("edate");
                        String area=(String)doc.get("area");
                        String address=(String)doc.get("address");
                        String fname=(String)doc.get("Fname");
                        String lname=(String)doc.get("Lname");
                       // String hadsress = (Strng) doc.get("LatLong");

                        Intent intent=new Intent(MainActivity.this,Home.class);
                        intent.putExtra("latitude",latitude);
                        intent.putExtra("longitude",longitude);
                        intent.putExtra("qdate",date);
                        intent.putExtra("area",area);
                        intent.putExtra("address",address);
                        intent.putExtra("fname",fname);
                        intent.putExtra("lname",lname);
                        intent.putExtra("edate",edate);
                       // intent.putExtra("haddress",hadsress);
                        startActivity(intent);
                        // Double la,lg;
                        // la=doc.get("User/LatLong/latitude/").toString()
                        Toast.makeText(getApplicationContext(),""+date+""+longitude,Toast.LENGTH_LONG).show();
                        //  textName.setText("Welcome "+doc.get("Name"));

                    }
                }
            }
        });
//        Intent intent=new Intent(MainActivity.this,Home.class);
//        startActivity(intent);
       /* navController= Navigation.findNavController(getActivity(),R.id.nav_host_fragment);
        Bundle bundle=new Bundle();
        bundle.putParcelable("User",fUser);
        navController.navigate(R.id.dashboardfragment,bundle);*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        curUser=auth.getCurrentUser();
        if(curUser!=null){
        updateUI(curUser);
        }
    }
}
