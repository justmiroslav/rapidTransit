package org.rapidTransit.dao;

import org.rapidTransit.model.Trip;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class TripDAOImplTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private Statement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    @Mock
    private Array mockArray;

    private TripDAOImpl tripDAO;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(mockPreparedStatement);
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockStatement.execute(anyString())).thenReturn(true);
        when(mockConnection.createArrayOf(eq("INTEGER"), any())).thenReturn(mockArray);

        tripDAO = new TripDAOImpl(mockConnection);
    }

    @Test
    public void testFindSimp() throws SQLException {
        // Arrange
        long tripId = 1L;
        when(mockResultSet.next()).thenReturn(true);
        setupMockResultSet();

        // Act
        Trip trip = tripDAO.findById(tripId);

        // Assert
        assertNotNull(trip);
        assertEquals(tripId, trip.getTripId());
    }

    @Test
    public void testFindComplex() throws SQLException {
        // Arrange
        int routeId = 1;
        LocalDate date = LocalDate.now();
        when(mockResultSet.next()).thenReturn(true);
        setupMockResultSet();

        // Act
        Trip trip = tripDAO.findByRouteAndDate(routeId, date);

        // Assert
        assertNotNull(trip);
        assertEquals(routeId, trip.getRouteId());
        assertEquals(date, trip.getTripDate());
    }

    @Test
    public void testGetList() throws SQLException {
        // Arrange
        when(mockResultSet.next()).thenReturn(true, true, false);
        setupMockResultSet();

        // Act
        List<Trip> trips = tripDAO.getLastTrips();

        // Assert
        assertEquals(2, trips.size());
    }

    @Test
    public void testTripExists() throws SQLException {
        // Arrange
        int routeId = 1;
        LocalDate date = LocalDate.now();
        when(mockResultSet.next()).thenReturn(true);

        // Act
        boolean exists = tripDAO.tripExists(routeId, date);

        // Assert
        assertTrue(exists);
    }

    @Test
    public void testGetUniqueDates() throws SQLException {
        // Arrange
        int routeId = 1;
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getDate("trip_date")).thenReturn(Date.valueOf(LocalDate.now()));

        // Act
        List<LocalDate> dates = tripDAO.getUniqueDates(routeId);

        // Assert
        assertEquals(2, dates.size());
    }

    @Test
    public void testUpdate() throws SQLException {
        // Arrange
        Trip trip = new Trip(1L, 1, 1, LocalDate.now(), LocalTime.now(), LocalTime.now(), Arrays.asList(1, 2, 3));

        // Act
        tripDAO.update(trip);

        // Assert
        verify(mockPreparedStatement).setInt(1, trip.getRouteId());
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    public void testSave() throws SQLException {
        // Arrange
        Trip trip = new Trip(0L, 1, 1, LocalDate.now(), LocalTime.now(), LocalTime.now(), Arrays.asList(1, 2, 3));
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong(1)).thenReturn(1L);

        // Act
        tripDAO.save(trip);

        // Assert
        assertEquals(1L, trip.getTripId());
        verify(mockPreparedStatement).executeUpdate();
    }

    private void setupMockResultSet() throws SQLException {
        when(mockResultSet.getLong("trip_id")).thenReturn(1L);
        when(mockResultSet.getInt("route_id")).thenReturn(1);
        when(mockResultSet.getInt("bus_id")).thenReturn(1);
        when(mockResultSet.getDate("trip_date")).thenReturn(Date.valueOf(LocalDate.now()));
        when(mockResultSet.getTime("departure_time")).thenReturn(Time.valueOf(LocalTime.now()));
        when(mockResultSet.getTime("arrival_time")).thenReturn(Time.valueOf(LocalTime.now()));
        when(mockResultSet.getArray("available_seats")).thenReturn(mockArray);
        when(mockArray.getArray()).thenReturn(new Integer[]{1, 2, 3});
    }
}
