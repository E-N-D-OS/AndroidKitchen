package ru.unpro.apktool.ui.dialogs;
import android.content.*;
import android.support.design.widget.*;
import android.view.*;
import android.widget.*;
import ru.unpro.apktool.*;
import android.view.View.*;
import ru.unpro.apktool.ui.activity.*;

public class BottomSheetBuilder
{
	public static BottomSheetDialog build(Context context, int Title,int Text){
		BottomSheetDialog BSD = new BottomSheetDialog(context);
		BSD.setContentView(R.layout.dlg_exit);
		TextView title = BSD.findViewById(R.id.dlg_btn_title),
		text = BSD.findViewById(R.id.dlg_btn_text);
		title.setText(Title);
		text.setText(Text);
		return BSD;
	}
	public static BottomSheetDialog build(Context context, int Title,int Text,int Btn1)
	{
		final BottomSheetDialog BSD = BottomSheetBuilder.build(context,Title,Text);
		Button btn1 = BSD.findViewById(R.id.dlg_btn_btn1);
		btn1.setVisibility(View.VISIBLE);
		btn1.setText(Btn1);
		btn1.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					BSD.dismiss();
				}
		});
		return BSD;
	}
	public static BottomSheetDialog build(Context context, int Title,int Text,int Btn1,int Btn2)
	{
		BottomSheetDialog BSD = BottomSheetBuilder.build(context,Title,Text,Btn1);
		Button btn2 = BSD.findViewById(R.id.dlg_btn_btn2);
		btn2.setVisibility(View.VISIBLE);
		btn2.setText(Btn2);
		return BSD;
	}
	public static BottomSheetDialog build(Context context, int Title,int Text,int Btn1,int Btn2,int Btn3)
	{
		BottomSheetDialog BSD = BottomSheetBuilder.build(context,Title,Text,Btn1,Btn2);
		Button btn3 = BSD.findViewById(R.id.dlg_btn_btn3);
		btn3.setVisibility(View.VISIBLE);
		btn3.setText(Btn3);
		return BSD;
	}
	//ListDialog
	public static BottomSheetDialog build(Context context, int Title,OptionAdapter adapter){
		BottomSheetDialog BSD = new BottomSheetDialog(context);
		BSD.setContentView(R.layout.dlg_exit);
		TextView title = BSD.findViewById(R.id.dlg_btn_title);
		ListView lv = BSD.findViewById(R.id.dlg_list);
		title.setText(Title);
		lv.setAdapter(adapter);
		return BSD;
	}
}
