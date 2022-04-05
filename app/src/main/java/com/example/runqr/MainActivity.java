package com.example.runqr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.events.Event;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


// Main activity of the RunQR game has an app bar with 2 icons: dropdown menu and an add QR Button which opens scanner for player to scan QRCodes.
// Main activity also contains a map with a refresh button and a nearbySearch button (AYUSH can elaborate on this).
// CURRENT ISSUES:
// - after destroying app and opening again, player's QRLibrary is null and pressing QRLibrary in menu causes app to crash

// Main activity of the RunQR game has an app bar with 3 icons:
// dropdown menu
// add QR Button which opens scanner for player to scan QRCodes
// & a search icon to allow a player to search for other players on the game.
// Main activity also contains a map with a nearbySearch button that displays nearbyQRCodes in a list.



public class MainActivity extends AppCompatActivity implements AddQRFragment.OnFragmentInteractionListener, OnMapReadyCallback {


    /*
    // build fragment/popup
    static AlertDialog.Builder dialogBuilder;
    static AlertDialog dialog;
    static Button take_photo, add_geolocation, yes, no;
*/

    /// fix below to do automatic log in and save player info
    static AlertDialog.Builder dialogBuilder;
    static AlertDialog dialog;
    static Button take_photo, add_geolocation, yes, no;
    /*next two lines are needed*/
    static Player currentPlayer = new Player();
    //PlayerStats playerStats;

    final String TAG = "Sample";

    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db;
    static CollectionReference collectionReference;
    static CollectionReference QRCodesReference;
    // Create HashMaps to store QRCode and account data in Firestore.
    static HashMap<String, String> qrData = new HashMap<>();
    static HashMap<String, String> accountData = new HashMap<>();
    SearchView searchView;
    Uri QRCodePhoto;

    SupportMapFragment mapFragment;
    FloatingActionButton loadBtn;
    FloatingActionButton listBtn;
    ArrayList<Marker> markerArrayList;
    ArrayList<MarkerOptions> markerOptionsArrayList;
    GoogleMap currentMap;

    String searchQuery;
    EventListener<QuerySnapshot> AccountsQuery;


    ListenerRegistration listenerReg;
    //From Stackoverflow
    //Source:  https://stackoverflow.com/questions/21403496/how-to-get-current-location-in-google-map-android
    //By Shailendra Madda: https://stackoverflow.com/users/2462531/shailendra-madda
    FusedLocationProviderClient fusedLocationProviderClient;

    LocationRequest locationRequest;
    LocationCallback locationCallback;

    //Google. (n.d.). Retrieved April 3, 2022 from the Android Developer Website: https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial
    Places places;
    PlacesClient placesClient;
    Marker currentMarker;



    private static final int locationRequestCode = 1;
    double currentLatitude = 0.0, currentLongitude = 0.0;

    private boolean locationPermissionGranted = false;
    Location lastKnownLocation;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private CameraPosition cameraPosition;
    private double radius = 6371 * Math.pow(10,3);


    /*
    Boolean locationAdded = false;
    Boolean photoAdded = false;


     */

