package lach_01298.qmd.jei.category;

import lach_01298.qmd.QMD;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import nc.recipe.ingredient.*;
import nc.util.*;
import nclegacy.jei.*;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public abstract class JEIQMDMachineCategory<WRAPPER extends JEIBasicRecipeWrapperLegacy<WRAPPER>> extends JEIBasicCategoryLegacy<WRAPPER>
{
	
	private final IDrawable background;
	protected String recipeTitle;
	protected final int backPosX, backPosY;
	
	public JEIQMDMachineCategory(IGuiHelper guiHelper, IJEIHandlerLegacy handler, String title, int backX, int backY, int backWidth, int backHeight)
	{
		this(guiHelper, handler, title, "", backX, backY, backWidth, backHeight);
	}
	
	public JEIQMDMachineCategory(IGuiHelper guiHelper, IJEIHandlerLegacy handler, String title, String guiExtra, int backX, int backY, int backWidth, int backHeight)
	{
		super(handler);
		ResourceLocation location = new ResourceLocation(QMD.MOD_ID + ":textures/gui/" + handler.getTextureName() + guiExtra + ".png");
		background = guiHelper.createDrawable(location, backX, backY, backWidth, backHeight);
		recipeTitle = Lang.localize("tile." + QMD.MOD_ID + "." + title + ".name");
		backPosX = backX;
		backPosY = backY;
	}
	
	@Override
	public void drawExtras(Minecraft minecraft)
	{
	
	}
	
	
	@Override
	public String getModName()
	{
		return QMD.MOD_NAME;
	}
	
	
	@Override
	public IDrawable getBackground()
	{
		return background;
	}
	
	@Override
	public void setRecipe(IRecipeLayout recipeLayout, WRAPPER recipeWrapper, IIngredients ingredients)
	{
		recipeLayout.getItemStacks().addTooltipCallback((slotIndex, input, ingredient, tooltip) ->
		{
			int outputIndex = slotIndex - recipeWrapper.recipeHandler.getItemInputSize();
			if (outputIndex >= 0 && outputIndex <= recipeWrapper.recipeHandler.getItemOutputSize() && recipeWrapper.recipe.getItemProducts().get(outputIndex) instanceof ChanceItemIngredient)
			{
				ChanceItemIngredient chanceIngredient = (ChanceItemIngredient)recipeWrapper.recipe.getItemProducts().get(outputIndex);
				tooltip.add(TextFormatting.WHITE + Lang.localize("jei.nuclearcraft.chance_output", chanceIngredient.minStackSize, chanceIngredient.getMaxStackSize(0), NCMath.decimalPlaces(chanceIngredient.meanStackSize, 2)));
			}
		});
		
		recipeLayout.getFluidStacks().addTooltipCallback((slotIndex, input, ingredient, tooltip) ->
		{
			int outputIndex = slotIndex - recipeWrapper.recipeHandler.getFluidInputSize();
			if (outputIndex >= 0 && outputIndex <= recipeWrapper.recipeHandler.getFluidOutputSize() && recipeWrapper.recipe.getFluidProducts().get(outputIndex) instanceof ChanceFluidIngredient)
			{
				ChanceFluidIngredient chanceIngredient = (ChanceFluidIngredient)recipeWrapper.recipe.getFluidProducts().get(outputIndex);
				tooltip.add(TextFormatting.WHITE + Lang.localize("jei.nuclearcraft.chance_output", chanceIngredient.minStackSize, chanceIngredient.getMaxStackSize(0), NCMath.decimalPlaces(chanceIngredient.meanStackSize, 2)));
			}
		});
	}
	
	@Override
	public String getTitle()
	{
		return recipeTitle;
	}
}
