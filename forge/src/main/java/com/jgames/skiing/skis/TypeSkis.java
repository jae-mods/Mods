package com.jgames.skiing.skis;

import java.util.ArrayList;

public class TypeSkis 
{
	public String name;
	
	public float maxSpeed;
	public float rotationSpeed;
	public boolean twinTips;
	
	public static ArrayList<TypeSkis> skis = new ArrayList<TypeSkis>();
	
	public TypeSkis(String name,
			float maxSpeed, float rotationSpeed, boolean twinTips)
	{
		this.name = name;
		
		this.maxSpeed = maxSpeed;
		this.rotationSpeed = rotationSpeed;
		this.twinTips = twinTips;
		
		TypeSkis.skis.add(this);
	}
	
	public static TypeSkis wood = new TypeSkis("wood",
		20f, 20f, false);
}