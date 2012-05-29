// TrainManifest.java

package jmri.jmrit.operations.trains;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.List;

import jmri.jmrit.operations.locations.Location;
import jmri.jmrit.operations.locations.LocationManager;
import jmri.jmrit.operations.rollingstock.cars.Car;
import jmri.jmrit.operations.rollingstock.cars.CarLoad;
import jmri.jmrit.operations.rollingstock.cars.CarLoads;
import jmri.jmrit.operations.routes.RouteLocation;
import jmri.jmrit.operations.setup.Setup;

/**
 * Builds a train's manifest. 
 * 
 * @author Daniel Boudreau  Copyright (C) 2011, 2012
 * @version $Revision: 1 $
 */
public class TrainManifest extends TrainCommon {
	
	LocationManager locationManager = LocationManager.instance();

	public TrainManifest(Train train){
		// create manifest file
		File file = TrainManagerXml.instance().createTrainManifestFile(
				train.getName());
		PrintWriter fileOut;

		try {
			fileOut = new PrintWriter(new BufferedWriter(new FileWriter(file)),
					true);
		} catch (IOException e) {
			log.error("can not open train manifest file");
			return;
		}
		// build header
		if (!train.getRailroadName().equals(""))
			addLine(fileOut, train.getRailroadName());
		else
			addLine(fileOut, Setup.getRailroadName());
		newLine(fileOut);
		addLine(fileOut, rb.getString("ManifestForTrain")+" (" + train.getName() + ") "+ train.getDescription());
		
		String valid = MessageFormat.format(rb.getString("Valid"), new Object[]{getDate()});
		
		if (Setup.isPrintTimetableNameEnabled()){
			TrainSchedule sch = TrainScheduleManager.instance().getScheduleById(TrainManager.instance().getTrainScheduleActiveId());
			if (sch != null)
				valid = valid + " ("+sch.getName()+")"; 
		}		
		if (Setup.isPrintValidEnabled())
			addLine(fileOut, valid);
		
		if (!train.getComment().equals(""))
			addLine(fileOut, train.getComment());
		
		List<String> engineList = engineManager.getByTrainList(train);		
		//pickupEngines(fileOut, engineList, train.getTrainDepartsRouteLocation());
		
		if (Setup.isPrintRouteCommentsEnabled() && !train.getRoute().getComment().equals(""))
			addLine(fileOut, train.getRoute().getComment());
		
		List<String> carList = carManager.getByTrainDestinationList(train);
		log.debug("Train has " + carList.size() + " cars assigned to it");
		int cars = 0;
		int emptyCars = 0;
		boolean work = false;
		boolean newWork = false;
		String previousRouteLocationName = null;
		List<String> routeList = train.getRoute().getLocationsBySequenceList();
		
		for (int r = 0; r < routeList.size(); r++) {
			RouteLocation rl = train.getRoute().getLocationById(routeList.get(r));
			// add line break between locations without work and ones with work
			boolean oldWork = work;
			work = isThereWorkAtLocation(carList, rl);
			if (oldWork == false && work == true)
				newLine(fileOut);		
			
			// print info only if new location
			Location location = locationManager.getLocationByName(rl.getName());
			String routeLocationName = splitString(rl.getName());
			if (!routeLocationName.equals(previousRouteLocationName) ||
					(routeLocationName.equals(previousRouteLocationName) && oldWork == false && work == true && newWork == false)){
				if (work){
					newWork = true;
					String expectedArrivalTime = train.getExpectedArrivalTime(rl);
					if (r == 0){
						addLine(fileOut, rb.getString("ScheduledWorkIn")+" " + routeLocationName 
								+", "+rb.getString("departureTime")+" "+train.getFormatedDepartureTime());
					} else if (!rl.getDepartureTime().equals("")){
						addLine(fileOut, rb.getString("ScheduledWorkIn")+" " + routeLocationName 
								+", "+rb.getString("departureTime")+" "+rl.getFormatedDepartureTime());
					} else if (!expectedArrivalTime.equals("-1")){
						addLine(fileOut, rb.getString("ScheduledWorkIn")+" " + routeLocationName 
								+", "+rb.getString("estimatedArrival")+" "+ expectedArrivalTime);
					} else {
						addLine(fileOut, rb.getString("ScheduledWorkIn")+" " + routeLocationName);
					}
					// add route comment
					if (!rl.getComment().equals(""))
						addLine(fileOut, rl.getComment());
				} else {
					// no work at this location
					String s = MessageFormat.format(rb.getString("NoScheduledWorkAt"), new Object[]{routeLocationName});
					// if a route comment, then only use location name and route comment, useful for passenger trains
					if (!rl.getComment().equals("")){
						s = routeLocationName;
						if (rl.getComment().trim().length()>0)
							s = s +", "+rl.getComment();
					}
					if (r == 0)
						s = s +", "+rb.getString("departureTime")+" "+train.getDepartureTime();
					else if (!rl.getDepartureTime().equals(""))
						s = s +", "+rb.getString("departureTime")+" "+rl.getFormatedDepartureTime();				
					addLine(fileOut, s);
				}
				// add location comment
				if (Setup.isPrintLocationCommentsEnabled() && !location.getComment().equals(""))
					addLine(fileOut, location.getComment());				
			}
			
			// engine change or helper service?
			if (train.getSecondLegOptions() != Train.NONE){
				if (rl == train.getSecondLegStartLocation())
					printChange(fileOut, rl, train.getSecondLegOptions());
				if (rl == train.getSecondLegEndLocation() && train.getSecondLegOptions() == Train.HELPER_ENGINES)
					addLine(fileOut, MessageFormat.format(rb.getString("RemoveHelpersAt"), new Object[]{splitString(rl.getName())}));
			}
			if (train.getThirdLegOptions() != Train.NONE){
				if (rl == train.getThirdLegStartLocation())
					printChange(fileOut, rl, train.getThirdLegOptions());
				if (rl == train.getThirdLegEndLocation() && train.getThirdLegOptions() == Train.HELPER_ENGINES)
					addLine(fileOut, MessageFormat.format(rb.getString("RemoveHelpersAt"), new Object[]{splitString(rl.getName())}));
			}
			
			pickupEngines(fileOut, engineList, rl);

			// block cars by destination
			for (int j = r; j < routeList.size(); j++) {
				RouteLocation rld = train.getRoute().getLocationById(routeList.get(j));				
				utilityCarTypes.clear();	// list utility cars by quantity
				for (int k = 0; k < carList.size(); k++) {
					Car car = carManager.getById(carList.get(k));
					if (car.getRouteLocation() == rl
							&& car.getRouteDestination() == rld) {
						if (car.isUtility())
							pickupCars(fileOut, carList, car, rl, rld);
						// use truncated format if there's a switch list
						else if (Setup.isTruncateManifestEnabled() && location.isSwitchListEnabled())
							pickUpCar(fileOut, car, new StringBuffer(Setup.getPickupCarPrefix()), Setup.getTruncatedPickupManifestMessageFormat(), Setup.getManifestOrientation());
						else 
							pickUpCar(fileOut, car);
						cars++;
						newWork = true;
						if (CarLoads.instance().getLoadType(car.getType(), car.getLoad()).equals(CarLoad.LOAD_TYPE_EMPTY))
							emptyCars++;
					}
				}
			}		
			utilityCarTypes.clear();	// list utility cars by quantity
			for (int j = 0; j < carList.size(); j++) {
				Car car = carManager.getById(carList.get(j));
				if (car.getRouteDestination() == rl) {
					if (car.isUtility())
						setoutCars(fileOut, carList, car, rl, car.getRouteLocation().equals(car.getRouteDestination()) && car.getTrack()!=null);
					// use truncated format if there's a switch list
					else if (Setup.isTruncateManifestEnabled() && location.isSwitchListEnabled())
						truncatedDropCar(fileOut, car);
					else
						dropCar(fileOut, car);
					cars--;
					newWork = true;
					if (CarLoads.instance().getLoadType(car.getType(), car.getLoad()).equals(CarLoad.LOAD_TYPE_EMPTY))
						emptyCars--;
				}
			}
			
			dropEngines(fileOut, engineList, rl);

			if (r != routeList.size() - 1) {
				// Is the next location the same as the previous?
				RouteLocation rlNext = train.getRoute().getLocationById(routeList.get(r+1));
				String nextRouteLocationName = splitString(rlNext.getName());
				if (!routeLocationName.equals(nextRouteLocationName)){
					if (newWork){
						StringBuffer buf = new StringBuffer(rb.getString("TrainDeparts")+ " " + routeLocationName +" "+ rl.getTrainDirectionString()
								+ rb.getString("boundWith") +" ");
						if (Setup.isPrintLoadsAndEmptiesEnabled())
							buf.append((cars-emptyCars)+" "+rb.getString("Loads")+", "+emptyCars+" "+rb.getString("Empties")+", ");
						else
							buf.append(cars +" "+rb.getString("cars")+", ");
						String s = train.getTrainLength(rl)+" "+Setup.getLengthUnit().toLowerCase()+", "+train.getTrainWeight(rl)+" "+rb.getString("tons");
						if (buf.length()+s.length()>lineLength(Setup.getManifestOrientation())){
							addLine(fileOut, buf.toString());
							buf = new StringBuffer();
						}
						buf.append(s);
						addLine(fileOut, buf.toString());
						newWork = false;
						newLine(fileOut);
					}
				}
			} else {
				addLine(fileOut, rb.getString("TrainTerminatesIn")+ " " + routeLocationName);
			}
			previousRouteLocationName = routeLocationName;
		}
		// Are there any cars that need to be found?
		getCarsLocationUnknown(fileOut);
		
		fileOut.flush();
		fileOut.close();
		
		train.setModified(false);
	}
	
