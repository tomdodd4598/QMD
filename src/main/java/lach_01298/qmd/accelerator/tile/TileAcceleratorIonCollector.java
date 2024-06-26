package lach_01298.qmd.accelerator.tile;

import com.google.common.collect.Lists;
import lach_01298.qmd.QMD;
import lach_01298.qmd.accelerator.*;
import lach_01298.qmd.util.InventoryStackList;
import nc.multiblock.cuboidal.CuboidalPartPositionType;
import nc.tile.fluid.ITileFluid;
import nc.tile.internal.fluid.*;
import nc.tile.internal.inventory.*;
import nc.tile.inventory.ITileInventory;
import nc.tile.passive.ITilePassive;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.*;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.*;
import java.util.*;

public class TileAcceleratorIonCollector extends TileAcceleratorPart implements ITileInventory,  ITileFluid, ITickable
{
	
	private final @Nonnull NonNullList<ItemStack> inventoryStacks = NonNullList.withSize(1, ItemStack.EMPTY);
	private TileMassSpectrometerController controller;
	
	private final @Nonnull String inventoryName = QMD.MOD_ID + ".container.ion_collector";
	private @Nonnull InventoryConnection[] inventoryConnections = ITileInventory.inventoryConnectionAll(Lists.newArrayList(ItemSorption.OUT));
	
	private final @Nonnull List<Tank> backupTanks = Lists.newArrayList(new Tank(1, new HashSet<>()));

	private @Nonnull FluidConnection[] fluidConnections = ITileFluid.fluidConnectionAll(Lists.newArrayList(TankSorption.OUT));
	
	private @Nonnull FluidTileWrapper[] fluidSides;
	
	private int IONumber;
	
	public TileAcceleratorIonCollector()
	{
		super(CuboidalPartPositionType.WALL);
		fluidSides = ITileFluid.getDefaultFluidSides(this);
		
		this.IONumber= 0;
	}

	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
	{
		return oldState.getBlock() != newState.getBlock();
	}
	
	@Override
	public void onMachineAssembled(Accelerator accelerator)
	{
		if(accelerator.controller instanceof TileMassSpectrometerController)
		{
			controller =  (TileMassSpectrometerController) accelerator.controller;
		}
		
		super.onMachineAssembled(accelerator);
	}
	
	
	@Override
	public void onMachineBroken()
	{
		controller = null;
		super.onMachineBroken();
	}

	@Override
	public void update()
	{
		EnumFacing facing = getPartPosition().getFacing();

		if (!world.isRemote && !getTanks().get(0).isEmpty() && facing != null && getTankSorption(facing, 0).canDrain())
		{
			pushFluidToSide(facing);
		}
	}
	
	
	// Items
	
	@Override
	public NonNullList<ItemStack> getInventoryStacks()
	{
		if(controller != null && IONumber !=0)
		{
			if(getLogic() instanceof MassSpectrometerLogic)
			{
				
				return new InventoryStackList(controller.getInventoryStacks().subList(IONumber-1,IONumber));
			}
		}

		return inventoryStacks;
	}

	@Override
	public String getName()
	{
		return inventoryName;
	}

	@Override
	public @Nonnull InventoryConnection[] getInventoryConnections()
	{
		return inventoryConnections;
	}

	@Override
	public void setInventoryConnections(@Nonnull InventoryConnection[] connections)
	{
		inventoryConnections = connections;
	}


	@Override
	public ItemOutputSetting getItemOutputSetting(int slot)
	{
		return ItemOutputSetting.DEFAULT;
	}

	@Override
	public void setItemOutputSetting(int slot, ItemOutputSetting setting)
	{
	}
	
	// Fluids
	
	@Override
	public @Nonnull List<Tank> getTanks()
	{
		if(getMultiblock() != null && IONumber !=0)
		{
			if(getLogic() instanceof MassSpectrometerLogic)
			{
				return getMultiblock().isAssembled() ? getMultiblock().tanks.subList(IONumber, IONumber+1) : backupTanks;
			}
		}

		return  backupTanks;
	}

	@Override
	@Nonnull
	public FluidConnection[] getFluidConnections()
	{
		return fluidConnections;
	}
	
	@Override
	public void setFluidConnections(@Nonnull FluidConnection[] connections)
	{
		fluidConnections = connections;
	}

	@Override
	@Nonnull
	public FluidTileWrapper[] getFluidSides()
	{
		return fluidSides;
	}

	@Override
	public GasTileWrapper getGasWrapper()
	{
		return null;
	}

	@Override
	public void pushFluidToSide(@Nonnull EnumFacing side)
	{
		TileEntity tile = getTileWorld().getTileEntity(getTilePos().offset(side));
		if (tile == null || tile instanceof TileAcceleratorIonCollector || tile instanceof TileAcceleratorIonSource)
			return;

		if (tile instanceof ITilePassive)
			if (!((ITilePassive) tile).canPushFluidsTo())
				return;

		IFluidHandler adjStorage = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,side.getOpposite());
		if (adjStorage == null)
			return;

		for (int i = 0; i < getTanks().size(); i++)
		{
			if (getTanks().get(i).getFluid() == null || !getTankSorption(side, i).canDrain())
				continue;

			getTanks().get(i).drain(adjStorage.fill(getTanks().get(i).drain(getTanks().get(i).getCapacity(), false), true), true);
		}
	}

	@Override
	public boolean getInputTanksSeparated()
	{
		return false;
	}

	@Override
	public void setInputTanksSeparated(boolean separated)
	{
	}

	@Override
	public boolean getVoidUnusableFluidInput(int tankNumber)
	{
		return false;
	}

	@Override
	public void setVoidUnusableFluidInput(int tankNumber, boolean voidUnusableFluidInput)
	{
	}

	@Override
	public TankOutputSetting getTankOutputSetting(int tankNumber)
	{
		return TankOutputSetting.DEFAULT;
	}

	@Override
	public void setTankOutputSetting(int tankNumber, TankOutputSetting setting)
	{
	}

	@Override
	public boolean hasConfigurableFluidConnections()
	{
		return true;
	}

	// NBT
	
	@Override
	public NBTTagCompound writeAll(NBTTagCompound nbt)
	{
		super.writeAll(nbt);
		writeInventory(nbt);
		writeInventoryConnections(nbt);
		writeTanks(nbt);
		writeFluidConnections(nbt);
		writeTankSettings(nbt);
		nbt.setInteger("IONumber", IONumber);

		return nbt;
	}
	
	@Override
	public void readAll(NBTTagCompound nbt)
	{
		super.readAll(nbt);
		readInventory(nbt);
		readInventoryConnections(nbt);
		readTanks(nbt);
		readFluidConnections(nbt);
		readTankSettings(nbt);
		IONumber = nbt.getInteger("IONumber");
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing side)
	{
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			return !getInventoryStacks().isEmpty() && hasInventorySideCapability(side);
		}
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
		{
			return !getTanks().isEmpty() && hasFluidSideCapability(side);
		}
		return super.hasCapability(capability, side);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side)
	{
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			if (!getInventoryStacks().isEmpty() && hasInventorySideCapability(side))
			{
				return (T) getItemHandler(side);
			}
			return null;

		}
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
		{
			if (!getTanks().isEmpty() && hasFluidSideCapability(side))
			{
				return (T) getFluidSide(nonNullSide(side));
			}
			return null;
		}
		return super.getCapability(capability, side);
	}
	
	public void setIONumber(int number)
	{
		if(number >= 0 && number <= 6)
		{
			IONumber= number;
		}
	}
	
	public int getIONumber()
	{
		return	IONumber;
	}

}
