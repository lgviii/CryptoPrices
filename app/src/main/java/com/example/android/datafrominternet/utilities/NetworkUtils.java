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
package com.example.android.datafrominternet.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {

    public static SelectedCurrency selectedCurrency = SelectedCurrency.USD;

    final static String GITHUB_PRICES_FOR_USD_URL = "https://min-api.cryptocompare.com/data/price?fsym=USD&tsyms=BTC,ETH,EUR,XRP";
    final static String GITHUB_PRICES_FOR_EUR_URL = "https://min-api.cryptocompare.com/data/price?fsym=EUR&tsyms=ETH,USD,BTC,XRP";
    final static String GITHUB_PRICES_FOR_BTC_URL = "https://min-api.cryptocompare.com/data/price?fsym=BTC&tsyms=ETH,USD,EUR,XRP";
    final static String GITHUB_PRICES_FOR_ETH_URL = "https://min-api.cryptocompare.com/data/price?fsym=ETH&tsyms=BTC,USD,EUR,XRP";
    final static String GITHUB_PRICES_FOR_XRP_URL = "https://min-api.cryptocompare.com/data/price?fsym=XRP&tsyms=BTC,USD,EUR,XRP";


    final static String PARAM_QUERY = "q";

    /*
     * The sort field. One of stars, forks, or updated.
     * Default: results are sorted by best match if no field is specified.
     */
    final static String PARAM_SORT = "sort";
    final static String sortBy = "stars";

    /**
     * Builds the URL used to query GitHub.
     *
     * @return The URL to use to query the GitHub.
     */
    public static URL buildUrl() {
//        Uri builtUri = Uri.parse(GITHUB_BASE_URL).buildUpon()
//                .appendQueryParameter(PARAM_QUERY, githubSearchQuery)
//                .appendQueryParameter(PARAM_SORT, sortBy)
//                .build();

        Uri builtUri = null;

        switch(selectedCurrency) {
            case USD:
                builtUri = Uri.parse(GITHUB_PRICES_FOR_USD_URL);
                break;
            case EUR:
                builtUri = Uri.parse(GITHUB_PRICES_FOR_EUR_URL);
                break;
            case XRP:
                builtUri = Uri.parse(GITHUB_PRICES_FOR_XRP_URL);
                break;
            case BTC:
                builtUri = Uri.parse(GITHUB_PRICES_FOR_BTC_URL);
                break;
            default :
                Uri.parse(GITHUB_PRICES_FOR_ETH_URL);
        }

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}