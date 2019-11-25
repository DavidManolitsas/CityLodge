package model;

import java.util.HashMap;

import model.exceptions.CapacityReachedException;

public class CityLodge {

	public static final int MAX_ROOMS = 50;
	private HashMap <String, HotelRoom> roomMap;

	public CityLodge() {
		roomMap = new HashMap<String, HotelRoom>();
	}

	
	public HotelRoom getRoom(String roomID) {
		return roomMap.get(roomID);
	}

	
	public void addRoomMap(HotelRoom room) throws CapacityReachedException {
		if (roomMap.size() + 1 > MAX_ROOMS) {
			throw new CapacityReachedException();
		}
		else {
			//add room to roomMap
			roomMap.put(room.getRoomID(), room);
		}
	}

	
	public HashMap<String, HotelRoom> getRoomMap() {
		return roomMap;
	}
	
}
