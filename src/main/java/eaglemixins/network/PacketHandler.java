package eaglemixins.network;

import eaglemixins.EagleMixins;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class PacketHandler {
    private static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(EagleMixins.MODID);

    public static void init(){
        // start at 1 so unregistered messages (ID 0) throw a more obvious exception when received
        int messageId = 1;

        NETWORK.registerMessage(
                PacketStartTeleportOverlay.Handler.class,
                PacketStartTeleportOverlay.class,
                messageId++,
                Side.CLIENT
        );
        NETWORK.registerMessage(
                PacketStopTeleportOverlay.Handler.class,
                PacketStopTeleportOverlay.class,
                messageId++,
                Side.CLIENT
        );
        NETWORK.registerMessage(
                PacketSyncHighRadiation.Handler.class,
                PacketSyncHighRadiation.class,
                messageId++,
                Side.CLIENT
        );
        NETWORK.registerMessage(
                PacketSyncCarapace.Handler.class,
                PacketSyncCarapace.class,
                messageId++,
                Side.CLIENT
        );
    }

    public static void sendTo(IMessage message, EntityPlayerMP player){
        NETWORK.sendTo(message, player);
    }

    public static void sendToAllTracking(IMessage message, Entity entity){
        NETWORK.sendToAllTracking(message, entity);
    }
}
