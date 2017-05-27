package com.codebrat.youtubedemo.adaptor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.codebrat.youtubedemo.R;
import com.codebrat.youtubedemo.model.Model;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Shikhar on 27-05-2017.
 */

public class ModelAdaptor extends BaseAdapter {

	private final ArrayList<Model> modelArrayList;
	private Activity mContext;

	public ModelAdaptor(Activity mContext, ArrayList<Model> modelArrayList) {
		this.mContext = mContext;
		this.modelArrayList = modelArrayList;
	}

	@Override
	public int getCount() {
		if(modelArrayList==null || modelArrayList.isEmpty())
			return 0;
		else
			return modelArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		if(modelArrayList!=null || !modelArrayList.isEmpty())
			return modelArrayList.get(position);
		else
			return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		Model model = (Model) getItem(position);
		LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (convertView == null) {
			convertView = vi.inflate(R.layout.list_item_model, null);
			holder = createViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Typeface face = Typeface.createFromAsset(mContext.getAssets(),
			"fonts/Quicksand-Regular.ttf");
		holder.comment.setTypeface(face);
		holder.videoId.setTypeface(face);
		holder.time.setTypeface(face);

		holder.videoId.setText(model.getVideoId());
		holder.time.setText(convertTime(model.getTime()));
		holder.comment.setText(model.getComment());
		return convertView;
	}

	private ViewHolder createViewHolder(View v) {
		ViewHolder holder = new ViewHolder();
		holder.videoId = (TextView) v.findViewById(R.id.video_id_text);
		holder.time = (TextView) v.findViewById(R.id.time_text);
		holder.comment = (TextView) v.findViewById(R.id.comment_text);
		return holder;
	}

	private static class ViewHolder {
		public TextView videoId;
		public TextView time;
		public TextView comment;
	}

	private String convertTime(int milliseconds){
		return String.format("%02d:%02d:%02d",
			TimeUnit.MILLISECONDS.toHours(milliseconds),
			TimeUnit.MILLISECONDS.toMinutes(milliseconds)
			 - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)),
			TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
				TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))
		);
	}
}
