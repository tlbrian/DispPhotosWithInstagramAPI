package tianli.dispphotos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

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
	private boolean scrollMode = true;
	private Button toggleButton;
	private ScrollListener mScrollListener;
	private View.OnTouchListener mTouchListener;
	private GridView gv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gridview_activity);

		toggleButton = (Button) findViewById(R.id.toggleButton);
		toggleButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (scrollMode) {
					scrollMode = false;
					Toast.makeText(GridViewActivity.this, "Enter Drag&Drop Mode", Toast.LENGTH_SHORT).show();
					toggleButton.setText("Click to scroll");
					gv.setOnTouchListener(mTouchListener);
					gv.setOnScrollListener(null);
				}
				else {
					scrollMode = true;
					Toast.makeText(GridViewActivity.this, "Enter Scroll mode", Toast.LENGTH_SHORT).show();
					toggleButton.setText("Click to drag&drop");
					gv.setOnTouchListener(null);
					gv.setOnScrollListener(mScrollListener);
				}
			}
		});

		sharedPref = getSharedPreferences(SHARED, Context.MODE_PRIVATE);
		URL url;
		try {
			url = new URL(TAGSELFIE_URL + "?access_token=" + sharedPref.getString(API_ACCESS_TOKEN, null));
			new GetImagesTask().execute(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}

	private class GetImagesTask extends AsyncTask<URL, Void, Integer> {
		private final ProgressDialog dialog = new ProgressDialog(GridViewActivity.this);

		protected void onPreExecute() {
			dialog.setMessage("Fetching Images...");
			dialog.show();
		}

		//get image info for tag selfie
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

			gv = (GridView) findViewById(R.id.grid_view);
			gv.setAdapter(new GridViewAdapter(GridViewActivity.this, jsonArr));

			mScrollListener = new ScrollListener(GridViewActivity.this);
			gv.setOnScrollListener(mScrollListener);

			mTouchListener = new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {

					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						GridView parent = (GridView) v;

						//initial position
						int x = (int) event.getX();
						int y = (int) event.getY();

						final int position = parent.pointToPosition(x, y);
						if (position > AdapterView.INVALID_POSITION) {

							int count = parent.getChildCount();
							for (int i = 0; i < count; i++) {
								View curr = parent.getChildAt(i);
								curr.setOnDragListener(new View.OnDragListener() {

									@Override
									public boolean onDrag(View v, DragEvent event) {

										boolean result = true;
										int action = event.getAction();
										switch (action) {
											case DragEvent.ACTION_DRAG_STARTED:
											case DragEvent.ACTION_DRAG_LOCATION:
											case DragEvent.ACTION_DRAG_ENTERED:
											case DragEvent.ACTION_DRAG_EXITED:
											case DragEvent.ACTION_DRAG_ENDED:
												break;
											case DragEvent.ACTION_DROP:
												if (event.getLocalState() == v) {
													result = false;
												} else {
													View dropped = (View) event.getLocalState();

													GridView parent = (GridView) dropped.getParent();
													GridViewAdapter adapter = (GridViewAdapter) parent.getAdapter();
													List<String> items = adapter.getItems();

//													//target position
													View target = v;
													int posTarget = (Integer) target.getTag();

													//initial position
													String dragURL = items.get(position);

													//change position
													items.remove(position);
													items.add(posTarget, dragURL);
													adapter.notifyDataSetChanged();
												}
												break;
											default:
												result = false;
												break;
										}
										return result;
									}
								});
							}

							int relativePosition = position - parent.getFirstVisiblePosition();

							View target = (View) parent.getChildAt(relativePosition);

							ClipData data = ClipData.newPlainText("DragData", "dragText");
							target.startDrag(data, new View.DragShadowBuilder(target), target, 0);
						}
					}
					return false;
				}
			};
		}
	}
}
