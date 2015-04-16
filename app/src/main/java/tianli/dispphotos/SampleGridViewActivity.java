package tianli.dispphotos;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.HttpURLConnection;
import java.net.URL;

import static instagram.InstagramApp.streamToString;

public class SampleGridViewActivity extends Activity {

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

		fetchTagSelfie();

		GridView gv = (GridView) findViewById(R.id.grid_view);
		gv.setAdapter(new SampleGridViewAdapter(this, jsonArr));
		gv.setOnScrollListener(new SampleScrollListener(this));

	}

	public void fetchTagSelfie() {
		new Thread() {
			@Override
			public void run() {
				Log.i("", "Fetching tag selfie info");
				try {
					URL url = new URL(TAGSELFIE_URL + "?access_token=" + sharedPref.getString(API_ACCESS_TOKEN, null));

					Log.d(TAG, "Opening URL " + url.toString());
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");
					urlConnection.setDoInput(true);
					urlConnection.setDoOutput(true);
					urlConnection.connect();
					String response = streamToString(urlConnection.getInputStream());

					System.out.println(response);
					JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
					jsonArr = jsonObj.getJSONArray("data");

					Log.i(TAG, "Images fetched successfully");
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		}.start();
	}
}
