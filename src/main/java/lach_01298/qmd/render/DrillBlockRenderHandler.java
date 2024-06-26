package lach_01298.qmd.render;

import com.google.common.collect.ImmutableList;
import lach_01298.qmd.item.ItemDrill;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.relauncher.*;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.*;

@SideOnly(Side.CLIENT)
public class DrillBlockRenderHandler implements IResourceManagerReloadListener
{
	public static final DrillBlockRenderHandler INSTANCE = new DrillBlockRenderHandler();
	@SubscribeEvent(priority = EventPriority.LOW)
	public void renderExtraBlockBreak(RenderWorldLastEvent event)
	{
		PlayerControllerMP controllerMP = Minecraft.getMinecraft().playerController;

		if (controllerMP == null)
		{
			return;
		}
		EntityPlayer player = Minecraft.getMinecraft().player;
		ItemStack stack = player.getHeldItemMainhand();

		if (!stack.isEmpty() && stack.getItem() instanceof ItemDrill)
		{
			Entity renderEntity = Minecraft.getMinecraft().getRenderViewEntity();
			if (renderEntity == null)
			{
				return;
			}
			ItemDrill drill = (ItemDrill) stack.getItem();

			RayTraceResult ray = renderEntity.rayTrace(controllerMP.getBlockReachDistance(), event.getPartialTicks());
			if (ray != null)
			{
				Set<BlockPos> extraBlocks = drill.getDiggedBlocks(ray.getBlockPos(), player, drill.getRadius());
				for (BlockPos pos : extraBlocks)
				{
					event.getContext().drawSelectionBox(player, new RayTraceResult(new Vec3d(0, 0, 0), null, pos), 0, event.getPartialTicks());

				}
				if (controllerMP.isHittingBlock)
				{

					if (!stack.isEmpty() && stack.getItem() instanceof ItemDrill)
					{
						BlockPos pos = controllerMP.currentBlock;
						drawBlockDamageTexture(Tessellator.getInstance(), Tessellator.getInstance().getBuffer(), player, event.getPartialTicks(), player.getEntityWorld(), ImmutableList.copyOf(drill.getDiggedBlocks(ray.getBlockPos(), player, drill.getRadius())));
					}
				}
			}
		}

	}

	// Copy of RenderGlobal.drawBlockDamageTexture
	public void drawBlockDamageTexture(Tessellator tessellatorIn, BufferBuilder bufferIn, Entity entityIn,
			float partialTicks, World world, List<BlockPos> blocks)
	{

		double d0 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double) partialTicks;
		double d1 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double) partialTicks;
		double d2 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double) partialTicks;

		TextureManager renderEngine = Minecraft.getMinecraft().renderEngine;
		int progress = (int) (Minecraft.getMinecraft().playerController.curBlockDamageMP * 10.0F) - 1;

		if (progress < 0)
		{
			return;
		}
		renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		preRenderDamagedBlocks();

		bufferIn.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		bufferIn.setTranslation(-d0, -d1, -d2);
		bufferIn.noColor();

		for (BlockPos blockpos : blocks)
		{
			TileEntity tile = world.getTileEntity(blockpos);
			boolean hasBreak = tile != null && tile.canRenderBreaking();

			if (!hasBreak)
			{
				IBlockState state = world.getBlockState(blockpos);
				if (state.getMaterial() != Material.AIR)
				{
					Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockDamage(state, blockpos, blockDamageIcons[progress], world);
				}
			}
		}
		tessellatorIn.draw();
		bufferIn.setTranslation(0.0D, 0.0D, 0.0D);
		postRenderDamagedBlocks();
	}

	// Copy of RenderGlobal.preRenderDamagedBlocks
	private void preRenderDamagedBlocks()
	{

		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR,
				GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.enableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
		GlStateManager.doPolygonOffset(-3.0F, -3.0F);
		GlStateManager.enablePolygonOffset();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableAlpha();
		GlStateManager.pushMatrix();
	}

	// Copy of RenderGlobal.postRenderDamagedBlocks
	private void postRenderDamagedBlocks()
	{

		GlStateManager.disableAlpha();
		GlStateManager.doPolygonOffset(0.0F, 0.0F);
		GlStateManager.disablePolygonOffset();
		GlStateManager.enableAlpha();
		GlStateManager.depthMask(true);
		GlStateManager.popMatrix();
	}
	
	
	
	
	@Override
	public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {

		TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();

		for (int i = 0; i < this.blockDamageIcons.length; ++i) {
			this.blockDamageIcons[i] = texturemap.getAtlasSprite("minecraft:blocks/destroy_stage_" + i);
		}
	}

	private final TextureAtlasSprite[] blockDamageIcons = new TextureAtlasSprite[10];
}
