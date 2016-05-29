package com.sac.busservice;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * <p>
 * 	Reads the timetable from the input file.
 * 	Creates service list. Checks and compare the two services, and adds only efficient services in the output timetable. 
 * 	Generates the output timetable.
 * 
 * 	@see BusService
 *  @see TreeSet
 *  @see LocalTime
 *      
 * </p>
 * @author sachin
 *
 */
public class BusTimeTable {

	private TreeSet <BusService> jaiBusServiceList = new TreeSet<> ();
	private TreeSet <BusService> veeruBusServiceList = new TreeSet<> ();

	/**
	 * <p>
	 * 		Reads the input original timetable, compare the services and generates the
	 * 		output timetable
	 * </p>
	 * @param fileReader
	 *            - A FileReader
	 * @param outputLocation
	 *            - Output location of the Timetable
	 */
	public void generateTimeTable (FileReader fileReader, String outputLocation) {

		getOriginalTimeTable (fileReader);

		checkTimeTableOfServices ();

		FileWriter fileWriter = null;

		BufferedWriter bufferedWriter = null;

		try {
			LocalTime currTime = LocalTime.now();
			
			String location = outputLocation + "timetable_" + currTime.getHour() + "_" + currTime.getMinute() + "_"
					+ currTime.getSecond() + ".txt";

			Path path = Paths.get(location);

			if(path == null){
				System.out.println("Output path is not properly defined. Now Exiting, please try again!");
				return;
			}

			fileWriter = new FileWriter (path.toFile());

			bufferedWriter = new BufferedWriter (fileWriter);

			Iterator <BusService> iterator;
			
			boolean isJaiServiceLst = false;
			
			if (jaiBusServiceList != null && jaiBusServiceList.size () > 0) {
				
				isJaiServiceLst = true;
				
				iterator = jaiBusServiceList.iterator ();

				while (iterator.hasNext ()) {
					bufferedWriter.write (iterator.next ().toString ());
					bufferedWriter.newLine ();
				}
			}

			if (veeruBusServiceList != null && veeruBusServiceList.size () > 0) {
				
				if (isJaiServiceLst){
					bufferedWriter.newLine ();
				}
				
				iterator = veeruBusServiceList.iterator ();

				while (iterator.hasNext ()) {
					bufferedWriter.write (iterator.next ().toString ());
					bufferedWriter.newLine ();
				}
			}

			System.out.println ("Output timetable generated at location:" + path.toString());

		} catch (IOException | IllegalArgumentException e) {
			e.printStackTrace ();
		} finally {
			try {
				if (bufferedWriter != null)
					bufferedWriter.close ();

				if (fileWriter != null)
					fileWriter.close ();

			} catch (IOException e) {
				e.printStackTrace ();
			}
		}
	}

	/**
	 * Loop through the both services and checks for inefficient services.
	 */
	private void checkTimeTableOfServices () {

		if (jaiBusServiceList != null && jaiBusServiceList.size () > 0 && veeruBusServiceList != null
				&& veeruBusServiceList.size () > 0) {

			Object jaiBusService[] = jaiBusServiceList.toArray ();

			Object veeruBusService[] = veeruBusServiceList.toArray ();

			BusService jaiService;

			BusService veeruService;

			int result;

			for (int i = 0; i < jaiBusService.length; i++) {

				jaiService = (BusService) jaiBusService[i];

				if (jaiService == null)
					continue;

				for (int j = 0; j < veeruBusService.length; j++) {

					veeruService = (BusService) veeruBusService[j];

					if (veeruService == null)
						continue;

					result = compareAndRemoveInefficentService (jaiService, veeruService);

					if (result == REMOVE_JAI_BUS_SERVICE) {
						jaiBusService[i] = null;
						break;
					} else if (result == REMOVE_VEERU_BUS_SERVICE) {
						veeruBusService[j] = null;
						continue;
					}
				}
			}
		}
	}

