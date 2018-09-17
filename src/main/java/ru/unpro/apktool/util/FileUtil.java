package ru.unpro.apktool.util;

import java.io.*;
import java.util.*;

import static java.util.Arrays.asList;

public class FileUtil
{

    public static final int DEFAULT_BYTE_SIZE = 1024;

    public static File open(String pathname)
    {
        return new File(pathname);
    }

    public static File open(String parent, String child)
    {
        return new File(parent, child);
    }

    /**
     * 根据文件名打开文件并返回
     *
     * @param pathname 文件名
     * @return File
     */
    public static File of(String pathname)
    {
        return new File(pathname);
    }

    /**
     * 根据目录名和文件名打开文件并返回，{@link File#File(String, String)}
     *
     * @param parent 目录
     * @param child  文件名
     * @return File
     */
    public static File of(String parent, String child)
    {
        return new File(parent, child);
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath 文件路径
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public static boolean isExists(String filePath)
    {
        return isExists(of(filePath));
    }

    /**
     * 判断文件是否存在
     *
     * @param file 文件
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public static boolean isExists(File file)
    {
        return file != null && file.exists();
    }

    /**
     * 重命名文件
     *
     * @param filePath 文件路径
     * @param newName  新名称
     * @return {@code true}: 重命名成功<br>{@code false}: 重命名失败
     */
    public static boolean rename(String filePath, String newName)
    {
        return rename(of(filePath), newName);
    }

    /**
     * 重命名文件
     *
     * @param file    文件
     * @param newName 新名称
     * @return {@code true}: 重命名成功<br>{@code false}: 重命名失败
     */
    public static boolean rename(File file, String newName)
    {
        if (file == null || !file.exists())
        {
            return false;
        }

        // 新的文件名为空返回false
        if (TextUtil.isBlank(newName))
        {
            return false;
        }

        if (newName.equals(file.getName()))
        {
            return false;
        }

        File newFile = new File(file.getParent() + File.separator + newName);
        // 如果重命名的文件已存/重命名失败, 返回false
        return !newFile.exists() && file.renameTo(newFile);
    }

    /**
     * 判断是否是目录
     *
     * @param dirPath 目录路径
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isDir(String dirPath)
    {
        return isDir(of(dirPath));
    }

    /**
     * 判断是否是目录
     *
     * @param file 文件
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isDir(File file)
    {
        return isExists(file) && file.isDirectory();
    }


    /**
     * 判断是否是文件
     *
     * @param filePath 文件路径
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isFile(String filePath)
    {
        return isFile(of(filePath));
    }

    /**
     * 判断是否是文件
     *
     * @param file 文件
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isFile(File file)
    {
        return isExists(file) && file.isFile();
    }

    /**
     * 根据pathname创建目录.
     *
     * @param pathname 文件路径
     */
    public static void createDir(String pathname)
    {
        createDir(of(pathname));
    }

    /**
     * 根据file创建目录.
     *
     * @param file File实例
     */
    public static void createDir(File file)
    {
        if (file != null && !isExists(file))
        {
            file.mkdirs();
        }
    }

    /**
     * 创建目录
     *
     * @param pathname 文件名
     */
    public static void createFile(String pathname)
    {
        createFile(of(pathname));
    }

    /**
     * 创建目录
     *
     * @param file 文件
     */
    public static void createFile(File file)
    {
        if (file == null || file.exists())
        {
            return;
        }

        File parentFile = file.getParentFile();
        if (!parentFile.exists())
        {
            parentFile.mkdirs();
        }

        try
        {
            file.createNewFile();
        }
		catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    /**
     * 写入content到filepath文件中，覆盖文件内容
     *
     * @param filepath File
     * @param content  内容
     * @see FileTool#writeFile(String, String, boolean)
     */
    public static void writeFile(String filepath, String content)
    {
        writeFile(filepath, content, false);
    }

    /**
     * 写入content到file，不处理filepath，content异常
     *
     * @param filepath 文件路径
     * @param content  文件内容
     * @param isAppend 是不是追加模式
     * @see FileTool#writeFile(File, String, boolean)
     */
    public static void writeFile(String filepath, String content, boolean isAppend)
    {
        writeFile(of(filepath), content, isAppend);
    }

    /**
     * 写入content到file,覆盖文件内容
     *
     * @param file    File
     * @param content 内容
     * @see FileTool#writeFile(File, String, boolean)
     */
    public static void writeFile(File file, String content)
    {
        writeFile(file, content, false);
    }

    /**
     * 写入content到file，不处理file，content异常
     *
     * @param file     File
     * @param content  文件内容
     * @param isAppend 是不是追加模式
     */
    public static void writeFile(File file, String content, boolean isAppend)
    {
        try
        {
            FileOutputStream fileOutputStream = new FileOutputStream(file, isAppend);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            bufferedOutputStream.write(content.getBytes());
            bufferedOutputStream.close();
            fileOutputStream.close();
        }
		catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 写入content到全路径为filepath的文件中,简化WriteFile方法名称.
     *
     * @param filepath 文件路径名
     * @param content  内容
     * @param isAppend true 追加, false 覆盖
     * @see FileTool#writeFile(String, String, boolean)
     */
    public static void write(String filepath, String content, boolean isAppend)
    {
        writeFile(filepath, content, isAppend);
    }

    /**
     * 写入content到文件filepath
     *
     * @param filepath File
     * @param content  内容
     * @see FileTool#write(File, String, boolean)
     */
    public static void write(String filepath, String content)
    {
        write(filepath, content, false);
    }

    /**
     * 写入content到文件file中，简化WriteFile方法名.
     *
     * @param file     File
     * @param content  内容
     * @param isAppend true 追加; false 覆盖
     * @see FileTool#writeFile(File, String, boolean)
     */
    public static void write(File file, String content, boolean isAppend)
    {
        writeFile(file, content, isAppend);
    }

    /**
     * 写入content到file中，覆盖文件内容.
     *
     * @param file    File
     * @param content 内容
     * @see FileTool#write(File, String, boolean)
     */
    public static void write(File file, String content)
    {
        write(file, content, false);
    }

    public static String readFile(String pathname)
    {
        return readFile(of(pathname));
    }

    public static String readFile(File file)
    {
        try
        {
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            byte[] byteArray = getByteArray();
            StringBuffer stringBuffer = new StringBuffer();
            int length;
            while ((length = bis.read(byteArray)) != -1)
            {
                stringBuffer.append(new String(byteArray, 0, length));
            }
            bis.close();
            fis.close();
            return stringBuffer.toString();
        }
		catch (IOException e)
        {
            e.printStackTrace();
        }

        return TextUtil.emptyString();
    }

    /**
     * 读取文件pathname，返回文件内容。如果文件为空，返回空字符串
     *
     * @param pathname File
     * @return 文件内容
     * @see FileTool#readFile(File)
     */
    public static String read(String pathname)
    {
        return readFile(pathname);
    }

    /**
     * 读取文件file，返回文件内容。如果文件为空，返回空字符串
     *
     * @param file File
     * @return 文件内容
     * @see FileTool#readFile(String)
     */
    public static String read(File file)
    {
        return readFile(file);
    }

    /**
     * 返回一个长度1024的byte数组
     *
     * @return byte array
     * @see FileTool#DEFAULT_BYTE_SIZE
     */
    public static byte[] getByteArray()
    {
        return new byte[DEFAULT_BYTE_SIZE];
    }

    /**
     * 获取文件最后修改的毫秒时间戳
     *
     * @param filePath 文件路径
     * @return 文件最后修改的毫秒时间戳
     */
    public static long getLastModified(String filePath)
    {
        return getLastModified(of(filePath));
    }

    /**
     * 获取文件最后修改的毫秒时间戳
     *
     * @param file 文件
     * @return 文件最后修改的毫秒时间戳
     */
    public static long getLastModified(File file)
    {
        return file.lastModified();
    }


    /**
     * 根据文件路径获取文件名.
     *
     * @param pathname 文件路径名
     * @return 文件名
     */
    public static String getName(String pathname)
    {
        return getName(of(pathname));
    }

    /**
     * 获取file的文件名
     *
     * @param file 文件
     * @return 文件名
     */
    public static String getName(File file)
    {
        return file.getName();
    }

    /**
     * 获取文件名，返回全小写名称
     *
     * @param pathname 文件名
     * @return 全小写文件名
     */
    public static String getLowerName(String pathname)
    {
        return getLowerName(of(pathname));
    }

    /**
     * 获取文件名，返回全小写名称
     *
     * @param file 文件
     * @return 全小写文件名
     */
    public static String getLowerName(File file)
    {
        return file.getName().toLowerCase();
    }


    /**
     * 获取文件标题，不包含扩展名
     *
     * @param pathname 文件路径名
     * @return 文件标题
     */
    public static String getTitle(String pathname)
    {
        return getTitle(of(pathname));
    }

    /**
     * 获取文件标题
     *
     * @param file File
     * @return 文件标题
     */
    public static String getTitle(File file)
    {
        String name = file.getName();
        if (!name.contains("."))
        {
            return name;
        }

        return name.substring(0, name.lastIndexOf("."));
    }

    /**
     * 根据pathname返回文件扩展名
     *
     * @param pathname 文件路径名
     * @return 扩展名
     */
    public static String getExtension(String pathname)
    {
        return getExtension(of(pathname));
    }

    /**
     * 根据File返回扩展名
     *
     * @param file 文件
     * @return 扩展名
     */
    public static String getExtension(File file)
    {
        String name = file.getName();
        if (!hasExtension(file))
        {
            return "";
        }

        return name.substring(name.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 判断文件是不是有扩展名
     *
     * @param pathname 文件名
     * @return true 有扩展名
     */
    public static boolean hasExtension(String pathname)
    {
        return hasExtension(of(pathname));
    }

    /**
     * 判断文件是不是有扩展名
     *
     * @param file 文件
     * @return {@code true} 有扩展名<br>{@code false} 没有扩展名
     */
    public static boolean hasExtension(File file)
    {
        //如果是目录，直接返回，判断扩展名没有意义
        if (file.isDirectory())
        {
            return false;
        }

        String name = file.getName();

        //需要考虑隐藏文件，隐藏文件以.开头，需要进行处理
        while (name.startsWith("."))
        {
            name = name.substring(1);
        }

        if (!name.contains("."))
        {
            return false;
        }

        //防止文件名以.结束
        if (name.lastIndexOf(".") == name.length() - 1)
        {
            return false;
        }

        return true;
    }

    /**
     * 判断文件是不是类型化的文件（相当于判断是不是有扩展名)
     *
     * @param pathname 文件名
     * @return {@code true} 是类型化文件<br>{@code false} 不是类型化文件
     */
    public static boolean isTyped(String pathname)
    {
        return isTyped(of(pathname));
    }

    /**
     * 判断文件是不是类型化的文件（相当于判断是不是有扩展名)
     *
     * @param file File
     * @return {@code true} 是类型化文件<br> {@code false} 不是类型化文件
     */
    public static boolean isTyped(File file)
    {
        return hasExtension(file);
    }

    /**
     * 列出名为pathname文件下的所有文件
     *
     * @param pathname 文件名
     * @return 文件列表
     */
    public static File[] listFiles(String pathname)
    {
        return listFiles(of(pathname));
    }

    /**
     * 列出文件file下的所有文件
     *
     * @param file 文件
     * @return 文件列表
     */
    public static File[] listFiles(File file)
    {
        return listFiles(file, false);
    }

    /**
     * 列出名为pathname文件下的所有文件
     *
     * @param pathname   文件名
     * @param isAllFiles <br>{@code true} 包含所有目录文件<br>{@code false} 仅当前目录
     * @return 文件列表
     */
    public static File[] listFiles(String pathname, boolean isAllFiles)
    {
        return listFiles(of(pathname), isAllFiles);
    }

    /**
     * 列出名为pathname文件下的所有文件
     *
     * @param file       文件
     * @param isAllFiles <br>{@code true} 包含所有目录文件<br>{@code false} 仅当前目录
     * @return 文件列表
     */
    public static File[] listFiles(File file, boolean isAllFiles)
    {
        if (isAllFiles)
        {
            List<File> list = new ArrayList<>();
            listAll(list, file);
            File[] files = new File[list.size()];
            return list.toArray(files);
        }
        else
        {
            return file.listFiles();
        }
    }

    /**
     * 列出文件file下所有文件，递归调用
     *
     * @param list 要传出的文件列表
     * @param file 文件
     */
    public static void listAll(List<File> list, File file)
    {
        File[] files = file.listFiles();
        list.addAll(Arrays.asList(files));
        for (File f : files)
        {
            if (f.isDirectory())
            {
                listAll(list, f);
            }
        }
    }

    /**
     * 列出名为pathname文件包含的所有文件名称
     *
     * @param pathname 文件名
     * @return 文件名称列表
     */
    public static String[] listFilesNames(String pathname)
    {
        return listFilesNames(of(pathname));
    }

    /**
     * 列出file文件包含的所有文件名称
     *
     * @param file 文件
     * @return 文件名称列表
     */
    public static String[] listFilesNames(File file)
    {
        return listFilesNames(file, false);
    }

    /**
     * 列出名为pathname文件包含的所有文件名称
     *
     * @param pathname   文件名
     * @param isAllFiles <br>{@code true} 包含所有目录文件<br>{@code false} 仅当前目录
     * @return 文件名称列表
     */
    public static String[] listFilesNames(String pathname, boolean isAllFiles)
    {
        return listFilesNames(of(pathname), isAllFiles);
    }

    /**
     * 列出file文件包含的所有文件名称
     *
     * @param file       文件
     * @param isAllFiles <br>{@code true} 包含所有目录文件<br>{@code false} 仅当前目录
     * @return 文件名称列表
     */
    public static String[] listFilesNames(File file, boolean isAllFiles)
    {
        File[] files = listFiles(file, isAllFiles);
        if (files == null)
        {
            return null;
        }

        String[] names = new String[files.length];
        for (int i = 0; i < files.length; i++)
        {
            names[i] = files[i].getName();
        }

        return names;
    }

    /**
     * 将文件数组转换为文件名称数组返回
     *
     * @param files 文件数组
     * @return 文件名称数组
     */
    public static String[] toFileNames(File[] files)
    {
        String[] names = new String[files.length];
        for (int i = 0; i < names.length; i++)
        {
            names[i] = files[i].getName();
        }

        return names;
    }

    /**
     * 将文件数组转换为文件路径数组返回
     *
     * @param files 文件数组
     * @return 文件路径数组
     */
    public static String[] toFilePaths(File[] files)
    {
        String[] names = new String[files.length];
        for (int i = 0; i < names.length; i++)
        {
            names[i] = files[i].getAbsolutePath();
        }

        return names;
    }

    /**
     * 列出名为pathname文件包含的所有文件名称
     *
     * @param pathname 文件名
     * @return 文件名称列表
     */
    public static String[] listFilesPaths(String pathname)
    {
        return listFilesPaths(of(pathname));
    }

    /**
     * 列出file文件中所有文件路径
     *
     * @param file 文件
     * @return 文件名称列表
     */
    public static String[] listFilesPaths(File file)
    {
        return listFilesPaths(file, false);
    }

    /**
     * 列出名为pathname文件中所有文件路径
     *
     * @param pathname   文件名
     * @param isAllFiles <br>{@code true} 包含所有目录文件<br>{@code false} 仅当前目录
     * @return 文件名称列表
     */
    public static String[] listFilesPaths(String pathname, boolean isAllFiles)
    {
        return listFilesPaths(of(pathname), isAllFiles);
    }

    /**
     * 列出file文件中所有文件路径
     *
     * @param file       文件
     * @param isAllFiles <br>{@code true} 包含所有目录文件<br>{@code false} 仅当前目录
     * @return 文件名称列表
     */
    public static String[] listFilesPaths(File file, boolean isAllFiles)
    {
        File[] files = listFiles(file, isAllFiles);
        if (files == null)
        {
            return null;
        }

        String[] names = new String[files.length];
        try
        {
            for (int i = 0; i < files.length; i++)
            {
                names[i] = files[i].getCanonicalPath();
            }
        }
		catch (IOException e)
        {
            e.printStackTrace();
        }

        return names;
    }

    /**
     * 列出名为pathname文件中所有文件路径
     *
     * @param pathname   文件名
     * @param fileFilter 筛选器
     * @return 文件名称列表
     */
    public static File[] listFiles(String pathname, FileFilter fileFilter)
    {
        return listFiles(of(pathname), fileFilter);
    }

    /**
     * 列出file文件中所有文件路径
     *
     * @param file       文件
     * @param fileFilter 筛选器
     * @return 文件名称列表
     */
    public static File[] listFiles(File file, FileFilter fileFilter)
    {
        return file.listFiles(fileFilter);
    }

    /**
     * 列出名为pathname文件中所有文件路径
     *
     * @param pathname   文件名
     * @param fileFilter 筛选器
     * @param isAllFiles <br>{@code true} 包含所有目录文件<br>{@code false} 仅当前目录
     * @return 文件名称列表
     */
    public static File[] listFiles(String pathname, FileFilter fileFilter, boolean isAllFiles)
    {
        return listFiles(of(pathname), fileFilter, isAllFiles);
    }

    /**
     * 列出file文件中所有文件路径
     *
     * @param file       文件
     * @param fileFilter 筛选器
     * @param isAllFiles <br>{@code true} 包含所有目录文件<br>{@code false} 仅当前目录
     * @return 文件名称列表
     */
    public static File[] listFiles(File file, FileFilter fileFilter, boolean isAllFiles)
    {
        if (isAllFiles)
        {
            List<File> list = new ArrayList<>();
            listAll(list, file);
            return list.toArray(new File[list.size()]);
        }
        else
        {
            return listFiles(file, fileFilter);
        }
    }}




