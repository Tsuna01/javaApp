package service;

import java.util.ArrayList;
import java.util.List;
import model.Job;
import model.SearchField;

public class SearchEngine {

    private final JobManager jobManager;

    public SearchEngine(JobManager jobManager) {
        this.jobManager = jobManager;
    }

    // =========================
    // 1) Search แบบ filter หลายเงื่อนไขพร้อมกัน (เหมาะกับ UI)
    // =========================
    public List<Job> search(SearchQuery q) {
        List<Job> result = new ArrayList<>();

        for (Job job : jobManager.getJobs()) {
            // ✅ แก้ไขจุดที่ 1: แปลง int เป็น String ก่อนส่งเข้า match
            if (!match(String.valueOf(job.getJobId()), q.getJobId())) continue;

            if (!match(job.getTitle(), q.getTitle())) continue;
            if (!match(job.getDetails(), q.getDetails())) continue;
            if (!match(job.getLocation(), q.getLocation())) continue;

            if (!match(String.valueOf(job.getWorkingHours()), q.getWorkingHours())) continue;
            if (!match(job.getDateTime(), q.getDateTime())) continue;

            if (!match(String.valueOf(job.getVacancies()), q.getVacancies())) continue;

            if (!match(job.getJobType(), q.getJobType())) continue;

            result.add(job);
        }

        return result;
    }

    // =========================
    // 2) Search ทีละ field
    // =========================
    public List<Job> searchByField(SearchField field, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return jobManager.getJobs();
        }

        String lower = keyword.toLowerCase();
        List<Job> result = new ArrayList<>();

        for (Job job : jobManager.getJobs()) {
            String value = getFieldValue(job, field);
            if (value != null && value.toLowerCase().contains(lower)) {
                result.add(job);
            }
        }
        return result;
    }

    // Convenience Methods
    public List<Job> searchByTitle(String keyword) { return search(new SearchQuery().title(keyword)); }
    public List<Job> searchByDetails(String keyword) { return search(new SearchQuery().details(keyword)); }
    public List<Job> searchByLocation(String keyword) { return search(new SearchQuery().location(keyword)); }
    public List<Job> searchByJobId(String keyword) { return search(new SearchQuery().jobId(keyword)); }

    private boolean match(String value, String query) {
        if (query == null || query.isBlank()) return true;
        if (value == null) return false;
        return value.toLowerCase().contains(query.toLowerCase());
    }

    private String getFieldValue(Job job, SearchField field) {
        return switch (field) {
            // ✅ แก้ไขจุดที่ 2: แปลง int เป็น String ใน switch case
            case JOBID -> String.valueOf(job.getJobId());

            case TITLE -> job.getTitle();
            case DETAILS -> job.getDetails();
            case LOCATION -> job.getLocation();
            case WORKING_HOURS -> String.valueOf(job.getWorkingHours());
            case DATE_TIME -> job.getDateTime();
            case VACANCIES -> String.valueOf(job.getVacancies());
            case JOB_TYPE -> job.getJobType();

            default -> null;
        };
    }
}