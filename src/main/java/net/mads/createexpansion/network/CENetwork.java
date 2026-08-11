package net.mads.createexpansion.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class CENetwork {
    private CENetwork() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                BindMultiblockSchedulePayload.TYPE,
                BindMultiblockSchedulePayload.STREAM_CODEC,
                BindMultiblockSchedulePayload::handle
        );
    }
}
