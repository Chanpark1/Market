package com.chanyoung.market;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Chrono {

    public static final int SEC = 60;
    public static final int MIN = 60;
    public static final int HOUR = 24;
    public static final int DAY = 30;
    public static final int MONTH = 12;

    public static String timesAgo(LocalDateTime dayBefore) {
        String word = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            long gap = ChronoUnit.MINUTES.between(dayBefore, LocalDateTime.now());


            if(gap == 0) {
                word = "방금 전";
            } else if (gap < 60) {
                word = gap + "분 전";
            }else if (gap < 60 * 24){
                word = (gap/60) + "시간 전";
            }else if (gap < 60 * 24 * 30) {
                word = (gap/60/24) + "일 전";
            } else if (gap < 60 * 24 * 30 * 12){
                word = (gap/60/24/30) + "달 전";
//                word = dayBefore.format(DateTimeFormatter.ofPattern("MM월 dd일"));
            }
        }
        return word;
    }



}
