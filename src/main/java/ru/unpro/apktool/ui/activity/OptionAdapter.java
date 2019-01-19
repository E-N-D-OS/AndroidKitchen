package ru.unpro.apktool.ui.activity;

import android.content.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import ru.unpro.apktool.*;
import ru.unpro.apktool.util.*;
import android.graphics.drawable.*;

public class OptionAdapter extends BaseAdapter
{
	Context ctx;
	LayoutInflater lInflater;
	ArrayList<Items> objects;

	OptionAdapter(Context context, ArrayList<Items> objectss)
	{
		ctx = context;
		objects = objectss;
		lInflater = (LayoutInflater) ctx
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	// кол-во элементов
	@Override
	public int getCount()
	{
		return objects.size();
	}
	// элемент по позиции
	@Override
	public Object getItem(int position)
	{
		return objects.get(position);
	}
	// id по позиции
	@Override
	public long getItemId(int position)
	{
		return position;
	}
	// пункт списка
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// используем созданные, но не используемые view
		View view = convertView;
		if (view == null)
		{
			view = lInflater.inflate(R.layout.list_item2, parent, false);
		}
		Items p = getSubjects(position);
		((TextView) view.findViewById(R.id.option_text)).
					setText(p.option);
		//setBackgroundColor(Color.parseColor("#ff2196F3"));
		((ImageView) view.findViewById(R.id.option_draw)).
					setImageResource(p.draw);
		return view;
	}

	// товар по позиции
	Items getSubjects(int position)
	{
		return ((Items) getItem(position));
	}
}
