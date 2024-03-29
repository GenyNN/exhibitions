package org.fally.einvite.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.fally.einvite.model.Permission;
import org.fally.einvite.model.Staff;
import org.fally.einvite.repository.StaffRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StaffDetailsServiceTest {
    @Mock
    private StaffRepository staffRepository;

    @Mock
    private Staff admin;

    private StaffDetailsService staffDetailsService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        staffDetailsService = new StaffDetailsService(staffRepository);
    }

    @Test
    public void testLoadUserByUsername() {
        Set<Permission> permissions = new HashSet<>();

        permissions.add(Permission.LOGIN);
        permissions.add(Permission.READ_LOGS);

        when(staffRepository.findByUsername(eq("admin"))).thenReturn(Optional.of(admin));
        when(admin.hasPermission(eq(Permission.LOGIN))).thenReturn(true);
        when(admin.hasPermission(eq(Permission.READ_LOGS))).thenReturn(true);
        when(admin.getPassword()).thenReturn("secret");
        when(admin.getPermissions()).thenReturn(permissions);

        UserDetails userDetails = staffDetailsService.loadUserByUsername("admin");

        verify(staffRepository).findByUsername(eq("admin"));

        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
        assertEquals("secret", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(Permission.READ_LOGS.name())));
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testLoadUserByUsernameLoginNotAllowed() {
        Set<Permission> permissions = new HashSet<>();

        permissions.add(Permission.READ_LOGS);

        when(staffRepository.findByUsername(eq("admin"))).thenReturn(Optional.of(admin));
        when(admin.hasPermission(eq(Permission.READ_LOGS))).thenReturn(true);
        when(admin.getPassword()).thenReturn("secret");
        when(admin.getPermissions()).thenReturn(permissions);

        staffDetailsService.loadUserByUsername("admin");
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testLoadUserByUsernameUserNotFound() {
        when(staffRepository.findByUsername(eq("admin"))).thenThrow(new UsernameNotFoundException("Success!"));

        staffDetailsService.loadUserByUsername("admin");
    }
}
