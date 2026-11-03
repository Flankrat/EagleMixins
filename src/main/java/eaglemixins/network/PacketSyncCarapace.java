package eaglemixins.network;

import eaglemixins.client.CarapaceBarRenderer;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Syncs the carapace budget of a parasite to clients, the client extrapolates regeneration on its own.
 */
public class PacketSyncCarapace implements IMessage {
    private int entityId;
    private float budget;
    private float maxBudget;
    private float recoverPerTick;

    public PacketSyncCarapace() {
    }

    public PacketSyncCarapace(int entityId, float budget, float maxBudget, float recoverPerTick) {
        this.entityId = entityId;
        this.budget = budget;
        this.maxBudget = maxBudget;
        this.recoverPerTick = recoverPerTick;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
        this.budget = buf.readFloat();
        this.maxBudget = buf.readFloat();
        this.recoverPerTick = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeFloat(this.budget);
        buf.writeFloat(this.maxBudget);
        buf.writeFloat(this.recoverPerTick);
    }

    public static class Handler implements IMessageHandler<PacketSyncCarapace, IMessage> {
        @Override
        public IMessage onMessage(PacketSyncCarapace message, MessageContext ctx) {
            if (ctx.side.isClient()) {
                FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> PacketProcessor.onMessage(message));
            }
            return null;
        }

        private static class PacketProcessor {
            public static void onMessage(PacketSyncCarapace message) {
                if (Minecraft.getMinecraft().world == null) return;
                Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityId);
                if (entity == null) return;
                CarapaceBarRenderer.setClientData(entity, message.budget, message.maxBudget, message.recoverPerTick);
            }
        }
    }
}
