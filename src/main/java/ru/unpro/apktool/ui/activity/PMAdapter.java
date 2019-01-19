package ru.unpro.apktool.ui.activity;

import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import ru.unpro.apktool.*;
import ru.unpro.apktool.ui.activity.*;
import android.support.v4.app.*;

public class PMAdapter extends ListFragment
{
	public static List<AppDetail> loadApps(PackageManager pm)
	{
		List<AppDetail> apps;
		apps = new ArrayList<AppDetail>();

		Intent i = new Intent(Intent.ACTION_MAIN, null);
		i.addCategory(Intent.CATEGORY_DEFAULT);

		List<ResolveInfo> availableActivities = pm.queryIntentActivities(i, 0);
		for (ResolveInfo ri:availableActivities)
		{
			AppDetail app = new AppDetail();
			app.label = ri.loadLabel(pm);
			app.name = ri.activityInfo.packageName;
			app.icon = ri.activityInfo.loadIcon(pm);
			apps.add(app);
		}
		return apps;
	}
	public static Adapter loadListView(final Context context, View convertView, final PackageManager pm)
	{
		final List<AppDetail> apps = PMAdapter.loadApps(pm);
		ArrayAdapter<AppDetail> adapter = new ArrayAdapter<AppDetail>(context, 
																	  R.layout.list_item, 
																	  apps) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{

				ImageView appIcon = convertView.findViewById(R.id.icon);
				appIcon.setImageDrawable(apps.get(position).icon);

				TextView appLabel = convertView.findViewById(R.id.file_name);
				appLabel.setText(apps.get(position).label);

				TextView appName = convertView.findViewById(R.id.file_modify);
				appName.setText(apps.get(position).name);

				return convertView;
			}
		};
		return adapter;
	}
}
