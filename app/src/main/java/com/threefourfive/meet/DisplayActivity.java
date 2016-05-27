package com.threefourfive.meet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class DisplayActivity extends AppCompatActivity {

    TextView tv;
    String my_id;
    private static final String APP_KEY = "eyJzaWduYXR1cmUiOiJCYnZiSGI2SGw4b0h4OUdEbWxRU0VzQ0ZRUnorQzZLeHQzOFBGajRYV1JjZ1lwRU1RSmRhKzc4UjRsY0NHays3aTVtc0xSaWplZmlBaDI3WEhnaDJtVHhEOUNWRkxWSllISkVIMWFYQTB2VTd2eFF1NlJKcktJUFhlZGR5Z2NML0gyTXBEVWVSUmdCRHVhZ1pOUHJEN1JRRU9DNWhiRHNwTG92Q3gzWE40UTQ9IiwiYXBwSWQiOjE2MDYsInZhbGlkVW50aWwiOjE3MDIwLCJhcHBVVVVJRCI6IkFGMERGMDg5LUREMTUtNDcwOS05NEI3LUFEMjkxODQ5MkQwNiJ9";
    ArrayList<String> proximity_ids = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        tv = (TextView)findViewById(R.id.tv);
        Intent intent = getIntent();
        my_id = intent.getStringExtra("my_id");
        tv.setText(my_id);
    }
}



/*
TO:DO

1. Decide on the data structure for handling the data about the user id and decide if youre gonna pull photos as well...we can even store in a json format
2. Decide on the REST endpoint to call for..
3. Think about a custom View for presenting data about the ranked user, Put in an custom adapter,,
4. Are you gonna sort the data in server or in the client
5. use org.JSON lib for this. maybe even jackson?
 */