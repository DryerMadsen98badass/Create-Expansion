package net.mads.createexpansion.machine.machines.foundry;

public enum FoundryHatchType {
    INPUT("foundry_input_hatch", "Seared Input Hatch", true, false),
    OUTPUT("foundry_output_hatch", "Seared Output Hatch", false, true),
    INPUT_BUS("foundry_input_bus", "Seared Input Bus", false, false);

    private final String id;
    private final String displayName;
    private final boolean input;
    private final boolean output;

    FoundryHatchType(String id, String displayName, boolean input, boolean output) {
        this.id = id;
        this.displayName = displayName;
        this.input = input;
        this.output = output;
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public boolean input() {
        return input;
    }

    public boolean output() {
        return output;
    }
}
