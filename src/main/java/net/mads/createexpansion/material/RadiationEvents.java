package net.mads.createexpansion.material;

import net.mads.createexpansion.CreateExpansion;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = CreateExpansion.MOD_ID)
public final class RadiationEvents {
    private static final int BASE_DAMAGE_INTERVAL_TICKS = 600;
    private static final int MIN_DAMAGE_INTERVAL_TICKS = 10;

    private RadiationEvents() {
    }

    @SubscribeEvent
    public static void damagePlayer(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide() || player.isCreative() || player.isSpectator()) {
            return;
        }

        int radioactivity = carriedRadioactivity(player);
        if (radioactivity <= 0) {
            return;
        }

        int interval = Math.max(
                MIN_DAMAGE_INTERVAL_TICKS,
                BASE_DAMAGE_INTERVAL_TICKS - radioactivity
        );

        if (player.tickCount % interval != 0) {
            return;
        }

        float damage = Math.min(6.0F, 0.5F + (radioactivity / 10) * 0.5F);
        player.hurt(player.damageSources().magic(), damage);
    }

    private static int carriedRadioactivity(Player player) {
        Inventory inventory = player.getInventory();
        int radioactivity = 0;

        for (ItemStack stack : inventory.items) {
            radioactivity += stackRadioactivity(stack);
        }

        for (ItemStack stack : inventory.armor) {
            radioactivity += stackRadioactivity(stack);
        }

        for (ItemStack stack : inventory.offhand) {
            radioactivity += stackRadioactivity(stack);
        }

        return radioactivity;
    }

    private static int stackRadioactivity(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }

        return MaterialLookup.find(stack)
                .map(target -> target.material().radioactivity() * stack.getCount())
                .orElse(0);
    }
}