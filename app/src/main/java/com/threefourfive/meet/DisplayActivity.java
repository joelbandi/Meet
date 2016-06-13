package com.threefourfive.meet;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import ch.uepaa.p2pkit.P2PKitClient;
import ch.uepaa.p2pkit.P2PKitStatusCallback;
import ch.uepaa.p2pkit.StatusResult;
import ch.uepaa.p2pkit.StatusResultHandling;
import ch.uepaa.p2pkit.discovery.InfoTooLongException;
import ch.uepaa.p2pkit.discovery.P2PListener;
import ch.uepaa.p2pkit.discovery.entity.Peer;
import ch.uepaa.p2pkit.discovery.entity.ProximityStrength;


import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import java.util.Iterator;
public class DisplayActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter rAdapter;
    RecyclerView.LayoutManager layoutManager;

    // profile_array already defined
    ArrayList<Scoped_Profile> arrayList = new ArrayList<Scoped_Profile>();
    String[] Name,Info;
    int[] img={R.drawable.me,R.drawable.me,R.drawable.me,R.drawable.me,R.drawable.me,R.drawable.me};

    ListView lv;
    String my_id;
    String accesstoken;
    private static final String P2P_APP_KEY = "eyJzaWduYXR1cmUiOiJCYnZiSGI2SGw4b0h4OUdEbWxRU0VzQ0ZRUnorQzZLeHQzOFBGajRYV1JjZ1lwRU1RSmRhKzc4UjRsY0NHays3aTVtc0xSaWplZmlBaDI3WEhnaDJtVHhEOUNWRkxWSllISkVIMWFYQTB2VTd2eFF1NlJKcktJUFhlZGR5Z2NML0gyTXBEVWVSUmdCRHVhZ1pOUHJEN1JRRU9DNWhiRHNwTG92Q3gzWE40UTQ9IiwiYXBwSWQiOjE2MDYsInZhbGlkVW50aWwiOjE3MDIwLCJhcHBVVVVJRCI6IkFGMERGMDg5LUREMTUtNDcwOS05NEI3LUFEMjkxODQ5MkQwNiJ9";
    ArrayList<String> cache;
    HashMap<String, Integer> maptobeSorted = new HashMap<String, Integer>();
    HashMap<String, Scoped_Profile> mapProfiles = new HashMap<String, Scoped_Profile>();
    List<String> names;
    //HashMap hm;// = new HashMap();
    P2PKitClient client;
    String placeholder;
    ArrayAdapter<String> adapter;
    List<Scoped_Profile> profile_array;

    // Enabling (1/2) - Enable the P2P Services
    public void enableKit(final boolean startP2PDiscovery, P2PKitEnabledCallback p2PKitEnabledCallback) {
        mShouldStartP2PDiscovery = startP2PDiscovery;
        mP2PKitEnabledCallback = p2PKitEnabledCallback;
        StatusResult result = P2PKitClient.isP2PServicesAvailable(this);
        if (result.getStatusCode() == StatusResult.SUCCESS) {
            Log.i("P2PKitClient", "Enable P2PKit");
            P2PKitClient client = P2PKitClient.getInstance(this);
            client.enableP2PKit(mStatusCallback, P2P_APP_KEY);
            mShouldEnable = false;
        } else {
            Log.w("P2PKitClient", "Cannot start P2PKit, status code: " + result.getStatusCode());
            mShouldEnable = true;
            StatusResultHandling.showAlertDialogForStatusError(this, result);
        }
    }
    // Enabling (2/2) - Handle the status callbacks with the P2P Services
    private final P2PKitStatusCallback mStatusCallback = new P2PKitStatusCallback() {
        @Override
        public void onEnabled() {
            Log.v("P2PKitStatusCallback", "Successfully enabled P2P Services, with node id: " + P2PKitClient.getInstance(DisplayActivity.this).getNodeId().toString());
            if (mP2PKitEnabledCallback != null) {
                mP2PKitEnabledCallback.onEnabled();
            }
            if (mShouldStartP2PDiscovery) {
                startP2pDiscovery();
            }
        }
        @Override
        public void onSuspended() {
            Log.v("P2PKitStatusCallback", "P2P Services suspended");
        }
        @Override
        public void onResumed() {
            Log.v("P2PKitStatusCallback", "P2P Services resumed");
        }
        @Override
        public void onDisabled() {
            Log.v("P2PKitStatusCallback", "P2P Services disabled");
        }
        @Override
        public void onError(StatusResult statusResult) {
            Log.e("P2PKitStatusCallback", "Error in P2P Services with status: " + statusResult.getStatusCode());
            StatusResultHandling.showAlertDialogForStatusError(DisplayActivity.this, statusResult);
        }
    };
    public void disableKit() {
        Log.i("P2PKitClient", "Disable P2PKit");
        P2PKitClient client = P2PKitClient.getInstance(this);
        client.getDiscoveryServices().removeP2pListener(mP2pDiscoveryListener);
        client.disableP2PKit();
        mShouldEnable = false;
        mShouldStartP2PDiscovery = false;
        mP2PServiceStarted = false;
        mGeoServiceStarted = false;
    }
    public void startP2pDiscovery() {
        Log.i("P2PKitClient", "Start discovery");
        mP2PServiceStarted = true;
        byte[] ownDiscoveryData = loadOwnDiscoveryData();
        publishP2pDiscoveryInfo(ownDiscoveryData);
        P2PKitClient.getInstance(this).getDiscoveryServices().addP2pListener(mP2pDiscoveryListener);
    }
    private void publishP2pDiscoveryInfo(byte[] data) {
        Log.i("P2PKitClient", "Publish discovery info");
        try {
            P2PKitClient.getInstance(this).getDiscoveryServices().setP2pDiscoveryInfo(data);
        } catch (InfoTooLongException e) {
            Log.e("P2PKitClient", "The discovery info is too long: " + ((data != null) ? data.length : "null") + " bytes");
        }
    }
    // Listener of P2P discovery events
    private final P2PListener mP2pDiscoveryListener = new P2PListener() {
        @Override
        public void onP2PStateChanged(final int state) {
            Log.v("P2PListener", "State changed: " + state);
        }
        @Override
        public void onPeerDiscovered(final Peer peer) {
            if (peer.getProximityStrength() == ProximityStrength.WIFI_PEER) {
                Log.v("P2PListener", "WIFI Peer discovered: " + peer.getNodeId() + ".");
            } else {
                Log.v("P2PListener", "Peer discovered: " + peer.getNodeId() + ". Proximity strength: " + peer.getProximityStrength());
            }
            handlePeerDiscovered(peer);
        }
        @Override
        public void onPeerLost(final Peer peer) {
            Log.v("P2PListener", "Peer lost: " + peer.getNodeId());
            handlePeerLost(peer);
        }
        @Override
        public void onPeerUpdatedDiscoveryInfo(Peer peer) {
            Log.v("P2PListener", "Peer updated discovery info: " + peer.getNodeId());
            handlePeerUpdatedDiscoveryInfo(peer);
        }
        @Override
        public void onProximityStrengthChanged(Peer peer) {
            Log.v("P2PListener", "Peer changed proximity strength: " + peer.getNodeId() + ". Proximity strength: " + peer.getProximityStrength());
            handlePeerChangedProximityStrength(peer);
        }
    };
    public void stopP2pDiscovery() {
        Log.i("P2PKitClient", "Stop discovery");
        P2PKitClient.getInstance(this).getDiscoveryServices().removeP2pListener(mP2pDiscoveryListener);
        mP2PServiceStarted = false;
    }
    private boolean mShouldEnable;
    private boolean mShouldStartP2PDiscovery;
    private P2PKitEnabledCallback mP2PKitEnabledCallback;
    private boolean mP2PServiceStarted;
    private boolean mGeoServiceStarted;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        mShouldEnable = true;
        mShouldStartP2PDiscovery = false;
        mP2PServiceStarted = false;
        mGeoServiceStarted = false;
        Intent intent = getIntent();
        my_id = intent.getStringExtra("my_id");
        accesstoken = intent.getStringExtra("accesstoken");
