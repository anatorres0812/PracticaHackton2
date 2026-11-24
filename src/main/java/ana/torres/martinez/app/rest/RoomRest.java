
package ana.torres.martinez.app.rest;

import ana.torres.martinez.app.model.Room;
import ana.torres.martinez.app.model.RoomRequest;
import ana.torres.martinez.app.service.RoomService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "http://localhost:4200")
public class RoomRest {

    private final RoomService roomService;

    public RoomRest(RoomService roomService) {
        this.roomService = roomService;
    }

    // GET: Find all active rooms
    @GetMapping
    public ResponseEntity<List<Room>> findAll() {
        return ResponseEntity.ok(roomService.findAll());
    }

    // GET: Find deleted rooms
    @GetMapping("/trash")
    public ResponseEntity<List<Room>> findAllDeleted() {
        return ResponseEntity.ok(roomService.findAllDeleted());
    }

    // GET: Find room by ID
    @GetMapping("/{roomId}")
    public ResponseEntity<Room> findById(
            @PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.findById(roomId));
    }

    // POST: Create room
    @PostMapping
    public ResponseEntity<Room> create(
            @Valid @RequestBody RoomRequest request) {
        Room room = roomService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(room);
    }

    // PUT: Update room
    @PutMapping("/{roomId}")
    public ResponseEntity<Room> update(
            @PathVariable Long roomId,
            @Valid @RequestBody RoomRequest request) {

        return ResponseEntity.ok(
            roomService.update(roomId, request)
        );
    }

    // DELETE: Soft delete room
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long roomId) {

        roomService.delete(roomId);

        return ResponseEntity.noContent().build();
    }

    // PATCH: Restore deleted room
    @PatchMapping("/{roomId}/restore")
    public ResponseEntity<Room> restore(
            @PathVariable Long roomId) {

        return ResponseEntity.ok(
            roomService.restore(roomId)
        );
    }
}
