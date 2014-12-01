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
    private double speedMultiplier;
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
        this.speedMultiplier = 0.07d;
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

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        int ticker = 5;
        double speedCheck = 0.0d;
        boolean isOnSnow;

        for (int i = 0; i < ticker; i++)
        {
            double d1 = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (double)(i + 0) / (double)ticker - 0.125d;
            double d3 = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (double)(i + 1) / (double)ticker - 0.125d;
            AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox(this.boundingBox.minX, d1, this.boundingBox.minZ, this.boundingBox.maxX, d3, this.boundingBox.maxZ);

            if (this.worldObj.isAABBInMaterial(axisalignedbb, Material.snow))
            {
            	speedCheck += 1.0d / (double)ticker;
            }
        }

        double speed = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        double d2;
        double d4;
        ticker = 0;

        if (speed > 0.26249999999999996d)
        {
            d2 = Math.cos((double)this.rotationYaw * Math.PI / 180.0d);
            d4 = Math.sin((double)this.rotationYaw * Math.PI / 180.0d);

            for (ticker = 0; (double)ticker < 1.0d + speed * 60.0d; ticker++)
            {
                double d5 = (double)(this.rand.nextFloat() * 2.0F - 1.0F);
                double d6 = (double)(this.rand.nextInt(2) * 2 - 1) * 0.7D;
                double d8;
                double d9;

                if (this.rand.nextBoolean())
                {
                    d8 = this.posX - d2 * d5 * 0.8D + d4 * d6;
                    d9 = this.posZ - d4 * d5 * 0.8D - d2 * d6;
                    this.worldObj.spawnParticle("snowballpoof", d8, this.posY - 0.125D, d9, this.motionX, this.motionY, this.motionZ);
                }
                else
                {
                    d8 = this.posX + d2 + d4 * d5 * 0.7D;
                    d9 = this.posZ + d4 - d2 * d5 * 0.7D;
                    this.worldObj.spawnParticle("snowballpoof", d8, this.posY - 0.125D, d9, this.motionX, this.motionY, this.motionZ);
                }
            }
        }

        double d11;
        double d12;

        if (this.worldObj.isRemote && this.areSkisEmpty)
        {
            if (this.serverTicker > 0)
            {
                d2 = this.posX + (this.skisX - this.posX) / (double)this.serverTicker;
                d4 = this.posY + (this.skisY - this.posY) / (double)this.serverTicker;
                d11 = this.posZ + (this.skisZ - this.posZ) / (double)this.serverTicker;
                d12 = MathHelper.wrapAngleTo180_double(this.skisYaw - (double)this.rotationYaw);
                this.rotationYaw = (float)((double)this.rotationYaw + d12 / (double)this.serverTicker);
                this.rotationPitch = (float)((double)this.rotationPitch + (this.skisPitch - (double)this.rotationPitch) / (double)this.serverTicker);
                --this.serverTicker;
                this.setPosition(d2, d4, d11);
                this.setRotation(this.rotationYaw, this.rotationPitch);
            }
            else
            {
                d2 = this.posX + this.motionX;
                d4 = this.posY + this.motionY;
                d11 = this.posZ + this.motionZ;
                this.setPosition(d2, d4, d11);

                if (this.onGround)
                {
                    this.motionX *= 0.5D;
                    this.motionY *= 0.5D;
                    this.motionZ *= 0.5D;
                }

                this.motionX *= 0.9900000095367432D;
                this.motionY *= 0.949999988079071D;
                this.motionZ *= 0.9900000095367432D;
            }
        }
        else
        {
            if (speedCheck < 1.0d)
            {
                d2 = speedCheck * 2.0d - 1.0d;
                this.motionY += 0.03999999910593033d * d2;
            }
            else
            {
                if (this.motionY < 0.0d)
                {
                    this.motionY /= 2.0d;
                }

                this.motionY += 0.007000000216066837d;
            }

            if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityLivingBase)
            {
            	float accelerate = 0f;
            	float strafe = 0f;
            	if(KeyBindings.leftKey.getIsKeyPressed())
            	{
            		strafe -= 1f; 
            	}
            	if(KeyBindings.rightKey.getIsKeyPressed())
            	{
            		strafe += 1f;
            	}
            	if(KeyBindings.upKey.getIsKeyPressed())
            	{
            		accelerate = 1f;
            	}
            	if(KeyBindings.downKey.getIsKeyPressed())
            	{
            		accelerate = -0.5f;
            	}
            	
                EntityLivingBase entitylivingbase = (EntityLivingBase)this.riddenByEntity;
                float f = this.riddenByEntity.rotationYaw + strafe * 90.0F;
                this.motionX += -Math.sin((double)(f * (float)Math.PI / 180.0F)) * this.speedMultiplier * (double)accelerate * 0.05000000074505806D;
                this.motionZ += Math.cos((double)(f * (float)Math.PI / 180.0F)) * this.speedMultiplier * (double)accelerate * 0.05000000074505806D;
            }

            d2 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

            if (d2 > this.getType().maxSpeed)
            {
                d4 = 0.35D / d2;
                this.motionX *= d4;
                this.motionZ *= d4;
                d2 = 0.35D;
            }

            if (d2 > speed && this.speedMultiplier < 0.35D)
            {
                this.speedMultiplier += (0.35D - this.speedMultiplier) / 35.0D;

                if (this.speedMultiplier > 0.35D)
                {
                    this.speedMultiplier = 0.35D;
                }
            }
            else
            {
                this.speedMultiplier -= (this.speedMultiplier - 0.07D) / 35.0D;

                if (this.speedMultiplier < 0.07D)
                {
                    this.speedMultiplier = 0.07D;
                }
            }

            int l;

            for (l = 0; l < 4; ++l)
            {
                int i1 = MathHelper.floor_double(this.posX + ((double)(l % 2) - 0.5D) * 0.8D);
                ticker = MathHelper.floor_double(this.posZ + ((double)(l / 2) - 0.5D) * 0.8D);

                for (int j1 = 0; j1 < 2; ++j1)
                {
                    int k = MathHelper.floor_double(this.posY) + j1;
                    Block block = this.worldObj.getBlock(i1, k, ticker);

                    if (block == Blocks.snow_layer)
                    {
                        this.worldObj.setBlockToAir(i1, k, ticker);
                        this.isCollidedHorizontally = false;
                    }
                }
            }

            /*if (this.onGround)
            {
                this.motionX *= 0.5D;
                this.motionY *= 0.5D;
                this.motionZ *= 0.5D;
            }*/

            this.moveEntity(this.motionX, this.motionY, this.motionZ);

            if (this.isCollidedHorizontally && speed > 0.2D)
            {
                if (!this.worldObj.isRemote && !this.isDead)
                {
                    this.setDead();
                    
                    this.func_145778_a(SkiMod.woodSkis, 1, 0.0F);
                }
            }
            else
            {
                this.motionX *= 0.9900000095367432d;
                this.motionY *= 0.949999988079071d;
                this.motionZ *= 0.9900000095367432d;
            }

            this.rotationPitch = 0.0F;
            d4 = (double)this.rotationYaw;
            d11 = this.prevPosX - this.posX;
            d12 = this.prevPosZ - this.posZ;

            if (d11 * d11 + d12 * d12 > 0.001D)
            {
                d4 = (double)((float)(Math.atan2(d12, d11) * 180.0D / Math.PI));
            }

            double d7 = MathHelper.wrapAngleTo180_double(d4 - (double)this.rotationYaw);

            if (d7 > 20.0D)
            {
                d7 = 20.0D;
            }

            if (d7 < -20.0D)
            {
                d7 = -20.0D;
            }

            this.rotationYaw = (float)((double)this.rotationYaw + d7);
            this.setRotation(this.rotationYaw, this.rotationPitch);

            if (!this.worldObj.isRemote)
            {
                List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));

                if (list != null && !list.isEmpty())
                {
                    for (int k1 = 0; k1 < list.size(); ++k1)
                    {
                        Entity entity = (Entity)list.get(k1);

                        if (entity != this.riddenByEntity && entity.canBePushed() && entity instanceof EntityBoat)
                        {
                            entity.applyEntityCollision(this);
                        }
                    }
                }

                if (this.riddenByEntity != null && this.riddenByEntity.isDead)
                {
                    this.riddenByEntity = null;
                }
            }
        }
    }

    public void updateRiderPosition()
    {
        if (this.riddenByEntity != null)
        {
            double d0 = Math.cos((double)this.rotationYaw * Math.PI / 180.0D) * 0.4D;
            double d1 = Math.sin((double)this.rotationYaw * Math.PI / 180.0D) * 0.4D;
            this.riddenByEntity.setPosition(this.posX + d0, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ + d1);
            this.riddenByEntity.setAngles((this.rotationYaw - this.prevRotationYaw) * (20/3), 0);
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
