
package ana.torres.martinez.app.service;

import ana.torres.martinez.app.model.Room;
import ana.torres.martinez.app.model.RoomRequest;

import java.util.List;

public interface RoomService {

    List<Room> findAll();

    List<Room> findAllDeleted();

    Room findById(Long roomId);

    Room create(RoomRequest request);

    Room update(Long roomId, RoomRequest request);

    void delete(Long roomId);

    Room restore(Long roomId);
}
