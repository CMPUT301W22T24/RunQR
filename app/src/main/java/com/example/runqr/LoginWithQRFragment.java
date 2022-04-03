package com.example.runqr;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.zxing.Result;

// This class is the Fragment used to host a codeScanner which allows players to scan QRCodes and uses Hasher to hash code contents.
// The newly created QRCode is passed back to MainActivity through method onConfirmPressed and added to player's QRLibrary there.
// This class uses CodeScanner object to scan QRCodes and borrows code from: https://github.com/yuriy-budiyev/code-scanner.


public class LoginWithQRFragment extends Fragment {

    private static final int RC_PERMISSION = 10;
    private CodeScanner mCodeScanner;
    private boolean mPermissionGranted;
    private String QRString = null;
    private OnFragmentInteractionListener listener;
    FirebaseFirestore db;
    String hashUsername = "";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        }
        else {
            throw new RuntimeException(context.toString()+
                    " must implement OnFragmentInteractionListener");
        }

    }

    public interface OnFragmentInteractionListener {
        void onConfirmPressed(QRCode QRCodeToAdd);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        db = FirebaseFirestore.getInstance();
        View root = inflater.inflate(R.layout.add_qr_fragment_layout, container, false);
        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);

        Button confirmAddQR = root.findViewById(R.id.confirm_addQR_button);
        Button cancelAddQR = root.findViewById(R.id.cancel_addQR_button);

        Hasher QRCodeHasher = new Hasher();

        mCodeScanner = new CodeScanner(activity, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                // result contains the String representing the QRCode's contents
                QRString = result.getText();

                /*
                // Below code shows QRCode string contents on screen, remove this for privacy purposes
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, result.getText(), Toast.LENGTH_SHORT).show();
                    }
                });

                 */
            }
        });

        // Deal with camera permissions for scanner
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = false;
                requestPermissions(new String[] {Manifest.permission.CAMERA}, RC_PERMISSION);
            } else {
                mPermissionGranted = true;
            }
        } else {
            mPermissionGranted = true;
        }


        confirmAddQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add QRCode
                if (QRString != null){
                    DocumentReference docRef = db.collection("Identifiers").document(QRString);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    saveData();
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    startActivity(intent);
                                    kill_activity();
                                } else {
                                    Context context = getApplicationContext();
                                    CharSequence text = "Cannot login with this QRCode. Please try another!";
                                    int duration = Toast.LENGTH_SHORT;

                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
                                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });



                    // prompt user to enter geolocation and/or photo of object hosting the QRCode
                    //ignore for now


                    getFragmentManager().beginTransaction().remove(LoginWithQRFragment.this).commit();
                    /*
                    // Set's ADD QR BUTTON VISIBILITY
                    final Button addQRButton = getActivity().findViewById(R.id.add_qr_button);
                    addQRButton.setVisibility(View.VISIBLE);

                     */
                }

            }
        });

        cancelAddQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // cancel ScannerFragment and return to main activity with no changes
                getFragmentManager().beginTransaction().remove(LoginWithQRFragment.this).commit();

                /*
                // Set's ADD QR BUTTON VISIBILITY
                final Button addQRButton = getActivity().findViewById(R.id.add_qr_button);
                addQRButton.setVisibility(View.VISIBLE);

                 */


            }
        });

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
        return root;
    }


    public interface OnConfirmPressed {
        void onConfirmPressed(QRCode qrCodeData);

    }
    /*

    public void passData(QRCode data) {
        dataPasser.onConfirmPressed(data);


    }
    */



    @Override
    public void onResume() {
        super.onResume();
        if(mPermissionGranted){
            mCodeScanner.startPreview();
        }
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = true;
                mCodeScanner.startPreview();
            } else {
                mPermissionGranted = false;
            }
        }
    }

    void saveData(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json =gson.toJson(hashUsername);
        editor.putString("hash username", json);
        editor.apply();
    }

    void kill_activity()
    {
        getActivity().finish();
    }



}
