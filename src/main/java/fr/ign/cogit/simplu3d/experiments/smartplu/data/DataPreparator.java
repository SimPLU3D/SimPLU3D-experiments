package fr.ign.cogit.simplu3d.experiments.smartplu.data;

import java.io.File;
import java.util.Map;

import org.geotools.referencing.CRS;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;
import fr.ign.cogit.simplu3d.util.distribution.ZonePackager;

/**
 * Class for data preparation for SmartPLU experiments
 * 
 * Input : A JSON File that represents parcels with the right format to store
 * the ruels with lon/lat WGS84 coordinates Output : a set of folder that
 * contains a shapefile with parcels corresponding to an ID
 * 
 * 
 * @author mbrasebin
 *
 */
public class DataPreparator {

	public static void main(String[] args) throws Exception {
		//Attribute that indicates if we simul or not the parcel
		ZonePackager.ATTRIBUTE_SIMUL = "has_rules";
		ZonePackager.ATTRIBUTE_SIMUL_TYPE = "Boolean";
		//Mapping attributes to generate IDPAR (concatenation of these four attributes)
		//In this case departement attribut is facultative as the commune attribute 
		//already contains  the departement number
		ZonePackager.ATTRIBUTE_DEPARTEMENT= "departement";
		ZonePackager.ATTRIBUTE_COMMUNE = "commune";
		ZonePackager.ATTRIBUTE_PREFIXE = "prefixe";
		ZonePackager.ATTRIBUTE_SECTION = "section";
		ZonePackager.ATTRIBUTE_NUMERO = "numero";
		
	
		
		if (args.length < 2) {
			System.out.println("Two arguments expected : parcelle.json and folderOut");
		}
		// File in
		// "/home/mbrasebin/Documents/Donnees/demo-numen/municipality/61230/parcelle.json";
		String fileIn = args[0];

		// Folder where results are stored
		// "/home/mbrasebin/Documents/Donnees/demo-numen/municipality/61230/out/";
		String folderOut = args[1];

		// Reading the features
		IFeatureCollection<IFeature> collectionParcels = null;

		(new File(folderOut)).mkdirs();

		if (fileIn.contains(".json")) {
			//We ensure that both SRID are the same (tranforming json files and writing output of the ZonePackager)
			ZonePackager.SRID_END = DefaultFeatureDeserializer.SRID_END;
			
			
			collectionParcels = DefaultFeatureDeserializer.readJSONFile(fileIn);

			// A temporary collection to store the agregated results
			IFeatureCollection<IFeature> collToExport = new FT_FeatureCollection<>();
			collToExport.addAll(collectionParcels);

			// This hint is to ensure that the first item has rules
			// Because the schema of the shapefile export is based on the schema
			// of the
			// first feature
			int nbElem = collToExport.size();
			for (int i = 0; i < nbElem; i++) {
				IFeature feat = collToExport.get(i);
				if (Boolean.parseBoolean(feat.getAttribute(ZonePackager.ATTRIBUTE_SIMUL).toString())) {
					collToExport.remove(i);
					collToExport.getElements().add(0, feat);
					break;
				}
			}

			// Storing the agregated results (only for debug and to check if the
			// blocks are
			// correctly generated)
			String agregatedFile = folderOut + "agregated.shp";
			ShapefileWriter.write(collToExport, agregatedFile, CRS.decode(DefaultFeatureDeserializer.SRID_END));
			collectionParcels = ShapefileReader.read(agregatedFile);

		} else {
			// If we want to use a shapefile instead (data has to be in
			// Lambert93)
			collectionParcels = ShapefileReader.read(fileIn);
		}

		int numberOfParcels = 20;
		double areaMax = 5000;

		// Creating the groups into a map
		Map<Integer, IFeatureCollection<IFeature>> map = ZonePackager.createParcelGroups(collectionParcels,
				numberOfParcels, areaMax);

		// Just checking if there are no double in the results
		long count = 0;
		for (Object s : map.keySet().toArray()) {
			long nbOfSimulatedParcel = map.get(s).getElements().stream()
					.filter(feat -> (feat.getGeom().area() < areaMax))
					.filter(feat -> (Boolean.parseBoolean(feat.getAttribute(ZonePackager.ATTRIBUTE_SIMUL).toString())))
					.count();

			System.out.println("For group : " + s + "  -  " + nbOfSimulatedParcel + "  entities");
			count = count + nbOfSimulatedParcel;
		}

		System.out.println("Number of features in map : " + count);

		// Creating the folder
		ZonePackager.exportFolder(map, folderOut, false);

		/////////////////////////////////////////////////////////////////////
		// This code is not useful for a final production and for simulation as
		///////////////////////////////////////////////////////////////////// it
		///////////////////////////////////////////////////////////////////// only
		///////////////////////////////////////////////////////////////////// proposes
		///////////////////////////////////////////////////////////////////// an
		///////////////////////////////////////////////////////////////////// aggregated
		///////////////////////////////////////////////////////////////////// export
		// WARNING !!!!!!!!!
		// Do not forget to remove the aggregated.shp from out folder
		// If you want to run simulation
		/////////////////////////////

		// Export with double to get a fast view of the folders
		IFeatureCollection<IFeature> exportWithDouble = new FT_FeatureCollection<>();
		for (Object s : map.keySet().toArray()) {
			exportWithDouble.addAll(map.get(s));
		}

		// Storing the agregated results (only for debug and to check if the
		// blocks are
		// correctly generated)
		ShapefileWriter.write(exportWithDouble, folderOut + "export_with_double.shp",
				CRS.decode(ZonePackager.SRID_END));

	}

}
