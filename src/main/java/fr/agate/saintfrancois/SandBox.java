package fr.agate.saintfrancois;

import fr.ign.cogit.simplu3d.rjmcmc.paramshp.geometry.impl.CuboidRoofed;

public class SandBox {
	
	public static void main(String[] args) {
		
		double x = 0;
		double y = 0;
		double orientation = 0;
		double hGutter = 10;
		double hTop = 10;
		double width = 10;
		double lenght = 10;
		double delta = 0;
		
		CuboidRoofed c = new CuboidRoofed(x,y, lenght,width,hGutter,orientation, hTop, delta);
		
		System.out.println("Aire 1 : " + c.getVolume());
		
		
		
		
		CuboidRoofed c2 = new CuboidRoofed(x,y, lenght,width,hGutter,orientation, hTop, 0.01);
		
		System.out.println("Aire 2 : " + c2.getVolume());
		
		CuboidRoofed c3 = new CuboidRoofed(x,y, lenght,width,hGutter + 1,orientation, hTop, delta);
		
		System.out.println("Aire 3 : " + c3.getVolume());
		
		
		CuboidRoofed c4 = new CuboidRoofed(x,y, lenght,width,hGutter ,orientation, hTop+ 1, delta);
		
		System.out.println("Aire 4 : " + c4.getVolume());
	}

}
