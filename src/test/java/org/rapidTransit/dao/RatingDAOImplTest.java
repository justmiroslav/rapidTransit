package org.rapidTransit.dao;

import org.rapidTransit.model.Rating;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.sql.*;
import java.util.List;

public class RatingDAOImplTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private Statement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    private RatingDAOImpl ratingDAO;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.execute(anyString())).thenReturn(true);
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        ratingDAO = new RatingDAOImpl(mockConnection);
    }

    @Test
    public void testSave() throws SQLException {
        // Arrange
        Rating rating = new Rating(0, 1, 1, 5, "Great service");
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        // Act
        ratingDAO.save(rating);

        // Assert
        verify(mockPreparedStatement, times(1)).executeUpdate();
        assertEquals(1, rating.getRatingId());
    }

    @Test
    public void testFind() throws SQLException {
        // Arrange
        long userId = 1L;
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt("rating_id")).thenReturn(1);
        when(mockResultSet.getLong("trip_id")).thenReturn(1L);
        when(mockResultSet.getLong("user_id")).thenReturn(userId);
        when(mockResultSet.getInt("rating")).thenReturn(5);
        when(mockResultSet.getString("comment")).thenReturn("Great service");

        // Act
        List<Rating> result = ratingDAO.findByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}