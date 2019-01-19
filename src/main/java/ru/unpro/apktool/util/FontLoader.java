package ru.unpro.apktool.util;

import android.content.*;
import android.graphics.*;

public class FontLoader
 {
	private static final int NUM_OF_CUSTOM_FONTS = 3;
	private static boolean fontsLoaded = false;
	private static Typeface[] fonts = new Typeface[3];
	private static String[] fontPath = {
		"fonts/Font1.ttf",
		"fonts/Font2.ttf",
		"fonts/Font3.otf"
	};
	/**
	 * Returns a loaded custom font based on it identifier. 
	 * 
	 * @param context - the current context
	 * @param fontIdentifier = the identifier of the requested font
	 * 
	 * @return Typeface object of the requested font.
	 */
	public static Typeface getTypeface(Context context, int fontIdentifier) {
		if (!fontsLoaded) {
			loadFonts(context);
		}
		return fonts[fontIdentifier];
	}


	private static void loadFonts(Context context) {
		for (int i = 0; i < NUM_OF_CUSTOM_FONTS; i++) {
			fonts[i] = Typeface.createFromAsset(context.getAssets(), fontPath[i]);
		}
		fontsLoaded = true;

	}
}
