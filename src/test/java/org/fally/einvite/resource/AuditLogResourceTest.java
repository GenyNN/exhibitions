package org.fally.einvite.resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.fally.einvite.exception.InsufficientPermissionException;
import org.fally.einvite.model.AuditLog;
import org.fally.einvite.model.Permission;
import org.fally.einvite.repository.AuditLogRepository;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AuditLogResourceTest {
    private static final int REQUEST_MAX = 10;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private UsernamePasswordAuthenticationToken principal;

    private AuditLogResource auditLogResource;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(auditLogRepository.findAll(any(PageRequest.class))).thenAnswer(invocation -> {
            PageRequest request = invocation.getArgument(0);
            List<AuditLog> items = new ArrayList<>();

            for (int i = 0; i < request.getPageSize(); i++) {
                AuditLog log = mock(AuditLog.class);

                when(log.getTimestamp()).thenReturn(System.currentTimeMillis());
                when(log.getUsername()).thenReturn("user" + i);
                when(log.getRemoteAddress()).thenReturn("1.2.3." + i);
                when(log.getMessage()).thenReturn("Fake!");

                items.add(log);
            }

            return new PageImpl<>(items);
        });

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.READ_LOGS.name())));

        auditLogResource = new AuditLogResource(REQUEST_MAX, auditLogRepository);
    }

    @Test
    public void testFetch() {
        List<AuditLog> results = auditLogResource.fetchAuditLogs(principal, null);

        assertEquals(REQUEST_MAX, results.size());
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testFetchNotAllowed() {
        when(principal.getAuthorities()).thenReturn(Collections.emptyList());

        auditLogResource.fetchAuditLogs(principal, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFetchZero() {
        auditLogResource.fetchAuditLogs(principal, 0);
    }

    @Test
    public void testFetchOne() {
        List<AuditLog> results = auditLogResource.fetchAuditLogs(principal, 1);

        assertEquals(1, results.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFetchTooMany() {
        auditLogResource.fetchAuditLogs(principal, REQUEST_MAX + 1);
    }

    @Test
    public void testFetchMax() {
        List<AuditLog> results = auditLogResource.fetchAuditLogs(principal, REQUEST_MAX);

        assertEquals(REQUEST_MAX, results.size());
    }
}
