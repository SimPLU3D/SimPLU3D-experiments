package fr.ign.cogit.simplu3d.exec.zonepacker;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;
import fr.ign.cogit.simplu3d.util.distribution.ZonePackager;

public class ZonePackerExec {

	public static void main(String[] args) throws Exception {
		String parcelFileIn = "/home/mbrasebin/Bureau/parcels_rulez/real/parcels_rulez.shp";
		String folderTemp = "/tmp/tmp/";
		String folderOut = "/tmp/out/";

		IFeatureCollection<IFeature> parcelles = ShapefileReader.read(parcelFileIn);

		int numberOfParcels = 20;
		double areaMax = 5000;

		ZonePackager.createParcelGroupsAndExport(parcelles, numberOfParcels, areaMax, folderTemp, folderOut, false);

	}

}
