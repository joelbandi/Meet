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
import com.google.gson.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Iterator;

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
    HashMap<String, Integer> map = new HashMap<String, Integer>();
    //HashMap hm;// = new HashMap();
    P2PKitClient client;
    String placeholder;
    ArrayAdapter<String> adapter;
    ArrayList<Scoped_Profile> profile_array;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        Intent intent = getIntent();
        my_id = intent.getStringExtra("my_id");
        accesstoken = intent.getStringExtra("accesstoken");
        lv = (ListView)findViewById(R.id.lv);
        profile_array = new ArrayList<Scoped_Profile>();//holds array of profile objects;
        cache = Collections.synchronizedList(new ArrayList<String>());
        placeholder="user_ID";
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,cache);

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

    private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap) {

        // Convert Map to List
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // Convert sorted map back to a Map
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
    private static List<String> sortByComparatorKeys(Map<String, Integer> unsortMap) {

        // Convert Map to List
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // Convert sorted map back to a Map
        List<String>sortedKeys = new ArrayList<String>();
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
            Map.Entry<String, Integer> entry = it.next();
            sortedKeys.add(entry.getKey());
        }
        return sortedKeys;
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
                    String friendID = new String(peer.getDiscoveryInfo());
                    result = backendCall(friendID,accesstoken); //result = "getDiscoveryInfo().toString()
                    //System.out.println("result");

                    if (result != null && !result.toLowerCase().contains("error".toLowerCase())){
                        Gson gson = new Gson();
                        Scoped_Profile profile = gson.fromJson(result, Scoped_Profile.class);
                        profile.setApp_scoped_id(friendID);
                        System.out.print("name: " + profile.getName());

                        String pic = profile.getPhotoURL();
                        System.out.print("pic: " + pic);
                        profile_array.add(profile);
                        map.put(profile.getName(), profile.getScore());
                        if (map.size() > 1)
                            cache = sortByComparatorKeys(map);
                        else{
                            cache.add(profile.getName());
                        }
                        System.out.println("cache:in On  Disc " + cache);
                        //adapter.notifyDataSetChanged();


                    }
                    System.out.println("Result in On Peer Discovered " + result);

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
                synchronized (cache) {
//                    String friendID = new String(peer.getDiscoveryInfo());
//                    result = backendCall(friendID,accesstoken);
//                    //System.out.println("result");
//                    if (result != null && !result.toLowerCase().contains("error".toLowerCase())) {
//                        Gson gson = new Gson();
//                        Scoped_Profile profile = gson.fromJson(result, Scoped_Profile.class);
//                        profile.setApp_scoped_id(friendID);
//
//                        String pic = profile.getPhotoURL();
//                        System.out.print("pic: " + pic);
//                        profile_array.add(profile);
//                        map.put(profile.getName(), profile.getScore());
//
//                        if (map.size() > 1)
//                            cache = sortByComparatorKeys(map);
//                        else{
//                            cache.add(profile.getName());
//                        }
//
//                        System.out.println("cache: Update " + cache);
//
//                        System.out.println("response in on peer update: " + resp);
//                        //adapter.notifyDataSetChanged();
//
//
//                    }
//                    System.out.println("Result in On Peer Update out " + result);

                    /* find a way to sort the cache now.*/

                }
                            /*use of synchronous to prevent racearounds on different onPeerDiscovered threads*/


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
        String url = "http://meetapi.herokuapp.com/api/amf/"+token+"/"+id;


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling  );
                        //Scoped_Profile profile = Scoped_Profile(peer.getDiscoveryInfo(), response["picture"], response["mutual_likes"], response["mutual_friends"]);
                        resp=response.toString();
                        System.out.println("response in backend call: " + resp);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resp = "error";
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

