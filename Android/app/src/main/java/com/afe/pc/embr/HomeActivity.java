package com.afe.pc.embr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import java.util.ArrayList;
import common.*;

/*
*  Making this view be a temporary Search on start, to maintain consistency with the iOS version
*
*  Author: Tyler Davis
*  Date: 10.9.15 - 2:12AM
 */
public class HomeActivity extends AppCompatActivity {

    Toolbar appBar;
    private ListView listView;
    private EditText action_bar_edit_text;
    private String[] values = new String[] {"ItemView", "Search", "Profile", "Library", "RecommendedItems", "Login", "test"};
    private ArrayList<String> listview_values = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        populate_listview();

        appBar = (Toolbar) findViewById(R.id.action_bar);
        ImageButton action_bar_button = (ImageButton) findViewById(R.id.action_bar_button);
        action_bar_edit_text = (EditText) findViewById(R.id.action_bar_edit_text);
        action_bar_edit_text.setHint("Home");

        action_bar_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // using action_bar_edit_text.getText().toString() to extract the search input
                // send that value to the backend, and everything that gets returned needs to
                // be put into a string array which will be used to populate the listview.
                listview_values.clear();
                for (int i = 0; i < values.length; i++)
                    if (values[i].equalsIgnoreCase(action_bar_edit_text.getText().toString()))
                        listview_values.add(values[i]);

                action_bar_edit_text.setText("");
                populate_listview();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getTitle().toString()) {
            case "Recommended Items": {
                Intent intent = new Intent(this, RecommendedItems.class);
                startActivity(intent);
                break;
            }
            case "Home": {
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                break;
            }
            case "Profile": {
                Intent intent = new Intent(this, Profile.class);
                startActivity(intent);
                break;
            }
            case "Libraries": {
                Intent intent = new Intent(this, Library.class);
                startActivity(intent);
                break;
            }
            case "Go to SearchResults": {
                Intent intent = new Intent(this, SearchResults.class);
                startActivity(intent);
                break;
            }
            case "Login": {
                Intent intent = new Intent(this, Login.class);
                startActivity(intent);
                break;
            }
            default: {
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void populate_listview() {
        listView = (ListView) findViewById(R.id.list);

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, listview_values);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itemPosition = position;
                String itemValue = (String) listView.getItemAtPosition(itemPosition);
                openActivity(itemValue);
            }
        });
    }

    public void openActivity(String S) {
        switch (S) {
            case "RecommendedItems": {
                Intent intent = new Intent(this, RecommendedItems.class);
                startActivity(intent);
                break;
            }
            case "ItemView": {
                Intent intent = new Intent(this, ItemView.class);
                startActivity(intent);
                break;
            }
            case "Profile": {
                Intent intent = new Intent(this, Profile.class);
                startActivity(intent);
                break;
            }
            case "Library": {
                Intent intent = new Intent(this, Library.class);
                startActivity(intent);
                break;
            }
            case "SearchResults": {
                Intent intent = new Intent(this, SearchResults.class);
                startActivity(intent);
                break;
            }
            case "Login": {
                Intent intent = new Intent(this, Login.class);
                startActivity(intent);
                break;
            }
            default: {
                Toast.makeText(HomeActivity.this, "Not Available", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }
}
