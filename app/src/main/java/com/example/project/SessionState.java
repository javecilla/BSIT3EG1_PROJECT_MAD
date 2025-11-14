package com.example.project;

/**
 * Enum representing the different states of a study session
 */
public enum SessionState {
    IDLE,           // No active session
    SELECTING,      // User is selecting a technique
    READY,          // Session created but not started
    ACTIVE_WORK,    // Work session is active
    ACTIVE_BREAK,   // Break session is active
    PAUSED,         // Session is paused
    COMPLETED;      // Session is completed

    /**
     * Checks if the session is currently active (work or break)
     */
    public boolean isActive() {
        return this == ACTIVE_WORK || this == ACTIVE_BREAK;
    }

    /**
     * Checks if the session is in work phase
     */
    public boolean isWorkPhase() {
        return this == ACTIVE_WORK;
    }

    /**
     * Checks if the session is in break phase
     */
    public boolean isBreakPhase() {
        return this == ACTIVE_BREAK;
    }

    /**
     * Gets a display-friendly name for the state
     */
    public String getDisplayName() {
        switch (this) {
            case IDLE:
                return "Idle";
            case SELECTING:
                return "Selecting";
            case READY:
                return "Ready";
            case ACTIVE_WORK:
                return "WORK SESSION";
            case ACTIVE_BREAK:
                return "BREAK TIME";
            case PAUSED:
                return "PAUSED";
            case COMPLETED:
                return "Completed";
            default:
                return this.name();
        }
    }
}

