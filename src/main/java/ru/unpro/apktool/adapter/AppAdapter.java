package ru.unpro.apktool.adapter;

import android.content.*;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;
import java.util.*;

public class AppAdapter extends SimpleAdapter {
	private int[] mTo;
	private String[] mFrom;
	private ViewBinder mViewBinder;
	private List<? extends Map<String,?>> mData;
	private int mResource;
	private LayoutInflater mInflater;
	public AppAdapter(Context context, List<? extends Map<String, ?>> data,
				   int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		mData = data;
		mResource = resource;
		mFrom = from;
		mTo = to;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mResource);
    }
    private View createViewFromResource(int position, View convertView,
										ViewGroup parent, int resource) {
        View v;
        if (convertView == null) {
            v = mInflater.inflate(resource, parent, false);

            final int[] to = mTo;
            final int count = to.length;
            final View[] holder = new View[count];

            for (int i = 0; i < count; i++) {
                holder[i] = v.findViewById(to[i]);
            }
            v.setTag(holder);
        } else {
            v = convertView;
        }
        bindView(position, v);
        return v;
    }

    private void bindView(int position, View view) {
        final Map dataSet = mData.get(position);
        if (dataSet == null) {
            return;
        }
		
        final ViewBinder binder = mViewBinder;
        final View[] holder = (View[]) view.getTag();
        final String[] from = mFrom;
        final int[] to = mTo;
        final int count = to.length;

        for (int i = 0; i < count; i++) {
            final View v = holder[i];
            if (v != null) {
                final Object data = dataSet.get(from[i]);
                String text = data == null ? "" : data.toString();
                if (text == null) {
                    text = "";
                }

                boolean bound = false;
                if (binder != null) {
                    bound = binder.setViewValue(v, data, text);
                } 
                if (!bound) {      
					if (v instanceof TextView) {               	 
                        setViewText((TextView) v, text);                      
                    } else if (v instanceof ImageView) {
                        setViewImage((ImageView) v, (Drawable) data);                        
					}                              
                }
            }
        }
    }
    public void setViewImage(ImageView v, Drawable value) {
    	v.setImageDrawable(value);
    }
}
