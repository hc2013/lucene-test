package com.hc.lucene.merge;

import com.hc.lucene.util.FileUtil;
import com.hc.lucene.util.OutputUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.hc.lucene.util.WriteUtil.*;

public class mergeTest {

  public static void testAutoMerge() throws IOException {
    int rows = 5000;
    FileUtil.clearDir(TEST_DATA_PATH);
    Path indexPath = Paths.get(TEST_DATA_PATH);
    Directory indexDirectory = new SimpleFSDirectory(indexPath);

    KeepOnlyLastCommitDeletionPolicy deletionPolicy = new KeepOnlyLastCommitDeletionPolicy();
    SnapshotDeletionPolicy snapshotDeletionPolicy = new SnapshotDeletionPolicy(deletionPolicy);
    IndexWriterConfig iwc = new IndexWriterConfig();
    iwc.setIndexDeletionPolicy(snapshotDeletionPolicy);
    iwc.setMergePolicy(new TieredMergePolicy());
    IndexWriter indexWriter = new IndexWriter(indexDirectory, iwc);

    for (int i = 1; i <= rows; i++) {
      Document document = new Document();
      String str = RandomStringUtils.random(DEFAULT_STRING_LENGTH);
      document.add(new StringField(String_FIELD_NAME, str, Field.Store.YES));
      indexWriter.addDocument(document);
      if(i % 500 == 0) {
        indexWriter.commit();
        IndexCommit indexCommit = snapshotDeletionPolicy.snapshot();
        System.out.println("first inserts files:");
        OutputUtil.printStringCollection(indexCommit.getFileNames());
        snapshotDeletionPolicy.release(indexCommit);
      }
    }
    indexWriter.close();
  }

  public static void main(String args[]) throws IOException {
    testAutoMerge();
  }

}
