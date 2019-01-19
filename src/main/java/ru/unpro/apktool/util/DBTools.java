package ru.unpro.apktool.util;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import java.util.*;
import ru.unpro.apktool.*;

public class DBTools
{
	public static void LoadToBD(SQLiteOpenHelper dbHelper)
	{
		String[][] options=
		{
			{"Распаковать всё","Распаковать dex","Распаковать res"},
			{"Переменовать","Удалить",}
		};
		int[][] ids=
		{
			{0,1,2},
			{3,4}
		};
		int[][] draws=
		{
			{R.drawable.ic_launcher,R.drawable.ic_info_outline_white_48dp,R.drawable.ic_launcher},
			{R.drawable.xml_ic,R.drawable.txt_ic}
		};
		ContentValues cv = new ContentValues();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		for (int i = 0; i < options.length; i++)
		{
			for (int j = 0; j < options[i].length; j++)
			{
				cv.put("id", ids[i][j]);
				cv.put("option", options[i][j]);
				cv.put("draw", draws[i][j]);
				db.insert("optionlist", null, cv);
			}
		}
		dbHelper.close();
	}
	public static ArrayList<Items> LoadFromBD(SQLiteOpenHelper dbHelper, ArrayList<Items> ArrItems)
	{
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor c = db.query("optionlist", null, null, null, null, null, null);
		if (c.moveToFirst())
		{
			int idIds = c.getColumnIndex("id");
			int idOptions = c.getColumnIndex("option");
			int idDraws = c.getColumnIndex("draw");
			do {
				ArrItems.add(new Items(c.getString(idOptions),c.getInt(idDraws), c.getInt(idIds)));
				// int clearCount = db.delete("mytable", null, null);
			} while (c.moveToNext());
		}
		c.close();
		return(ArrItems);
	}
}
