# Cinema Tickets Booking Service

This project is a Java-based ticket booking service for a cinema. It validates ticket requests, calculates total costs, and interacts with external services for payment and seat reservations.

## Features

- **Ticket Validation**: Ensures at least one adult ticket is included in the booking.
- **Ticket Limits**: Restricts bookings to a maximum of 25 tickets per request.
- **Dynamic Pricing**: Reads ticket prices from a properties file (`ticket-prices.properties`).
- **External Service Integration**:
    - Payment processing via `TicketPaymentService`.
    - Seat reservation via `SeatReservationService`.

## Project Structure

### Main Files

- `src/main/java/uk/gov/dwp/uc/pairtest/TicketServiceImpl.java`: Implements the ticket booking logic.
- `src/main/resources/ticket-prices.properties`: Contains ticket prices for different ticket types (Adult, Child, Infant).

### Test Files

- `src/test/java/uk/gov/dwp/uc/pairtest/TicketServiceImplTest.java`: Unit tests for the `TicketServiceImpl` class.
- `src/test/resources/ticket-prices.properties`: Test-specific ticket prices.

## Prerequisites

- **Java**: Version 21 or higher.
- **Maven**: For dependency management and build.

## Dependencies

- **JUnit 5**: For unit testing.
- **Mockito**: For mocking external services during testing.

## How to Run

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd cinema-tickets-java



2. Build the project using Maven:
   ```bash
    mvn clean install
    ```
3. Run the tests:
    ```bash
    mvn test
    ```
## Configuration
Ticket prices are defined in the ticket-prices.properties file:
* ADULT=25
* CHILD=15
* INFANT=0

## Key Classes
 **TicketServiceImpl**
**Methods:**
* purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests): Validates and processes ticket purchases.
* calculateTotalAmount(TicketTypeRequest... ticketTypeRequests): Calculates the total cost of tickets.
* calculateTotalSeats(TicketTypeRequest... ticketTypeRequests): Calculates the total number of seats to reserve.


 **TicketServiceImplTest**
Contains unit tests for various scenarios:
* Valid ticket purchases.
* Invalid ticket requests (e.g., no adult tickets, exceeding ticket limits).

## Error Handling
* Throws InvalidPurchaseException for invalid ticket requests or errors during processing.
