package lach_01298.qmd.multiblock.network;

import io.netty.buffer.ByteBuf;
import lach_01298.qmd.multiblock.IMultiBlockTank;
import lach_01298.qmd.network.QMDPacket;
import lach_01298.qmd.util.Util;
import nc.multiblock.Multiblock;
import nc.tile.internal.fluid.Tank;
import nc.tile.multiblock.ITileMultiblockPart;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

public class QMDClearTankPacket extends QMDPacket
{
	
	private BlockPos pos;
	private int tankID;
	
	public QMDClearTankPacket()
	{
	
	}
	
	public QMDClearTankPacket(BlockPos pos, int tankID)
	{
		this.pos = pos;
		this.tankID = tankID;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
		tankID = buf.readInt();
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
		buf.writeInt(tankID);
	}
	
	public static class Handler implements IMessageHandler<QMDClearTankPacket, IMessage>
	{
		
		@Override
		public IMessage onMessage(QMDClearTankPacket message, MessageContext ctx)
		{
			if (ctx.side == Side.SERVER)
			{
				FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> processMessage(message, ctx));
			}
			return null;
		}
		
		void processMessage(QMDClearTankPacket message, MessageContext ctx)
		{
			EntityPlayerMP player = ctx.getServerHandler().player;
			World world = player.getServerWorld();
			if (!world.isBlockLoaded(message.pos) || !world.isBlockModifiable(player, message.pos))
			{
				return;
			}
			TileEntity tile = world.getTileEntity(message.pos);
			if (tile instanceof ITileMultiblockPart) {
				Multiblock multiblock = ((ITileMultiblockPart) tile).getMultiblock();
				if(multiblock instanceof IMultiBlockTank)
				{
					IMultiBlockTank mbTanks = (IMultiBlockTank) multiblock;
					List<Tank> tanks = mbTanks.getTanks();
					if(tanks.size() > message.tankID)
					{
						tanks.get(message.tankID).setFluid(null);
					}
					else
					{
						Util.getLogger().error("cannot clear multiblock tank " +message.tankID + " as multiblock only has "+tanks.size()+" tanks");
					}
				}
			}
		}
	}
}
