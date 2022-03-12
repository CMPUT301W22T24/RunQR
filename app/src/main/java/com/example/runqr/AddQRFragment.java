package com.example.runqr;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
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
import com.google.zxing.Result;

//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.app.FragmentManager;
//import android.app.FragmentTransaction;

public class AddQRFragment extends Fragment {

    private static final int RC_PERMISSION = 10;
    private CodeScanner mCodeScanner;
    private boolean mPermissionGranted;
    private String QRString = null;
    private OnFragmentInteractionListener listener;


    OnConfirmPressed dataPasser;
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

        //dataPasser = (AddQRFragment.OnConfirmPressed) context;
    }

    public interface OnFragmentInteractionListener {
        void onConfirmPressed(QRCode QRCodeToAdd);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        View root = inflater.inflate(R.layout.add_qr_fragment_layout, container, false);
        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);

        Button confirmAddQR = root.findViewById(R.id.confirm_addQR_button);
        Button cancelAddQR = root.findViewById(R.id.cancel_addQR_button);

        Scanner QRCodeScanner = new Scanner();
        mCodeScanner = new CodeScanner(activity, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                // result is string corresponding to contents of QRCode
                // hash the result string of the QR Code and store hash in the QRCode

                QRString = result.getText();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, result.getText(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

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
                    String hashedString = QRCodeScanner.hashQRCode(QRString);
                    QRCode QRCodeToAdd = QRCodeScanner.scanQRCode(hashedString);
                    // send QRCode to MainActivity to add it to player's QRLibrary
                    listener.onConfirmPressed(QRCodeToAdd);


                    // prompt user to enter geolocation and/or photo of object hosting the QRCode


                    //ignore for now


                    getFragmentManager().beginTransaction().remove(AddQRFragment.this).commit();
                    final Button addQRButton = getActivity().findViewById(R.id.add_qr_button);
                    addQRButton.setVisibility(View.VISIBLE);
                }

            }
        });

        cancelAddQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // cancel Scanner by returning to main activity
                getFragmentManager().beginTransaction().remove(AddQRFragment.this).commit();
                final Button addQRButton = getActivity().findViewById(R.id.add_qr_button);
                addQRButton.setVisibility(View.VISIBLE);


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

        /*adding playerstats*/
        /*

        int number_scanned = currentPlayer.playerStats.getNum_of_scanned();
        currentPlayer.playerStats.setNum_of_scanned(number_scanned+1);

        if QRCode.getScore() > currentPlayer.playerStats.getHigh_qr() {
            currentPlayer.playerStats.setHigh_qr(QRCode.getScore());
            }
        if QRCode.getScore() < currentPlayer.playerStats.getLow_qr() {
            currentPlayer.playerStats.setLow_qr(QRCode.getScore());
            }

        */
    }

    public void passData(QRCode data) {
        dataPasser.onConfirmPressed(data);
        /*add playerstats data here?*/

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
    }



}
