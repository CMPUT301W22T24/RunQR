package com.example.runqr;


import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;

import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.Result;


import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import java.util.concurrent.Executor;

// This class is the Fragment used to host a codeScanner which allows players to scan QRCodes and uses Hasher to hash code contents.
// The newly created QRCode is passed back to MainActivity through method onConfirmPressed and added to player's QRLibrary there.
// This class uses CodeScanner object to scan QRCodes and borrows code from: https://github.com/yuriy-budiyev/code-scanner.




public class AddQRFragment extends Fragment implements View.OnClickListener, LocationListener {

    FirebaseFirestore db;

    private static final int RC_PERMISSION = 10;
    private CodeScanner mCodeScanner;
    private boolean mPermissionGranted;
    private String QRString = null;
    private OnFragmentInteractionListener listener;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    // build fragment/popup
    static AlertDialog.Builder dialogBuilder;
    static AlertDialog dialog;
    private Button take_photo, add_geolocation, yes, no;


    private Boolean locationAdded = false;
    private Boolean photoAdded = false;


    Boolean alreadyScanned;

    Location QRCodeLocation;
    QRCode QRCodeToAdd;
    Uri QRCodePhoto;
    Bitmap bitmap;
    View conformationPopup;

    Context mContext;
    int LOCATION_PERMISSION_REQUEST_CODE = 100;
    Player currentPlayer;
    private ConstraintLayout buttonLayout;
    private ConstraintLayout cameraLayout;
    Button takePicture;
    PreviewView previewView;
    private ImageCapture imageCapture;


    Boolean unique;
    ArrayList<Player> scannedByList;
    Boolean addCode = true;
    FusedLocationProviderClient fusedLocationProviderClient;
    android.location.Location lastKnownLocation;

