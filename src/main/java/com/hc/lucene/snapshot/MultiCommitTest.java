package com.hc.lucene.snapshot;

import com.hc.lucene.util.FileUtil;
import com.hc.lucene.util.OutputUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.KeepOnlyLastCommitDeletionPolicy;
import org.apache.lucene.index.SnapshotDeletionPolicy;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.hc.lucene.util.WriteUtil.*;

public class MultiCommitTest {

  public static void testMultiCommit() throws IOException {
    int rows = 1000;
    FileUtil.clearDir(TEST_DATA_PATH);
    Path indexPath = Paths.get(TEST_DATA_PATH);
    Directory indexDirectory = new SimpleFSDirectory(indexPath);

    KeepOnlyLastCommitDeletionPolicy deletionPolicy = new KeepOnlyLastCommitDeletionPolicy();
    SnapshotDeletionPolicy snapshotDeletionPolicy = new SnapshotDeletionPolicy(deletionPolicy);
    IndexWriterConfig iwc = new IndexWriterConfig();
    iwc.setIndexDeletionPolicy(snapshotDeletionPolicy);
    iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    IndexWriter indexWriter = new IndexWriter(indexDirectory, iwc);

    List<String> strs1 = new ArrayList<String>();
    for (int i = 0; i < rows; i++) {
      Document document = new Document();
      String str = RandomStringUtils.random(DEFAULT_STRING_LENGTH);
      document.add(new StringField(String_FIELD_NAME, str, Field.Store.YES));
      if (i % 10 == 0) {
        strs1.add(str);
      }
      indexWriter.addDocument(document);
    }
    indexWriter.commit();
    IndexCommit indexCommit = snapshotDeletionPolicy.snapshot();
    System.out.println("first commit files:");
    OutputUtil.printStringCollection(indexCommit.getFileNames());
    System.out.println("first commit generation:" + indexCommit.getGeneration());

    indexWriter.commit();
    indexCommit = snapshotDeletionPolicy.snapshot();
    System.out.println("second commit files:");
    OutputUtil.printStringCollection(indexCommit.getFileNames());
    System.out.println("second commit generation:" + indexCommit.getGeneration());
  }

  public static void main(String args[]) throws IOException {
    testMultiCommit();
  }
}
