package com.seastar.wasai.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.text.TextUtils;

/**
 * Tencent.
 * Author:jamie
 * Date: 13-10-24
 * Time: 下午7:27
 */
public class FileUtil {

    public static final int ZIP_BUFFER_SIZE = 4 * 1024;
    /**
     * Comparator of files.
     */
    public interface FileComparator {
        public boolean equals(File lhs, File rhs);
    }

    /**
     * Simple file comparator which only depends on file length and modification time.
     */
    public final static FileComparator SIMPLE_COMPARATOR = new FileComparator() {
        @Override
        public boolean equals(File lhs, File rhs) {
            return (lhs.length() == rhs.length()) && (lhs.lastModified() == rhs.lastModified());
        }
    };


    /**
     * Copy files. If src is a directory, then all it's sub files will be copied into directory dst.
     * If src is a file, then it will be copied to file dst.
     *
     * @param src file or directory to copy.
     * @param dst destination file or directory.
     * @return true if copy complete perfectly, false otherwise (more than one file cannot be copied).
     */
    public static boolean copyFiles(File src, File dst) {
        return copyFiles(src, dst, null);
    }

    /**
     * Copy files. If src is a directory, then all it's sub files will be copied into directory dst.
     * If src is a file, then it will be copied to file dst.
     *
     * @param src    file or directory to copy.
     * @param dst    destination file or directory.
     * @param filter a file filter to determine whether or not copy corresponding file.
     * @return true if copy complete perfectly, false otherwise (more than one file cannot be copied).
     */
    public static boolean copyFiles(File src, File dst, FileFilter filter) {
        return copyFiles(src, dst, filter, SIMPLE_COMPARATOR);
    }

    /**
     * Copy files. If src is a directory, then all it's sub files will be copied into directory dst.
     * If src is a file, then it will be copied to file dst.
     *
     * @param src        file or directory to copy.
     * @param dst        destination file or directory.
     * @param filter     a file filter to determine whether or not copy corresponding file.
     * @param comparator a file comparator to determine whether src & dst are equal files. Null to overwrite all dst files.
     * @return true if copy complete perfectly, false otherwise (more than one file cannot be copied).
     */
    public static boolean copyFiles(File src, File dst, FileFilter filter, FileComparator comparator) {
        if (src == null || dst == null) {
            return false;
        }

        if (!src.exists()) {
            return false;
        }
        if (src.isFile()) {
            return performCopyFile(src, dst, filter, comparator);
        }

        File[] paths = src.listFiles();
        if (paths == null) {
            return false;
        }
        // default is true.
        boolean result = true;
        for (File sub : paths) {
            if (!copyFiles(sub, new File(dst, sub.getName()), filter)) {
                result = false;
            }
        }
        return result;
    }

    private static boolean performCopyFile(File srcFile, File dstFile, FileFilter filter, FileComparator comparator) {
        if (srcFile == null || dstFile == null) {
            return false;
        }
        if (filter != null && !filter.accept(srcFile)) {
            return false;
        }

        FileChannel inc = null;
        FileChannel ouc = null;
        try {
            if (!srcFile.exists() || !srcFile.isFile()) {
                return false;
            }

            if (dstFile.exists()) {
                if (comparator != null && comparator.equals(srcFile, dstFile)) {
                    // equal files.
                    return true;
                } else {
                    // delete it in case of folder.
                    delete(dstFile);
                }
            }

            File toParent = dstFile.getParentFile();
            if (toParent.isFile()) {
                delete(toParent);
            }
            if (!toParent.exists() && !toParent.mkdirs()) {
                return false;
            }

            inc = (new FileInputStream(srcFile)).getChannel();
            ouc = (new FileOutputStream(dstFile)).getChannel();

            ouc.transferFrom(inc, 0, inc.size());

        } catch (Throwable e) {
            e.printStackTrace();
            // exception occur, delete broken file.
            delete(dstFile);
            return false;
        } finally {
            try {
                if (inc != null) inc.close();
                if (ouc != null) ouc.close();
            } catch (Throwable e) {
                // empty.
            }
        }
        return true;
    }

