package tianli.dispphotos;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

final class GridViewAdapter extends BaseAdapter {
	private final Context context;
	private final ArrayList<String> items;

	public GridViewAdapter(Context context, JSONArray jsonArr) {
		this.context = context;
		items = new ArrayList<>();
		try {
		for (int i = 0; i < jsonArr.length(); i++) {
				items.add(jsonArr.getJSONObject(i).getJSONObject("images").getJSONObject("low_resolution").getString("url"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
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

	    view.setTag(position);

	    return view;
    }

	@Override public int getCount() {
		Log.i("picLength", "" + items.size());
		return items.size();
	}

	public ArrayList<String> getItems() {
		return items;
	}

	@Override public String getItem(int position) {
		return items.get(position);
	}

	@Override public long getItemId(int position) {
		return position;
	}
}
