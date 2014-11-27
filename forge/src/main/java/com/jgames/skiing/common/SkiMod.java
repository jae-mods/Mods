package com.jgames.skiing.common;

import com.jgames.skiing.network.Message;
import com.jgames.skiing.network.MessageHandler;
import com.jgames.skiing.skis.EntitySkis;
import com.jgames.skiing.skis.ItemSkis;
import com.jgames.skiing.skis.TypeSkis;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = SkiMod.MODID, name = "Ski Mod", version = SkiMod.VERSION)
public class SkiMod 
{
	public static final String MODID = "skimod";
	public static final String VERSION = "0.0.1";
	
	@SidedProxy(clientSide = "com.jgames.skiing.minecraft.ClientProxy", serverSide = "com.jgames.skiing.common.CommonProxy")
	public static CommonProxy PROXY = new CommonProxy();
	
	public static SimpleNetworkWrapper SNW;
	
	public static final CreativeTabs TAB = new CreativeTab(CreativeTabs.getNextID(), "Ski Mod");
	
	public static ItemSkis woodSkis = new ItemSkis(TypeSkis.wood);
	
	@EventHandler
	public void preInitialization(FMLPreInitializationEvent event)
	{
    	EntityRegistry.registerGlobalEntityID(EntitySkis.class, "Skis", EntityRegistry.findGlobalUniqueEntityId());
    	EntityRegistry.registerModEntity(EntitySkis.class, "Skis", 0, this, 40, 3, false);
    	
    	SkiMod.PROXY.addRenders();
    	SkiMod.PROXY.registerKeys();
    	
    	SkiMod.SNW = NetworkRegistry.INSTANCE.newSimpleChannel(this.MODID);
    	SkiMod.SNW.registerMessage(MessageHandler.class, Message.class, 0, Side.CLIENT);
	}
    
    @EventHandler
	public void initialization(FMLInitializationEvent event)
	{
	}
}