	// returns true if there's work at location
	private boolean isThereWorkAtLocation(List<String> carList, RouteLocation rl){
		for (int i = 0; i < carList.size(); i++) {
			Car car = carManager.getById(carList.get(i));
			if (car.getRouteLocation() == rl || car.getRouteDestination() == rl)
				return true;
		}
		return false;
	}
	
	private void printChange(PrintWriter fileOut, RouteLocation rl, int legOptions){
		if ((legOptions & Train.HELPER_ENGINES) == Train.HELPER_ENGINES)
			addLine(fileOut, MessageFormat.format(rb.getString("AddHelpersAt"), new Object[]{splitString(rl.getName())}));
		else if ((legOptions & Train.CHANGE_ENGINES) == Train.CHANGE_ENGINES && ((legOptions & Train.REMOVE_CABOOSE) == Train.REMOVE_CABOOSE || (legOptions & Train.ADD_CABOOSE) == Train.ADD_CABOOSE))
			addLine(fileOut, MessageFormat.format(rb.getString("EngineAndCabooseChangeAt"), new Object[]{splitString(rl.getName())}));
		else if ((legOptions & Train.CHANGE_ENGINES) == Train.CHANGE_ENGINES)
			addLine(fileOut, MessageFormat.format(rb.getString("EngineChangeAt"), new Object[]{splitString(rl.getName())}));
		else if ((legOptions & Train.REMOVE_CABOOSE) == Train.REMOVE_CABOOSE || (legOptions & Train.ADD_CABOOSE) == Train.ADD_CABOOSE)
			addLine(fileOut, MessageFormat.format(rb.getString("CabooseChangeAt"), new Object[]{splitString(rl.getName())}));
	}
}

