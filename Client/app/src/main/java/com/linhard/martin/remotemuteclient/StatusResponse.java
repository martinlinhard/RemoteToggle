package com.linhard.martin.remotemuteclient;

public class StatusResponse {
    private boolean muted;

    public StatusResponse(boolean muted) {
        this.muted = muted;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    @Override
    public String toString() {
        return "StatusResponse{" +
                "muted=" + muted +
                '}';
    }
}
