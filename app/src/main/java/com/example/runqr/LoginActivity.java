package com.example.runqr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.hash.Hashing;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

/** Represents login screen. When yser opens the app, he has two options:
 * 1) Create a new valid user (username should be at least 5 characters and must be unique. Email should also follow appropriate format.)
 * 2) Login using qr code identifier attached to a player (is not yet implemented).
 * This activity stores a newly created player to the database.
 * This activity stores a unique identifier to the phone so that if user is already logged in, he could directly move to main activity.
 * This activity sends the newest player information from database to the main activity if user has an existing account on the device.
 */

// COMMENT: Initializing with PlayerStats object is giving errors with opening app, need to make getter/setter for private attributes
// For now: run without adding PlayerStats to currentPLayer

public class LoginActivity extends AppCompatActivity implements LoginWithQRFragment.OnFragmentInteractionListener {

    String hashUsername;
    Boolean emailExists;
    Boolean usernameExists;
    FirebaseFirestore db;
    Button loginQR;
    Button signupButton;
    TextView usernameMessage;
    EditText email;
    TextView validEmailTextView;
    EditText username;
    Player currentPlayer;
    final String TAG = "Sample";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loadData();
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Accounts");
        final CollectionReference collectionIdentifier = db.collection("Identifiers");
        if(hashUsername!=""){
            Log.d(TAG, hashUsername);
            DocumentReference identifiersRef = db.collection("Identifiers").document(hashUsername);
            identifiersRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String user = (String) document.get("username");
                            DocumentReference userRef = db.collection("Accounts").document(user);
                            Log.d(TAG, user);
                            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            currentPlayer = document.toObject(Player.class);
                                        } else {
                                            Log.d(TAG, "No such document");
                                        }
                                    } else {
                                        Log.d(TAG, "get failed with ", task.getException());
                                    }
                                }
                            });

                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
            savePlayer();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            kill_activity();
        }
        else {
            usernameExists = Boolean.FALSE;
            emailExists = Boolean.FALSE;
            loginQR = findViewById(R.id.login_qr_button);
            signupButton = findViewById(R.id.signup_button);
            email = findViewById(R.id.email);
            usernameMessage = findViewById(R.id.sizeError);
            username = findViewById(R.id.username);
            validEmailTextView = findViewById(R.id.validEmail);
            username.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {

                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String usernameText = username.getText().toString();
                    if (usernameText.length() < 5 && usernameText.length()>0 ) {
                        usernameMessage.setText("Username should contain at least 5 symbols");
                        usernameMessage.setVisibility(View.VISIBLE);
                    } else if(usernameText.length() >= 5){
                        usernameMessage.setVisibility(View.INVISIBLE);
                        DocumentReference docRef = db.collection("Accounts").document(usernameText);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        usernameMessage.setText("This username is used");
                                        usernameMessage.setVisibility(View.VISIBLE);
                                        usernameExists = Boolean.FALSE;
                                        Log.d(TAG, "No such document");
                                    } else {
                                        usernameExists = Boolean.TRUE;
                                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });
                    }
                }
            });

            email.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {

                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String emailText = email.getText().toString();
                    if (emailText.length()>0) {
                        if (isValidEmailId(emailText.trim())) {
                            emailExists = Boolean.TRUE;
                            validEmailTextView.setVisibility(View.INVISIBLE);
                        } else {
                            validEmailTextView.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });

            signupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String usernameData = username.getText().toString();
                    final String emailData = email.getText().toString();
                    Log.d(TAG, "Here!");
                    HashMap<String, PlayerStats> data = new HashMap<>();
                    if (usernameExists && emailExists) {
                        //PlayerStats newStats = new PlayerStats(null, null, 0, 0, null, null, null);
                        //PlayerStats newStats = new PlayerStats(0, 0);
                        //    public PlayerStats(QRCode highQR, QRCode lowQR, Integer sumScores, Integer numScanned, Integer rankHighQR,
                        //                       Integer rankNumScanned, Integer rankSumScores) {


                        PlayerStats newStats = new PlayerStats(usernameData, null,null,0,0,0,0,0);

                        //PlayerStats newStats = new PlayerStats(usernameData);

                        Account newAccount = new Account(usernameData, emailData);
                        QRLibrary newLibrary = new QRLibrary(new ArrayList<QRCode>(),0 );
                        currentPlayer = new Player(newAccount, newStats, newLibrary);
                        //currentPlayer = new Player(newAccount, newLibrary);
                        //currentPlayer = new Player(newAccount);
                        data.put("playerStats", newStats);
                        //data.put("playerAccount", newAccount);
                        //data.put("playerQRLibrary", newLibrary);
                        collectionReference
                                .document(usernameData)
                                .set(currentPlayer)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // These are a method which gets executed when the task is succeeded
                                        Log.d(TAG, "Data has been added successfully!");
                                        hashUsername = hashUsernameToQR(usernameData);
                                        HashMap<String, String> identifierData = new HashMap<>();
                                        identifierData.put("username", usernameData);
                                        collectionIdentifier
                                                .document(hashUsername)
                                                .set(identifierData)
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

                                        saveData();
                                        savePlayer();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        kill_activity();
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
                }
            });


            loginQR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openLoginWithQRFragment();
                }
            });
        }


    }

    public void openLoginWithQRFragment(){
        // open addQRFragment to scan QRcode and add it to player's account
        //addQR.setVisibility(View.GONE);

        final FloatingActionButton searchLocationsMap = findViewById(R.id.searchLocationsBtn);


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        LoginWithQRFragment loginQRFragment = new LoginWithQRFragment();
        //fragmentTransaction.add(R.id.addQRFragment_container,addQRFragment);\
        fragmentManager.beginTransaction()
                .replace(R.id.frameLayout, loginQRFragment).commit();
        //addQR.setVisibility(View.VISIBLE);

        //getSupportFragmentManager().beginTransaction().add(R.id.container, new AddQRFragment()).commit();

        //final View addQR = findViewById(R.id.fragment_container_view);
        //addQR.setVisibility(View.VISIBLE);





        //AddQRFragment addQRFragment = new AddQRFragment();
        //FragmentManager manager = getFragmentManager();
        //FragmentTransaction transaction = manager.beginTransaction();
        //transaction.add(R.id.fragment_container_view,AddQRFragment.class,"OPEN_SCANNER");
        //transaction.replace(R.id.container,);
        //transaction.addToBackStack(null);
        //transaction.commit();

    }

    public void onConfirmPressed(QRCode qrCodeData) {
        //String test = qrCodeData.getHash();




        // THIS NEEDS TO BE UPDATED BY KENNY
        // Below: open activity/fragment which prompts user to access their device's location and take photo of the object containing scannedQRCode



    }


    private boolean isValidEmailId(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    public String hashUsernameToQR(String rawContent){

        // do hashing here
        String sha256hex = Hashing.sha256()
                .hashString(rawContent, StandardCharsets.UTF_8)
                .toString();
        //this.hash = sha256hex;
        return sha256hex;
    }

    void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json =gson.toJson(hashUsername);
        editor.putString("hash username", json);
        editor.apply();
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

    void kill_activity()
    {
        finish();
    }

}
