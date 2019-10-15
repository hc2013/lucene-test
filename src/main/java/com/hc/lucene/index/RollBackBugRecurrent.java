package com.hc.lucene.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RollBackBugRecurrent {

  private static final String TEST_DATA_PATH = "/home/hanchun/WorkProjects/lucene-test/data";
  private static final String TEST_FIELD_NAME = "name";

  public static void main(String args[]) throws IOException {
    recurrentRollback();
  }

  public static void recurrentRollback() throws IOException {
    Path indexPath = Paths.get(TEST_DATA_PATH);
    Directory indexDirectory = new SimpleFSDirectory(indexPath);
    IndexWriter indexWriter = new IndexWriter(indexDirectory, new IndexWriterConfig());

    Document document1 = new Document();
    document1.add(new StringField(TEST_FIELD_NAME, "Jiejie", Field.Store.YES));
    Document document2 = new Document();
    document2.add(new StringField(TEST_FIELD_NAME, "JinGoogoo", Field.Store.YES));
    Document document3 = new Document();
    document3.add(new StringField(TEST_FIELD_NAME, "JiWawa", Field.Store.YES));
    Document document4 = new Document();
    document4.add(new StringField(TEST_FIELD_NAME, "ZhangWuji", Field.Store.YES));
    Document document5 = new Document();
    document5.add(new StringField(TEST_FIELD_NAME, "ZhanTian", Field.Store.YES));
    Document document6 = new Document();
    document6.add(new StringField(TEST_FIELD_NAME, "hhhh", Field.Store.YES));

    indexWriter.addDocument(document1);
    indexWriter.addDocument(document2);
    indexWriter.addDocument(document3);
    indexWriter.addDocument(document4);
    indexWriter.addDocument(document5);
    indexWriter.addDocument(document6);

    indexWriter.commit();
    createFile(TEST_DATA_PATH + "/_version");
    indexWriter.rollback();
    indexDirectory.close();
  }

  private static void createFile(String filePath) throws IOException {
    File file = new File(filePath);
    if(!file.exists()) {
      file.createNewFile();
    }
  }
}
