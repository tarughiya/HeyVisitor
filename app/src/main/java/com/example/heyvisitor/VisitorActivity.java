package com.example.heyvisitor;

import com.example.heyvisitor.Model.Host;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
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
    EditText visitorName,visitorEmail,visitorPhone,etcheckout;
    Spinner hostname;
    Button checkIn,checkout;
    private static final String TAG = "MY OUTPUT";
    String e;


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visitor);
        visitorName=findViewById(R.id.vname);
        visitorEmail=findViewById(R.id.vemail);
        visitorPhone=findViewById(R.id.vphone);
        checkIn=findViewById(R.id.checkin);
        hostname = findViewById(R.id.hostname);
        etcheckout=findViewById(R.id.etcheckout);
        checkout=findViewById(R.id.checkout);

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
                List<String> emailIds = new ArrayList<String>();
                if(queryDocumentSnapshots!=null){
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                        temp_host.add(document.getString("Name").toString());

                    }
                    if(queryDocumentSnapshots.isEmpty()){
                        Toast.makeText(getApplicationContext(), "No hosts found", Toast.LENGTH_LONG).show();
                        finish();
                    }

                    else {
                        List<Host> types = queryDocumentSnapshots.toObjects(Host.class);
                        for (Host temp : types) {
                            emailIds.add(temp.getEmail());
                            //getting each email ID and storing in a list.
                        }
                    }

                    host.clear();
                    host.addAll(temp_host);
                    host.addAll(emailIds);
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
                final int VNumber = Integer.parseInt(visitorPhone.getText().toString());



                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
                Date currentLocalTime = cal.getTime();
                DateFormat date = new SimpleDateFormat("HH:mm a");
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
                                    alert.setMessage("You're checked in! Please enter checkout time when checking out.");
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

                db.collection("Host")
                        .whereEqualTo("Name",role)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful())
                                {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                    final String  em = document.getString("Email");


                                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                                        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                                        intent.putExtra(Intent.EXTRA_EMAIL, em);
                                        intent.putExtra(Intent.EXTRA_SUBJECT, "Visitor Here!");
                                        intent.putExtra(Intent.EXTRA_TEXT, "A visitor is here having details as follows:" +
                                                " \n Name :" +VName+"\n Email:"+ VEmail+"\n Number:"+ VNumber );

                                        if (intent.resolveActivity(getPackageManager()) != null) {
                                            startActivity(intent);
                                        }

                                    }
                                }
                            }
                        });


//                            String[] TO = {e};
//                            String[] CC = {"tarughiya@gmail.com"};
//                            Intent emailIntent = new Intent(Intent.ACTION_SEND);
//                            emailIntent.setData(Uri.parse("mailto:"));
//                            emailIntent.setType("text/plain");
//
//
//                            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
//                            emailIntent.putExtra(Intent.EXTRA_CC, CC);
//                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Visitor here!");
//                            emailIntent.putExtra(Intent.EXTRA_TEXT, "A visitor is here having details as follows." +
//                                    " \n Name :" +VName+"\n Email:"+ VEmail+"\n Number:"+ VNumber );
//
//                            try {
//                                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
//                                finish();
//                                Log.i("Finished sending email...", "");
//                            } catch (android.content.ActivityNotFoundException ex) {
//                                Toast.makeText(VisitorActivity.this,
//                                        "There is no email client installed.", Toast.LENGTH_SHORT).show();
//                            }
//                    }




           }
            });


        etcheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(VisitorActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {


                        etcheckout.setText(hourOfDay + ":" + minutes);
                    }
                }, 0, 0, false);
                timePickerDialog.show();
                checkout.setVisibility(View.VISIBLE);

            }

        });


        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                    db.collection("Visitor")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful())
                                    {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                            final String vem = document.getString("Email");
                                            final String n = document.getString("Name");
                                            final String t = document.getString("Time");
                                            final String nu = document.getString("Number");
                                            final String hn = document.getString("HostName");
                                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                                            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                                            intent.putExtra(Intent.EXTRA_EMAIL,vem);
                                            intent.putExtra(Intent.EXTRA_SUBJECT, "Visitor Here!");
                                            intent.putExtra(Intent.EXTRA_TEXT, "Here are your details and You have checked out at "+ etcheckout.getText()+
                                                    " \n Name :" +n+"\n Number:"+ nu+"\n" +
                                                    "Address visited :Innovacer Office \n Checkin time:"+ t+ "\n Host visited:" +hn );


                                            if (intent.resolveActivity(getPackageManager()) != null) {
                                                startActivity(intent);
                                            }

                                        }
                                    }
                                }
                            });



            }
        });


    }
}
