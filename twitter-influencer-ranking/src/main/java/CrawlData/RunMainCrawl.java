package CrawlData;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RunMainCrawl {

    public void RunSingleThread(String inputIndex, String outputIndex) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        String[] filePaths1 = {inputIndex};
        ExcelFileWriter ew = new ExcelFileWriter(outputIndex);
        CountDownLatch latch = new CountDownLatch(filePaths1.length);
        for (int i = 0; i < filePaths1.length; i++) {
            try {
                CrawlTask task = new CrawlTask(filePaths1[i], outputIndex, ew, latch);
                executor.execute(task);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try {
            ew.close();
            System.out.println("ExcelFileWriter closed successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void RunMultiThread(String inputIndex1, String inputIndex2, String outputIndex) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        String[] filePaths1 = {inputIndex1, inputIndex2};
        ExcelFileWriter ew = new ExcelFileWriter(outputIndex);
        CountDownLatch latch = new CountDownLatch(filePaths1.length);
        for (int i = 0; i < filePaths1.length; i++) {
            try {
                CrawlTask task = new CrawlTask(filePaths1[i], outputIndex, ew, latch);
                executor.execute(task);
                Thread.sleep(35000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try {
            ew.close();
            System.out.println("ExcelFileWriter closed successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void RunMultiThread(String inputIndex1, String inputIndex2, String inputIndex3, String outputIndex) {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        String[] filePaths1 = {inputIndex1, inputIndex2, inputIndex3};
        ExcelFileWriter ew = new ExcelFileWriter(outputIndex);
        CountDownLatch latch = new CountDownLatch(filePaths1.length);
        for (int i = 0; i < filePaths1.length; i++) {
            try {
                CrawlTask task = new CrawlTask(filePaths1[i], outputIndex, ew, latch);
                executor.execute(task);
                Thread.sleep(35000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try {
            ew.close();
            System.out.println("ExcelFileWriter closed successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void RunMultiThread(String inputIndex1, String inputIndex2, String inputIndex3, String inputIndex4, String outputIndex) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        String[] filePaths1 = {inputIndex1, inputIndex2, inputIndex3, inputIndex4};
        ExcelFileWriter ew = new ExcelFileWriter(outputIndex);
        CountDownLatch latch = new CountDownLatch(filePaths1.length);
        for (int i = 0; i < filePaths1.length; i++) {
            try {
                CrawlTask task = new CrawlTask(filePaths1[i], outputIndex, ew, latch);
                executor.execute(task);
                Thread.sleep(35000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try {
            ew.close();
            System.out.println("ExcelFileWriter closed successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void RunSingleThread1(String inputIndex, String outputIndex) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        String[] filePaths1 = {inputIndex};
        ExcelFileWriter ew = new ExcelFileWriter(outputIndex);
        CountDownLatch latch = new CountDownLatch(filePaths1.length);
        for (int i = 0; i < filePaths1.length; i++) {
            try {
                CrawlTask1 task = new CrawlTask1(filePaths1[i], outputIndex, ew, latch);
                executor.execute(task);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try {
            ew.close();
            System.out.println("ExcelFileWriter closed successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void RunMultiThread1(String inputIndex1, String inputIndex2, String outputIndex) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        String[] filePaths1 = {inputIndex1, inputIndex2};
        ExcelFileWriter ew = new ExcelFileWriter(outputIndex);
        CountDownLatch latch = new CountDownLatch(filePaths1.length);
        for (int i = 0; i < filePaths1.length; i++) {
            try {
                CrawlTask1 task = new CrawlTask1(filePaths1[i], outputIndex, ew, latch);
                executor.execute(task);
                Thread.sleep(35000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try {
            ew.close();
            System.out.println("ExcelFileWriter closed successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void RunMultiThread1(String inputIndex1, String inputIndex2, String inputIndex3, String outputIndex) {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        String[] filePaths1 = {inputIndex1, inputIndex2, inputIndex3};
        ExcelFileWriter ew = new ExcelFileWriter(outputIndex);
        CountDownLatch latch = new CountDownLatch(filePaths1.length);
        for (int i = 0; i < filePaths1.length; i++) {
            try {
                CrawlTask1 task = new CrawlTask1(filePaths1[i], outputIndex, ew, latch);
                executor.execute(task);
                Thread.sleep(35000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try {
            ew.close();
            System.out.println("ExcelFileWriter closed successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void RunMultiThread1(String inputIndex1, String inputIndex2, String inputIndex3, String inputIndex4, String outputIndex) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        String[] filePaths1 = {inputIndex1, inputIndex2, inputIndex3, inputIndex4};
        ExcelFileWriter ew = new ExcelFileWriter(outputIndex);
        CountDownLatch latch = new CountDownLatch(filePaths1.length);
        for (int i = 0; i < filePaths1.length; i++) {
            try {
                CrawlTask1 task = new CrawlTask1(filePaths1[i], outputIndex, ew, latch);
                executor.execute(task);
                Thread.sleep(35000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try {
            ew.close();
            System.out.println("ExcelFileWriter closed successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}