    //double latitude = 0.0;
    //double longitude = 0.0;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() +
                    " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onLocationChanged(@NonNull android.location.Location location) {

    }

    public interface OnFragmentInteractionListener {
        void onConfirmPressed(QRCode QRCodeToAdd);
        void onPhotoCaptured(QRCode QRCodeWithPhoto);
        //Bitmap openCamera();

    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final Activity activity = (Activity) getActivity();
        currentPlayer = MainActivity.getCurrentPlayer();
        //CameraActivity result = (CameraActivity) getActivity();
        //QRCodePhoto.setImage(result.bitmap);

        db = FirebaseFirestore.getInstance();
        View root = inflater.inflate(R.layout.add_qr_fragment_layout, container, false);
        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);


        Button confirmAddQR = root.findViewById(R.id.confirm_addQR_button);
        Button cancelAddQR = root.findViewById(R.id.cancel_addQR_button);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

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
                requestPermissions(new String[]{Manifest.permission.CAMERA}, RC_PERMISSION);
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
                if (QRString != null) {
                    Log.v("Confirm 1", "Confirm 1");
                    String hashedString = QRCodeHasher.hashQRCode(QRString);
                    /*
                    QRCode QRCodeToAdd = new QRCode(hashedString);
                    checkWhetherUnique(QRCodeToAdd);
                    // send QRCodeToAdd to MainActivity to add it to the player's QRLibrary
                    listener.onConfirmPressed(QRCodeToAdd);
                    */

                    // prompt user to enter geolocation and/or photo of object hosting the QRCode
                    //ignore for now


                    // THIS NEEDS TO BE UPDATED BY KENNY
                    // Below: open activity/fragment which prompts user to access their device's location and take photo of the object containing scannedQRCode
                    Log.v("Confirm 2", "Confirm 2");

                    dialogBuilder = new AlertDialog.Builder(getActivity());
                    conformationPopup = getLayoutInflater().inflate(R.layout.scanner_popup, null);

                    take_photo = (Button) conformationPopup.findViewById(R.id.takePhotoButton);
                    add_geolocation = (Button) conformationPopup.findViewById(R.id.addGeolocationButton);
                    yes = (Button) conformationPopup.findViewById(R.id.yesButton);
                    no = (Button) conformationPopup.findViewById(R.id.noButton);


                    LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                    Log.v("Confirm 3", "Confirm 3");

                    if (ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        ActivityCompat.requestPermissions(getActivity(), new String[]{
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

                            photoAdded = true;
                            //FIX BELOW
                            //QRCodePhoto = new Photo();

                            //define Take Photo here
                            //bitmap = listener.openCamera();
                            //QRCodePhoto.setImage(bitmap);

                            // cite: https://stackoverflow.com/questions/13067033/how-to-access-activity-variables-from-a-fragment-android by David M
                            //CameraActivity result = (CameraActivity) getActivity();
                            //QRCodePhoto.setImage(result.bitmap);
                            //bitmap = QRCodePhoto.getImage();

                            //Intent intent = getIntent();
                            //Bitmap bitmap = (Bitmap) intent.getParcelableExtra("BitmapImage");

                        }
                    });



                    add_geolocation.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onClick(View view) {
                            locationAdded = true;


                           //getDeviceCurrentLocation();
                            // cited: https://stackoverflow.com/questions/42367681/using-fragment-i-want-get-current-location-in-android, koksalb

                            Criteria criteria = new Criteria();

                            //LocationManager mgr =
                                    //(LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
                            String provider = lm.getBestProvider(criteria, true);
                            android.location.Location location = lm.getLastKnownLocation(provider);
                            double latitude = location.getLatitude(); //Y
                            double longitude = location.getLongitude(); //X

                            QRCodeLocation = new Location(longitude, latitude);

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



                    // Instantiate new QRCode to add to the QRLibrary
                    if (locationAdded && photoAdded) {
                        // NOTE: photo is temporarily null here

                        QRCodeToAdd = new QRCode(hashedString, QRCodeLocation, QRCodePhoto);

                        //listener.onPhotoCaptured(QRCodeToAdd);
                    }
                    else if (locationAdded && !photoAdded) {
                        QRCodeToAdd = new QRCode(hashedString, QRCodeLocation,  null);
                    }
                    else if (!locationAdded && photoAdded) {
                        // NOTE: photo is temporarily null here
                        QRCodeToAdd = new QRCode(hashedString,null,  QRCodePhoto);
                        //listener.onPhotoCaptured(QRCodeToAdd);
                    }
                    else {
                        QRCodeToAdd = new QRCode(hashedString);
                    }

                    unique = checkWhetherUnique(QRCodeToAdd);

                    // send QRCodeToAdd to MainActivity to add it to the player's QRLibrary

                    if (addCode) {
                        listener.onConfirmPressed(QRCodeToAdd);
                    }

                    getFragmentManager().beginTransaction().remove(AddQRFragment.this).commit();
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
                getFragmentManager().beginTransaction().remove(AddQRFragment.this).commit();

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

    private void getDeviceCurrentLocation() {
        @SuppressLint("MissingPermission")
        Task<android.location.Location> locationResult = fusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener((Executor) this, new OnCompleteListener<android.location.Location>() {
            @Override
            public void onComplete(@NonNull Task<android.location.Location> task) {
                if(task.isSuccessful()){
                    lastKnownLocation = task.getResult();
                    Log.v("lat", String.valueOf(lastKnownLocation.getLatitude()));

                }
            }
        });
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        buttonLayout = view.findViewById(R.id.buttonView);
        cameraLayout = view.findViewById(R.id.cameraView);
        takePicture = view.findViewById(R.id.takePhoto);

        previewView = view.findViewById(R.id.previewView);


        /*take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoAdded = true;*/
                //FIX BELOW
                //QRCodePhoto = new Photo();

                //define Take Photo here

                /*buttonLayout.setVisibility(View.GONE);
                cameraLayout.setVisibility(View.VISIBLE);

                takePicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view){*/
                        //photo = new Photo();

                        /*bitmap = previewView.getBitmap();
                        int byteCount = bitmap.getByteCount();
                        if(byteCount > 8000000){
                            bitmap = bitmap.createScaledBitmap(bitmap,100,100,false);
                        }

                        cameraLayout.setVisibility(View.GONE);
                        buttonLayout.setVisibility(View.VISIBLE);*/

                        //photo.setImage(bitmap);
                                    /*Context context = mContext;
                                    CharSequence text = "This Picture Has Been Saved";
                                    int duration = Toast.LENGTH_SHORT;
                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();*/
                                    /*ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    byteArray = stream.toByteArray();*/
        //photo.setImage(bitmap);
        //Intent intent = new Intent(this, AddQRFragment.class);
        //intent.putExtra("PhotoImage", (Parcelable) photo);
        //Intent intent = new Intent(this, CameraActivity.class);
        //intent.putExtra("BitmapImage", bitmap);
        //finish();
    /*}
                );
                cameraProviderFuture = ProcessCameraProvider.getInstance(getActivity());

                cameraProviderFuture.addListener(() -> {
                    try {
                        ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                        startCameraX(cameraProvider);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }, getExecutor());*/
                //QRCodePhoto.setImage(bitmap);

                // cite: https://stackoverflow.com/questions/13067033/how-to-access-activity-variables-from-a-fragment-android by David M
                //CameraActivity result = (CameraActivity) getActivity();
                //QRCodePhoto.setImage(result.bitmap);
                //bitmap = QRCodePhoto.getImage();

                //Intent intent = getIntent();
                //Bitmap bitmap = (Bitmap) intent.getParcelableExtra("BitmapImage");

            }



    public interface OnConfirmPressed {
        void onConfirmPressed(QRCode qrCodeData);

    }

    Boolean checkWhetherUnique(QRCode qrcode) {
        String hash = qrcode.getHash();
        DocumentReference docRef = db.collection("QR Codes").document(hash);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        unique = false;

                        //unique = false;
                        // document already exists on db
                        // just add player to existing scannedbylist
                        //alreadyScanned = true;
                        //savePlayerToQRCode(qrcode, unique);

                        savePlayerToQRCode(qrcode);

                    } else {
                        unique = true;
                        saveQRCodeToDB(qrcode);
                        //Context context = getApplicationContext();
                        Context context = mContext;
                        CharSequence text = "Congratulations! You are the first one who scanned this QRCode!!";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        return unique;
    }

    public void saveQRCodeToDB(QRCode qrcode) {

        String hash = qrcode.getHash();
        CollectionReference collectionIdentifier = db.collection("QR Codes");
        collectionIdentifier
                .document(hash)
                .set(qrcode)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // These are a method which gets executed when the task is succeeded
                        savePlayerToQRCode(qrcode);
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



    public void savePlayerToQRCode(QRCode qrcode) {
        String hash = qrcode.getHash();
        String playerUsername =  currentPlayer.getPlayerAccount().getUsername();
        DocumentReference docref = db.collection("QR Codes").document(hash).collection("Players").document(playerUsername);

        Log.v("User", docref.getId());

        docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Context context = getApplicationContext();
                        Context context = mContext;
                        CharSequence text = "This QR code was already scanned by you. Scan another one!";
                        // DON'T add to library!!
                        addCode = false;
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                    else{
                        CollectionReference collectionIdentifier = db.collection("QR Codes").document(hash).collection("Players");
                        collectionIdentifier
                                .document(playerUsername)
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
                }
            }
        });




    }




    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(mContext);
    }

    private void startCameraX(ProcessCameraProvider cameraProvider) {
        //Structure of the code referenced from "https://www.youtube.com/watch?v=IrwhjDtpIU0" by Coding Reel
        cameraProvider.unbindAll();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Preview preview = new Preview.Builder().build();

        preview.setSurfaceProvider(previewView.createSurfaceProvider());

        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
    }

    @Override
    public void onClick(View view){
        if (view.getId() == R.id.takePhoto) {
            capturePhoto();


        }
    }





    private void capturePhoto() {

        long timestamp = System.currentTimeMillis();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timestamp);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

        imageCapture.takePicture(

                new ImageCapture.OutputFileOptions.Builder(
                        mContext.getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                ).build(),
                ContextCompat.getMainExecutor(mContext),
                new ImageCapture.OnImageSavedCallback(){

                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        //ImageDecoder.Source source = ImageDecoder.createSource(contentValues, outputFileResults.getSavedUri());
                        Context context = null;
                        try {
                            context.getContentResolver().openInputStream(outputFileResults.getSavedUri());

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        Log.v("hello","Photo saved");
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.v("hello","Photo not saved");

                    }
                }

        );

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            QRCodePhoto = selectedImage;

        }
    }

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
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                locationAdded = true;

            } else { // if permission is not granted

                // decide what you want to do if you don't get permissions
            }
        }
    }






}
