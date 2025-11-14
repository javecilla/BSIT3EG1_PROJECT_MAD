package com.example.project;

/**
 * Data class to represent a completed study session
 * Used for displaying session history in "My Activity" tab
 * All data is transient - lost when app closes
 */
public class CompletedSession {
    private String technique;
    private String subject;
    private String task;
    private long timeSpentMillis;
    private long completionTimestamp;

    public CompletedSession(String technique, String subject, String task, long timeSpentMillis, long completionTimestamp) {
        this.technique = technique;
        this.subject = subject;
        this.task = task;
        this.timeSpentMillis = timeSpentMillis;
        this.completionTimestamp = completionTimestamp;
    }

    // Getters
    public String getTechnique() {
        return technique;
    }

    public String getSubject() {
        return subject;
    }

    public String getTask() {
        return task;
    }

    public long getTimeSpentMillis() {
        return timeSpentMillis;
    }

    public long getCompletionTimestamp() {
        return completionTimestamp;
    }

    /**
     * Formats time spent as a human-readable string
     * Format rules:
     * - If only seconds: "37 seconds"
     * - If minutes (include seconds): "38 minutes and 28 seconds"
     * - If hours (include minutes, but not seconds): "2 hours and 18 minutes"
     */
    public String getFormattedTimeSpent() {
        long totalSeconds = timeSpentMillis / 1000;
        long totalMinutes = totalSeconds / 60;
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        long seconds = totalSeconds % 60;
        
        StringBuilder text = new StringBuilder();
        
        if (hours > 0) {
            // Has hours: show "X hours and Y minutes" (no seconds)
            text.append(hours);
            text.append(hours == 1 ? " hour" : " hours");
            
            if (minutes > 0) {
                text.append(" and ");
                text.append(minutes);
                text.append(minutes == 1 ? " minute" : " minutes");
            }
        } else if (minutes > 0) {
            // Only minutes: show "X minutes and Y seconds"
            text.append(minutes);
            text.append(minutes == 1 ? " minute" : " minutes");
            
            if (seconds > 0) {
                text.append(" and ");
                text.append(seconds);
                text.append(seconds == 1 ? " second" : " seconds");
            }
        } else {
            // Only seconds: show "X seconds"
            text.append(seconds);
            text.append(seconds == 1 ? " second" : " seconds");
        }
        
        return text.toString();
    }

    /**
     * Formats completion timestamp as relative time
     * Examples:
     * - "Just now" (< 1 minute ago)
     * - "5 minutes ago"
     * - "2 hours ago"
     * - "3 days ago"
     */
    public String getRelativeTime() {
        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - completionTimestamp;
        
        long minutesAgo = timeDifference / (60 * 1000);
        long hoursAgo = timeDifference / (60 * 60 * 1000);
        long daysAgo = timeDifference / (24 * 60 * 60 * 1000);
        
        if (minutesAgo < 1) {
            return "Just now";
        } else if (minutesAgo < 60) {
            return minutesAgo + (minutesAgo == 1 ? " minute ago" : " minutes ago");
        } else if (hoursAgo < 24) {
            return hoursAgo + (hoursAgo == 1 ? " hour ago" : " hours ago");
        } else {
            return daysAgo + (daysAgo == 1 ? " day ago" : " days ago");
        }
    }
}

