package ru.unpro.apktool.ui.settings;

import android.content.*;
import ru.unpro.apktool.R.*;
import android.support.compat.*;
import android.view.*;

public class SettingsLoader
{
	//Настройка темы
	public static int loadTheme(String prefValue)
	{
		int theme = R.style.DarkTheme;
		if (prefValue.contains("Dark"))
		{
			theme = R.style.DarkTheme;
		}
		else if(prefValue.contains("Light")){
			theme = R.style.LightTheme;
		}
		return theme;
	}
	
	//Настройки гравитации диалогов
	public static int loadDGravity(String prefValue)
	{
		int adgravity = Gravity.BOTTOM;
		if (prefValue.contains("bottom"))
		{
			adgravity = Gravity.BOTTOM;
		}
		else if (prefValue.contains("center"))
		{
			adgravity = Gravity.CENTER;
		}
		else if (prefValue.contains("top"))
		{
			adgravity = Gravity.TOP;
		}
		return adgravity;
	}
}
