package lach_01298.qmd.item;

import lach_01298.qmd.accelerator.tile.TileAcceleratorBeamPort;
import lach_01298.qmd.capabilities.CapabilityParticleStackHandler;
import lach_01298.qmd.enums.EnumTypes.IOType;
import lach_01298.qmd.particle.IParticleStackHandler;
import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.particle.ParticleStorageBeamline;
import lach_01298.qmd.particleChamber.CollisionChamberLogic;
import lach_01298.qmd.particleChamber.tile.TileParticleChamberBeamPort;
import lach_01298.qmd.util.Equations;
import lach_01298.qmd.util.Units;
import nc.item.NCItem;
import nc.util.Lang;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.text.DecimalFormat;



public class ItemBeamMeter extends NCItem
{

	public ItemBeamMeter(String... tooltip)
	{
		super(tooltip);
		maxStackSize = 1;
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX,
			float hitY, float hitZ, EnumHand hand)
	{
		if (!world.isRemote)
		{
			TileEntity tile = world.getTileEntity(pos);
			if (tile != null)
			{
				if (player.isSneaking())
				{
					if(tile instanceof TileParticleChamberBeamPort)
					{
						TileParticleChamberBeamPort port = (TileParticleChamberBeamPort) tile;
						if(port.getIOType() == IOType.OUTPUT)
						{
							int inputNumberOffset = 0;
							if (port.getMultiblock().getLogic() instanceof CollisionChamberLogic)
							{
								inputNumberOffset = 1;
							}
							TextComponentString message = new TextComponentString(
									 Lang.localize("qmd.block.particle_chamber_port_setting",TextFormatting.LIGHT_PURPLE + " "+(port.getIONumber()-inputNumberOffset)));
							player.sendMessage(message);
							return EnumActionResult.SUCCESS;
						}
					}
					if(tile instanceof TileAcceleratorBeamPort)
					{
						TileAcceleratorBeamPort port = (TileAcceleratorBeamPort) tile;
						TextFormatting colour;
						switch(port.getSetting())
						{
						case INPUT:
							colour = TextFormatting.DARK_AQUA;
							break;
						case OUTPUT:
							colour = TextFormatting.RED;
							break;
						default:
							colour = TextFormatting.GRAY;
							break;
						}
						TextComponentString message = new TextComponentString(Lang.localize("qmd.block.accelerator_port_setting",colour+Lang.localize("qmd.block.port_mode."+ port.getSetting().name())));
						player.sendMessage(message);
						return EnumActionResult.SUCCESS;
					}
				}
				else
				{
					for (EnumFacing face : EnumFacing.VALUES)
					{
						if (tile.hasCapability(CapabilityParticleStackHandler.PARTICLE_HANDLER_CAPABILITY, face))
						{
							IParticleStackHandler particleStorage = tile.getCapability(CapabilityParticleStackHandler.PARTICLE_HANDLER_CAPABILITY, face);

							if (particleStorage instanceof ParticleStorageBeamline)
							{
								ParticleStorageBeamline beamline = (ParticleStorageBeamline) particleStorage;
								ParticleStack stored = beamline.getParticle();

								if(stored == null)
								{
									sendParticleStackInfoBeamline(player, beamline.getPreviousArrivals().getStack(),beamline.getLength());
								}
								else
								{
									sendParticleStackInfoBeamline(player, stored,beamline.getLength());
								}
							}
							else
							{
								sendParticleStackInfo(player, particleStorage.getParticle());
							}
							
							return EnumActionResult.SUCCESS;
						}
					}
				}
			}
		}

		return EnumActionResult.PASS;
	}

	private static void sendParticleStackInfo(EntityPlayer player, ParticleStack particle)
	{
		if (particle == null)
		{
			player.sendMessage(new TextComponentString(Lang.localize("gui.qmd.particlestack.empty")));
			return;
		}

		player.sendMessage(new TextComponentString(getParticleStackString(particle, true, true, false)));
		player.sendMessage(new TextComponentString(getFocusLossString(particle, 0)));
	}

	private static void sendParticleStackInfoBeamline(EntityPlayer player, ParticleStack particle, int length)
	{
		if (particle == null)
		{
			player.sendMessage(new TextComponentString(Lang.localize("gui.qmd.particlestack.empty")));
			return;
		}
		player.sendMessage(new TextComponentString(getParticleStackString(particle, true, true, false)));
		player.sendMessage(new TextComponentString(getFocusLossString(particle, length)));
	}


	private static String getParticleStackString(ParticleStack particle, boolean showAmount, boolean showEnergy, boolean showFocus)
	{
		String description = TextFormatting.AQUA + Lang.localize("gui.qmd.particlestack.name", TextFormatting.WHITE + Lang.localize("qmd.particle." + particle.getParticle().getName() + ".name"));
		if(showAmount) description += " " + TextFormatting.YELLOW + Lang.localize("gui.qmd.particlestack.amount", TextFormatting.WHITE + Units.getSIFormat(particle.getAmount(), "pu"));
		if(showEnergy) description += " " + TextFormatting.GREEN + Lang.localize("gui.qmd.particlestack.mean_energy", TextFormatting.WHITE + Units.getSIFormat(particle.getMeanEnergy(), "eV", 3));
		if(showFocus) description += " " + TextFormatting.DARK_AQUA + Lang.localize("gui.qmd.particlestack.focus", TextFormatting.WHITE + new DecimalFormat("#.###").format(particle.getFocus()));

		return description;
	}

	private static String getFocusLossString(ParticleStack particleIn, int distance)
	{
		double focusLoss = Equations.focusLoss(distance, particleIn);
		ParticleStack particleOut = particleIn.copy();

		String description = "";
		if (distance>0)
		{
			particleOut.addFocus(-focusLoss);
			description += TextFormatting.DARK_AQUA + Lang.localize("gui.qmd.particlestack.focus_in_out", TextFormatting.BLUE + new DecimalFormat("#.###").format(particleIn.getFocus())+TextFormatting.WHITE,TextFormatting.RED + new DecimalFormat("#.###").format(particleOut.getFocus()));
		}
		else
		{
			description += TextFormatting.DARK_AQUA + Lang.localize("gui.qmd.particlestack.focus", TextFormatting.WHITE + new DecimalFormat("#.###").format(particleIn.getFocus()));
		}
		description += " " +TextFormatting.LIGHT_PURPLE + Lang.localize("gui.qmd.particlestack.focus_loss", TextFormatting.WHITE + new DecimalFormat("#.###").format(Equations.focusLoss(1, particleOut)));
		description += "\n" + TextFormatting.GREEN + Lang.localize("gui.qmd.particlestack.travel_distance", TextFormatting.WHITE + new DecimalFormat("#.#").format(Equations.travelDistance(particleOut)));


		return description;
	}




	
}
