package com.example.project;

/**
 * Transient data structure to hold study session information
 * No persistence - all data is in-memory only
 */
public class StudySession {
    // Session information
    private String technique;
    private String subject;
    private String task;
    private int workDuration; // in minutes
    private int breakDuration; // in minutes
    
    // Session state
    private SessionState state;
    private boolean isPaused;
    private int currentCycle; // for multi-cycle techniques (e.g., Pomodoro)
    
    // Time tracking
    private long startTime; // timestamp when session started (original session start)
    private long cycleStartTime; // timestamp when current cycle started (for multi-cycle techniques)
    private long elapsedTime; // milliseconds elapsed
    private long remainingTimeMillis; // for pause/resume

    public StudySession(String technique, String subject, String task, int workDuration, int breakDuration) {
        this.technique = technique;
        this.subject = subject;
        this.task = task;
        this.workDuration = workDuration;
        this.breakDuration = breakDuration;
        this.state = SessionState.READY;
        this.startTime = System.currentTimeMillis();
        this.cycleStartTime = System.currentTimeMillis(); // Initialize cycle start time to session start
        this.elapsedTime = 0;
        this.isPaused = false;
        this.currentCycle = 1;
        this.remainingTimeMillis = workDuration * 60 * 1000; // convert minutes to milliseconds
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

    public int getWorkDuration() {
        return workDuration;
    }

    public int getBreakDuration() {
        return breakDuration;
    }

    public SessionState getState() {
        return state;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public int getCurrentCycle() {
        return currentCycle;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getCycleStartTime() {
        return cycleStartTime;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public long getRemainingTimeMillis() {
        return remainingTimeMillis;
    }

    // Setters
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setState(SessionState state) {
        this.state = state;
    }

    public void setPaused(boolean paused) {
        this.isPaused = paused;
    }

    public void setCurrentCycle(int currentCycle) {
        this.currentCycle = currentCycle;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public void setRemainingTimeMillis(long remainingTimeMillis) {
        this.remainingTimeMillis = remainingTimeMillis;
    }

    public void setCycleStartTime(long cycleStartTime) {
        this.cycleStartTime = cycleStartTime;
    }

    /**
     * Gets formatted time remaining as MM:SS string
     */
    public String getFormattedTimeRemaining() {
        long totalSeconds = remainingTimeMillis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Gets formatted time remaining with smart labels (hours, minutes, seconds)
     * Examples:
     * - "1 hour 30 minutes remaining" (for 90:00)
     * - "25 minutes 49 seconds remaining" (for 25:49)
     * - "56 seconds remaining" (for 0:56)
     */
    public String getFormattedTimeRemainingText() {
        long totalSeconds = remainingTimeMillis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        
        StringBuilder text = new StringBuilder();
        
        // Format based on time remaining
        if (hours > 0) {
            // Has hours: show "X hour(s) and Y minute(s) remaining"
            // For hours, we typically don't show seconds to keep it clean
            text.append(hours);
            text.append(hours == 1 ? " hour" : " hours");
            
            if (minutes > 0) {
                text.append(" and ");
                text.append(minutes);
                text.append(minutes == 1 ? " minute" : " minutes");
            }
        } else if (minutes > 0) {
            // Only minutes: show "X minute(s) Y second(s) remaining"
            text.append(minutes);
            text.append(minutes == 1 ? " minute" : " minutes");
            
            // Always show seconds when we have minutes (for precision)
            if (seconds > 0) {
                text.append(" ");
                text.append(seconds);
                text.append(seconds == 1 ? " second" : " seconds");
            }
        } else {
            // Only seconds: show "X second(s) remaining"
            text.append(seconds);
            text.append(seconds == 1 ? " second" : " seconds");
        }
        
        text.append(" remaining");
        return text.toString();
    }

    /**
     * Gets the total duration in milliseconds for work session
     */
    public long getWorkDurationMillis() {
        return workDuration * 60 * 1000;
    }

    /**
     * Gets the total duration in milliseconds for break session
     */
    public long getBreakDurationMillis() {
        return breakDuration * 60 * 1000;
    }

    /**
     * Increments the cycle counter (for multi-cycle techniques)
     */
    public void incrementCycle() {
        this.currentCycle++;
    }

    /**
     * Resets the cycle counter
     */
    public void resetCycle() {
        this.currentCycle = 1;
    }

    /**
     * Gets the break duration based on technique
     * In TEST_MODE: Returns duration in seconds
     * In PRODUCTION: Returns duration in minutes
     * Note: For Pomodoro long break (cycle 4), use getLongBreakDurationForTechnique()
     */
    public static int getBreakDurationForTechnique(String technique) {
        // Check if we're in test mode (we'll pass a flag or check MainActivity's TEST_MODE)
        // For now, we'll add a parameter or check MainActivity's static field
        // Actually, let's check MainActivity's TEST_MODE via reflection or add a parameter
        // Simpler: Add a static method in MainActivity to check test mode, or pass it as parameter
        // For now, let's use a simpler approach - check if MainActivity.TEST_MODE exists
        boolean testMode = false;
        try {
            java.lang.reflect.Field field = Class.forName("com.example.project.MainActivity")
                .getDeclaredField("TEST_MODE");
            field.setAccessible(true);
            testMode = field.getBoolean(null);
        } catch (Exception e) {
            // Default to production mode if can't access
        }
        
        if (testMode) {
            // TEST MODE: Return durations in seconds
            switch (technique) {
                case "Pomodoro":
                    return 30; // 30 seconds (for cycles 1, 2, 3)
                case "52/17":
                    return 30; // 30 seconds
                case "90-Minute":
                    return 30; // 30 seconds
                case "Sprint":
                    return 30; // 30 seconds
                case "Deadline":
                case "Custom Goal":
                default:
                    return 0; // No break for deadline/custom
            }
        } else {
            // PRODUCTION MODE: Return durations in minutes
            switch (technique) {
                case "Pomodoro":
                    return 5; // 5 minutes break
                case "52/17":
                    return 17; // 17 minutes break
                case "90-Minute":
                    return 25; // 25 minutes break
                case "Sprint":
                    return 2; // 1.5 minutes, rounded up to 2 minutes (using integer minutes)
                case "Deadline":
                case "Custom Goal":
                default:
                    return 0; // No break for deadline/custom
            }
        }
    }
    
    /**
     * Gets the long break duration for Pomodoro (cycle 4)
     * In TEST_MODE: Returns duration in seconds
     * In PRODUCTION: Returns duration in minutes
     */
    public static int getLongBreakDurationForTechnique(String technique) {
        boolean testMode = false;
        try {
            java.lang.reflect.Field field = Class.forName("com.example.project.MainActivity")
                .getDeclaredField("TEST_MODE");
            field.setAccessible(true);
            testMode = field.getBoolean(null);
        } catch (Exception e) {
            // Default to production mode if can't access
        }
        
        if (testMode) {
            // TEST MODE: Return duration in seconds
            if ("Pomodoro".equals(technique)) {
                return 120; // 2 min = 120 seconds (for cycle 4)
            }
        } else {
            // PRODUCTION MODE: Return duration in minutes
            if ("Pomodoro".equals(technique)) {
                return 25; // 25 minutes (for cycle 4)
            }
        }
        // For other techniques, return regular break duration
        return getBreakDurationForTechnique(technique);
    }
}

