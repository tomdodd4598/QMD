package lach_01298.qmd.multiblock.particleChamber.tile;

import javax.annotation.Nullable;

import lach_01298.qmd.EnumTypes;
import lach_01298.qmd.Util;
import lach_01298.qmd.config.QMDConfig;
import lach_01298.qmd.multiblock.particleChamber.ParticleChamber;
import nc.config.NCConfig;
import nc.multiblock.cuboidal.CuboidalPartPositionType;
import nc.util.BlockPosHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public abstract class TileParticleChamberDetector extends TileParticleChamberPart
{

	public final double efficiency;
	public final int basePower;
	public final String name;

	public boolean isFunctional = false;

	public TileParticleChamberDetector(double efficiency, int basePower, String name)
	{
		super(CuboidalPartPositionType.INTERIOR);
	
		this.efficiency =efficiency;
	
		this.basePower = basePower;
		this.name = name;
	}

	

	public static class BubbleChamber extends TileParticleChamberDetector
	{
		public BubbleChamber()
		{
			super( QMDConfig.detector_efficiency[0], QMDConfig.detector_base_power[0], EnumTypes.DetectorType.BUBBLE_CHAMBER.getName());
		}
		
		public boolean isInvalidPostion(BlockPos target)
		{
			if(Util.getTaxiDistance(target, getPos()) <= 2)
			{
				return true;
			}
			return false;
		}
		
	}
	
	public static class SiliconTracker extends TileParticleChamberDetector
	{
		public SiliconTracker()
		{
			super( QMDConfig.detector_efficiency[1], QMDConfig.detector_base_power[1], EnumTypes.DetectorType.SILLICON_TRACKER.getName());
		}
		
		public boolean isInvalidPostion(BlockPos target)
		{
			if(Util.getTaxiDistance(target, getPos()) <= 1)
			{
				return true;
			}
			return false;
		}
	}
	
	public static class WireChamber extends TileParticleChamberDetector
	{
		public WireChamber()
		{
			super( QMDConfig.detector_efficiency[2], QMDConfig.detector_base_power[2], EnumTypes.DetectorType.WIRE_CHAMBER.getName());
		}
		
		public boolean isInvalidPostion(BlockPos target)
		{
			if(Util.getTaxiDistance(target, getPos()) <= 2)
			{
				return true;
			}
			return false;
		}
	}
	
	public static class EMCalorimeter extends TileParticleChamberDetector
	{
		public EMCalorimeter()
		{
			super( QMDConfig.detector_efficiency[3], QMDConfig.detector_base_power[3], EnumTypes.DetectorType.EM_CALORIMETER.getName());
		}
		
		public boolean isInvalidPostion(BlockPos target)
		{
			if(Util.getTaxiDistance(target, getPos()) <= 3)
			{
				return true;
			}
			return false;
		}
	}
	
	public static class HadronCalorimeter extends TileParticleChamberDetector
	{
		public HadronCalorimeter()
		{
			super( QMDConfig.detector_efficiency[4], QMDConfig.detector_base_power[4], EnumTypes.DetectorType.HADRON_CALORIMETER.getName());
		}
		
		public boolean isInvalidPostion(BlockPos target)
		{
			if(Util.getTaxiDistance(target, getPos()) <= 5)
			{
				return true;
			}
			return false;
		}
	}



	
	

	@Override
	public void onMachineAssembled(ParticleChamber controller)
	{
		super.onMachineAssembled(controller);
		
	}

	@Override
	public void onMachineBroken()
	{
		super.onMachineBroken();
		
	}


	@Override
	public void update()
	{
	}







	
	// NBT
		@Override
		public NBTTagCompound writeAll(NBTTagCompound nbt)
		{
			super.writeAll(nbt);
			return nbt;
		}

		@Override
		public void readAll(NBTTagCompound nbt)
		{
			super.readAll(nbt);
		}
	


		public boolean isInvalidPostion(BlockPos target)
		{
			return false;
		}
	
}
