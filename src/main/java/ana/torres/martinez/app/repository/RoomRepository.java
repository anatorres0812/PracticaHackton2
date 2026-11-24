
package ana.torres.martinez.app.repository;

import ana.torres.martinez.app.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByDeletedAtIsNullOrderByRoomIdDesc();

    List<Room> findByDeletedAtIsNotNullOrderByDeletedAtDesc();

    Optional<Room> findByRoomIdAndDeletedAtIsNull(Long roomId);

    Optional<Room> findByRoomIdAndDeletedAtIsNotNull(Long roomId);

    boolean existsByNumberAndDeletedAtIsNull(String number);

    boolean existsByNumberAndDeletedAtIsNullAndRoomIdNot(
        String number,
        Long roomId
    );
}