	/**
	 * <p>
	 * Compares the services.
	 * 
	 * Only efficient services shall be added to the timetable. A service is
	 * considered efficient compared to the other one:
	 * </p>
	 * 		<ul>
	 * 			<li>If it starts at the same time and reaches earlier, or</li>
	 * 
	 * 			<li>If it starts later and reaches at the same time, or</li>
	 * 
	 * 			<li>If it starts later and reaches earlier.</li>
	 * 
	 * 			<li>
	 * 				If both companies offer a service having the same departure and
	 * 				arrival times then always choose Jai Bus Company over Veeru Bus Company,
	 * 				since Veeru Bus Company busses are not as comfortable as those of Jai Bus
	 * 				Company.
	 * 			</li>
	 * 		</ul>
	 * 
	 * @param jaiService
	 *            - BusService Object of the Jai Bus Service
	 *            
	 * @param veeruService
	 *            - BusService Object of the Veeru Bus Service
	 *            
	 * @return which service has been removed: 
	 * 		   <blockquote>
	 * 		   <ol>
	 * 		   		<li> DO_NOTHING = 0 </li>
	 *         		<li> REMOVE_JAI_BUS_SERVICE = 1 </li> 
	 *         		<li> REMOVE_VEERU_BUS_SERVICE = 2 </li>
	 *         </ol>
	 *         </blockquote>
	 */

	private int compareAndRemoveInefficentService (BusService jaiService, BusService veeruService) {

		LocalTime jaiBusDepTime = jaiService.getDepartureTime ();

		LocalTime jaiBusArrTime = jaiService.getArrivalTime ();

		LocalTime veeruBusDepTime = veeruService.getDepartureTime ();

		LocalTime veeruBusArrTime = veeruService.getArrivalTime ();

		if (jaiBusDepTime.compareTo (veeruBusDepTime) == EQUAL_VALUE
				&& jaiBusArrTime.compareTo (veeruBusArrTime) == EQUAL_VALUE) {
			veeruBusServiceList.remove (veeruService);
			return REMOVE_VEERU_BUS_SERVICE;
		}

		int result;

		if (jaiBusDepTime.compareTo (veeruBusDepTime) == EQUAL_VALUE) {

			result = jaiBusArrTime.compareTo (veeruBusArrTime);

			if (result == GREATER_VALUE) {
				
				jaiBusServiceList.remove (jaiService);
			
				return REMOVE_JAI_BUS_SERVICE;
			} else {
				
				veeruBusServiceList.remove (veeruService);
				
				return REMOVE_VEERU_BUS_SERVICE;
			}
		}

		if (jaiBusArrTime.compareTo (veeruBusArrTime) == EQUAL_VALUE) {
			result = jaiBusDepTime.compareTo (veeruBusDepTime);

			if (result == GREATER_VALUE) {
				
				veeruBusServiceList.remove (veeruService);
				
				return REMOVE_VEERU_BUS_SERVICE;
			} else {
				
				jaiBusServiceList.remove (jaiService);
				
				return REMOVE_JAI_BUS_SERVICE;
			}
		}

		if ((jaiBusDepTime.isAfter (veeruBusDepTime) && (jaiBusDepTime.isBefore (veeruBusArrTime)
				|| jaiBusDepTime.compareTo (veeruBusArrTime) == EQUAL_VALUE))
				|| (veeruBusDepTime.isAfter (jaiBusDepTime) && (veeruBusDepTime.isBefore (jaiBusArrTime)
						|| veeruBusDepTime.compareTo (jaiBusArrTime) == EQUAL_VALUE))) {

			int jaiBusTotalJourneyTime = jaiBusArrTime.toSecondOfDay () - jaiBusDepTime.toSecondOfDay ();

			int veeruBusTotalJourneyTime = veeruBusArrTime.toSecondOfDay () - veeruBusDepTime.toSecondOfDay ();

			result = Integer.compare (jaiBusTotalJourneyTime, veeruBusTotalJourneyTime);

			if (result == EQUAL_VALUE || result == LESSER_VALUE) {
				
				veeruBusServiceList.remove (veeruService);
				
				return REMOVE_VEERU_BUS_SERVICE;
			}

			jaiBusServiceList.remove (jaiService);
			
			return REMOVE_JAI_BUS_SERVICE;
		}

		return DO_NOTHING;
	}

