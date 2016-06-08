package com.threefourfive.meet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    String accesstoken;
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
        accesstoken = intent.getStringExtra("accesstoken");
        lv = (ListView)findViewById(R.id.lv);
        cache = Collections.synchronizedList(new ArrayList<String>());
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,cache);
        placeholder="user_ID";
        lv.setAdapter(adapter);


        final StatusResult result = P2PKitClient.isP2PServicesAvailable(DisplayActivity.this);
        if(result.getStatusCode()== StatusResult.SUCCESS){
            client = P2PKitClient.getInstance(DisplayActivity.this);
            client.enableP2PKit(callback, P2P_APP_KEY);
            try {
                client.getDiscoveryServices().setP2pDiscoveryInfo(my_id.getBytes());
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

            if(client.isEnabled()){
                try {
                    client.getDiscoveryServices().setP2pDiscoveryInfo(my_id.getBytes());

                } catch (InfoTooLongException e) {
                    e.printStackTrace();
                }
                client.getDiscoveryServices().addP2pListener(listener);
            }
            Toast.makeText(DisplayActivity.this,"onEnabled",Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onSuspended() {

        }

        @Override
        public void onResumed() {
            try {
                client.getDiscoveryServices().setP2pDiscoveryInfo(my_id.getBytes());

            } catch (InfoTooLongException e) {
                e.printStackTrace();
            }
            Toast.makeText(DisplayActivity.this,"onResumed",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDisabled() {

        }

        @Override
        public void onError(StatusResult result) {
            Log.d("Err",Integer.toString(result.getStatusCode()));
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

                            /*use of synchronous to prevent racearounds on different onPeerDiscovered threads*/

                synchronized (cache){
                    result = backendCall(new String(peer.getDiscoveryInfo()),accesstoken); //result = "getDiscoveryInfo().toString()
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
            cache.remove(new String(peer.getDiscoveryInfo()));
            lv.setAdapter(adapter);
        }

        @Override
        public void onPeerUpdatedDiscoveryInfo(Peer peer) {
            Toast.makeText(DisplayActivity.this,"onPeerDiscovered",Toast.LENGTH_SHORT).show();
            String result;
            try {
                result = backendCall(new String(peer.getDiscoveryInfo()),accesstoken);
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
        public void onProximityStrengthChanged(Peer peer) {

        }
    };


    String resp;
    public String backendCall(String id,String token){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://meetapi.herokuapp.com/api/aml"+token+"/"+id;


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Result handling
                        resp=response.toString();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Error handling
                System.out.println("ERR");
                error.printStackTrace();

            }
        });

        queue.add(stringRequest);

        return resp;

        //resp should contain JSON data contains - > name
    }




    public void refresh(View view){

        client.getDiscoveryServices().removeAllP2pListener();
        client.disableP2PKit();

        synchronized (cache){

            cache.clear();
        }


        client.enableP2PKit(callback,P2P_APP_KEY);
        try {
            client.getDiscoveryServices().setP2pDiscoveryInfo(my_id.getBytes());
        } catch (InfoTooLongException e) {
            e.printStackTrace();
        }
        client.getDiscoveryServices().addP2pListener(listener);

    }



}

