package ru.unpro.apktool.ui.buttons;

import android.content.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.io.*;

public class TagBtn extends Button implements View.OnClickListener
 {
    String dir;

    public TagBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
    }
    public void onClick(View arg0) {
        dir = getTag().toString();
        try {
            Process sh;
            sh = Runtime.getRuntime().exec(new String[] {"su", "-c", "sh " + dir});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
