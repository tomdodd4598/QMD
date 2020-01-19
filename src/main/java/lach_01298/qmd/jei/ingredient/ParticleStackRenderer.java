package lach_01298.qmd.jei.ingredient;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import lach_01298.qmd.QMD;
import lach_01298.qmd.particle.Particle;
import lach_01298.qmd.particle.ParticleStack;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.ingredients.IIngredientRenderer;
import nc.util.Lang;
import nc.util.UnitHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class ParticleStackRenderer  implements IIngredientRenderer<ParticleStack> 
{

	private static final int TEX_WIDTH = 16;
	private static final int TEX_HEIGHT = 16;
	
	private final int amount;
	private final int energy;
	private final int width;
	private final int height;
	
	@Nullable
	private final IDrawable overlay;
	
	public ParticleStackRenderer() 
	{
		this(0,0, TEX_WIDTH, TEX_HEIGHT, null);
	}
	
	public ParticleStackRenderer(int amount, int energy, int width, int height, @Nullable IDrawable overlay) 
	{
		this.amount = amount;
		this.energy = energy;
		//this.tooltipMode = tooltipMode;
		this.width = width;
		this.height = height;
		this.overlay = overlay;
	}

	
	
	
	@Override
	public void render(Minecraft minecraft, final int xPosition, final int yPosition, @Nullable ParticleStack particleStack) 
	{
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();

		drawParticle(minecraft, xPosition, yPosition, particleStack);

		GlStateManager.color(1, 1, 1, 1);

		if (overlay != null) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0, 200);
			overlay.draw(minecraft, xPosition, yPosition);
			GlStateManager.popMatrix();
		}

		GlStateManager.disableAlpha();
		GlStateManager.disableBlend();
	}
	
	@Override
	public List<String> getTooltip(Minecraft minecraft, ParticleStack ingredient, ITooltipFlag tooltipFlag) 
	{
		List<String> list = new ArrayList<>();
		list.add(Lang.localise(ingredient.getParticle().getUnlocalizedName()));
		list.add(TextFormatting.GRAY + Lang.localise("gui.qmd.particlestack.amount",UnitHelper.prefix(ingredient.getAmount(),4,"pu")));
		list.add(TextFormatting.GRAY + Lang.localise("gui.qmd.particlestack.mean_energy",UnitHelper.prefix(ingredient.getMeanEnergy()*1000,4,"eV")));
		
		
		return list;
	}
	
	private void drawParticle(Minecraft minecraft, final int xPosition, final int yPosition,
			@Nullable ParticleStack particleStack)
	{
		if (particleStack == null)
		{
			return;
		}
		Particle particle = particleStack.getParticle();
		if (particle == null)
		{
			return;
		}

	
		minecraft.renderEngine.bindTexture(particleStack.getParticle().getTexture());
		double zLevel = 100;
		double width = 16;
		double uMin = 0;
		double uMax = 1;
		double vMin = 0;
		double vMax = 1;
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferBuilder.pos(xPosition, yPosition + width, zLevel).tex(uMin, vMax).endVertex();
		bufferBuilder.pos(xPosition + width, yPosition + 16, zLevel).tex(uMax, vMax).endVertex();
		bufferBuilder.pos(xPosition + width, yPosition, zLevel).tex(uMax, vMin).endVertex();
		bufferBuilder.pos(xPosition, yPosition, zLevel).tex(uMin, vMin).endVertex();
		tessellator.draw();

	}

}