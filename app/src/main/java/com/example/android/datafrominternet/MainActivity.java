/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.datafrominternet;

import android.content.DialogInterface;
import android.net.Network;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.datafrominternet.utilities.NetworkUtils;
import com.example.android.datafrominternet.utilities.SelectedCurrency;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    //    private EditText mSearchBoxEditText;
//    private TextView mUrlDisplayTextView;
    private TextView mSearchResultsTextView;
    private final int REFRESH_MILLISECONDS = 1000;
    private boolean dialogUp = false;
    private URL githubSearchUrl = null;
    private RadioGroup radioGroupCurrency = null;
//    private ProgressBar progressBar = null;
    private String currencyServiceResults = null;
    private TextView usdResultBox = null;
    private TextView eurResultBox = null;
    private TextView btcResultBox = null;
    private TextView ethResultBox = null;
    private TextView xrpResultBox = null;
    RadioButton usdRadioButton = null;
    RadioButton eurRadioButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mSearchBoxEditText = (EditText) findViewById(R.id.et_search_box);

        mSearchResultsTextView = (TextView) findViewById(R.id.tv_price_search_results_json);
//        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        usdResultBox = (TextView) findViewById(R.id.usd_box);
        eurResultBox = (TextView) findViewById(R.id.eur_box);
        btcResultBox = (TextView) findViewById(R.id.btc_box);
        ethResultBox = (TextView) findViewById(R.id.eth_box);
        xrpResultBox = (TextView) findViewById(R.id.xrp_box);

        createTimer();

        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("Warning");
        dialog.setMessage("Prices are obtained from a third party, user accepts all responsibillity for verifying prices.");
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                createTimer();
            }
        });
        final AlertDialog alert = dialog.create();
        alert.show();

        addListenerOnRadioButton();

        // set default on button
        radioGroupCurrency.check(usdRadioButton.getId());
    }

    public void addListenerOnRadioButton() {
        radioGroupCurrency = (RadioGroup) findViewById(R.id.radioCurrency);
        usdRadioButton = (RadioButton) findViewById(R.id.radioButtonUsd);
        eurRadioButton = (RadioButton) findViewById(R.id.radioButtonEur);

        usdRadioButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NetworkUtils.selectedCurrency = SelectedCurrency.USD;
                        makePriceSearchQuery();
                    }
                }
        );

        eurRadioButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NetworkUtils.selectedCurrency = SelectedCurrency.EUR;
                        makePriceSearchQuery();
                    }
                }
        );
    }

    private void createTimer() {
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                makePriceSearchQuery();
                System.out.println("Calling makePriceSearchQuery");
            }

            @Override
            public void onFinish() {
                showContinueDialog();
            }
        }.start();
    }

    private void showContinueDialog() {
        if (dialogUp == false) {
            dialogUp = true;
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Are you still there?");
            dialog.setMessage("Do you still want updates?");
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    createTimer();
                    dialogUp = false;
                }
            });
            final AlertDialog alert = dialog.create();
            alert.show();
        }
    }

    /**
     * This method retrieves the search text from the EditText, constructs the
     * URL (using {@link NetworkUtils}) for the github repository you'd like to find, displays
     * that URL in a TextView, and finally fires off an AsyncTask to perform the GET request using
     * our {@link PriceQueryTask}
     */
    private void makePriceSearchQuery() {
//        progressBar.setVisibility(View.VISIBLE);
        githubSearchUrl = NetworkUtils.buildUrl();
        new PriceQueryTask().execute(githubSearchUrl);
    }

    public class PriceQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String priceSearchResults = null;
            try {
                priceSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return priceSearchResults;
        }

        @Override
        protected void onPostExecute(String priceSearchResults) {
            if (priceSearchResults != null && !priceSearchResults.equals("")) {
                currencyServiceResults = priceSearchResults;
                updateVisibleResults(priceSearchResults);
                mSearchResultsTextView.setText("Result: " + priceSearchResults + "\nUpdate Time: " + System.currentTimeMillis());
//                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void updateVisibleResults(String response) {
        try {
//            JSONArray arr = new JSONArray(response);
//            JSONObject obj = arr.getJSONObject(0);
            JSONObject obj = new JSONObject(response);

            if (NetworkUtils.selectedCurrency != SelectedCurrency.BTC) {
                String btc = obj.getString("BTC");
                btcResultBox.setText("Bit Coin: " + btc);
            }
            if (NetworkUtils.selectedCurrency != SelectedCurrency.EUR) {
                String eur = obj.getString("EUR");
                eurResultBox.setText("Euro: " + eur);
            }
            if (NetworkUtils.selectedCurrency != SelectedCurrency.ETH) {
                String eth = obj.getString("ETH");
                ethResultBox.setText("Ethereum: " + eth);
            }
            if (NetworkUtils.selectedCurrency != SelectedCurrency.USD) {
                String usd = obj.getString("USD");
                usdResultBox.setText("USD: " + usd);
            }
            if (NetworkUtils.selectedCurrency != SelectedCurrency.XRP) {
                String xrp = obj.getString("XRP");
                xrpResultBox.setText("Ripple: " + xrp);
            }

            usdResultBox.setVisibility(View.VISIBLE);
            eurResultBox.setVisibility(View.VISIBLE);
            btcResultBox.setVisibility(View.VISIBLE);
            ethResultBox.setVisibility(View.VISIBLE);
            xrpResultBox.setVisibility(View.VISIBLE);

            switch (NetworkUtils.selectedCurrency) {
                case USD:
                    usdResultBox.setVisibility(View.INVISIBLE);
                    break;
                case EUR:
                    eurResultBox.setVisibility(View.INVISIBLE);
                    break;
                case XRP:
                    xrpResultBox.setVisibility(View.INVISIBLE);
                    break;
                case BTC:
                    btcResultBox.setVisibility(View.INVISIBLE);
                    break;
                default:
                    ethResultBox.setVisibility(View.INVISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(MainActivity.this, "Something has gone wrong with the currency service...", Toast.LENGTH_LONG);
            toast.show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.action_info) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("Informaion");
            dialog.setMessage("Prices are refreshed every " + REFRESH_MILLISECONDS + " milliseconds. \n\nPublic Data Origin: \n\n" + githubSearchUrl.toString());
            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    createTimer();
                }
            });
            final AlertDialog alert = dialog.create();
            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
