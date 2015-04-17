package tianli.dispphotos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static instagram.InstagramApp.streamToString;

public class GridViewActivity extends Activity {

	private SharedPreferences sharedPref;
	private SharedPreferences.Editor editor;

	private static final String SHARED = "Instagram_Preferences";
	private static final String API_USERNAME = "username";
	private static final String API_ID = "id";
	private static final String API_NAME = "name";
	private static final String API_ACCESS_TOKEN = "access_token";

	private final String TAGSELFIE_URL = "https://api.instagram.com/v1/tags/selfie/media/recent";

	private static final String TAG = "InstagramAPI";
	private JSONArray jsonArr;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gridview_activity);

		sharedPref = getSharedPreferences(SHARED, Context.MODE_PRIVATE);
		URL url;
		try {
			url = new URL(TAGSELFIE_URL + "?access_token=" + sharedPref.getString(API_ACCESS_TOKEN, null));
			new GetImagesTask().execute(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}

	public void fetchTagSelfie() {
		Log.i("", "Fetching tag selfie info");

	}

	private class GetImagesTask extends AsyncTask<URL, Void, Integer> {
		private final ProgressDialog dialog = new ProgressDialog(GridViewActivity.this);

		protected void onPreExecute() {
			dialog.setMessage("Fetching Images...");
			dialog.show();
		}


		protected Integer doInBackground(URL... urls) {
			try {
				Log.d(TAG, "Opening URL " + urls[0].toString());
				HttpURLConnection urlConnection = (HttpURLConnection) urls[0].openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.setDoInput(true);
				//urlConnection.setDoOutput(true);
				urlConnection.connect();
				String response = streamToString(urlConnection.getInputStream());

				System.out.println(response);
				JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
				jsonArr = jsonObj.getJSONArray("data");
				System.out.println("jsonArr" + jsonArr);
				Log.i(TAG, "Images fetched successfully");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return jsonArr.length();
		}

		protected void onPostExecute(Integer result) {
			dialog.dismiss();
			GridView gv = (GridView) findViewById(R.id.grid_view);
			gv.setAdapter(new GridViewAdapter(GridViewActivity.this, jsonArr));
			gv.setOnScrollListener(new ScrollListener(GridViewActivity.this));
		}
	}
}
