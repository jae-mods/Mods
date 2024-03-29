package com.jgames.skiing.skis;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.jgames.skiing.common.SkiMod;
import com.jgames.skiing.minecraft.KeyBindings;
import com.jgames.skiing.models.ModelSkis;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntitySkis extends Entity
{
    private boolean areSkisEmpty;
    private int serverTicker;
    private double skisX;
    private double skisY;
    private double skisZ;
    public double skisYaw;
    public double skisPitch;
    public double skisRoll;
    
    //Representing the original entity values
    public double rotationRoll;
    public double prevRotationRoll;
    
    @SideOnly(Side.CLIENT)
    private double velocityX;
    @SideOnly(Side.CLIENT)
    private double velocityY;
    @SideOnly(Side.CLIENT)
    private double velocityZ;

    public EntitySkis(World p_i1704_1_)
    {
        super(p_i1704_1_);
        this.areSkisEmpty = true;
        this.preventEntitySpawning = true;
        this.setSize(2f, 0.6f);
        this.yOffset = 1f / 16f;
    }
    
    protected boolean canTriggerWalking()
    {
        return true;
    }

    protected void entityInit()
    {
    	this.dataWatcher.addObject(17, new String("Type"));
    	this.dataWatcher.addObject(18, new Integer(1));
    	this.dataWatcher.addObject(19, new Float(0.0f));
    }

    public AxisAlignedBB getCollisionBox(Entity entity)
    {
        return entity.boundingBox;
    }

    public AxisAlignedBB getBoundingBox()
    {
        return this.boundingBox;
    }

    public boolean canBePushed()
    {
        return true;
    }

    public EntitySkis(World world, double x, double y, double z, TypeSkis type)
    {
        this(world);
        this.setPosition(x, y, z);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.setType(type.name);
    }
    
    public double getMountedYOffset()
    {
        return 1d / 16d;
    }

    public boolean attackEntityFrom(DamageSource source, float damage)
    {
        if (this.isEntityInvulnerable())
        {
            return false;
        }
        else if (!this.worldObj.isRemote && !this.isDead)
        {
            this.setDamageTaken(this.getDamageTaken() + damage * 10.0f);
            this.setBeenAttacked();
            boolean flag = source.getEntity() instanceof EntityPlayer && ((EntityPlayer)source.getEntity()).capabilities.isCreativeMode;

            if (flag || this.getDamageTaken() > 40.0f)
            {
                if (this.riddenByEntity != null)
                {
                    this.riddenByEntity.mountEntity(this);
                }

                if (!flag)
                {
                    this.func_145778_a(SkiMod.woodSkis, 1, 0.0f);
                }

                this.setDead();
            }

            return true;
        }
        else
        {
            return true;
        }
    }

    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }

    @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double p_70056_1_, double p_70056_3_, double p_70056_5_, float p_70056_7_, float p_70056_8_, int ticks)
    {
        if (this.areSkisEmpty)
        {
            this.serverTicker = ticks + 5;
        }
        else
        {
            double d3 = p_70056_1_ - this.posX;
            double d4 = p_70056_3_ - this.posY;
            double d5 = p_70056_5_ - this.posZ;
            double d6 = d3 * d3 + d4 * d4 + d5 * d5;

            if (d6 <= 1.0D)
            {
                return;
            }

            this.serverTicker = 3;
        }

        this.skisX = p_70056_1_;
        this.skisY = p_70056_3_;
        this.skisZ = p_70056_5_;
        this.skisYaw = (double)p_70056_7_;
        this.skisPitch = (double)p_70056_8_;
        this.motionX = this.velocityX;
        this.motionY = this.velocityY;
        this.motionZ = this.velocityZ;
    }
    
    public void setPositionRotationAndMotion(double x, double y, double z, double yaw, double pitch, double roll, double motX, double motY, double motZ)
    {
    	if(this.worldObj.isRemote)
    	{
    		this.posX = x;
    		this.posY = y;
    		this.posZ = z;
    		this.skisYaw = yaw;
    		this.skisPitch = pitch;
    		this.skisRoll = roll;
    		this.serverTicker = 5;
    	}
    	else
    	{
    		this.setPosition(x, y, z);
    		this.prevRotationYaw = (float)yaw;
    		this.prevRotationPitch = (float)pitch;
    		this.prevRotationRoll = roll;
    	}
    	
    	this.motionX = motX;
    	this.motionY = motY;
    	this.motionZ = motZ;
    }

    @SideOnly(Side.CLIENT)
    public void setVelocity(double x, double y, double z)
    {
        this.velocityX = this.motionX = x;
        this.velocityY = this.motionY = y;
        this.velocityZ = this.motionZ = z;
    }

    public void onUpdate()
    {
        super.onUpdate();
        
        //all other previous are set in super.onUpdate();
        this.prevRotationRoll = this.rotationRoll;
        
        //new position, rotation, and motion fields to store data, we set all values at the end
        double newX = this.posX;
        double newY = this.posY;
        double newZ = this.posZ;
        double newYaw = this.rotationYaw;
        double newPitch = this.rotationPitch;
        double newRoll = this.rotationRoll;
        double newMX = this.motionX;
        double newMY = this.motionY;
        double newMZ = this.motionZ;
        
        //other variables for various uses
        boolean isOnSnow = false;
        
        //first, check if we are on the ground
        if(this.onGround)
        {
        	//-----update loop for on ground-----//
        	
        	//first, check if we are on snow
     
        	if(this.worldObj.getBlock((int)Math.floor(this.posX), (int)(this.posY - 0.1d), (int)Math.floor(this.posZ)).getMaterial() == Material.snow)
        	{
        		isOnSnow = true;
        	}
        	
        	//if on snow, run updates differently
        }
        else
        {
        	//-----update loop for in the air-----//
        }
    }

    public void updateRiderPosition()
    {
        if (this.riddenByEntity != null)
        {
            double d0 = Math.cos((double)this.rotationYaw * Math.PI / 180.0D) * 0.4D;
            double d1 = Math.sin((double)this.rotationYaw * Math.PI / 180.0D) * 0.4D;
            this.riddenByEntity.setPosition(this.posX + d0, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ + d1);
            this.riddenByEntity.setAngles(this.rotationYaw, 0);
        }
    }
    
    protected void writeEntityToNBT(NBTTagCompound p_70014_1_) 
    {
    	
    }

    protected void readEntityFromNBT(NBTTagCompound p_70037_1_) 
    {
    	
    }

    @SideOnly(Side.CLIENT)
    public float getShadowSize()
    {
        return 0.0F;
    }

    public boolean interactFirst(EntityPlayer p_130002_1_)
    {
        if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != p_130002_1_)
        {
            return true;
        }
        else
        {
            if (!this.worldObj.isRemote)
            {
                p_130002_1_.mountEntity(this);
            }

            return true;
        }
    }

    protected void updateFallState(double p_70064_1_, boolean p_70064_3_)
    {
        int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_double(this.posY);
        int k = MathHelper.floor_double(this.posZ);

        if (this.worldObj.getBlock(i, j - 1, k).getMaterial() != Material.water && p_70064_1_ < 0.0d)
        {
            this.fallDistance = (float)((double)this.fallDistance - p_70064_1_);
        }
    }

    public void setDamageTaken(float p_70266_1_)
    {
        this.dataWatcher.updateObject(19, Float.valueOf(p_70266_1_));
    }
    
    public float getDamageTaken()
    {
        return this.dataWatcher.getWatchableObjectFloat(19);
    }
    
    public void setForwardDirection(int p_70269_1_)
    {
        this.dataWatcher.updateObject(18, Integer.valueOf(p_70269_1_));
    }

    public int getForwardDirection()
    {
        return this.dataWatcher.getWatchableObjectInt(18);
    }
    
    public void setType(String type)
    {
    	this.dataWatcher.updateObject(17, String.valueOf(type));
    }
    
    public TypeSkis getType()
    {
    	return TypeSkis.getType(this.dataWatcher.getWatchableObjectString(17));
    }

    @SideOnly(Side.CLIENT)
    public void setIsBoatEmpty(boolean p_70270_1_)
    {
        this.areSkisEmpty = p_70270_1_;
    }
    
    @Override
    public boolean shouldRiderSit()
    {
    	return false;
    }
}
