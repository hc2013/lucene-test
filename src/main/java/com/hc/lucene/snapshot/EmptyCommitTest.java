package com.hc.lucene.snapshot;

import com.hc.lucene.util.FileUtil;
import com.hc.lucene.util.OutputUtil;
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

import static com.hc.lucene.util.WriteUtil.TEST_DATA_PATH;

public class EmptyCommitTest {

  public static void testEmptyCommit() throws IOException {
    FileUtil.clearDir(TEST_DATA_PATH);
    Path indexPath = Paths.get(TEST_DATA_PATH);
    Directory indexDirectory = new SimpleFSDirectory(indexPath);

    KeepOnlyLastCommitDeletionPolicy deletionPolicy = new KeepOnlyLastCommitDeletionPolicy();
    SnapshotDeletionPolicy snapshotDeletionPolicy = new SnapshotDeletionPolicy(deletionPolicy);
    IndexWriterConfig iwc = new IndexWriterConfig();
    iwc.setIndexDeletionPolicy(snapshotDeletionPolicy);
    iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    IndexWriter indexWriter = new IndexWriter(indexDirectory, iwc);

    IndexCommit indexCommit1 = snapshotDeletionPolicy.snapshot();
    assert indexCommit1 == null;
    indexWriter.commit();
    IndexCommit indexCommit2 = snapshotDeletionPolicy.snapshot();
    System.out.println("first commit files:");
    OutputUtil.printStringCollection(indexCommit2.getFileNames());
    indexWriter.close();
  }

  public static void main(String args[]) throws IOException {
    testEmptyCommit();
  }

}
