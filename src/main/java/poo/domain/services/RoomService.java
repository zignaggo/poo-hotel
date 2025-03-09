package poo.domain.services;

import java.util.ArrayList;

import poo.domain.entities.Room;
import poo.infra.RoomDao;

public class RoomService {
  private RoomDao roomDao;

  public RoomService(RoomDao roomDao) {
    this.roomDao = roomDao;
  }

  public void list() {
    try {
      ArrayList<Room> rooms = roomDao.find();
      if(rooms.isEmpty()) {
        System.out.println("No rooms found");
        return;
      }
      rooms.forEach(room -> System.out.println(room.toString()));
    } catch (Exception e) {
      System.out.println("Failed to list rooms: " + e.getMessage());
    }
  }
}
