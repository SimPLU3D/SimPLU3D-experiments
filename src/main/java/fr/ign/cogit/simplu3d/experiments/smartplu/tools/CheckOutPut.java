package fr.ign.cogit.simplu3d.experiments.smartplu.tools;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * Check if all the simulations have been proceeded and copy the missing to a
 * new folder
 * 
 * @author mbrasebin
 *
 */
public class CheckOutPut {

	public static void main(String[] args) throws Exception {

		// Folder with the output of the results
		String folderResults = "/home/mbrasebin/.openmole/ZBOOK-SIGOPT-2016/webui/projects/pchoisy_openmole_tarball/results_pchoisy/";

		// Folder with the data used for the simulation
		String folderDataIn = "/home/mbrasebin/.openmole/ZBOOK-SIGOPT-2016/webui/projects/pchoisy_openmole_tarball/dataBasicSimu/pchoisy/";

		// Folder with the data that were not simulated
		String folderOut = "/home/mbrasebin/.openmole/ZBOOK-SIGOPT-2016/webui/projects/pchoisy_openmole_tarball/pchoisy_missing/";
		
		(new File(folderOut)).mkdirs();

		for (File f : (new File(folderDataIn)).listFiles()) {

			String name = f.getName();

			if (!new File(folderResults, name).exists()) {
				try {
					FileUtils.copyDirectory(f, new File(folderOut + name));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

	}

}
