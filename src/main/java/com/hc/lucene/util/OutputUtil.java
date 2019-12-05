package com.hc.lucene.util;

import java.util.Collection;

public class OutputUtil {

  public static void printStringCollection(Collection<String> strings) {
    for (String s: strings) {
      System.out.print(s + " ");
    }
    System.out.println();
  }
}
