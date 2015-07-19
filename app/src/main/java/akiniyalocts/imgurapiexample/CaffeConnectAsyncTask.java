package akiniyalocts.imgurapiexample;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.PriorityQueue;

import akiniyalocts.imgurapiexample.activities.MainActivity;
import akiniyalocts.imgurapiexample.imgurmodel.ImgurAPI;
import akiniyalocts.imgurapiexample.utils.NetworkUtils;
import retrofit.RestAdapter;
import retrofit.mime.TypedFile;

/**
 * Created by trijones on 7/17/15.
 */
public class CaffeConnectAsyncTask extends AsyncTask<Void, Void, Void>{

    private final Activity activity;
    private final String uploadUrl;
    String[] correlationKeys;
    String[] correlationValues;

    public CaffeConnectAsyncTask(Activity activity, String imageUploadUrl) {
        this.activity = activity;
        this.uploadUrl = imageUploadUrl;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if(NetworkUtils.isConnected(activity)) {
            if (NetworkUtils.connectionReachable()) {

                try {
                    String url = "http://demo.caffe.berkeleyvision.org/classify_url?imageurl=" + this.uploadUrl;
                    Document doc = Jsoup.connect(url).get();
                    Elements correlations = doc.select("#infopred ul li");
                    this.correlationKeys = correlations.select("a").text().split(" ");
                    this.correlationValues = correlations.select("span").text().split(" ");
                    HttpClient httpclient = new DefaultHttpClient();

                    HttpGet request = new HttpGet();
                    URI website = new URI("http://first-1010.appspot.com?word="+this.correlationKeys[0]);
                    request.setURI(website);
                    HttpResponse response = httpclient.execute(request);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (correlationKeys != null && correlationValues != null) {

            // IT WAS SUCCESSFUL, CHAIN ANOTHER REQUEST HERE
            Toast.makeText(this.activity,
                    "Corrleation received " + Arrays.toString(this.correlationKeys) + " " + Arrays.toString(this.correlationValues),
                    Toast.LENGTH_LONG).show();

        }
    }
}
