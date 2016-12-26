package app.com.biodiv.speciestraits;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Build;
import android.app.ProgressDialog;

/**
 * Created by sravanthi on 25/12/16.
 */

public class LoadRestWebServiceTask extends AsyncTask<String, Void, JSONObject> {

    //private final HttpClient client = new DefaultHttpClient();
    private static final int CONNECTION_TIMEOUT = 100000;
    private static final int DATARETRIEVAL_TIMEOUT = 100000;
    private ProgressDialog dialog;
    private Context mContext;

    public LoadRestWebServiceTask(Listener listener, Context context) {
        mListener = listener;
        mContext = context;
    }

    public interface Listener {
        void onDataLoaded(JSONObject json);

        void onError();
    }

    private Listener mListener;

    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(mContext);
        dialog.setMessage("Loading..");
        dialog.show();
    }

    protected JSONObject doInBackground(String... urls) {
        return requestWebService(urls[0]);
    }

    public static JSONObject requestWebService(String serviceUrl) {
        disableConnectionReuseIfNecessary();

        HttpURLConnection urlConnection = null;
        try {
            // create connection
            URL urlToRequest = new URL(serviceUrl);
            urlConnection = (HttpURLConnection)
                    urlToRequest.openConnection();
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(DATARETRIEVAL_TIMEOUT);

            // handle issues
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                System.out.println("unauthorized");
                // handle unauthorized (if service requires user login)
            } else if (statusCode != HttpURLConnection.HTTP_OK) {
                // handle any other errors, like 404, 500,..
                System.out.println(statusCode);
            }

            // create JSON object from content
            InputStream in = new BufferedInputStream(
                    urlConnection.getInputStream());
            return new JSONObject(getResponseText(in));

        } catch (MalformedURLException e) {
            // URL is invalid
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            // data retrieval or connection timed out
            e.printStackTrace();
        } catch (IOException e) {
            // could not read response body
            // (could not create input stream)
            e.printStackTrace();
        } catch (JSONException e) {
            // response body is no valid JSON string
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        System.out.println("returning null");
        return null;
    }

    /**
     * required in order to prevent issues in earlier Android version.
     */
    private static void disableConnectionReuseIfNecessary() {
        // see HttpURLConnection API doc
        if (Integer.parseInt(Build.VERSION.SDK)
                < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    private static String getResponseText(InputStream inStream) {
        // very nice trick from
        // http://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
        return new Scanner(inStream).useDelimiter("\\A").next();
    }

    protected void onPostExecute(JSONObject content) {
        // Close progress dialog
        dialog.dismiss();
        if (content != null) {
            mListener.onDataLoaded(content);
        } else {
            mListener.onError();
        }
    }
}

