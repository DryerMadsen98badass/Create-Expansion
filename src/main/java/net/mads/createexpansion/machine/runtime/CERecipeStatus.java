package net.mads.createexpansion.machine.runtime;

public enum CERecipeStatus {
    IDLE,
    WORKING,
    WAITING_FOR_RESOURCE,
    WAITING_FOR_PH,
    WAITING_FOR_RPM,
    WAITING_FOR_OUTPUT,
    MACHINE_INVALID
}
