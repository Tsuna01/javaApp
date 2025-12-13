package service;

import java.util.ArrayList;

public class Test {
    public static void main(String[] args) {
        ArrayList<API> job = API.getJobs();
        ArrayList<String> txt = new ArrayList<>();


        for(API work : job){
            System.out.println("--------------------------------");
            System.out.println("ชื่องาน: " + work.title);
            System.out.println("สถานที่: " + work.location);
            System.out.println("ประเภท: " + work.jobType);
            System.out.println(work.dateTime);

            if (work.dateTime != null) {
                String dates = work.dateTime;

                String[] parts = dates.split(" ");

                // วนลูปปริ้นส่วนที่แยกได้ (วันที่ กับ เวลา)
                for(String part : parts){
                    System.out.println("ส่วนย่อย: " + part);
                    txt.add(part);
                }
            } else {
                System.out.println("ไม่มีข้อมูลวันเวลา");
            }
        }

        for(String i : txt){
            System.out.println(i);
        }

    }
}