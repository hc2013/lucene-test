package com.hc.lucene.merge;

import com.hc.lucene.util.FileUtil;
import com.sun.management.OperatingSystemMXBean;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.KeepOnlyLastCommitDeletionPolicy;
import org.apache.lucene.index.NoMergePolicy;
import org.apache.lucene.index.SnapshotDeletionPolicy;
import org.apache.lucene.index.TieredMergePolicy;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.hc.lucene.util.WriteUtil.DEFAULT_STRING_LENGTH;
import static com.hc.lucene.util.WriteUtil.String_FIELD_NAME;

/**
 * The test program to quantificate performance cost for lucene merge operation
 * created by hanchun on 2019-12-05 14:40 transwarp 12f
 */
public class MergeLoadTest {

  static int segmentSizeLevel[] = {2000, 20000, 200000};

  static final String MERGE_DATA_PATH = "/home/hanchun/WorkProjects/lucene-test/test-data";
  static final String MERGE_TEST_PATH = "/home/hanchun/WorkProjects/lucene-test/test-merge";
  static final String TEST_OUTPUT_FILE = "./test.result";
  static final String WRITE_LOCK_FILE ="write.lock";

  private static void prepareMergeData(int level, int segmentCount)  throws Exception {
    FileUtil.clearDir(MERGE_DATA_PATH);
    Path indexPath = Paths.get(MERGE_DATA_PATH);
    Directory indexDirectory = new SimpleFSDirectory(indexPath);

    KeepOnlyLastCommitDeletionPolicy deletionPolicy = new KeepOnlyLastCommitDeletionPolicy();
    SnapshotDeletionPolicy snapshotDeletionPolicy = new SnapshotDeletionPolicy(deletionPolicy);
    IndexWriterConfig iwc = new IndexWriterConfig();
    iwc.setIndexDeletionPolicy(snapshotDeletionPolicy);
    iwc.setMergePolicy(NoMergePolicy.INSTANCE);
    IndexWriter indexWriter = new IndexWriter(indexDirectory, iwc);

    for (int i = 1; i <= segmentSizeLevel[level] * segmentCount; i++) {
      Document document = new Document();
      String str = RandomStringUtils.random(DEFAULT_STRING_LENGTH);
      document.add(new StringField(String_FIELD_NAME, str, Field.Store.YES));
      indexWriter.addDocument(document);
      if(i % segmentSizeLevel[level] == 0) {
        indexWriter.commit();
      }
    }
    indexWriter.close();
  }

  public static void testMergeLoad(int time, OsMonitor osMonitor) throws Exception {
    for (int i = 0; i < time; i++) {
      FileUtil.deleteDirectory(MERGE_TEST_PATH);
      FileUtil.copyDirectory(MERGE_DATA_PATH, MERGE_TEST_PATH);
      FileUtil.delteFile(MERGE_TEST_PATH + "/" + WRITE_LOCK_FILE);
      Path indexPath = Paths.get(MERGE_TEST_PATH);
      Directory indexDirectory = new SimpleFSDirectory(indexPath);
      KeepOnlyLastCommitDeletionPolicy deletionPolicy = new KeepOnlyLastCommitDeletionPolicy();
      SnapshotDeletionPolicy snapshotDeletionPolicy = new SnapshotDeletionPolicy(deletionPolicy);
      IndexWriterConfig iwc = new IndexWriterConfig();
      iwc.setIndexDeletionPolicy(snapshotDeletionPolicy);
      iwc.setMergePolicy(new TieredMergePolicy());
      IndexWriter indexWriter = new IndexWriter(indexDirectory, iwc);
      osMonitor.setState(OsMonitor.State.MERGING);
      indexWriter.maybeMerge();
      while(indexWriter.getMergingSegments().size()!=0) {
        Thread.sleep(1000);
      }
      indexWriter.commit();
      osMonitor.setState(OsMonitor.State.IDLE);
      Thread.sleep(10000);
      osMonitor.setState(OsMonitor.State.IDLE_TEST);
      Thread.sleep(10000);
      osMonitor.setState(OsMonitor.State.IDLE);
      indexWriter.close();
    }
    osMonitor.setStop(true);
  }

  public static void main(String args[]) throws Exception {
    PrintStream pw = new PrintStream(TEST_OUTPUT_FILE);
    System.setOut(pw);
    prepareMergeData(0, 10);
    OsMonitor osMonitor = new OsMonitor();
    new Thread(osMonitor).start();
    testMergeLoad(10, osMonitor);
  }

  private static class OsMonitor implements Runnable {

    private enum State {
      IDLE,IDLE_TEST,MERGING
    }
    private State state = State.IDLE;
    private static final int delay = 10;
    private boolean stop = false;
    private double idleAvgUsedCpuload = 0.0;
    private long idleAvgUsedMem = 0;
    private long idleCount = 0;
    private double runningAvgUsedCpuload = 0.0;
    private long runningAvgUsedMem = 0;
    private long runningCount = 0;

    public void setState(State state) {
      this.state = state;
    }

    public void setStop(boolean stop) {
      this.stop = stop;
    }

    @Override
    public void run() {
      try {
        while (!stop) {
          OperatingSystemMXBean operatingSystemMXBean =
            (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
          double usedCpuLoadPercent = operatingSystemMXBean.getSystemCpuLoad();
          long usedMemorySize = operatingSystemMXBean.getTotalPhysicalMemorySize() -
            operatingSystemMXBean.getFreePhysicalMemorySize();
          if(usedCpuLoadPercent == 0.0) {
            Thread.sleep(delay);
            continue;
          }

          if(state == State.MERGING) {
            StringBuilder sb = new StringBuilder();
            sb.append("runing usedCpuLoadPercent: " + usedCpuLoadPercent + ";");
            sb.append("running usedMemorySize: " + usedMemorySize);
            sb.append("\n");
            System.out.println(sb.toString());
            runningCount++;
            runningAvgUsedCpuload = ((runningAvgUsedCpuload * (runningCount - 1)) + usedCpuLoadPercent) / (runningCount * 1.0);
            runningAvgUsedMem = ((runningAvgUsedMem * (runningCount - 1)) + usedMemorySize) / (runningCount);
          }
          else if(state == State.IDLE_TEST){
            StringBuilder sb = new StringBuilder();
            sb.append("idle test usedCpuLoadPercent: " + usedCpuLoadPercent + ";");
            sb.append("idle test usedMemorySize: " + usedMemorySize);
            sb.append("\n");
            System.out.println(sb.toString());
            idleCount++;
            idleAvgUsedCpuload = ((idleAvgUsedCpuload * (idleCount - 1)) + usedCpuLoadPercent) / (idleCount * 1.0);
            idleAvgUsedMem = ((idleAvgUsedMem * (idleCount - 1)) + usedMemorySize) / (idleCount);
          }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("===========================" + "\n");
        sb.append("runningAvgUsedCpuload: " + runningAvgUsedCpuload + "\n");
        sb.append("runningAvgUsedMem: " + runningAvgUsedMem + "\n");
        sb.append("idleAvgUsedCpuload: " + idleAvgUsedCpuload + "\n");
        sb.append("idleAvgUsedMem: " + idleAvgUsedMem + "\n");
        sb.append("===========================");
        System.out.println(sb.toString());

      } catch (InterruptedException e) {
        System.err.println("Occur interrupt while sleep");
      }
    }

  }
}
