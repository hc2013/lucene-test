package com.hc.lucene.util;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {
  
  public static File createNewFile(String filePath) throws IOException {
    File file = new File(filePath);
    if(!file.exists()) {
      recursiveCreateDir(file.getParentFile());
      file.createNewFile();
    }
    return file;
  }

  public static void refreshFile(String filePath) throws IOException {
    File file = new File(filePath);
    if (file.isDirectory()) return;
    if (!file.exists()) {
      recursiveCreateDir(file.getParentFile());
      file.createNewFile();
    }
    else if (file.exists()) {
      file.delete();
      file.createNewFile();
    }
  }

  public static void delteFile(String filePath) throws IOException {
    File file = new File(filePath);
    if (!file.exists()) {
      throw new IOException(filePath + "doesn't exist! Cannot be deleted");
    }
    if (file.isFile()) {
      file.delete();
    }
    else {
      throw new IOException(filePath + "is a diretory, cannot delete as a file");
    }
  }

  public static void clearDir(String dirPath) throws IOException {
    File dir = new File(dirPath);
    if (!dir.exists()) recursiveCreateDir(dir);
    File[] files = dir.listFiles();
    for(File f : files) {
      recuriveDeleteDir(f);
    }
  }

  private static void recuriveDeleteDir(File f) throws IOException {
    if (!f.exists()) return;
    if (f.isDirectory()) {
      for(File file : f.listFiles()) {
        recuriveDeleteDir(file);
      }
    }
    f.delete();
  }

  public static void recursiveCreateDir(File dir) throws IOException {
    if (dir.exists()) return;
    else if (!dir.exists() && dir.getParentFile().exists()) {
      dir.mkdir();
    }
    else if (!dir.exists() && !dir.getParentFile().exists()) {
      recursiveCreateDir(dir.getParentFile());
      dir.mkdir();
    }
  }

  public static void copyDirectory(String source, String dest) throws IOException {
    File sourceLocation= new File(source);
    File targetLocation = new File(dest);

    FileUtils.copyDirectory(sourceLocation, targetLocation);
  }

  public static void deleteDirectory(String dest) throws IOException {
    FileUtils.deleteDirectory(new File(dest));
  }
}