    String hashUsername = "";
    Boolean successFirst = false;
    Boolean successSecond = false;
    String user = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadPlayer();
        getLocationPermissions();
        Log.v("Boo", "Boo");

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }


        loadData();
        Log.v(TAG, hashUsername);
        db = FirebaseFirestore.getInstance();
        loadUsername();
        //if(successFirst == true) {
        Log.v(TAG, "AHHHH");
        Log.v("YSERRRRRRRRR", user);
        //if (successSecond == true) {
        // Get a top level reference to the collection
        collectionReference = db.collection("Accounts");
        //HashMap<String, String> accountData = new HashMap<>();

        // Creating collection for global QRCodes
        QRCodesReference = db.collection("QR Codes");

        //HashMap<String, String> qrData = new HashMap<>();


        Places.initialize(getApplicationContext(), "AIzaSyBCVF_eq7zdUk2jwTJTKK0rCOISbiLlqlY");
        placesClient = Places.createClient(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Droid By Me, Get Current location using FusedLocationProviderClient in Android, https://droidbyme.medium.com/get-current-location-using-fusedlocationproviderclient-in-android-cb7ebf5ab88e
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(5 * 1000);

        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {


                super.onLocationResult(locationResult);
                if (locationResult == null) {
                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    if (location != null && lastKnownLocation != null) {
                        Log.v("Update lat", String.valueOf(lastKnownLocation.getLatitude()));
                        Log.v("Update long", String.valueOf(lastKnownLocation.getLongitude()));
                        lastKnownLocation.setLatitude(location.getLatitude());
                        lastKnownLocation.setLongitude(location.getLongitude());


                        updateLocationLists();

                    }
                }
            }
        };

                /*
                HashMap<String, Account> accountData = new HashMap<>();
                //HashMap<String, String> accountData = new HashMap<>();
                //accountData.put("Account", currentPlayer.getPlayerAccount().getUsername());


                // The set method sets a unique id for the document
                collectionReference
                        .document("test_username")
                        .set(accountData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
        // These are a method which gets executed when the task is succeeded

                                Log.d(TAG, "Data has been added successfully!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
        // These are a method which gets executed if there’s any problem
                                Log.d(TAG, "Data could not be added!" + e.toString());
                            }
                        });



                 */
                /*QRCodesReference = db.collection("QR Codes");
                qrData.put("Location X", "53.5198");
                qrData.put("Location Y", "-113.4970");
                int random_int = (int)Math.floor(Math.random()*(100-0+1)+0);

                QRCodesReference
                        .document(String.valueOf(random_int))
                        .set(qrData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // These are a method which gets executed when the task is succeeded

                                Log.v(TAG, "Global QRData has been added successfully!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // These are a method which gets executed if there’s any problem
                                Log.v(TAG, "Global QRData could not be added!" + e.toString());
                            }
                        });*/




        /*




         */
        /*QRCodesReference = db.collection("QR Codes");
        qrData.put("Location X", "53.51863");
        qrData.put("Location Y", "-113.49429");
        int random_int = (int)Math.floor(Math.random()*(100-0+1)+0);

        QRCodesReference
                .document(String.valueOf(random_int))
                .set(qrData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // These are a method which gets executed when the task is succeeded

                        Log.v(TAG, "Global QRData has been added successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // These are a method which gets executed if there’s any problem
                        Log.v(TAG, "Global QRData could not be added!" + e.toString());
                    }
                });*/


        //Any change in the QR Codes collection in the database is noticed here and the map is updated accordingly
        //markerOptionsArrayList used to store all marker options in order to be passed into fragment to display addresses of QR Codes
        //markerArrayList is used to store Marker objects displayed on map so that each of their states can be easily manipulated
        Log.v("Here Marker", "here");
        markerOptionsArrayList = new ArrayList<MarkerOptions>();
        markerArrayList = new ArrayList<Marker>();
        /*QRCodesReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {






                if(!markerArrayList.isEmpty()){
                    for(Marker marker: markerArrayList){
                        marker.remove();
                    }}



                markerArrayList.clear();
                markerOptionsArrayList.clear();

                for(QueryDocumentSnapshot doc: queryDocumentSnapshots){



                }
            }
        });*/





        //Button to display fragment of addresses
        listBtn = findViewById(R.id.searchLocationsBtn);

        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MarkerFragment locationFragment = MarkerFragment.newInstance(markerOptionsArrayList);
                locationFragment.show(getSupportFragmentManager(), "LOCATIONS");

            }
        });



        accountData.put("Account Username", "test_username");
        Log.v("Hello", "test_message");
        collectionReference
                .document("Usernames")
                .set(accountData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // These are a method which gets executed when the task is succeeded

                        Log.v(TAG, "Data has been added successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // These are a method which gets executed if there’s any problem
                        Log.v(TAG, "Data could not be added!" + e.toString());
                    }
                });
        Log.v("Hello", "test_message");


        db = FirebaseFirestore.getInstance();
        // Get a top level reference to the collection
        collectionReference = db.collection("Accounts");
        //HashMap<String, String> accountData = new HashMap<>();

        // Creating collection for global QRCodes

        //HashMap<String, String> qrData = new HashMap<>();



        //Google. (n.d.). Retrieved March,9 2022 from the Android Developer Website: https://developers.google.com/maps/documentation/android-sdk/map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




                /*
                final Button addQR = findViewById(R.id.add_qr_button);
                addQR.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view){
                        openAddQRFragment(addQR);

                    }


                });


                */
        //}
        //}




    }



    //cite https://developer.android.com/training/location/request-updates
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (currentMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, currentMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    public void onLocationChanged(Location location){
        currentLongitude = location.getLongitude();
        currentLatitude = location.getLatitude();

    }

    @Override
    public void onStart(){
        loadPlayer();
        loadData();
        loadUsername();
        super.onStart();


    }
    @Override
    public void onStop(){
        super.onStop();

    }
    // Trying to fix null objects on second time opening the app
    @Override
    public void onPause(){
        savePlayerToDatabase();

        super.onPause();
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }

    }

    @Override
    public void onLowMemory(){
        super.onLowMemory();

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume(){
        super.onResume();
        if(lastKnownLocation == null){
            getDeviceLocation();
        }
        if(locationPermissionGranted){
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        } else {
            //Toast.makeText(this, "App will not run properly if permission denied", Toast.LENGTH_LONG).show();


        }


    }


    @Override
    public void onDestroy(){
        //savePlayer();
        savePlayerToDatabase();
        super.onDestroy();

    }


    private void getLocationPermissions(){
        // Droid By Me, Get Current location using FusedLocationProviderClient in Android, https://droidbyme.medium.com/get-current-location-using-fusedlocationproviderclient-in-android-cb7ebf5ab88e
        // check permission
        // https://github.com/droidbyme/Location/blob/master/app/src/main/java/com/coders/location/MainActivity.java
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
               ) {
            // request for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    locationRequestCode);


        } else {
            locationPermissionGranted = true;
        }
    }

    //cite https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;

        if(requestCode == locationRequestCode) {

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    locationPermissionGranted = true;

                }



            } else {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }


            Log.v("repeat", "repeat");
            updateLocationUI();

    }

    static public Player getCurrentPlayer() {return currentPlayer;}

    void savePlayerToDatabase() {
        String usernameData = currentPlayer.getPlayerAccount().getUsername();
        HashMap<String, Player> data = new HashMap<>();
        data.put("playerInfo", currentPlayer);
        collectionReference
                .document(usernameData)
                .set(currentPlayer)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // These are a method which gets executed when the task is succeeded
                        Log.d(TAG, "Data has been added successfully!");
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // These are a method which gets executed if there’s any problem
                        Log.d(TAG, "Data could not be added!" + e.toString());
                    }
                });
    }

    public void loadUsername(){
        db = FirebaseFirestore.getInstance();
        DocumentReference identifiersRef = db.collection("Identifiers").document(hashUsername);
        identifiersRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    if (document.exists()) {
                        Log.v(TAG, "IIII EXIIIST");
                        user = (String) document.get("username");
                        successFirst = true;
                        Log.v(TAG, user);
                        loadPlayerFromDB();
                    }
                }
            }
        });
    }
    void loadPlayerFromDB(){
        db = FirebaseFirestore.getInstance();
        Log.v("PLEASEEEEEEEEE", user);
        DocumentReference docRef = db.collection("Accounts").document(user);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        currentPlayer = document.toObject(Player.class);
                        //currentPlayer = objectMapper.convertValue(doc1, Player.class);

                        successSecond = true;
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
    void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("hash username", null);
        Type type = new TypeToken<String>(){}.getType();
        hashUsername = gson.fromJson(json, type);
        if(hashUsername == null){
            hashUsername = "";
        }
    }

    public interface MyCallback {
        void onCallback(List<Event> eventList);
    }


    void savePlayer(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json =gson.toJson(currentPlayer);
        editor.putString("player", json);
        editor.apply();
    }

    void loadPlayer(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("player", null);
        Type type = new TypeToken<Player>(){}.getType();
        currentPlayer = gson.fromJson(json, type);
    }



    //    public void openAddQRFragment(Button addQR){
    public void openAddQRFragment(){
        // open addQRFragment to scan QRcode and add it to player's account
        //addQR.setVisibility(View.GONE);

        final FloatingActionButton searchLocationsMap = findViewById(R.id.searchLocationsBtn);

        searchLocationsMap.setVisibility(View.GONE);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AddQRFragment addQRFragment = new AddQRFragment();
        //fragmentTransaction.add(R.id.addQRFragment_container,addQRFragment);\
        fragmentTransaction.add(R.id.addQRFragment_container, addQRFragment, "Add QR Code");
        fragmentTransaction.commit();
        //addQR.setVisibility(View.VISIBLE);

        //getSupportFragmentManager().beginTransaction().add(R.id.container, new AddQRFragment()).commit();

        //final View addQR = findViewById(R.id.fragment_container_view);
        //addQR.setVisibility(View.VISIBLE);

        searchLocationsMap.setVisibility(View.VISIBLE);




        //AddQRFragment addQRFragment = new AddQRFragment();
        //FragmentManager manager = getFragmentManager();
        //FragmentTransaction transaction = manager.beginTransaction();
        //transaction.add(R.id.fragment_container_view,AddQRFragment.class,"OPEN_SCANNER");
        //transaction.replace(R.id.container,);
        //transaction.addToBackStack(null);
        //transaction.commit();

    }



    @Override
    public void onConfirmPressed(QRCode qrCodeData) {
        //String test = qrCodeData.getHash();
        // Add QRCode to player's library
        currentPlayer.getPlayerQRLibrary().addQRCode(qrCodeData);

        /*update PlayerStats*/
        PlayerStats playerStats = currentPlayer.getPlayerStats();
        int currentCodes = playerStats.getNumOfScanned();
        playerStats.setNumOfScanned(currentCodes+ 1);
        int currentScore = playerStats.getSumOfScores();
        playerStats.setSumOfScores(currentScore + qrCodeData.getScore());
        if (playerStats.getLowQr() == null) {
            playerStats.setLowQr(qrCodeData);
        }
        else {
            if (qrCodeData.getScore() < playerStats.getLowQr().getScore()) {
                playerStats.setLowQr(qrCodeData);
            }

        }

        if (playerStats.getHighQr() == null) {
            playerStats.setHighQr(qrCodeData);
        }
        else {
            if (qrCodeData.getScore() > playerStats.getHighQr().getScore()){
                playerStats.setHighQr(qrCodeData);
            }
        }


/*
        // THIS NEEDS TO BE UPDATED BY KENNY
        // Below: open activity/fragment which prompts user to access their device's location and take photo of the object containing scannedQRCode

        dialogBuilder = new AlertDialog.Builder(this);
        final View conformationPopup = getLayoutInflater().inflate(R.layout.scanner_popup,null);

        take_photo = (Button) conformationPopup.findViewById(R.id.takePhotoButton);
        add_geolocation = (Button) conformationPopup.findViewById(R.id.addGeolocationButton);
        yes = (Button) conformationPopup.findViewById(R.id.yesButton);
        no = (Button) conformationPopup.findViewById(R.id.noButton);


        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);

            return;
        }
        android.location.Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);



        dialogBuilder.setView(conformationPopup);
        dialog = dialogBuilder.create();
        dialog.show();

        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //define Take Photo here
                openCamera(view);
            }
        });

        add_geolocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //define Geo-Location here
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                view.setX(Math.round(longitude));
                view.setY(Math.round(latitude));

            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //define Got it here
                dialog.dismiss();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //define Cancel here
                dialog.dismiss();
            }
        });


      
 */


        // call method to add location data to qrCodeCollection for nearbyQRCodeSearch algorithm
        if (qrCodeData.getLocation() != null) { // and if it's not in the database
            addQRLocationGlobally(qrCodeData, qrData, QRCodesReference );

        }


    }


    public void addQRLocationGlobally(QRCode qrCodeData,HashMap<String, String> qrData, CollectionReference QRCodesReference ) {
        // Creating collection for global QRCodes
        //final CollectionReference QRCodesReference = db.collection("QR Codes");
        //HashMap<String, String> qrData = new HashMap<>();

        qrData.put("Location X", String.valueOf(qrCodeData.getLocation().getX()));
        qrData.put("Location Y", String.valueOf(qrCodeData.getLocation().getY()));

        QRCodesReference
                .document(qrCodeData.getHash())
                .set(qrData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // These are a method which gets executed when the task is succeeded

                        Log.v(TAG, "Global QRData has been added successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // These are a method which gets executed if there’s any problem
                        Log.v(TAG, "Global QRData could not be added!" + e.toString());
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        //Google. (n.d.). Retrieved April 4, 2022 from the Android Developer Website: https://developer.android.com/training/search/setup
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search_bar).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(new ComponentName(this, UsernameListActivity.class)));
        return true;

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
            case R.id.qr_library_item:
                //Open activity to show QRLibrary
                Intent intent = new Intent(this, QRLibraryActivity.class);
                //intent.putExtra("Player QRLibrary", (Serializable) currentPlayer.getPlayerQRLibrary());
                intent.putExtra("Player QRLibraryActivity", (Serializable) currentPlayer);
                intent.putExtra("Allow Deletion?", (Serializable) true);
                startActivityForResult(intent, 1);

                /*
                //update PlayerStats to account for deletion of codes//

                // THIS NEEDS TO BE FIXED, PROFILE DOESN'T TRACK DELETION OF QRCODES //
                // Note; seems to be a delay in updating numScanned
                PlayerStats playerStats = currentPlayer.getPlayerStats();
                int currentCodes = currentPlayer.getPlayerQRLibrary().getSize();
                playerStats.setNumOfScanned(currentCodes);
                */
                break;
            //return true;

            case R.id.profile_item:
                //open player profile activity
                Intent intent1 = new Intent(this, ProfileActivity.class);
                intent1.putExtra("Display Player Profile", (Serializable) currentPlayer);
                startActivity(intent1);
                break;
            //return true;


            case R.id.add_qr_item:
                //Open fragment to scan QR code
                FloatingActionButton searchLocationsBtn;
                openAddQRFragment();
                break;
            //return true;


            case R.id.leaderboard_item:
                //Open new activity to show leaderboards
                Intent intent2 = new Intent(this, LeaderboardActivity.class);
                intent2.putExtra("Player", (Serializable) currentPlayer);
                startActivity(intent2);
                break;
            //return true;


            case R.id.add_device_item:
                Intent intent3 = new Intent(this, AddDeviceActivity.class);
                intent3.putExtra("Player AddDeviceActivity", (Serializable) currentPlayer);
                startActivity(intent3);
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed(){


        super.onBackPressed();
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                //QRLibrary updatedQRLibrary = (QRLibrary) data.getSerializableExtra("Player QRLibrary");
                Player updatedCurrentPlayer = (Player) data.getSerializableExtra("Player QRLibrary Updated");
                //currentPlayer.setPlayerQRLibrary(updatedQRLibrary);
                currentPlayer = updatedCurrentPlayer;
            }
        }
        if(requestCode == 2){
            if(resultCode == RESULT_OK){
               // QRCodePhoto = (Photo) data.getSerializableExtra("photo");
            }
        }
        if(requestCode == 3){
            if(resultCode == RESULT_OK){
                Uri selectedImage = data.getData();
                QRCodePhoto = selectedImage;
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void updateLocationUI(){
        if(currentMap == null){
            return;
        }

        try{
            if(locationPermissionGranted){
                currentMap.setMyLocationEnabled(true);
                currentMap.getUiSettings().setMyLocationButtonEnabled(true);

            }else{
                currentMap.setMyLocationEnabled(false);
                currentMap.getUiSettings().setMyLocationButtonEnabled(false);
                //lastKnownLocation = null;
                //Have to do something if user said no
            }
        } catch (SecurityException e){
            Log.e("Exception: %s", e.getMessage());
        }



    }

    public void updateLocationLists(){
        if(!markerArrayList.isEmpty()){
            for(Marker marker: markerArrayList){
                marker.remove();
            }}
        markerArrayList.clear();
        markerOptionsArrayList.clear();
        QRCodesReference
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                if(String.valueOf(doc.getId()).length() <=2 ){
                                    Log.v("id", (String)doc.getId());
                                    Log.v("x",String.valueOf(doc.getData().get("Location X")) );
                                    Log.v("y", String.valueOf(doc.getData().get("Location Y")));
                                    Float x = Float.parseFloat((String)doc.getData().get("Location X"));
                                    Float y = Float.parseFloat((String)doc.getData().get("Location Y"));
                                    double currentLatitudeRadians = Math.toRadians(lastKnownLocation.getLatitude());
                                    double locationLatitudeRadians = Math.toRadians(x);
                                    double currentLongitudeRadians = Math.toRadians(lastKnownLocation.getLongitude());
                                    double locationLongitudeRadians = Math.toRadians(y);
                                    double currentLatitude = lastKnownLocation.getLatitude();
                                    double currentLongitude = lastKnownLocation.getLongitude();
                                    double latitudeDifference = Math.toRadians(x - lastKnownLocation.getLatitude());
                                    double longitudeDifference = Math.toRadians(y - lastKnownLocation.getLongitude());

                                    double a = Math.sin(latitudeDifference/2) * Math.sin(latitudeDifference/2) + Math.cos(currentLatitudeRadians) * Math.cos(locationLatitudeRadians) * Math.sin(longitudeDifference/2) * Math.sin(longitudeDifference/2);
                                    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

                                    double distance = radius * c;

                                    if (distance <= 2000){
                                        MarkerOptions newMarkerOptions = new MarkerOptions()
                                                .position(new LatLng(x, y))
                                                .title("new Marker");
                                        Marker newMarker = currentMap.addMarker(newMarkerOptions);

                                        markerOptionsArrayList.add(newMarkerOptions);
                                        markerArrayList.add(newMarker);
                                    }


                                }
                            }
                        } else {
                            Log.e("Error", "Error fetching documents from database");
                        }
                    }
                });
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                //currentMarker.remove();
                                currentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), 9));
                                Log.v("Last known Latitude", String.valueOf(lastKnownLocation.getLatitude()));
                                Log.v("Last known Longitude", String.valueOf(lastKnownLocation.getLongitude()));
                                currentLatitude = lastKnownLocation.getLatitude();
                                currentLongitude = lastKnownLocation.getLongitude();
                                updateLocationLists();



                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            currentMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(new LatLng(53.5232, -113.5263), 9));
                            currentMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.currentMap = googleMap;
        updateLocationUI();
        Log.v("Here map", "here");
        getDeviceLocation();
        /*if(this.currentMap != null) {
            this.currentMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            this.currentMap.addMarker(new MarkerOptions()
                    .position(new LatLng(53.5232, -113.5263))
                    .title("UofA"));
            //https://stackoverflow.com/questions/57096105/google-map-not-centered-in-desired-location
            //User: https://stackoverflow.com/users/11797134/alex
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(53.631611, -113.323975)).zoom(9).build();
            this.currentMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }*/
    }


    /*
    public Bitmap openCamera(){
        Intent intent = new Intent(this, CameraActivity.class);
        startActivityForResult(intent, 2);

        //startActivityFromFragment((Fragment) AddQRFragment,intent,1000);
        //Photo getPhoto = (Photo) intent.getParcelableExtra("PhotoImage");

        return new Bitmap();
        //return QRCodePhoto.getImage();
    }

     */

    public void onPhotoCaptured(QRCode QRCodeToAddPhotoTo) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 3);

        QRCodeToAddPhotoTo.setPhoto(QRCodePhoto);
        QRCodePhoto = null;
    }







}

