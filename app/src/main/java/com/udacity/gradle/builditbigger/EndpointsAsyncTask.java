package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.pushpal.jokinggraphically.JokeActivity;
import com.udacity.gradle.builditbigger.backend.myApi.MyApi;

import java.io.IOException;

class EndpointsAsyncTask extends AsyncTask<Void, Void, String> {
    private final String TAG = EndpointsAsyncTask.class.getSimpleName();
    private static MyApi myApiService = null;
    private Context context;
    private ProgressBar progressBar;

    EndpointsAsyncTask(Context context, ProgressBar progressBar) {
        this.context = context;
        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(Void... params) {

        if (myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")

                    // For testing on a physical device with a local development server.
                    // .setRootUrl("http://<Computer's IP Address>/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });

            myApiService = builder.build();
        }

        try {
            return myApiService.pullJoke().execute().getJoke();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (progressBar != null && progressBar.isShown())
            progressBar.setVisibility(View.INVISIBLE);
        Intent jokeIntent = new Intent(context, JokeActivity.class);
        jokeIntent.putExtra("joke", result);
        context.startActivity(jokeIntent);
    }
}