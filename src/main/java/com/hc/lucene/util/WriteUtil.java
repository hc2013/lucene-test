package com.hc.lucene.util;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WriteUtil {

  public static final String TEST_DATA_PATH = "/home/hanchun/WorkProjects/lucene-test/data1";
  public static final String String_FIELD_NAME = "c_string";
  public static final int DEFAULT_STRING_LENGTH = 10;

  public static void prepareDocs() throws IOException {
    Path indexPath = Paths.get(TEST_DATA_PATH);
    Directory indexDirectory = new SimpleFSDirectory(indexPath);
    IndexWriter indexWriter = new IndexWriter(indexDirectory, new IndexWriterConfig());

    Document document1 = new Document();
    document1.add(new StringField(String_FIELD_NAME, "Jiejie", Field.Store.YES));
    Document document2 = new Document();
    document2.add(new StringField(String_FIELD_NAME, "JinGoogoo", Field.Store.YES));
    Document document3 = new Document();
    document3.add(new StringField(String_FIELD_NAME, "JiWawa", Field.Store.YES));
    Document document4 = new Document();
    document4.add(new StringField(String_FIELD_NAME, "ZhangWuji", Field.Store.YES));
    Document document5 = new Document();
    document5.add(new StringField(String_FIELD_NAME, "ZhanTian", Field.Store.YES));
    Document document6 = new Document();
    document6.add(new StringField(String_FIELD_NAME, "hhhh", Field.Store.YES));

    indexWriter.addDocument(document1);
    indexWriter.addDocument(document2);
    indexWriter.addDocument(document3);
    indexWriter.addDocument(document4);
    indexWriter.addDocument(document5);
    indexWriter.addDocument(document6);

    indexWriter.commit();
    indexWriter.flush();
    indexWriter.close();
    indexDirectory.close();
  }

}
