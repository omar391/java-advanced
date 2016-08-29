package com.astronlab.tut.utils

/**
 * Created by omar-mac on 12/6/15.
 */
class TimeAnalyzer {
    private Date sTime = new Date();

    private TimeAnalyzer() {}

    public static TimeAnalyzer newAnalyzer() {
        def analyzer = new TimeAnalyzer();

        return analyzer;
    }

    public void analyze(){
        calculateExecTime(sTime, new Date());
    }

    public static void calculateExecTime(Date sTime, Date eTime) {
        // Calculate time difference

        System.out.println("\n\n================Time analysis====================");
        System.out.println("Started at:" + sTime.toString());
        System.out.println("End at:" + eTime.toString());

        long diff = eTime.getTime() - sTime.getTime();
        float time = (float) diff / (float) (1000);// sec chk
        int h = 0, m = 0;
        float s = 0;
        if (time < 60) {
            s = time;
        } else {
            time /= 60; // min chk
            if (time < 60) {
                m = (int) time;
                s = (time - m) * 60;
            } else {
                time /= 60; // hr chk
                h = (int) time;
                time = (time - h) * 60;
                m = (int) time;
                s = time - m;
            }
        }
        System.out.println("Execution time: " + h + " hour(s) " + m + " minute(s) "
                + s + " second(s)");
    }
}
