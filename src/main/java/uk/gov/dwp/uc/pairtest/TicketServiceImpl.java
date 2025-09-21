package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */
    TicketPaymentService ticketPaymentService;
    SeatReservationService  seatReservationService;

    private final Properties ticketPrices = new Properties();

    public TicketServiceImpl() {
        loadTicketPrices();
        this.ticketPaymentService = new TicketPaymentServiceImpl();
        this.seatReservationService = new SeatReservationServiceImpl();
    }

    public TicketServiceImpl(TicketPaymentService ticketPaymentService, SeatReservationService seatReservationService) {
        loadTicketPrices();
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {

        try{
            if (isTicketRequestValid(ticketTypeRequests)) {
                int totalTickets = Arrays.stream(ticketTypeRequests).mapToInt(TicketTypeRequest::getNoOfTickets).sum();
                if (totalTickets > 25) {
                    throw new InvalidPurchaseException("Your request is not processed, maximum allowed 25 tickets in a single booking, please request with less number of tickets.");
                }
                int totalAmountToPay = calculateTotalAmount(ticketTypeRequests);
                ticketPaymentService.makePayment(accountId, totalAmountToPay);
                int totalSeatsToReserve = calculateTotalSeats(ticketTypeRequests);
                seatReservationService.reserveSeat(accountId, totalSeatsToReserve);
            } else {
                throw new InvalidPurchaseException("Invalid tickets request: At least one adult ticket must be purchased as part of booking.");
            }
        } catch (InvalidPurchaseException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidPurchaseException("An error occurred while processing the ticket purchase: " + e.getMessage());
        }


    }


    private boolean isTicketRequestValid(TicketTypeRequest... ticketTypeRequests) {
        return Arrays.stream(ticketTypeRequests).anyMatch(request -> request.getTicketType() == TicketTypeRequest.Type.ADULT && request.getNoOfTickets() > 0);
    }

    private int calculateTotalAmount(TicketTypeRequest... ticketTypeRequests) {
        int totalAmount = 0;
        for (TicketTypeRequest request : ticketTypeRequests) {
            int price = Integer.parseInt(ticketPrices.getProperty( request.getTicketType().name(), "0"));
            totalAmount += price * request.getNoOfTickets();
        }
        return totalAmount;
    }

    private int calculateTotalSeats(TicketTypeRequest... ticketTypeRequests) {
        return (int) Arrays.stream(ticketTypeRequests)
                .filter(request -> request.getTicketType() != TicketTypeRequest.Type.INFANT)
                .mapToLong(TicketTypeRequest::getNoOfTickets)
                .sum();
    }

    private void loadTicketPrices() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("ticket-prices.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find ticket-prices details");
            }
            ticketPrices.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading ticket prices", e);
        }
    }

}
