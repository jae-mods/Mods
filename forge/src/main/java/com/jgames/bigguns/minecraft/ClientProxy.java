package com.jgames.bigguns.minecraft;

import net.minecraft.client.Minecraft;

import com.jgames.bigguns.common.CommonProxy;
import com.jgames.bigguns.data.Settings;

public class ClientProxy extends CommonProxy
{
	@Override
	public void addRenders()
	{
		Settings.mouseSensitivity = Minecraft.getMinecraft().gameSettings.mouseSensitivity;
	}
	
	@Override
	public void renderHitMarker()
	{
		
	}
	
	@Override
	public void renderSight()
	{
		
	}
}
