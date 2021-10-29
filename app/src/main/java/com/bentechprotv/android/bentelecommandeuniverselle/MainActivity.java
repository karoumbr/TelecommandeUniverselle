package com.bentechprotv.android.bentelecommandeuniverselle;


import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Button btnApparie;
    ListView listeDispositifs;
    //Bluetooth
    private BluetoothAdapter monBluetooth = null;
    private Set<BluetoothDevice> DispositifsApparies;
    public static String EXTRA_ADDRESS = "device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnApparie = (Button) findViewById(R.id.btnListerDispositifsApparies);
        listeDispositifs = (ListView) findViewById(R.id.lsvDispositifsApparies);
        // si le dispositif a bluetooth
        monBluetooth = BluetoothAdapter.getDefaultAdapter();
        if (monBluetooth == null){
            Toast.makeText(getApplicationContext(),"Le dispositif Bluetooth est indisponible",Toast.LENGTH_LONG).show();
            //Terminer l'APK
            finish();
        } else if (!monBluetooth.isEnabled()){
            //Demander à l'utilisateur d'activer le Bluetooth
            Intent ActiverBTOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(ActiverBTOn,1);

        }
        btnApparie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListeDispoApparies();
            }
        });




    }
    private void ListeDispoApparies(){
        DispositifsApparies = monBluetooth.getBondedDevices();
        ArrayList liste = new ArrayList();
        if (DispositifsApparies.size()>0){
            for(BluetoothDevice ba : DispositifsApparies){
                liste.add(ba.getName() + "\n" + ba.getAddress());

            }
        } else  {
            Toast.makeText(getApplicationContext(),"Dispositifs Appariés non trouvés.",Toast.LENGTH_LONG).show();
        }
        final ArrayAdapter adaptateur = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,liste);
        listeDispositifs.setAdapter(adaptateur);
        listeDispositifs.setOnClickListener((View.OnClickListener) monListeClickListener);
    }

    private AdapterView.OnItemClickListener monListeClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String info = ((TextView) view).getText().toString();
            String adresse = info.substring(info.length() -17);
            // lancer la nouvelle intent
            Intent i = new Intent(getApplicationContext(),TelecommandeUnivActivity.class);
            i.putExtra(EXTRA_ADDRESS, adresse);
            startActivity(i);

        }
    };
}