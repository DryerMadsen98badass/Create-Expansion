package net.mads.createexpansion.material;

import net.minecraft.world.item.Item;

public class MaterialItem extends Item {
    private final IndustrialMaterial material;
    private final MaterialPart part;

    public MaterialItem(IndustrialMaterial material, MaterialPart part) {
        super(new Item.Properties());
        this.material = material;
        this.part = part;
    }

    public IndustrialMaterial material() {
        return material;
    }

    public MaterialPart part() {
        return part;
    }

}
