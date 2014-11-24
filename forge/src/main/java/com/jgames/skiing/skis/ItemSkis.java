package com.jgames.skiing.skis;

import com.jgames.skiing.common.SkiMod;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemSkis extends Item
{
	public TypeSkis typeSkis;
	
	public ItemSkis(TypeSkis typeSkis)
	{
		super();
		this.setMaxStackSize(1);
		this.setCreativeTab(SkiMod.TAB);
		this.typeSkis = typeSkis;
    	
    	GameRegistry.registerItem(this, this.typeSkis.name);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(ItemStack itemStack)
	{
		return this.typeSkis.name + " Skis";
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister)
	{
		this.itemIcon = iconRegister.registerIcon("skimod:skis/" + this.typeSkis.name);
	}
}