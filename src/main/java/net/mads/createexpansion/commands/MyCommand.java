package net.mads.createexpansion.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import net.mads.createexpansion.debug.CEPerformanceProfiler;
import net.mads.createexpansion.worldgen.OreVeinLocator;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import java.util.Optional;

public class MyCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(root("create_expansion"));
        dispatcher.register(root("expansion"));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> root(String name) {
        return Commands.literal(name)
                .requires(source -> source.hasPermission(2))
                .executes(ctx -> {
                    ctx.getSource().sendSuccess(
                            () -> Component.literal("Use /create_expansion ore_vein <vein or material>"),
                            false
                    );
                    return 1;
                })
                .then(Commands.literal("ore_vein")
                        .executes(ctx -> {
                            ctx.getSource().sendSuccess(
                                    () -> Component.literal("Use /create_expansion ore_vein <copper|iron|diamond|...>"),
                                    false
                            );
                            return 1;
                        })
                        .then(Commands.argument("target", StringArgumentType.word())
                                .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(OreVeinLocator.searchableIds(), builder))
                                .executes(ctx -> locateOreVein(
                                        ctx.getSource(),
                                        StringArgumentType.getString(ctx, "target")
                                ))))
                .then(Commands.literal("profile")
                        .executes(ctx -> showProfileStats(ctx.getSource()))
                        .then(Commands.literal("on")
                                .executes(ctx -> {
                                    CEPerformanceProfiler.enable();
                                    ctx.getSource().sendSuccess(() -> Component.literal("CE profiler enabled."), false);
                                    return 1;
                                }))
                        .then(Commands.literal("off")
                                .executes(ctx -> {
                                    CEPerformanceProfiler.disable();
                                    ctx.getSource().sendSuccess(() -> Component.literal("CE profiler disabled."), false);
                                    return 1;
                                }))
                        .then(Commands.literal("reset")
                                .executes(ctx -> {
                                    CEPerformanceProfiler.reset();
                                    ctx.getSource().sendSuccess(() -> Component.literal("CE profiler reset."), false);
                                    return 1;
                                }))
                        .then(Commands.literal("stats")
                                .executes(ctx -> showProfileStats(ctx.getSource()))));
    }

    private static int showProfileStats(CommandSourceStack source) {
        for (Component line : CEPerformanceProfiler.stats()) {
            source.sendSuccess(() -> line, false);
        }
        return 1;
    }

    private static int locateOreVein(CommandSourceStack source, String target) {
        BlockPos origin = BlockPos.containing(source.getPosition());
        Optional<OreVeinLocator.Result> result = OreVeinLocator.locate(source.getLevel(), origin, target);
        if (result.isEmpty()) {
            source.sendFailure(Component.literal("No ore vein found for '" + target + "' nearby."));
            return 0;
        }

        OreVeinLocator.Result vein = result.get();
        BlockPos center = vein.center();
        source.sendSuccess(
                () -> Component.literal(
                        "Nearest " + vein.depositId() + " vein: "
                                + center.getX() + " " + center.getY() + " " + center.getZ()
                                + " (" + vein.distanceBlocks() + " blocks away)"
                ),
                false
        );
        source.sendSuccess(
                () -> Component.literal("Layers: " + String.join(", ", vein.layers())),
                false
        );
        source.sendSuccess(
                () -> Component.literal("Surface indicators: " + String.join(", ", vein.surfaceIndicators())),
                false
        );

        return 1;
    }
}