//        lv = (ListView) findViewById(R.id.lv);
        profile_array = new ArrayList<Scoped_Profile>();//holds array of profile objects;
        cache =  new ArrayList<String>();
        names = new ArrayList<String>();
        placeholder = "user_ID";

        // Added by Moin
        recyclerView = (RecyclerView)findViewById(R.id.rv);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setHasFixedSize(true);
        Name = getResources().getStringArray(R.array.profile_name);
        Info = getResources().getStringArray(R.array.profile_info);
        int count =0;
        for(String NAME : Name) // Initializing sample profiles
        {
            Scoped_Profile scopedProfile = new Scoped_Profile("",count,count,NAME);
            arrayList.add(scopedProfile);
            count++;
        }
        rAdapter = new RecyclerAdapter(arrayList);
        recyclerView.setAdapter(rAdapter);
//        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
//        lv.setAdapter(adapter);
    }
    @Override
    public void onResume() {
        super.onResume();
        // When the user comes back from the play store after installing p2p services, try to enable p2pkit again
        if (mShouldEnable && !P2PKitClient.getInstance(this).isEnabled()) {
            enableKit(true, null);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        disableKit();
    }

    private void handlePeerDiscovered(final Peer peer) {
        byte[] peerDiscoveryInfo = peer.getDiscoveryInfo();
        float proximityStrength = (peer.getProximityStrength() - 1f) / 4;
        boolean proximityStrengthImmediate = peer.getProximityStrength() == ProximityStrength.IMMEDIATE;
        String result;
        try {
            String friendID = new String(peer.getDiscoveryInfo());
            if(new String("default").equals(friendID)){
                return;
            }
            Toast.makeText(DisplayActivity.this, "handlePeerDiscovered", Toast.LENGTH_SHORT).show();
            System.out.println(" L1O1GGG The discovered id = " + friendID);
            backendCall(friendID, accesstoken); //result = "getDiscoveryInfo().toString()
            //result.equals("") && !result.toLowerCase().contains("error".toLowerCase())
//            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(DisplayActivity.this, " Null pointer hit", Toast.LENGTH_SHORT).show();
        }

    }
    private void handlePeerLost(final Peer peer) {
    }
    private void handlePeerUpdatedDiscoveryInfo(final Peer peer) {
        byte[] peerDiscoveryInfo = peer.getDiscoveryInfo();
        handlePeerDiscovered(peer);
    }
    private void handlePeerChangedProximityStrength(final Peer peer) {
    }
    private void updateOwnDiscoveryInfo() {
        P2PKitClient client = P2PKitClient.getInstance(DisplayActivity.this);
        if (!client.isEnabled()) {
            Toast.makeText(this,"Not enabled!!!", Toast.LENGTH_LONG).show();
            return;
        }
        byte[] ownDiscoveryData = loadOwnDiscoveryData();
        publishP2pDiscoveryInfo(ownDiscoveryData);
    }
    private byte[] loadOwnDiscoveryData() {
        return my_id.getBytes();
    }
    String resp;
    private void backendCall(String id, String token) {
        final String app_scoped_id = id;
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://meetapi.herokuapp.com/api/amf/" + token + "/" + id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling  );
                        //Scoped_Profile profile = Scoped_Profile(peer.getDiscoveryInfo(), response["picture"], response["mutual_likes"], response["mutual_friends"]);
                        resp = response.toString();
                        System.out.println("L1O1GGG response in backend call: " + resp);


                        if (true) {
                            Gson gson = new Gson();
                            Scoped_Profile profile = gson.fromJson(resp, Scoped_Profile.class);
//                Scoped_Profile profile = new Scoped_Profile(friendID,)
                            profile.setApp_scoped_id(app_scoped_id);
                            String pic = profile.getPhotoURL();
                            maptobeSorted.put(profile.getApp_scoped_id(), profile.getScore());
                            mapProfiles.put(profile.getApp_scoped_id(), profile);
                            cache = sortByComparatorKeys(maptobeSorted);


                            for (String s : cache) {
                                names.add(mapProfiles.get(s).getName());
                                System.out.println(" L1O1GGG Added to names array -> " + mapProfiles.get(s).getName());
                            }
                        }
                        System.out.println("L1O1GGG Add finished " + resp);

//                        adapter.notifyDataSetChanged();
                        rAdapter.notifyDataSetChanged();
                        System.out.println(" L1O1GGG adapter updated ");
                        recyclerView.setAdapter(rAdapter);
//                        lv.setAdapter(adapter);


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
        //resp should contain JSON data contains - > name
    }

    public void refresh(View view){


        String data = "default";
        try {
            P2PKitClient.getInstance(this).getDiscoveryServices().setP2pDiscoveryInfo(data.getBytes());
        } catch (InfoTooLongException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("L1O1GGG" + P2PKitClient.getInstance(this).isEnabled());

        updateOwnDiscoveryInfo();

    }


    private static ArrayList<String> sortByComparatorKeys(Map<String, Integer> unsortMap) {
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
        // Convert sorted maptobeSorted back to a Map
        List<String> sortedKeys = new ArrayList<String>();
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<String, Integer> entry = it.next();
            sortedKeys.add(entry.getKey());
        }
        return new ArrayList<String>(sortedKeys);
    }
}