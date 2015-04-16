package tianli.dispphotos;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

final class SampleGridViewAdapter extends BaseAdapter {
	private final Context context;
	private final JSONArray jsonArr;

	public SampleGridViewAdapter(Context context, JSONArray jsonArr) {
		this.context = context;
		this.jsonArr = jsonArr;
	}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
	    SquaredImageView view = (SquaredImageView) convertView;
	    if (view == null) {
		    view = new SquaredImageView(context);
		    view.setScaleType(CENTER_CROP);
	    }

	    // Get the image URL for the current position.
	    String url = getItem(position);

	    // Trigger the download of the URL asynchronously into the image view.
	    Picasso.with(context) //
			    .load(url) //
			    .fit() //
			    .tag(context) //
			    .into(view);

	    return view;
    }

	@Override public int getCount() {
		return jsonArr.length();
	}

	@Override public String getItem(int position) {
		try {
		  return jsonArr.getJSONObject(position).getJSONObject("images").getString("low_resolution");
		} catch (JSONException e) {
		  e.printStackTrace();
		}
		return null;
	}

	@Override public long getItemId(int position) {
		return position;
	}
}