    /**
     * Copy asset files. If assetName is a directory, then all it's sub files will be copied into directory dst.
     * If assetName is a file, the it will be copied to file dst.
     *
     * @param context   application context.
     * @param assetName asset name to copy.
     * @param dst       destination file or directory.
     */
    public static void copyAssets(Context context, String assetName, String dst) {
        if (isEmpty(dst)) {
            return;
        }
        if (assetName == null) {
            assetName = "";
        }
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list(assetName);
        } catch (FileNotFoundException e) {
            // should be file.
            if (assetName.length() > 0) {
                performCopyAssetsFile(context, assetName, dst);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (files == null) {
            return;
        }

        if (files.length == 0) {
            // should be file or empty dir. Try to copy it.
            if (assetName.length() > 0) {
                performCopyAssetsFile(context, assetName, dst);
            }
        }

        for (String file : files) {
            if (isEmpty(file))
                continue;

            String newAssetDir = assetName.length() == 0 ? file : assetName + File.separator + file;
            String newDestDir = dst + File.separator + file;
            copyAssets(context, newAssetDir, newDestDir);
        }
    }

    private static void performCopyAssetsFile(Context context, String assetPath, String dstPath) {
        if (isEmpty(assetPath) || isEmpty(dstPath)) {
            return;
        }

        AssetManager assetManager = context.getAssets();
        File dstFile = new File(dstPath);

        InputStream in = null;
        OutputStream out = null;
        try {
            if (dstFile.exists()) {
                // try to determine whether or not copy this asset file, using their size.
                boolean tryStream = false;
                try {
                    AssetFileDescriptor fd = assetManager.openFd(assetPath);
                    if (dstFile.length() == fd.getLength()) {
                        // same file already exists.
                        return;
                    } else {
                        if (dstFile.isDirectory()) {
                            delete(dstFile);
                        }
                    }
                } catch (IOException e) {
                    // this file is compressed. cannot determine it's size.
                    tryStream = true;
                }

                if (tryStream) {
                    InputStream tmpIn = assetManager.open(assetPath);
                    try {
                        if (dstFile.length() == tmpIn.available()) {
                            return;
                        } else {
                            if (dstFile.isDirectory()) {
                                delete(dstFile);
                            }
                        }
                    } catch (IOException e) {
                        // do nothing.
                    } finally {
                        tmpIn.close();
                    }
                }
            }

            File parent = dstFile.getParentFile();
            if (parent.isFile()) {
                delete(parent);
            }
            if (!parent.exists() && !parent.mkdirs()) {
                return;
            }

            in = assetManager.open(assetPath);
            out = new BufferedOutputStream(new FileOutputStream(dstFile));
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

        } catch (Throwable e) {
            e.printStackTrace();
            // delete broken file.
            delete(dstFile);
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (Throwable e) {
                // empty.
            }
        }
    }

    /**
     * Delete corresponding path, file or directory.
     *
     * @param file path to delete.
     */
    public static void delete(File file) {
        delete(file, false);
    }

    /**
     * Delete corresponding path, file or directory.
     *
     * @param file      path to delete.
     * @param ignoreDir whether ignore directory. If true, all files will be deleted while directories is reserved.
     */
    public static void delete(File file, boolean ignoreDir) {
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
            return;
        }

        File[] fileList = file.listFiles();
        if (fileList == null) {
            return;
        }

        for (File f : fileList) {
            delete(f, ignoreDir);
        }
        // delete the folder if need.
        if (!ignoreDir) file.delete();
    }

    private static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
    
    
    public static boolean zip(File[] srcFiles, FileOutputStream dest) {
        // 参数检查
        if (srcFiles == null || srcFiles.length < 1 || dest == null) {
            return false;
        }

        boolean resu = false;

        ZipOutputStream zos = null;

        try {
            byte[] buffer = new byte[ZIP_BUFFER_SIZE];

            zos = new ZipOutputStream(new BufferedOutputStream(dest));

            // 添加文件到ZIP压缩流
            for (File src : srcFiles) {
                doZip(zos, src, null, buffer);
            }

            zos.flush();
            zos.closeEntry();

            resu = true;
        } catch (IOException e) {
            // e.print*StackTrace();

            resu = false;
        } finally {
            IOUtils.closeQuietly(zos);
        }

        return resu;
    }
    
    /**
     * ZIP压缩多个文件/文件夹
     * 
     * @param srcFiles
     *            要压缩的文件/文件夹列表
     * @param dest
     *            目标文件
     * @return 压缩成功/失败
     */
    public static boolean zip(File[] srcFiles, File dest) {
        try {
            return zip(srcFiles, new FileOutputStream(dest));
        } catch (FileNotFoundException e) {
//           TLog.e("FileUtil", e.getMessage(),e);
        }
        return false;
    }

    /**
     * 方法：ZIP压缩单个文件/文件夹
     * 
     * @param source
     *            源文件/文件夹
     * @param dest
     *            目标文件
     * @return 压缩成功/失败
     */
    public static boolean zip(File src, File dest) {
        return zip(new File[] { src }, dest);
    }

