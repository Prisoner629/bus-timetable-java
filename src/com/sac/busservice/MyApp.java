package com.sac.busservice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Run this file to launch the bus service modified timetable application.
 * JDK 1.8 has been used to create this console application.
 * 
 * @author sachin
 *
 */
public class MyApp {

	public static void main (String[] args) {

		Scanner input = new Scanner (System.in);

		MyApp appBusService = new MyApp ();

		appBusService.application (input);
	}

	/**
	 * <p>
	 * 		Ask for the input location and output location of the timetable.
	 * </p>
	 * 
	 * @param input
	 *            - A Scanner
	 */
	private void application (Scanner input) {

		FileReader fileReader = null;

		try {

			File file = null;

			int tryAgain = 5;

			while (tryAgain > 0) {

				System.out.println ("Please enter the Original Timetable file location");
				
				String inputFile = input.nextLine ();
				
				if(inputFile != null){
					inputFile = inputFile.replaceAll ("\"", "");
				}

				Path path = Paths.get(inputFile);
				
				file = path != null? path.toFile(): null;

				if (file != null && file.isFile ()) {
					break;
				}

				System.out.println ("Invalid File Location");

				tryAgain--;
			}

			if (file == null || file.exists () == false || file.isFile () == false) {
				System.out.println ("Please try it some other time. Thank You");
				return;
			}

			fileReader = new FileReader (file);

			System.out.println (
					"Please enter the valid Output Timetable location (Entering blank or invalid path will save the output in the Original timetable directory)");

			String outputLocation = input.nextLine ();

			File outputFile = new File (outputLocation);

			if (outputFile == null || outputFile.exists () == false || outputFile.isDirectory () == false) {
				outputLocation = file.getParent () + "/";
			}
			
			if(outputLocation != null){
				outputLocation = outputLocation.replaceAll ("\"", "");
			}

			BusTimeTable busTimeTable = new BusTimeTable ();

			busTimeTable.generateTimeTable (fileReader, outputLocation);

		} catch (FileNotFoundException e) {
			e.printStackTrace ();
		} finally {

			try {

				if (fileReader != null)
					fileReader.close ();

			} catch (IOException e) {
				e.printStackTrace ();
			}

			input.close ();
		}
	}
}
