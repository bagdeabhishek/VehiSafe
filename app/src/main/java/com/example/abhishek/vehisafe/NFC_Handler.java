package com.example.abhishek.vehisafe;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

public class NFC_Handler extends AppCompatActivity {

    private static String emergencyName = null;
    private static String emergencyPhone = null;
    private static String UID = null;
    private static String payload = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc__handler);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        ReadNFCData(this.getIntent());
        fab.setVisibility(View.VISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                //
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Place a call?")
                        .setMessage("Are you sure you want to place a call?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                intent.setData(Uri.parse("tel:" + emergencyPhone));
                                if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                view.getContext().startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });


    }

    private void PrintData(String s)
    {
        Snackbar.make(findViewById(R.id.content_nfc__handler), s, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        Log.v("LOG Value",s);
    }
    private void ReadNFCData(Intent intent) {
        Parcelable[] rawMessages =
                intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMessages != null) {
            NdefMessage[] messages = new NdefMessage[rawMessages.length];
            for (int i = 0; i < rawMessages.length; i++) {
                messages[i] = (NdefMessage) rawMessages[i];
                NdefRecord[] record = messages[i].getRecords();

                try {
                    payload = new String(record[i].getPayload(),"US-ASCII");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                PrintData("Payload : "+ payload);
                ExtractFields();
            }}
    }

    private void ExtractFields() {
        String[] temp=payload.split("\\|");
        UID=temp[0].replace("en","");
        emergencyName=temp[1];
        emergencyPhone=temp[2];
        PrintData(UID+emergencyName+emergencyPhone);
    }

}