    /**
     * 方法：解压缩单个ZIP文件
     * 
     * @param source
     *            源文件/文件夹
     * @param dest
     *            目标文件夹
     * @return 解压缩成功/失败
     */
    public static boolean unzip(File src, File destFolder) {
        if (src == null || src.length() < 1 || !src.canRead()) {
            return false;
        }

        boolean resu = false;

        if (!destFolder.exists()) {
            destFolder.mkdirs();
        }

        ZipInputStream zis = null;

        BufferedOutputStream bos = null;

        ZipEntry entry = null;

        byte[] buffer = new byte[8 * 1024];

        int readLen = 0;

        try {
            zis = new ZipInputStream(new FileInputStream(src));

            while (null != (entry = zis.getNextEntry())) {
                System.out.println(entry.getName());

                if (entry.isDirectory()) {
                    new File(destFolder, entry.getName()).mkdirs();
                } else {
                    File entryFile = new File(destFolder, entry.getName());

                    entryFile.getParentFile().mkdirs();

                    bos = new BufferedOutputStream(new FileOutputStream(entryFile));

                    while (-1 != (readLen = zis.read(buffer, 0, buffer.length))) {
                        bos.write(buffer, 0, readLen);
                    }

                    bos.flush();
                    bos.close();
                }
            }

            zis.closeEntry();
            zis.close();

            resu = true;
        } catch (IOException e) {
            resu = false;
        } finally {
            IOUtils.closeQuietly(bos);
            IOUtils.closeQuietly(zis);
        }

        return resu;
    }

    /**
     * 压缩文件/文件夹到ZIP流中 <br>
     * <br>
     * <i>本方法是为了向自定义的压缩流添加文件/文件夹，若只是要压缩文件/文件夹到指定位置，请使用 {@code FileUtils.zip()} 方法</i>
     * 
     * @param zos
     *            ZIP输出流
     * @param file
     *            被压缩的文件
     * @param root
     *            被压缩的文件在ZIP文件中的入口根节点
     * @param buffer
     *            读写缓冲区
     * @throws IOException
     *             读写流时可能抛出的I/O异常
     */
    public static void doZip(ZipOutputStream zos, File file, String root, byte[] buffer) throws IOException {
        // 参数检查
        if (zos == null || file == null) {
            throw new IOException("I/O Object got NullPointerException");
        }

        if (!file.exists()) {
            throw new FileNotFoundException("Target File is missing");
        }

        BufferedInputStream bis = null;

        int readLen = 0;

        String rootName = TextUtils.isEmpty(root) ? (file.getName()) : (root + File.separator + file.getName());

        // 文件直接放入压缩流中
        if (file.isFile()) {
            try {
                bis = new BufferedInputStream(new FileInputStream(file));

                zos.putNextEntry(new ZipEntry(rootName));

                while (-1 != (readLen = bis.read(buffer, 0, buffer.length))) {
                    zos.write(buffer, 0, readLen);
                }

                IOUtils.closeQuietly(bis);
            } catch (IOException e) {
                IOUtils.closeQuietly(bis);
                // 关闭BIS流，并抛出异常
                throw e;
            }
        }
        // 文件夹则子文件递归
        else if (file.isDirectory()) {
            File[] subFiles = file.listFiles();

            for (File subFile : subFiles) {
                doZip(zos, subFile, rootName, buffer);
            }
        }
    }

    public static boolean unjar(File src, File destFolder) {
        if (src == null || src.length() < 1 || !src.canRead()) {
            return false;
        }

        boolean resu = false;

        if (!destFolder.exists()) {
            destFolder.mkdirs();
        }

        JarInputStream zis = null;

        BufferedOutputStream bos = null;

        JarEntry entry = null;

        byte[] buffer = new byte[8 * 1024];

        int readLen = 0;

        try {
            zis = new JarInputStream(new FileInputStream(src));

            while (null != (entry = zis.getNextJarEntry())) {
                System.out.println(entry.getName());

                if (entry.isDirectory()) {
                    new File(destFolder, entry.getName()).mkdirs();
                } else {
                    bos = new BufferedOutputStream(new FileOutputStream(new File(destFolder, entry.getName())));

                    while (-1 != (readLen = zis.read(buffer, 0, buffer.length))) {
                        bos.write(buffer, 0, readLen);
                    }

                    bos.flush();
                    bos.close();
                }
            }

            zis.closeEntry();
            zis.close();

            resu = true;
        } catch (IOException e) {
            resu = false;
        } finally {
            IOUtils.closeQuietly(bos);
            IOUtils.closeQuietly(zis);
        }

        return resu;
    }
}
