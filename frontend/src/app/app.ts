import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

interface Room {
  roomId: number;
  number: string;
  type: string;
  pricePerNight: number;
  capacity: number;
  cleaningStatus: string;
  image: string;
  createdAt: string;
  updatedAt: string | null;
  deletedAt: string | null;
  restoredAt: string | null;
}

interface Activity {
  action: string;
  number: string;
  date: string;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnInit {

  private readonly apiUrl = 'http://localhost:8081/api/rooms';

  activeView = 'dashboard';
  searchTerm = '';
  selectedStatus = 'All';
  showModal = false;
  editingId: number | null = null;

  rooms: Room[] = [];
  activities: Activity[] = [];

  roomForm = {
    number: '',
    type: 'Simple',
    pricePerNight: 120,
    capacity: 1,
    cleaningStatus: 'Clean'
  };

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadRooms();
  }

  // LOAD ROOMS FROM SQL SERVER
  loadRooms(): void {
    this.http.get<Room[]>(this.apiUrl).subscribe({
      next: (activeRooms) => {
        this.http.get<Room[]>(`${this.apiUrl}/trash`).subscribe({
          next: (deletedRooms) => {
            this.rooms = [
              ...activeRooms.map(room => this.prepareRoom(room)),
              ...deletedRooms.map(room => this.prepareRoom(room))
            ];
          },
          error: (error) => {
            console.error('Error loading deleted rooms:', error);
            this.rooms = activeRooms.map(room => this.prepareRoom(room));
          }
        });
      },
      error: (error) => {
        console.error('Error connecting to the rooms API:', error);
        alert('Could not load rooms. Check that the backend is running.');
      }
    });
  }

  private prepareRoom(room: Room): Room {
    const images: Record<string, string> = {
      Simple: 'https://images.unsplash.com/photo-1618221195710-dd6b41faaea6?w=1000',
      Double: 'https://images.unsplash.com/photo-1616486338812-3dadae4b4ace?w=1000',
      Suite: 'https://images.unsplash.com/photo-1611892440504-42a792e24d32?w=1000'
    };

    return {
      ...room,
      image: images[room.type] ?? images['Simple']
    };
  }

  get activeRooms(): Room[] {
    return this.rooms.filter(room => room.deletedAt === null);
  }

  get deletedRooms(): Room[] {
    return this.rooms.filter(room => room.deletedAt !== null);
  }

  get cleanRooms(): number {
    return this.activeRooms.filter(room => room.cleaningStatus === 'Clean').length;
  }

  get occupiedRooms(): number {
    return this.activeRooms.filter(room => room.cleaningStatus === 'Occupied').length;
  }

  get maintenanceRooms(): number {
    return this.activeRooms.filter(room => room.cleaningStatus === 'Maintenance').length;
  }

  get occupancyRate(): number {
    return this.activeRooms.length
      ? Math.round(this.occupiedRooms / this.activeRooms.length * 100)
      : 0;
  }

  get filteredRooms(): Room[] {
    return this.activeRooms.filter(room => {
      const search = this.searchTerm.toLowerCase();

      const matchesSearch =
        room.number.toLowerCase().includes(search) ||
        room.type.toLowerCase().includes(search);

      const matchesStatus =
        this.selectedStatus === 'All' ||
        room.cleaningStatus === this.selectedStatus;

      return matchesSearch && matchesStatus;
    });
  }

  get currentDate(): string {
    return new Intl.DateTimeFormat('en-US', {
      weekday: 'long',
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    }).format(new Date());
  }

  navigate(view: string): void {
    this.activeView = view;
    this.searchTerm = '';
    this.selectedStatus = 'All';
    this.loadRooms();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  openCreate(): void {
    this.editingId = null;
    this.roomForm = {
      number: '',
      type: 'Simple',
      pricePerNight: 120,
      capacity: 1,
      cleaningStatus: 'Clean'
    };
    this.showModal = true;
  }

  openEdit(room: Room): void {
    this.editingId = room.roomId;

    this.roomForm = {
      number: room.number,
      type: room.type,
      pricePerNight: room.pricePerNight,
      capacity: room.capacity,
      cleaningStatus: room.cleaningStatus
    };

    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
  }

  saveRoom(): void {
    const number = this.roomForm.number.trim();

    if (
      !number ||
      this.roomForm.pricePerNight <= 0 ||
      this.roomForm.capacity < 1
    ) {
      alert('Please complete all fields correctly.');
      return;
    }

    const request = {
      number,
      type: this.roomForm.type,
      pricePerNight: Number(this.roomForm.pricePerNight),
      capacity: Number(this.roomForm.capacity),
      cleaningStatus: this.roomForm.cleaningStatus
    };

    if (this.editingId !== null) {
      const id = this.editingId;

      this.http.put<Room>(`${this.apiUrl}/${id}`, request).subscribe({
        next: (updatedRoom) => {
          this.addActivity('Room updated', updatedRoom.number);
          this.closeModal();
          this.loadRooms();
          alert('Room updated successfully.');
        },
        error: (error) => {
          console.error('Error updating room:', error);
          alert(error.error?.message || 'Could not update the room.');
        }
      });

    } else {
      this.http.post<Room>(this.apiUrl, request).subscribe({
        next: (createdRoom) => {
          this.addActivity('Room created', createdRoom.number);
          this.closeModal();
          this.loadRooms();
          alert('Room created successfully.');
        },
        error: (error) => {
          console.error('Error creating room:', error);
          alert(error.error?.message || 'Could not create the room.');
        }
      });
    }
  }

  deleteRoom(room: Room): void {
    if (!confirm(`Move room ${room.number} to the recycle bin?`)) {
      return;
    }

    this.http.delete<void>(`${this.apiUrl}/${room.roomId}`).subscribe({
      next: () => {
        this.addActivity('Room deleted', room.number);
        this.loadRooms();
        alert('Room moved to the recycle bin.');
      },
      error: (error) => {
        console.error('Error deleting room:', error);
        alert(error.error?.message || 'Could not delete the room.');
      }
    });
  }

  restoreRoom(room: Room): void {
    const duplicate = this.activeRooms.some(
      existing => existing.number.toLowerCase() === room.number.toLowerCase()
    );

    if (duplicate) {
      alert('Another active room already uses this number.');
      return;
    }

    this.http.patch<Room>(
      `${this.apiUrl}/${room.roomId}/restore`,
      {}
    ).subscribe({
      next: () => {
        this.addActivity('Room restored', room.number);
        this.loadRooms();
        alert('Room restored successfully.');
      },
      error: (error) => {
        console.error('Error restoring room:', error);
        alert(error.error?.message || 'Could not restore the room.');
      }
    });
  }

  addActivity(action: string, number: string): void {
    this.activities.unshift({
      action,
      number,
      date: new Date().toLocaleString()
    });
  }

  statusClass(status: string): string {
    return status.toLowerCase();
  }

  trackRoom(_: number, room: Room): number {
    return room.roomId;
  }
}