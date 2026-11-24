
package ana.torres.martinez.app.model;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class RoomRequest {

    @NotBlank(message = "Room number is required")
    @Size(max = 20, message = "Room number cannot exceed 20 characters")
    private String number;

    @NotBlank(message = "Room type is required")
    @Pattern(
        regexp = "Simple|Double|Suite",
        message = "Room type must be Simple, Double or Suite"
    )
    private String type;

    @NotNull(message = "Price per night is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    @Digits(integer = 8, fraction = 2, message = "Invalid price format")
    private BigDecimal pricePerNight;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @NotBlank(message = "Cleaning status is required")
    @Pattern(
        regexp = "Clean|Occupied|Maintenance",
        message = "Invalid cleaning status"
    )
    private String cleaningStatus;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getCleaningStatus() {
        return cleaningStatus;
    }

    public void setCleaningStatus(String cleaningStatus) {
        this.cleaningStatus = cleaningStatus;
    }
}
