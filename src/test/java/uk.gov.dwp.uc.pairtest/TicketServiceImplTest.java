package uk.gov.dwp.uc.pairtest;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class TicketServiceImplTest {

    private TicketPaymentService ticketPaymentService;
    private SeatReservationService seatReservationService;
    private TicketServiceImpl ticketService;

    @BeforeEach
    void setUp() {
        ticketPaymentService = mock(TicketPaymentService.class);
        seatReservationService = mock(SeatReservationService.class);
        ticketService = new TicketServiceImpl(ticketPaymentService, seatReservationService);
    }

    @Test
    @DisplayName("Test to valid purchase tickets")
    void testTheValidTicketsPurchase() throws InvalidPurchaseException {
        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest childTicket = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        TicketTypeRequest infantTicket = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

        ticketService.purchaseTickets(1L, adultTicket, childTicket, infantTicket);

        verify(ticketPaymentService).makePayment(1L, 65);
        verify(seatReservationService).reserveSeat(1L, 3);
    }

    @Test
    @DisplayName("Test to valid purchase tickets with adult and Infant")
    void testTheValidTicketsPurchaseWithAdultAndInfant() throws InvalidPurchaseException {
        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest infantTicket = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

        ticketService.purchaseTickets(1L, adultTicket, infantTicket);

        verify(ticketPaymentService).makePayment(1L, 50);
        verify(seatReservationService).reserveSeat(1L, 2);
    }

    @Test
    @DisplayName("Test to Invalid purchase tickets with only Child ticket Type")
    void testInvalidPurchaseWhenNoAdultTicketIsPresent() {
        TicketTypeRequest childTicket = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2);

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L, childTicket));
        verifyNoInteractions(ticketPaymentService, seatReservationService);
        assertEquals("Invalid tickets request: At least one adult ticket must be purchased as part of booking.", exception.getMessage());
    }
    @Test
    @DisplayName("Test to Invalid purchase tickets with only Infant ticket Type")
    void testInvalidPurchaseWhenOnlyInfantPresent() {
        TicketTypeRequest infantTickets = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L, infantTickets));
        verifyNoInteractions(ticketPaymentService, seatReservationService);
        assertEquals("Invalid tickets request: At least one adult ticket must be purchased as part of booking.", exception.getMessage());
    }
    @Test
    @DisplayName("Test to Invalid purchase tickets with Maximum limit")
    void testInvalidPurchaseWhenMoreThan25TicketsAreRequested() {
        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 26);

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L, adultTicket));
        verifyNoInteractions(ticketPaymentService, seatReservationService);
        assertEquals("Your request is not processed, maximum allowed 25 tickets in a single booking, please request with less number of tickets.", exception.getMessage());
    }

    @Test
    @DisplayName("Test to Maximum number of tickets including adult and Infant")
    void testInvalidPurchaseWhenMoreThan25TicketsAreRequestedIncludingInfant() {
        TicketTypeRequest adultTickets = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 20);
        TicketTypeRequest infantTickets = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 6);

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L, adultTickets, infantTickets));
        verifyNoInteractions(ticketPaymentService, seatReservationService);
        assertEquals("Your request is not processed, maximum allowed 25 tickets in a single booking, please request with less number of tickets.", exception.getMessage());
    }
}