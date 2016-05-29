package com.threefourfive.meet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ch.uepaa.p2pkit.P2PKitClient;
import ch.uepaa.p2pkit.discovery.InfoTooLongException;
import ch.uepaa.p2pkit.P2PKitStatusCallback;
import ch.uepaa.p2pkit.StatusResult;
import ch.uepaa.p2pkit.StatusResultHandling;
import ch.uepaa.p2pkit.discovery.P2PListener;
import ch.uepaa.p2pkit.discovery.entity.Peer;

public class DisplayActivity extends AppCompatActivity {

    ListView lv;
    String my_id;
    private static final String P2P_APP_KEY = "eyJzaWduYXR1cmUiOiJCYnZiSGI2SGw4b0h4OUdEbWxRU0VzQ0ZRUnorQzZLeHQzOFBGajRYV1JjZ1lwRU1RSmRhKzc4UjRsY0NHays3aTVtc0xSaWplZmlBaDI3WEhnaDJtVHhEOUNWRkxWSllISkVIMWFYQTB2VTd2eFF1NlJKcktJUFhlZGR5Z2NML0gyTXBEVWVSUmdCRHVhZ1pOUHJEN1JRRU9DNWhiRHNwTG92Q3gzWE40UTQ9IiwiYXBwSWQiOjE2MDYsInZhbGlkVW50aWwiOjE3MDIwLCJhcHBVVVVJRCI6IkFGMERGMDg5LUREMTUtNDcwOS05NEI3LUFEMjkxODQ5MkQwNiJ9";
    List<String> cache;
    P2PKitClient client;
    String placeholder;
    ArrayAdapter<String> adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        Intent intent = getIntent();
        my_id = intent.getStringExtra("my_id");
        lv = (ListView)findViewById(R.id.lv);
        cache = Collections.synchronizedList(new ArrayList<String>());
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,cache);
        placeholder="user_ID";


        lv.setAdapter(adapter);


        final StatusResult result = P2PKitClient.isP2PServicesAvailable(DisplayActivity.this);
        if(result.getStatusCode()== StatusResult.SUCCESS){
            client = P2PKitClient.getInstance(this);
            try {
                client.getDiscoveryServices().setP2pDiscoveryInfo(my_id.getBytes());
                client.enableP2PKit(callback, P2P_APP_KEY);
                client.getDiscoveryServices().addP2pListener(listener);
            } catch (InfoTooLongException e) {
                e.printStackTrace();
            }
        }else{
            StatusResultHandling.showAlertDialogForStatusError(this, result);
        }
    }



    private final P2PKitStatusCallback callback = new P2PKitStatusCallback() {
        @Override
        public void onEnabled() {
            try {
                client.getDiscoveryServices().setP2pDiscoveryInfo(my_id.getBytes());
            } catch (InfoTooLongException e) {
                e.printStackTrace();
            }
            Toast.makeText(DisplayActivity.this,"onEnabled",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSuspended() {

        }

        @Override
        public void onResumed() {

        }

        @Override
        public void onDisabled() {

        }

        @Override
        public void onError(StatusResult result) {

        }
    };



    private final P2PListener listener = new P2PListener() {
        @Override
        public void onP2PStateChanged(int state) {

        }

        @Override
        public void onPeerDiscovered(Peer peer) {
            Toast.makeText(DisplayActivity.this,"onPeerDiscovered",Toast.LENGTH_SHORT).show();
            String result;
            try {
                result = backendCall(new String(peer.getDiscoveryInfo()));
                            /*use of synchronous to prevent racearounds on different onPeerDiscovered threads*/

                synchronized (cache){

                    cache.add(result);
                /* find a way to sort the cache now.*/

                }
            }catch (Exception e){
                e.printStackTrace();
            }

            /*We need to find a way to include the mutual likes information as well..possible a sortedmap implementation

                maybe even a separate profile class of our own and put each data class wrappper in a separate custom view based

                ALSO GENERAL REFINEMENT IS IN ORDER...

            */



            lv.setAdapter(adapter);
        }

        @Override
        public void onPeerLost(Peer peer) {

        }

        @Override
        public void onPeerUpdatedDiscoveryInfo(Peer peer) {

        }

        @Override
        public void onProximityStrengthChanged(Peer peer) {

        }
    };


    public String backendCall(String id){
        /* make the back end call to the server
        * and return the same id along with the number of mutual likes present in it*/

        return id;
    }















}

