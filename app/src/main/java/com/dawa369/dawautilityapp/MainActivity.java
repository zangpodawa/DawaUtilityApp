package com.dawa369.dawautilityapp;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    String[] shortLanguages;
    String currentLanguage;

    final String CORE_API_URL = "https://api.exchangerate-api.com/v4/latest/";
    Handler handler;
    Button button;
    EditText currencyToBeConverted;
    EditText currencyConverted;
    Spinner convertToDropdown;
    Spinner convertFromDropdown;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shortLanguages = getResources().getStringArray(R.array.shortLanguages);
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        currentLanguage = preferences.getString("my_lang", "aus");
        setLocal(currentLanguage);

        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.app_name);

        //Initialization
        currencyToBeConverted = findViewById(R.id.currency_to_be_converted);
        currencyConverted = findViewById(R.id.currency_converted);
        convertFromDropdown = findViewById(R.id.fromSpinner);
        convertToDropdown = findViewById(R.id.toSpinner);
        button = findViewById(R.id.convertButton);

        //initialize handler
        handler = new Handler();
        //display the current time
        displayTime();
    }

    //to create the options menu for the app, which includes a language option
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.language).setTitle(currentLanguage);
        return super.onCreateOptionsMenu(menu);
    }

    //called when an item in the options menu is selected.
    // If the language option is selected, it calls the changeLanguage() method.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.language){
            changeLanguage();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //creates an alert dialog with a list of languages to choose from.
    // When a language is selected, it calls the setLocal() method to update
    // the app's locale and recreate the activity.
    private void changeLanguage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Language...");
        builder.setSingleChoiceItems(shortLanguages, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setLocal(shortLanguages[i]);
                recreate();
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //sets the app's locale based on the language selected and saves the
    // language setting in shared preferences.
    private void setLocal(String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.setLocale(locale);
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        sharedPreferences.edit().putString("my_lang", lang).apply();
    }

    //displays the current time on the UI by using a handler to post a runnable that updates
    // the time every second.
    private void displayTime() {
        //time tick by second
        handler.post(new Runnable() {
            @Override
            public void run() {
                String currentTime = Helper.getCurrentTime();
                //display the current time
                TextView timeView = findViewById(R.id.timeView);
                timeView.setText(currentTime);
                handler.postDelayed(this, 1000); // 1000 ms = 1s
            }
        });
    }

    //called when the convert button is clicked. It retrieves the selected
    // currencies from the Spinners, calls an API to retrieve the exchange rate,
    // calculates the converted amount, and displays it on the UI. It does this
    // in a separate thread to avoid blocking the UI thread.
    public void convertClicked(View view) {
        //get user selection from fromSpinner
        int convertAmountFrom = convertFromDropdown.getSelectedItemPosition();

        //get user selection from fromSpinner
        int convertAmountTo = convertToDropdown.getSelectedItemPosition();
        //get the array currencies
        String[] currencies = getResources().getStringArray(R.array.currencies);
        Log.i("selectedItem", currencies[convertAmountFrom]);
        Log.i("selectedItem", currencies[convertAmountTo]);

        new Thread(new Runnable() {
            @Override
            public void run() {
                //get information
                String rateInfo = Helper.getInfo(CORE_API_URL + currencies[convertAmountFrom]); // run in another thread
                //do things on the main thread by runOnUiThread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (rateInfo != null) {
                            Log.i("rateInfo", rateInfo);
                            //get the rate between fromCurrency and toCurrency
                            try {
                                JSONObject jsonObject = new JSONObject(rateInfo);
                                JSONObject rateObject = jsonObject.getJSONObject("rates");
                                double currency = Double.parseDouble(currencyToBeConverted.getText().toString());
                                if (currency != 0 && currency == 0){

                                }
                                double convertedAmount = rateObject.getDouble(currencies[convertAmountTo]) * currency;
                                currencyConverted.setText(String.valueOf(convertedAmount));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            Log.i("rateInfo", "no data");

                        }
                    }
                });
            }
        }).start();


    }


}