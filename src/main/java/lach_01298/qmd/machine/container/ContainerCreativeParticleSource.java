package lach_01298.qmd.machine.container;

import lach_01298.qmd.tile.TileCreativeParticleSource;
import nclegacy.container.ContainerTileLegacy;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerCreativeParticleSource extends ContainerTileLegacy<TileCreativeParticleSource>
{

	
	public ContainerCreativeParticleSource(EntityPlayer player, TileCreativeParticleSource tile)
	{
		super(tile);
		
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return true;
	}

}
