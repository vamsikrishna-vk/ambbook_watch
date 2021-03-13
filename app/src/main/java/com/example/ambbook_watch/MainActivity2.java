package com.example.ambbook_watch;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.wear.widget.CircularProgressLayout;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.type.LatLng;

import java.util.HashMap;
import java.util.Map;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

public class MainActivity2 extends WearableActivity implements
        CircularProgressLayout.OnTimerFinishedListener, View.OnClickListener, SensorEventListener {
    private Button signOut,emergency1;
    private ConstraintLayout emergency;
    private ImageView image;
    private TextView text;
    private CircularProgressLayout circularProgress;
    private static final String TAG = "MainActivity";
    private LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationClient;


    private final CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    SensorManager mSensorManager;
    Sensor mHeartRateSensor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        startService(new Intent(MainActivity2.this,service.class));
        boolean gps_enabled = false;
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
        Log.i(TAG, "LISTENER REGISTERED.");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();

        circularProgress =
                (CircularProgressLayout) findViewById(R.id.circular_progress);
        circularProgress.setOnTimerFinishedListener(this);
        circularProgress.setOnClickListener(this);


        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity2.this, MainActivity.class));
                    finish();
                }
            }
        };
        emergency1=(Button) findViewById(R.id.button12);
        signOut = (Button) findViewById(R.id.btn_logout);
        emergency = (ConstraintLayout) findViewById(R.id.constraintLayout);
        image = (ImageView) findViewById(R.id.image_view);
        text = (TextView) findViewById(R.id.textView2);


        emergency1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request=false;
                text.setText("Do you want to book \n ambulance ");
                //requestCurrentLocation();
                System.out.print("sign out");
                text.setVisibility(View.VISIBLE);
                signOut.setVisibility(View.INVISIBLE);
                emergency.setVisibility(View.INVISIBLE);
                circularProgress.setVisibility(View.VISIBLE);
                circularProgress.setTotalTime(10000);
// Start the timer
                circularProgress.startTimer();

                System.out.println("clicked");

            }

        });
        image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                circularProgress.setVisibility(View.INVISIBLE);
                signOut.setVisibility(View.VISIBLE);
                emergency.setVisibility(View.VISIBLE);
                text.setVisibility(View.INVISIBLE);
                System.out.println("some one clicked");// User canceled, abort the action
                circularProgress.stopTimer();

            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
         request=true;
                System.out.print("sign out");

                text.setVisibility(View.VISIBLE);
                signOut.setVisibility(View.INVISIBLE);
                emergency.setVisibility(View.INVISIBLE);
                circularProgress.setVisibility(View.VISIBLE);
                circularProgress.setTotalTime(10000);
// Start the timer
                circularProgress.startTimer();


                //signOut();
            }

        });
    }


    public void signOut() {
        auth.signOut();
        startActivity(new Intent(MainActivity2.this, MainActivity.class));
        finish();
    }
boolean request;
    public void onTimerFinished(CircularProgressLayout layout) {
        final FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        if(request==true) {

            signOut();
        }
        else {
            System.out.println(("i can sign out"));
            senddata(user1.getUid());
            startActivity(new Intent(MainActivity2.this, MainActivity2.class));
        }// User didn't c
        // ancel, perform the action
    }


        // User didn't c
        // ancel, perform the action



    @Override
    public void onClick(View view) {
        System.out.println("some one clicked");
    }

    public void requestCurrentLocation() {
        Log.d(TAG, "requestCurrentLocation()");
        // Request permission

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

            // Main code
            Task<Location> currentLocationTask = fusedLocationClient.getCurrentLocation(
                    PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.getToken()
            );

            currentLocationTask.addOnCompleteListener((new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {

                    String result = "";

                    if (task.isSuccessful()) {
                        // Task completed successfully
                        Location location = task.getResult();
                        result = "Location (success): " +
                                location.getLatitude() +
                                ", " +
                                location.getLongitude();

                    } else {
                        // Task failed with an exception
                        Exception exception = task.getException();
                        result = "Exception thrown: " + exception;
                    }

                    Log.d(TAG, "getCurrentLocation() result: " + result);
                }
            }));
        } else {
            // TODO: Request fine location permission
            Log.d(TAG, "Request fine location permission.");
        }

    }
    public void onResume(){
        super.onResume();
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged - accuracy: " + accuracy);
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            String msg = "" + (int)event.values[0];

            Log.d(TAG, msg);
        }
        else
            Log.d(TAG, "Unknown sensor type");
    }

    public void senddata(String uid){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put("user_id",uid);
        user.put("user id",uid);
        user.put("name","vishnu");
        user.put("phone", "8333936499");
        user.put("emergency", true);
        user.put("latitude","13.1146264");
        user.put("longitude","77.634715");
        user.put("status","Waiting for Confirmation");
        db.collection("users").document(uid).set(user, SetOptions.merge()  ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
                Toast.makeText(getApplicationContext(),"Request Sent",Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                    }
                });


    }
}