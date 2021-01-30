package com.linhard.martin.remotemuteclient;

public enum ToggleState {
    MUTED,
    UNMUTED;

    public ToggleState getNextState() {
        if (this == ToggleState.UNMUTED) {
            return ToggleState.MUTED;
        }
        else {
            return ToggleState.UNMUTED;
        }
    }

    public static ToggleState fromStatusReponse(StatusResponse s) {
        if (s.isMuted()) {
            return ToggleState.MUTED;
        }
        else {
            return ToggleState.UNMUTED;
        }
    }
}
