package fr.agate.saintfrancois;

import java.io.File;

import org.apache.commons.math3.random.RandomGenerator;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.util.attribute.AttributeManager;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;
import fr.ign.cogit.simplu3d.io.export.ExportInstance;
import fr.ign.cogit.simplu3d.io.nonStructDatabase.shp.LoaderSHP;
import fr.ign.cogit.simplu3d.model.BasicPropertyUnit;
import fr.ign.cogit.simplu3d.model.Environnement;
import fr.ign.cogit.simplu3d.rjmcmc.generic.object.ISimPLU3DPrimitive;
import fr.ign.cogit.simplu3d.rjmcmc.generic.optimizer.DefaultSimPLU3DOptimizer;
import fr.ign.cogit.simplu3d.rjmcmc.paramshp.geometry.impl.CuboidRoofed;
import fr.ign.cogit.simplu3d.rjmcmc.paramshp.optimizer.OptimisedRCuboidDirectRejection;
import fr.ign.cogit.simplu3d.util.SimpluParametersJSON;
import fr.ign.mpp.configuration.BirthDeathModification;
import fr.ign.mpp.configuration.GraphConfiguration;
import fr.ign.random.Random;

public class Main {

	public static void main(String[] args) throws Exception {

		RandomGenerator rng = Random.random();

		String project = "Q:/Production/Communes/Communes de Savoie/SAINT FRANCOIS  LONGCHAMP/2018 VIGIE/SIG/Projet/";

		Environnement env = LoaderSHP.loadNoDTM(new File(project));

		ExportInstance.export(env, project + "/tmp/");

		String simulationParamters = project + "building_parameters_project_rcuboid.json";

		SimpluParametersJSON p = new SimpluParametersJSON(new File(simulationParamters));

		for (BasicPropertyUnit bPU : env.getBpU()) {
			if (!bPU.getCadastralParcels().get(0).hasToBeSimulated()) {
				System.out.println("Not simulated" + bPU.getCadastralParcels().get(0).getCode());
				continue;
			}
			System.out.println("Simulated" + bPU.getCadastralParcels().get(0).getCode());

			SaintFrancoisPredicate<CuboidRoofed, GraphConfiguration<CuboidRoofed>, BirthDeathModification<CuboidRoofed>> pred = null;

			pred = new SaintFrancoisPredicate<CuboidRoofed, GraphConfiguration<CuboidRoofed>, BirthDeathModification<CuboidRoofed>>(
					bPU, env, 8.0, 14.7, 0.6);
			// On génère l'optimizer et on le lance (à faire)
			DefaultSimPLU3DOptimizer<? extends ISimPLU3DPrimitive> optimizer = new OptimisedRCuboidDirectRejection();
			GraphConfiguration<? extends ISimPLU3DPrimitive> cc = ((OptimisedRCuboidDirectRejection) optimizer)
					.process(rng, bPU, p, env, bPU.getId(), pred, bPU.getGeom());
			
			
			IFeatureCollection<IFeature> featCollOut = new FT_FeatureCollection<IFeature>();
			
			for(ISimPLU3DPrimitive prim : cc) {
				CuboidRoofed cR = (CuboidRoofed) prim;
				
				IFeature feat = new DefaultFeature(prim.generated3DGeom());
				
				AttributeManager.addAttribute(feat, "CenterX", cR.getCenterx(), "Double");
				AttributeManager.addAttribute(feat, "CenterY", cR.getCentery(), "Double");
				AttributeManager.addAttribute(feat, "HautGout", cR.getHeight(), "Double");
				AttributeManager.addAttribute(feat, "HautFair", cR.getHeightT(), "Double");
				AttributeManager.addAttribute(feat, "PenteToit", cR.getHeightT()/ (0.5 * cR.getWidth()), "Double");
				AttributeManager.addAttribute(feat, "Largeur", cR.getWidth(), "Double");
				AttributeManager.addAttribute(feat, "Longueur", cR.getLength(), "Double");
				
				featCollOut.add(feat);
			}
			
			ShapefileWriter.write(featCollOut, project+"out/result.shp");
		}

	}

}
