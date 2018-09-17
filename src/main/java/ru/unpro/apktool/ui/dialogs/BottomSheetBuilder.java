package ru.unpro.apktool.ui.dialogs;
import android.content.*;
import android.support.design.widget.*;
import android.view.*;
import ru.unpro.apktool.*;

public class BottomSheetBuilder
{
	public static BottomSheetDialog build(Context context, int sheetView){
		BottomSheetDialog BSD = new BottomSheetDialog(context);
		//View sheetView = getLayoutInflater().inflate(R.layout.settings, null);
		BSD.setContentView(sheetView);
		return BSD;
	}
}
