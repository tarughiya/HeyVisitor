package com.example.heyvisitor;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
import java.util.Objects;
import java.util.TimeZone;


public class VisitorActivity extends AppCompatActivity {
    EditText visitorName,visitorEmail,visitorPhone;
    Spinner hostname;
    Button checkIn;
    private static final String TAG = "MY OUTPUT";


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visitor);
        visitorName=findViewById(R.id.vname);
        visitorEmail=findViewById(R.id.vemail);
        visitorPhone=findViewById(R.id.vphone);
        checkIn=findViewById(R.id.checkin);
        hostname = findViewById(R.id.hostname);

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        final List<String> host = new ArrayList<String>();

        final ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, host );
        areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hostname.setAdapter(areasAdapter);

        db.collection("Host").addSnapshotListener(this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                progress.dismiss();
                List<String> temp_host = new ArrayList<String>();
                if(queryDocumentSnapshots!=null){
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                        temp_host.add(document.getData().toString());
                    }
                    if(temp_host.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "No hosts found", Toast.LENGTH_LONG).show();
                        finish();
                    }

                    host.clear();
                    host.addAll(temp_host);
                    areasAdapter.notifyDataSetChanged();

                }
                else if(e!=null){
                    Log.d(TAG,"Got an exception",e);
                    Toast.makeText(getApplicationContext(),"Error in fetching data!!",Toast.LENGTH_LONG).show();
                }
            }
        });

        checkIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String VName = visitorName.getText().toString().trim();
                final String VEmail = visitorEmail.getText().toString().trim();
                final int VNumber = 0;//Integer.parseInt(visitorPhone.getText().toString());



                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
                Date currentLocalTime = cal.getTime();
                DateFormat date = new SimpleDateFormat("HH:mm a");
// you can get seconds by adding  "...:ss" to it
                date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));

                final String localTime = date.format(currentLocalTime);




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
                    visitor.put("HostName",role);

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
