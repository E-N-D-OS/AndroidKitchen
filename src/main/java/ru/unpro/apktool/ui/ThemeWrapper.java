package ru.unpro.apktool.ui;
import android.app.*;
import android.content.*;
import android.os.*;
import android.preference.*;
import ru.unpro.apktool.*;

public class ThemeWrapper
{
	public static int applyTheme(Context context, String prefValue)
	{
		int theme = 0;
			if (prefValue.contains("Dark"))
				{
					theme = R.style.DarkTheme;
				}
			else if(prefValue.contains("Light")){
				theme = R.style.LightTheme;
			}
		return theme;
	}
}