	/**
	 * <p>
	 * 		Reads the original timetable and split each service in Company Name Departure Time Arrival Time  
	 * </p>
	 * 
	 * @param fileReader
	 *            - A FileReader
	 */
	private void getOriginalTimeTable (FileReader fileReader) {

		BufferedReader bufferedReader = null;

		String line = null;

		try {
			bufferedReader = new BufferedReader (fileReader);
			
			int noOfEnteries = 0;

			while ((line = bufferedReader.readLine ()) != null) {
				
				noOfEnteries++;
				
				if (noOfEnteries > MAXIMUM_NUMBER_OF_ENTRIES)
					break;

				String service[] = line.split (" ");

				String companyName = service[0];

				boolean isJai = true;

				if (companyName.equals (VEERU)) {
					isJai = false;
				}

				setServiceDTO (service, isJai);
			}

		} catch (IOException | IllegalArgumentException e) {
			e.printStackTrace ();
		} finally {

			try {
				if (bufferedReader != null) {
					bufferedReader.close ();
				}
			} catch (IOException e) {
				e.printStackTrace ();
			}
		}
	}

	/**
	 * <p>
	 * Generates the separate sorted list with respect to departure time of
	 * services. Removes the service if any service longer than an hour.
	 * </p>
	 * 
	 * @param service
	 *            - String array object, which contains:
	 *            <ul>
	 *            		<li>Company Name</li>
	 *            		<li>Departure Time</li>
	 *            		<li>Arrival Time</li>
	 *            </ul>
	 * 
	 * @param isJai
	 *            - Is Service belongs to Jai Company
	 */
	private void setServiceDTO (String service[], boolean isJai) {

		String companyName = service[0];

		String depTime[] = service[1].split (":");

		LocalTime departureTime = LocalTime.of (Integer.parseInt (depTime[0]), Integer.parseInt (depTime[1]));

		String arrTime[] = service[2].split (":");

		LocalTime arrivalTime = LocalTime.of (Integer.parseInt (arrTime[0]), Integer.parseInt (arrTime[1]));

		long netDuration = arrivalTime.toSecondOfDay () - departureTime.toSecondOfDay ();

		if (netDuration > SECONDS_IN_AN_HOUR)
			return;

		TreeSet <BusService> busServiceList;

		if (isJai) {
			busServiceList = jaiBusServiceList == null ? jaiBusServiceList = new TreeSet<> () : jaiBusServiceList;
		} else {
			busServiceList = veeruBusServiceList == null ? veeruBusServiceList = new TreeSet<> () : veeruBusServiceList;
		}

		BusService busService = new BusService ();

		busService.setCompanyName (companyName);
		busService.setDepartureTime (departureTime);
		busService.setArrivalTime (arrivalTime);

		busServiceList.add (busService);
	}

	public static final int DO_NOTHING 						= 0;
	public static final int REMOVE_JAI_BUS_SERVICE 			= 1;
	public static final int REMOVE_VEERU_BUS_SERVICE 		= 2;

	public static final String JAI 							= "Jai";
	public static final String VEERU 						= "Veeru";

	public static final long SECONDS_IN_AN_HOUR 			= 60 * 60;

	public static final int GREATER_VALUE 					= 1;
	public static final int LESSER_VALUE 					= -1;
	public static final int EQUAL_VALUE 					= 0;
	
	public static final int MAXIMUM_NUMBER_OF_ENTRIES 		= 50;
}