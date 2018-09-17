package ru.unpro.apktool.util;

import java.util.*;
import java.io.*;

public class FileComparator implements Comparator
{  
	public final int compare(Object pFirst, Object pSecond)
	{  

		if (((File) pFirst).isDirectory() && !((File) pSecond).isDirectory())
		{
			return -1;
		}
		else if (!((File) pFirst).isDirectory() && ((File) pSecond).isDirectory())
		{
			return 1;
		}
		String name1 = ((File) pFirst).getName();
		String name2 = ((File) pSecond).getName();
		if (name1.compareToIgnoreCase(name2) < 0)
		{
			return -1;
		}
		else
			return 1;
	}  
}
