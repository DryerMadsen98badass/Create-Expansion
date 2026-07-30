package net.mads.createexpansion.material;

public interface IndustrialSubstance {
    String id();

    String displayName();

    int color();

    String formula();

    String formula(boolean nested);

    int componentTemperature();
}
