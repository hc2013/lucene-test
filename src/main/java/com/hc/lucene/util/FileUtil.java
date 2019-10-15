package com.hc.lucene.util;

import java.io.File;
import java.io.IOException;

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
}
