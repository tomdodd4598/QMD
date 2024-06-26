package lach_01298.qmd.multiblock.network;

import io.netty.buffer.ByteBuf;
import lach_01298.qmd.particle.*;
import lach_01298.qmd.util.ByteUtil;
import nc.tile.internal.energy.EnergyStorage;
import nc.tile.internal.fluid.Tank;
import nc.tile.internal.fluid.Tank.TankInfo;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class ParticleChamberUpdatePacket extends QMDMultiblockUpdatePacket
{

	public boolean isChamberOn;
	public int requiredEnergy;
	public double efficiency;
	public EnergyStorage energyStorage;
	public List<TankInfo> tanksInfo;
	public List<ParticleStorageAccelerator> beams;
	
	public ParticleChamberUpdatePacket()
	{
		
		beams = new ArrayList<ParticleStorageAccelerator>();
	}
	
	public ParticleChamberUpdatePacket(BlockPos pos, boolean isChamberOn, int requiredEnergy, double efficiency, EnergyStorage energyStorage, List<Tank> tanks, List<ParticleStorageAccelerator> beams)
	{
		
		this.pos = pos;
		this.isChamberOn = isChamberOn;
		this.requiredEnergy =requiredEnergy;
		this.efficiency = efficiency;
		this.energyStorage = energyStorage;
		
		tanksInfo = TankInfo.getInfoList(tanks);
		this.beams = beams;
		
		
	}
	
	
	
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
		isChamberOn = buf.readBoolean();
		requiredEnergy = buf.readInt();
		efficiency = buf.readDouble();
		energyStorage = ByteUtil.readBufEnergy(buf);
		tanksInfo = readTankInfos(buf);
		
		int size = buf.readInt();
		for (int i = 0; i < size; i++)
		{
			ParticleStorage storage = ByteUtil.readBufBeam(buf);
			ParticleStorageAccelerator beam = new ParticleStorageAccelerator();
			beam.setParticleStack(storage.getParticleStack());
			beam.setMaxEnergy(storage.getMaxEnergy());
			beam.setMinEnergy(storage.getMinEnergy());
			beam.setCapacity(storage.getCapacity());
			beams.add(beam);
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
		buf.writeBoolean(isChamberOn);
		buf.writeInt(requiredEnergy);
		buf.writeDouble(efficiency);
		ByteUtil.writeBufEnergy(energyStorage, buf);
		writeTankInfos(buf, tanksInfo);
		
		buf.writeInt(beams.size());
		for(ParticleStorageAccelerator beam : beams)
		{
			ByteUtil.writeBufBeam(beam, buf);
		}
		
	}

}
