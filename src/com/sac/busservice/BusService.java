package com.sac.busservice;

import java.time.LocalTime;

/**
 * <p>
 * 		BusService DTO is used to store the company name, departure time and arrival time.
 * 		It implements Comparable interface, thus implementing method compareTo which compares departure time for sorting purpose.
 * </p>
 * 
 * @author sachin
 *
 */
public class BusService implements Comparable <BusService> {

	private String companyName;
	private LocalTime departureTime;
	private LocalTime arrivalTime;

	public String getCompanyName () {
		return companyName;
	}

	public void setCompanyName (String companyName) {
		this.companyName = companyName;
	}

	public LocalTime getDepartureTime () {
		return departureTime;
	}

	public void setDepartureTime (LocalTime departureTime) {
		this.departureTime = departureTime;
	}

	public LocalTime getArrivalTime () {
		return arrivalTime;
	}

	public void setArrivalTime (LocalTime arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	@Override
	public int compareTo (BusService busServiceObject) {
		return departureTime.compareTo ((busServiceObject).departureTime);
	}

	@Override
	public String toString () {
		return companyName + " " + departureTime + " " + arrivalTime;
	}
}
