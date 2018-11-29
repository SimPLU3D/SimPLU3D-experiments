package fr.ign.cogit.simplu3d.experiments.smartplu.data;

import java.io.File;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.geometry.DirectPosition2D;
import org.opengis.geometry.DirectPosition;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.util.attribute.AttributeManager;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;
import fr.ign.cogit.simplu3d.rjmcmc.cuboid.geometry.impl.Cuboid;
import fr.ign.cogit.simplu3d.rjmcmc.cuboid.geometry.loader.LoaderCuboid;

/**
 * This class aims at converting the 3D geometry of cuboid to 2D extrudable geometry for ITowns
 * 
 * @author mbrasebin
 *
 */
public class ConvertOutput {

	
	public static void main(String[] args) throws Exception {

		//Outshapefile from SimPLU3D
		String shapefileIn = "/home/mbrasebin/Documents/Donnees/demo-numen/sorties/74042/buildings/buildings.shp";

		//DTM as asc file
		String rasterIn = "/home/mbrasebin/Documents/Donnees/demo-numen/sorties/export/74042/dtm.asc";

		//The output shapefile
		String shapefileOut = "/home/mbrasebin/Documents/Donnees/demo-numen/sorties/export/74042/buildings.shp";

		
		//Reading the asc and creating a GRIDCoverage
		File f = new File(rasterIn);
		// Reading the coverage through a file
		AbstractGridFormat format = GridFormatFinder.findFormat(f);
		GridCoverage2DReader reader = format.getReader(f);
		GridCoverage2D gc = reader.read(null);

		IFeatureCollection<IFeature> featColInl = ShapefileReader.read(shapefileIn);
		IFeatureCollection<IFeature> featColl = new FT_FeatureCollection<>();

		System.out.println("Nb entite before : " + featColInl.size());

		bouclefeature : for (IFeature featIn : featColInl) {
			//Reading the cuboid from SimPLU3D
			Cuboid c = LoaderCuboid.transformFeature(featIn);

			//Creating a new feature with a 2D geometry (the footprint)
			IFeature feat = new DefaultFeature();
			feat.setGeom(c.getFootprint());

			double z = Double.POSITIVE_INFINITY;

			//For each coordinate of the footprint
			for (IDirectPosition dp : feat.getGeom().coord()) {
				//We get the Z in a 1 column table
				double[] tab = new double[1];
				DirectPosition coord = new DirectPosition2D(dp.getX(), dp.getY());
				gc.evaluate(coord, tab);
				
				if (coord != null) {
					z = Math.min(z, tab[0]);
				} else {
					//It may be null according to the documentation
					System.out.println("Coord null : " + dp);
					//It is an error, we go to the next iteration
					continue bouclefeature;
					
				}
			}

			//Reinjected attributes in the feature
			AttributeManager.addAttribute(feat, "height", c.getHeight(), "Double");
			AttributeManager.addAttribute(feat, "area", c.getFootprint().area(), "Double");
			AttributeManager.addAttribute(feat, "volume", c.getHeight() * c.getFootprint().area(), "Double");
			AttributeManager.addAttribute(feat, "idpar", featIn.getAttribute("idpar"), "String");
			AttributeManager.addAttribute(feat, "imu_dir", featIn.getAttribute("imu_dir"), "String");
			AttributeManager.addAttribute(feat, "z_min", z, "Double");

			featColl.add(feat);
		}

		System.out.println("Nb entite after : " + featColl.size());

		ShapefileWriter.write(featColl, shapefileOut);

	}

}
