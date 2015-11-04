package com.afe.pc.embr;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import common.Strings;
import utilities.HttpConnect;

/*
*  Making this view be a temporary Search on start, to maintain consistency with the iOS version
*
*  Author: Tyler Davis
*  Date: 10.9.15 - 2:12AM
 */
public class Search extends AppCompatActivity {

    private boolean isLoggedIn = false;
    private String[] emptyStrings = new String[]{"", "", "", "", "", "", "", "", "", "", "", ""};
    private ArrayList<String> genres = new ArrayList<>();
    private ArrayList<String> description = new ArrayList<>();
    private ArrayList<String> creator = new ArrayList<>();
    private ArrayList<String> image = new ArrayList<>();
    private ArrayList<String> title = new ArrayList<>();
    private ArrayList<Integer> id = new ArrayList<>();
    private ArrayList<ArrayList<String>> listview_values = new ArrayList<>();
    private ArrayList<String> listview_values_temp = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle search_bundle = getIntent().getExtras();
        String loggedIn_status = "";
        try {
            loggedIn_status = search_bundle.getString("LoggedIn");
        } catch (Exception e) {
        }
        super.onCreate(savedInstanceState);
        if (loggedIn_status.equalsIgnoreCase("true"))
            isLoggedIn = true;
        setContentView(R.layout.search_layout);
        populate_listview_on_start(appendStrings(listview_values_temp, emptyStrings), (ListView) findViewById(R.id.search_listview));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        if (isLoggedIn)
            menu.getItem(4).setTitle("Logout");
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        if (null != searchView) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
        }
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                // there is a known issue, I think it is based on timing of the query and the call
                // of populate_listview. basically you need to have a value in each arraylist
                // before you call getData or it will crash saying that it has a size of 0.
                description.add("");
                creator.add("");
                image.add("");
                title.add("");
                listview_values.add(description);
                listview_values.add(creator);
                listview_values.add(image);
                listview_values.add(title);
                getData(query);
                populate_listview(listview_values, (ListView) findViewById(R.id.search_listview));
                return true;
            }
        };
        if (searchView != null)
            searchView.setOnQueryTextListener(queryTextListener);
        else
            Toast.makeText(Search.this, "Search Not Available", Toast.LENGTH_SHORT).show();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String s = item.getTitle().toString();
        if (s.equals("Recommended Items")) {
            Intent intent = new Intent(this, RecommendedItems.class);
            startActivity(intent);
        } else if (s.equals("Home")) {
            Intent intent = new Intent(this, Search.class);
            startActivity(intent);
        } else if (s.equals("Profile")) {
            Intent intent = new Intent(this, Profile.class);
            startActivity(intent);
        } else if (s.equals("Libraries")) {
            Intent intent = new Intent(this, Library.class);
            startActivity(intent);
        } else if (s.equals("SearchResults")) {
            Intent intent = new Intent(this, SearchResults.class);
            startActivity(intent);
        } else if (s.equals("Login") || s.equals("Logout")) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        return;
    }

    public void populate_listview(final ArrayList<ArrayList<String>> values, final ListView listView) {

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.single_row, R.id.results_Title, values.get(3));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = values.get(3).get(position);
                String author = values.get(1).get(position);
                String picture = values.get(2).get(position);
                openItemViewActivity(title, author, picture);
            }
        });
    }

    public void populate_listview_on_start(ArrayList<String> values, final ListView listView) {

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
        listView.setAdapter(adapter);
    }

    public void openItemViewActivity(String title, String author, String picture) {
        Intent intent = new Intent(this, ItemView.class);
        intent.putExtra("Book Title", title);
        intent.putExtra("Book Author", author);
        intent.putExtra("Book Picture", picture);
        startActivity(intent);
    }

    public ArrayList<String> appendStrings(ArrayList<String> arrayList, String[] stringArray) {
        for (int count = 0; count < stringArray.length; count++)
            arrayList.add(stringArray[count]);
        return arrayList;
    }

    public void getData(String s) {
        HttpConnect.requestJson("http://52.88.5.108/cgi-bin/SearchItems.py?title=" + s, Request.Method.GET, null, new HttpResult() {

            @Override
            public void onCallback(JSONObject response, boolean success) {
                if (!success) {
                    Toast.makeText(Search.this, "Success = false", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        JSONArray jsonArray = response.getJSONArray("response");
                        clearAll();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            description.add(jsonArray.getJSONObject(i).getString("description"));
                            creator.add(jsonArray.getJSONObject(i).getString("creator"));
                            image.add(jsonArray.getJSONObject(i).getString("image"));
                            title.add(jsonArray.getJSONObject(i).getString("title"));
                        }
                        listview_values.add(description);
                        listview_values.add(creator);
                        listview_values.add(image);
                        listview_values.add(title);
                        populate_listview(listview_values, (ListView) findViewById(R.id.search_listview));
                    } catch (Exception e) {}
                }
            }
        });
    }

    public void clearAll() {
        listview_values.clear();
        genres.clear();
        description.clear();
        creator.clear();
        image.clear();
        title.clear();
        id.clear();
    }
}
