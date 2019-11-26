package com.example.heyvisitor;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.example.heyvisitor.Model.Visitor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


public class VisitorActivity extends AppCompatActivity {
    EditText vname,vemail,vphone;
    Spinner hostname;
   // TextView textView;
    Button checkin;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // [END get_firestore_instance]

    // [START set_firestore_settings]
    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build();

//    FirebaseDatabase database;
//    DatabaseReference visitordatabase;
//    DatabaseReference hostdatabase;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visitor);
        vname=findViewById(R.id.vname);
        vemail=findViewById(R.id.vemail);
        vphone=findViewById(R.id.vphone);
       // textView=findViewById(R.id.vtextview);
        checkin=findViewById(R.id.checkin);


//        database=FirebaseDatabase.getInstance();
//        visitordatabase= database.getReference();
//        hostdatabase= database.getReference();


        checkin.setOnClickListener(new View.OnClickListener() {
            private static final String TAG = "";

            @Override
            public void onClick(View v) {
                final String VName = vname.getText().toString().trim();
                final String VEmail = vemail.getText().toString().trim();
                final int VNumber = 0;//Integer.parseInt(vphone.getText().toString());



                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
                Date currentLocalTime = cal.getTime();
                DateFormat date = new SimpleDateFormat("HH:mm a");
// you can get seconds by adding  "...:ss" to it
                date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));

                final String localTime = date.format(currentLocalTime);



                db.collection("Host")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        //Log.d(TAG, document.getId() + " => " + document.getData());
                                        final List<String> host = new ArrayList<String>();
                                        host.add(document.getData().toString());
                                        hostname = findViewById(R.id.hostname);
                       ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(VisitorActivity.this, android.R.layout.simple_spinner_item, host );
                        areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        hostname.setAdapter(areasAdapter);
                                        areasAdapter.add(host.toString());
                                        areasAdapter.notifyDataSetChanged();
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
                final String role = hostname.getSelectedItem().toString();

                if (VName.isEmpty() || VEmail.isEmpty()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(VisitorActivity.this);
                    alert.setTitle("Failure");
                    alert.setMessage("Fill up all the fields!");
                    alert.show();}
                else
                {
                    Map<String, Object> visitor = new HashMap<>();
                    visitor.put("Name", VName);
                    visitor.put("Email", VEmail);
                    visitor.put("Number",VNumber);
                    visitor.put("Time",localTime);
                    visitor.put("HostNmae",role);

                    db.collection("Visitor")
                            .add(visitor)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    AlertDialog.Builder alert = new AlertDialog.Builder(VisitorActivity.this);
                                    alert.setTitle("Success");
                                    alert.setMessage("Visitor added successfully!");
                                    alert.show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
//                                    Log.w(TAG, "Error adding document", e);
                                }
                            });
                }



//                hostdatabase.child("Name").addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        // Is better to use a List, because you don't know the size
//                        // of the iterator returned by dataSnapshot.getChildren() to
//                        // initialize the array
//                        final List<String> host = new ArrayList<String>();
//
//                        for (DataSnapshot hostSnapshot : dataSnapshot.getChildren()) {
//                            String hostname = hostSnapshot.child("hostName").getValue(String.class);
//                            host.add(hostname);
//                        }
//
//                        hostname = findViewById(R.id.hostname);
//                        ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(VisitorActivity.this, android.R.layout.simple_spinner_item, host );
//                        areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        hostname.setAdapter(areasAdapter);
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//                final String role = hostname.getSelectedItem().toString();


//                if (VName.isEmpty() || VEmail.isEmpty()) {
//                    AlertDialog.Builder alert = new AlertDialog.Builder(VisitorActivity.this);
//                    alert.setTitle("Failure");
//                    alert.setMessage("Fill up all the fields!");
//                    alert.show();
//                } else {
//                    visitordatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            Visitor visitor = new Visitor(VName, VEmail, VNumber, localTime,role );
//                            visitordatabase.child(VName).setValue(visitor);
//
//                            AlertDialog.Builder alert = new AlertDialog.Builder(VisitorActivity.this);
//                            alert.setTitle("Success");
//                            alert.setMessage("Visitor added successfully!");
//                            alert.show();
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
//                }
            }});


    }
}
