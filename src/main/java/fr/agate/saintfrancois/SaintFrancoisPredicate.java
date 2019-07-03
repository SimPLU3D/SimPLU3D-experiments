package fr.agate.saintfrancois;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.convert.FromGeomToLineString;
import fr.ign.cogit.geoxygene.convert.FromGeomToSurface;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;
import fr.ign.cogit.simplu3d.model.BasicPropertyUnit;
import fr.ign.cogit.simplu3d.model.Environnement;
import fr.ign.cogit.simplu3d.model.ParcelBoundary;
import fr.ign.cogit.simplu3d.model.ParcelBoundaryType;
import fr.ign.cogit.simplu3d.model.Road;
import fr.ign.cogit.simplu3d.rjmcmc.generic.object.ISimPLU3DPrimitive;
import fr.ign.cogit.simplu3d.rjmcmc.paramshp.geometry.impl.CuboidRoofed;
import fr.ign.mpp.configuration.AbstractBirthDeathModification;
import fr.ign.mpp.configuration.AbstractGraphConfiguration;
import fr.ign.rjmcmc.configuration.ConfigurationModificationPredicate;

public class SaintFrancoisPredicate<O extends ISimPLU3DPrimitive, C extends AbstractGraphConfiguration<O, C, M>, M extends AbstractBirthDeathModification<O, C, M>>
implements ConfigurationModificationPredicate<C, M> {
	
	Geometry forbiddenZone = null;
	Geometry limitToRoad = null;
	

	GeometryFactory gf = new GeometryFactory();
	
	double heightMax;
	double slopeMin;
	public SaintFrancoisPredicate(BasicPropertyUnit bPU, Environnement env, double distancetoRoad, double heightParam, double slopeMinParam) throws Exception {
		this.slopeMin = slopeMinParam;
		this.heightMax = heightParam;
		
		IMultiSurface<IOrientableSurface> multiSurface = new GM_MultiSurface<IOrientableSurface>();
		
		for(Road road : env.getRoads()) {
			
			
			multiSurface.addAll(FromGeomToSurface.convertGeom(road.getAxis().buffer(distancetoRoad)));
			
			
		}
		
		
		IMultiCurve<IOrientableCurve> iMultiCurve = new GM_MultiCurve<IOrientableCurve>();
		
		for(ParcelBoundary bP : bPU.getCadastralParcels().get(0).getBoundariesByType(ParcelBoundaryType.ROAD)) {
			
			iMultiCurve.addAll(FromGeomToLineString.convert(bP.getGeom()));
		}
		
		limitToRoad = AdapterFactory.toGeometry(gf, iMultiCurve);
		forbiddenZone = AdapterFactory.toGeometry(gf, multiSurface);
		
		System.out.println(forbiddenZone);
		System.out.println(limitToRoad);
		
	}

	@Override
	public boolean check(C arg0, M arg1) {
		//Réglement zone UD
		
		int count = arg1.getBirth().size() -  arg1.getDeath().size()  + arg0.getGraph().vertexSet().size();
		
		if(count > 1) {
			return false;
		}

		
		for(O o : arg1.getBirth()) {
			CuboidRoofed cuboidRoofed = (CuboidRoofed)o;
			double hauteurFaitage = cuboidRoofed.getHeightT() +  cuboidRoofed.getHeight();
			
			
			Geometry geom = o.toGeometry();
	
			
			//Article 10 : le faitage principal du bâtiment ne devra pas excéder la côte 14.70 m
			if(this.heightMax < (hauteurFaitage) ) {
				return false;
			}
			
			
			//Article 11 : toiture avec une pente minimale de 60%
			
		
			
			double width = cuboidRoofed.getWidth();
			double heightRoof = cuboidRoofed.getHeightT();
			
			double slope = heightRoof / (0.5 * width);
			
			//System.out.println("Largeur : " + width + " heightRoof " + heightRoof + "  slope : " + slope );
			
			if(slope < slopeMin) {
				return false;
			}
			
			
		
			//Article 6 : recul de 8m par rapport à l'axe de la voirie pour les voies communales
			// (Bonus les marges initiales peuvet être conserver)
			
			if(geom.distance(forbiddenZone) == 0) {
				return false;
			}
			
			//Article 7 : 
			//- la distance comptée horizontalement entre un point du bâtiment et le point le plus proche de la limite séparative doit etre au moins égale 
			//à la moitié de la hauteur au faitage sans pouvoir être inférieure à 4m
			
			//System.out.println( hauteurFaitage);
			
			if(geom.distance(limitToRoad) <= (hauteurFaitage /2) ) {
				return false;
			}
			
			
			if(geom.distance(limitToRoad) <= 4) {
				return false;
			}
			
		}
		

		
		
			

		
		
		
		
		return true;
	}
	
	

}
