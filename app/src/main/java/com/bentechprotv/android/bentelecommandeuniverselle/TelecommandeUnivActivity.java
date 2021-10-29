package com.bentechprotv.android.bentelecommandeuniverselle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class TelecommandeUnivActivity extends AppCompatActivity {


    public static final String modeles[]= {"Samsung TV","Starsat 1000HD", "Starsat 2000HD Hyper"};
    Spinner spnModeles;
    Button btnPower, btnChannelUp, btnChannelDown, btnVolumeUp, btnVolumeDown, btnMenu, btnExit, btnOK,btnDeconnexion;
    String adresse = null;
    private ProgressDialog progress;
    BluetoothAdapter monBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean bluetoothConnecte = false;
    String mo = "";
    static  final UUID monUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telecommande_univ);
        Intent nouveauInt= getIntent();
        //Recevoir l'adresse du dispositif bluettoth
        adresse = nouveauInt.getStringExtra(MainActivity.EXTRA_ADDRESS);
        spnModeles = (Spinner) findViewById(R.id.spnModeles);
        btnPower = (Button) findViewById(R.id.btnPower);
        btnChannelUp = (Button) findViewById(R.id.btnChUp);
        btnChannelDown = (Button) findViewById(R.id.btnChDown);
        btnVolumeUp = (Button) findViewById(R.id.btnVolUp);
        btnVolumeDown = (Button) findViewById(R.id.btnVolDown);
        btnMenu = (Button) findViewById(R.id.btnMenu);
        btnExit = (Button) findViewById(R.id.btnExit);
        btnOK = (Button) findViewById(R.id.btnOK);
        btnDeconnexion = (Button) findViewById(R.id.btnDeconnexion);
        //initialisations
        ArrayAdapter<String> adaptateur = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,modeles);
        spnModeles.setAdapter(adaptateur);
        //appel d'une tâche en arrière plan
        new ConnexionBT().execute();
        btnPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lancer la commande
                Commande("power");
            }
        });
        btnChannelUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lancer la commande
                Commande("channelup");
            }
        });
        btnChannelDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lancer la commande
                Commande("channeldown");
            }
        });
        btnVolumeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lancer la commande
                Commande("volumeup");
            }
        });
        btnVolumeDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lancer la commande
                Commande("volumedown");
            }
        });
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lancer la commande
                Commande("menu");
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lancer la commande
                Commande("exit");
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lancer la commande
                Commande("ok");
            }
        });
        btnDeconnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btSocket != null) {
                    try {
                        btSocket.close();
                    } catch (IOException e) {
//                        e.printStackTrace();
                        msg("Erreur.");
                    }
                    finish(); // retourner à la page de démarrage
                }
            }
        });

    }

    private void Commande(String cmd){
        if (btSocket != null){
            try {
                mo = getModeles(spnModeles.getSelectedItem().toString());
                String cm = "<";
                switch (cmd) {
                    case "power":
                        cm += "a" + "-" + mo;
                        break;
                    case "channelup":
                        cm += "b" + "-" + mo;
                        break;
                    case "channeldown":
                        cm += "c" + "-" + mo;
                        break;
                    case "volumeup":
                        cm += "d" + "-" + mo;
                        break;
                    case "volumedown":
                        cm += "e" + "-" + mo;
                        break;
                    case "menu":
                        cm += "f" + "-" + mo;
                        break;
                    case "exit":
                        cm += "g" + "-" + mo;
                        break;
                    case "ok":
                        cm += "h" + "-" + mo;
                        break;
                    default:
                        break;
                }
                cm = cm + ">";
                btSocket.getOutputStream().write((cm).toString().getBytes());

            } catch (IOException e) {
                msg("Erreur.");
            }
        }
    }
    public String getModeles(String modele){
        String m = "";
        switch (modele){
            case "Samsung TV":
                m="1";
                break;
            case "Starsat 1000HD":
                m="2";
                break;
            case "Starsat 2000HD Hyper":
                m="3";
                break;
            default:
                m="3";
                break;
        }
        return m;
    }
    private class   ConnexionBT extends AsyncTask<Void,Void,Void>{
        private boolean ConnexionAvecSucces = true;
        @Override
        protected void onPreExecute(){
            progress = ProgressDialog.show(TelecommandeUnivActivity.this,"Connexion En Cours...","Attendez svp");
        }

        @Override
        protected Void doInBackground(Void... dispositifs) {
            try {
                if (btSocket == null || !bluetoothConnecte ){
                    monBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositif = monBluetooth.getRemoteDevice(adresse);
                    //créer une connexion RFCOMM (SPP)
                    btSocket = dispositif.createRfcommSocketToServiceRecord(monUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    //démarrer la connexion
                    btSocket.connect();

                }
            } catch (IOException e) {
                //e.printStackTrace();
                ConnexionAvecSucces = false;
            }
            return  null;
        }
        @Override
        protected void onPostExecute(Void resultat){
            super.onPostExecute(resultat);
            if (!ConnexionAvecSucces){
                msg("Echec de Connexion");
                finish();
            } else {
                msg("Connexion.");
                bluetoothConnecte = true;

            }
            progress.dismiss();

        }

    }
    private void msg(String s){
        Toast.makeText(TelecommandeUnivActivity.this,s,Toast.LENGTH_LONG).show();
    }
}

