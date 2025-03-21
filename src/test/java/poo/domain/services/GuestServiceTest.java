package poo.domain.services;

// import org.junit.Before;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import poo.domain.entities.Guest;
import poo.domain.exceptions.GuestException;
import poo.infra.GuestDao;

@ExtendWith(MockitoExtension.class)
class GuestServiceTest {
    @Mock
    private  Connection conn;

    @Mock
    private GuestDao guestDao;

    @Mock
    private PreparedStatement st;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private GuestService guestService;

    @Test()
    @DisplayName("Should create a guest successfully")
    void createGuestSuccessfully() throws GuestException, SQLException {
        guestService.create("name", "12345678901", "email", "phone", "address",
                Date.from(LocalDate.now().minusYears(18).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        verify(guestDao, times(1)).create(any(Guest.class));
    }

    @Test()
    @DisplayName("Should throw exception when guest is under 18 years old")
    void createGuestUnder18YearsOld() {
        assertThrows(GuestException.class, () -> guestService.create("name", "12345678901", "email", "phone", "address",
                Date.from(LocalDate.now().minusYears(17).atStartOfDay(ZoneId.systemDefault()).toInstant())));
    }

    @Test()
    @DisplayName("Should throw exception when guest already exists")
    void createGuestAlreadyExists() throws GuestException, SQLException {
        when(guestDao.find("12345678901"))
                .thenReturn(Optional.of(new Guest("12345678901", "name", "email", "phone", "address", new Date())));
        assertThrows(GuestException.class, () -> guestService.create("name", "12345678901", "email", "phone", "address",
                Date.from(LocalDate.now().minusYears(18).atStartOfDay(ZoneId.systemDefault()).toInstant())));
    }

    @Test()
    @DisplayName("Should list all guests successfully")
    void listAllGuests() throws GuestException, SQLException {
        when(guestDao.find(true)).thenReturn(new ArrayList<>());
        guestService.getAllGuests();
        verify(guestDao, times(1)).find(true);
    }
}