
package ana.torres.martinez.app.service.impl;

import ana.torres.martinez.app.model.Room;
import ana.torres.martinez.app.model.RoomRequest;
import ana.torres.martinez.app.repository.RoomRepository;
import ana.torres.martinez.app.service.RoomService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> findAll() {
        return roomRepository.findByDeletedAtIsNullOrderByRoomIdDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> findAllDeleted() {
        return roomRepository.findByDeletedAtIsNotNullOrderByDeletedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Room findById(Long roomId) {
        return roomRepository.findByRoomIdAndDeletedAtIsNull(roomId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Room not found"
            ));
    }

    @Override
    public Room create(RoomRequest request) {
        String number = request.getNumber().trim();

        if (roomRepository.existsByNumberAndDeletedAtIsNull(number)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Room number already exists"
            );
        }

        Room room = new Room();
        applyRequest(room, request);
        room.setNumber(number);

        return roomRepository.save(room);
    }

    @Override
    public Room update(Long roomId, RoomRequest request) {
        Room room = findById(roomId);
        String number = request.getNumber().trim();

        if (roomRepository.existsByNumberAndDeletedAtIsNullAndRoomIdNot(
                number, roomId)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Room number already exists"
            );
        }

        applyRequest(room, request);
        room.setNumber(number);

        return roomRepository.save(room);
    }

    @Override
    public void delete(Long roomId) {
        Room room = findById(roomId);

        room.setDeletedAt(LocalDateTime.now());

        roomRepository.save(room);
    }

    @Override
    public Room restore(Long roomId) {
        Room room = roomRepository
            .findByRoomIdAndDeletedAtIsNotNull(roomId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Deleted room not found"
            ));

        if (roomRepository.existsByNumberAndDeletedAtIsNull(room.getNumber())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Another active room already uses this number"
            );
        }

        room.setDeletedAt(null);
        room.setRestoredAt(LocalDateTime.now());

        return roomRepository.save(room);
    }

    private void applyRequest(Room room, RoomRequest request) {
        room.setNumber(request.getNumber().trim());
        room.setType(request.getType());
        room.setPricePerNight(request.getPricePerNight());
        room.setCapacity(request.getCapacity());
        room.setCleaningStatus(request.getCleaningStatus());
    }
}
