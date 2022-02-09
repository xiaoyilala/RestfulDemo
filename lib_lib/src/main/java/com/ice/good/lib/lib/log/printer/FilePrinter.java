package com.ice.good.lib.lib.log.printer;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.ice.good.lib.lib.log.base.ILogPrinter;
import com.ice.good.lib.lib.log.base.LogConfig;
import com.ice.good.lib.lib.log.been.LogModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 1、BlockingQueue的使用，防止频繁的创建线程；
 * 2、线程同步；
 * 2、文件操作，BufferedWriter的应用；
 */
public class FilePrinter implements ILogPrinter {
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private final String logPath;
    private final long retentionTime;
    private static FilePrinter instance;
    private LogWriter logWriter;
    private volatile PrintWorker worker;

    private FilePrinter(String logPath, long retentionTime){
        this.logPath = logPath;
        this.retentionTime = retentionTime;
        this.logWriter = new LogWriter();
        this.worker = new PrintWorker();
        cleanExpiredLog();
    }

    /**
     * FilePrinter
     *
     * @param logPath       log保存路径，如果是外部路径需要确保已经有外部存储的读写权限
     * @param retentionTime log文件的有效时长，单位毫秒，<=0表示一直有效
     */
    public static synchronized FilePrinter getInstance(String logPath, long retentionTime){
        if(instance == null){
            instance = new FilePrinter(logPath, retentionTime);
        }
        return instance;
    }

    /**
     * 开始打印时，由worker提交任务，循环取log对象，
     * writer真正执行写入文件操作，内聚文件状态，文件初始化等
     * */
    @Override
    public void print(@NonNull LogConfig config, int level, String tag, @NonNull String printString) {
        long timeMillis = System.currentTimeMillis();
        if(!worker.isRunning()){
            worker.start();
        }
        worker.put(new LogModel(timeMillis, level, tag, printString));
    }

//    public static void main(String[] args){
//        FilePrinter.getInstance("/ice/ice", 23);
//    }

    private void cleanExpiredLog() {
        if(retentionTime<=0){
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        File logDir = new File(logPath);
        //logDir不存在时 files为null
        File[] files = logDir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (currentTimeMillis - file.lastModified() > retentionTime) {
                file.delete();
            }
        }
    }

    private class PrintWorker implements Runnable{

        private BlockingQueue<LogModel> logs = new LinkedBlockingQueue<>();
        private volatile boolean running;

        /**
         * 将log放入打印队列
         *
         * @param log 要被打印的log
         */
        void put(LogModel log){
            try {
                logs.put(log);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /**
         * 判断工作线程是否还在运行中
         *
         * @return true 在运行
         */
        boolean isRunning(){
            synchronized (this){
                return running;
            }
        }

        /**
         * 启动工作线程
         */
        void start(){
            synchronized (this){
                EXECUTOR.execute(this);
                running = true;
            }
        }

        @Override
        public void run() {
            LogModel logModel;
            try {
                while (true){
                    logModel = logs.take();
                    doPrint(logModel);
                }
            }catch (Exception e){
                e.printStackTrace();
                synchronized (this){
                    running = false;
                }
            }
        }
    }

    private void doPrint(LogModel logModel) {
        String lastFileName = logWriter.getPreFileName();
        if(TextUtils.isEmpty(lastFileName)){
            String newFileName = genFileName();
            if(logWriter.isReady()){
                logWriter.close();
            }
            if(!logWriter.ready(newFileName)){
                return;
            }
        }
        logWriter.append(logModel.flattenedLog());
    }

    private String genFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date(System.currentTimeMillis()));
    }

    /**
     * 基于BufferedWriter将log写入文件
     */
    private class LogWriter{

        private String preFileName;
        private File logFile;
        BufferedWriter bufferedWriter;

        boolean isReady() {
            return bufferedWriter != null;
        }

        String getPreFileName() {
            return preFileName;
        }

        /**
         * log写入前的准备操作
         *
         * @param newFileName 要保存log的文件名
         * @return true 表示准备就绪,创建了bufferedWriter
         */
        boolean ready(String newFileName){
            this.preFileName = newFileName;
            this.logFile = new File(logPath, preFileName);

            // 当log文件不存在时创建log文件
            if(!logFile.exists()){
                try {
                    File parentFile = logFile.getParentFile();
                    if(!parentFile.exists()){
                        parentFile.mkdirs();
                    }
                    logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    this.preFileName = null;
                    this.logFile = null;
                }
            }

            //创建bufferWriter
            try {
                bufferedWriter = new BufferedWriter(new FileWriter(logFile, true));
            } catch (IOException e) {
                e.printStackTrace();
                this.preFileName = null;
                this.logFile = null;
                return false;
            }
            return true;
        }

        /**
         * 关闭bufferedWriter
         */
        boolean close() {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    bufferedWriter = null;
                    preFileName = null;
                    logFile = null;
                }
            }
            return true;
        }

        /**
         * 将log写入文件
         *
         * @param logContent 格式化后的log
         */
        void append(String logContent){
            try {
                bufferedWriter.write(logContent);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
