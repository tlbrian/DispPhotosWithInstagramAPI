package tianli.dispphotos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import instagram.InstagramApp;


public class MainActivity extends ActionBarActivity {

	public static final String APIURL = "https://api.instagram.com/v1";
	public static final String CALLBACKURL = "http://phantom.com";
	private static final String AUTHURL = "https://api.instagram.com/oauth/authorize/";
	private static final String TOKENURL = "https://api.instagram.com/oauth/access_token";

	private final String client_id = "f639058534f94f70870e942af04757f3";
	private final String client_secret = "7bb18ff37d184c4fb1ff762cac1c1048";
	private final String callback_url = "http://phantom.com";

	private InstagramApp mApp;
	private Button btnConnect;
	private TextView tvSummary;
	private Button btnDisp;

	private InstagramApp.OAuthAuthenticationListener listener;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		listener = new InstagramApp.OAuthAuthenticationListener() {

			@Override
			public void onSuccess() {
				tvSummary.setText("Connected as " + mApp.getUserName());
				btnConnect.setText("Disconnect");
			}

			@Override
			public void onFail(String error) {
				Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
			}
		};

		mApp = new InstagramApp(this, client_id, client_secret, callback_url);
		mApp.setListener(listener);

		tvSummary = (TextView) findViewById(R.id.tvSummary);

		btnConnect = (Button) findViewById(R.id.btnConnect);
		btnConnect.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				if (mApp.hasAccessToken()) {
					final AlertDialog.Builder builder = new AlertDialog.Builder(
							MainActivity.this);
					builder.setMessage("Disconnect from Instagram?")
							.setCancelable(false)
							.setPositiveButton("Yes",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											mApp.resetAccessToken();
											btnConnect.setText("Connect");
											tvSummary.setText("Not connected");
											btnDisp.setEnabled(false);
										}
									})
							.setNegativeButton("No",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});
					final AlertDialog alert = builder.create();
					alert.show();
				} else {
					mApp.authorize();
				}
			}
		});

		btnDisp = (Button) findViewById(R.id.btnDispImages);
		if (mApp.hasAccessToken()) {
			btnDisp.setEnabled(true);
		}
		btnDisp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MainActivity.this, SampleGridViewActivity.class);
				startActivity(intent);
			}
		});

		if (mApp.hasAccessToken()) {
			tvSummary.setText("Connected as " + mApp.getUserName());
			btnConnect.setText("Disconnect");
		}

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
