package org.fally.einvite.resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.fally.einvite.exception.InsufficientPermissionException;
import org.fally.einvite.model.Asset;
import org.fally.einvite.model.Event;
import org.fally.einvite.model.Permission;
import org.fally.einvite.model.transport.AssetCreateRequest;
import org.fally.einvite.model.transport.AssetEditRequest;
import org.fally.einvite.repository.AssetRepository;
import org.fally.einvite.repository.EventRepository;
import org.fally.einvite.service.AuditLogService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AssetResourceTest {
    @Captor
    private ArgumentCaptor<Asset> assetArgumentCaptor;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private UsernamePasswordAuthenticationToken principal;

    @Mock
    private HttpServletRequest request;

    private List<Asset> allAssets = new ArrayList<>();
    private List<Event> allEvents = new ArrayList<>();

    private Event event = new Event();
    private Asset asset = new Asset();

    private AssetResource assetResource;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        for (int i = 0; i < 3; i++) {
            allAssets.add(mock(Asset.class));
        }

        for (int i = 0; i < 3; i++) {
            Event event = new Event();

            event.setId(UUID.randomUUID());
            event.setName("Event " + i);

            when(eventRepository.findById(eq(event.getId()))).thenReturn(Optional.of(event));
            allEvents.add(event);
        }

        event.setId(UUID.randomUUID());
        asset.setId(UUID.randomUUID());

        //asset.setEvent(event);

        when(eventRepository.findById(eq(event.getId()))).thenReturn(Optional.of(event));
        when(assetRepository.findAll()).thenReturn(allAssets);
        //when(assetRepository.findByEvent(eq(event))).thenReturn(Collections.singletonList(asset));
        when(assetRepository.save(any(Asset.class))).thenAnswer(i -> {
            Asset asset = i.getArgument(0);

            asset.setId(UUID.randomUUID());

            return asset;
        });
        when(eventRepository.findAll()).thenReturn(allEvents);

        assetResource = new AssetResource(
                assetRepository,
                eventRepository,
                auditLogService);
    }

    @Test
    public void testFetchAsset() {
        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.LIST_ASSET.name())));

        List<Asset> result = assetResource.fetchAsset(principal, null);

        assertEquals(allAssets.size(), result.size());
    }

    @Test
    public void testFetchAssetByEvent() {
        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.LIST_ASSET.name())));

        List<Asset> result = assetResource.fetchAsset(principal, event.getId());

        assertEquals(asset, result.get(0));
        assertEquals(1, result.size());
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testFetchAssetNoPermission() {
        assetResource.fetchAsset(principal, null);
    }

    @Test
    public void testCreateAsset() {
        AssetCreateRequest createRequest = mock(AssetCreateRequest.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.CREATE_ASSET.name())));
        when(createRequest.getName()).thenReturn("Foop");
        when(createRequest.getDescription()).thenReturn("A big bag of foop.");
        when(createRequest.getEventId()).thenReturn(allEvents.get(0).getId());

        Asset response = assetResource.createAsset(
                createRequest,
                bindingResult,
                principal,
                request
        );

        assertNotNull(response);
        verify(assetRepository).save(assetArgumentCaptor.capture());
        verify(auditLogService).extractRemoteIp(eq(request));
        verify(auditLogService).log(any(), any(), anyString());

        Asset asset = assetArgumentCaptor.getValue();

        assertEquals("Foop", asset.getName());
        assertEquals("A big bag of foop.", asset.getDescription());
        //assertEquals(allEvents.get(0), asset.getEvent());
        assertNotNull(asset.getId());
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testCreateAssetNoPermission() {
        AssetCreateRequest createRequest = mock(AssetCreateRequest.class);

        when(createRequest.getName()).thenReturn("Foop");
        when(createRequest.getDescription()).thenReturn("A big bag of foop.");

        assetResource.createAsset(
                createRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = ValidationException.class)
    public void testCreateAssetInvalidName() {
        AssetCreateRequest createRequest = mock(AssetCreateRequest.class);
        ObjectError objectError = mock(ObjectError.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.CREATE_ASSET.name())));
        when(objectError.getDefaultMessage()).thenReturn("Invalid name.");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(objectError));
        when(createRequest.getName()).thenReturn("");
        when(createRequest.getDescription()).thenReturn("A big bag of foop.");

        assetResource.createAsset(
                createRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = ValidationException.class)
    public void testCreateAssetInvalidDescription() {
        AssetCreateRequest createRequest = mock(AssetCreateRequest.class);
        ObjectError objectError = mock(ObjectError.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.CREATE_ASSET.name())));
        when(objectError.getDefaultMessage()).thenReturn("Invalid description.");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(objectError));
        when(createRequest.getName()).thenReturn("Foop");
        when(createRequest.getDescription()).thenReturn("");

        assetResource.createAsset(
                createRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = ValidationException.class)
    public void testCreateAssetMissingEvent() {
        AssetCreateRequest createRequest = new AssetCreateRequest();
        ObjectError objectError = mock(ObjectError.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.CREATE_ASSET.name())));
        when(objectError.getDefaultMessage()).thenReturn("Missing event.");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(objectError));

        createRequest.setName("Foop");
        createRequest.setDescription("A big bag of foop.");
        createRequest.setEventId(null);

        assetResource.createAsset(
                createRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test
    public void testUpdateAsset() {
        UUID uuid = UUID.randomUUID();
        AssetEditRequest editRequest = mock(AssetEditRequest.class);
        Asset asset = mock(Asset.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_ASSET.name())));
        when(assetRepository.findById(eq(uuid))).thenReturn(Optional.of(asset));
        when(editRequest.getName()).thenReturn("Foop");
        when(editRequest.getDescription()).thenReturn("A big bag of foop.");
        when(editRequest.getEventId()).thenReturn(allEvents.get(0).getId());

        Asset response = assetResource.updateAsset(
                uuid.toString(),
                editRequest,
                bindingResult,
                principal,
                request
        );

        assertNotNull(response);
        verify(asset).setName(eq("Foop"));
        verify(asset).setDescription(eq("A big bag of foop."));
        //verify(asset).setEvent(eq(allEvents.get(0)));
        verify(assetRepository).save(eq(asset));
        verify(auditLogService).extractRemoteIp(eq(request));
        verify(auditLogService).log(any(), any(), anyString());
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testUpdateAssetNoPermission() {
        UUID uuid = UUID.randomUUID();
        AssetEditRequest editRequest = mock(AssetEditRequest.class);
        Asset asset = mock(Asset.class);

        when(assetRepository.findById(eq(uuid))).thenReturn(Optional.of(asset));
        when(editRequest.getName()).thenReturn("Carp");
        when(editRequest.getDescription()).thenReturn("A bucket of carp.");

        assetResource.updateAsset(
                uuid.toString(),
                editRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateAssetNotFound() {
        UUID uuid = UUID.randomUUID();
        AssetEditRequest editRequest = mock(AssetEditRequest.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_ASSET.name())));
        when(editRequest.getName()).thenReturn("Carp");
        when(editRequest.getDescription()).thenReturn("A bucket of carp.");

        assetResource.updateAsset(
                uuid.toString(),
                editRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = ValidationException.class)
    public void testUpdateAssetBadName() {
        UUID uuid = UUID.randomUUID();
        AssetEditRequest editRequest = mock(AssetEditRequest.class);
        Asset asset = mock(Asset.class);
        ObjectError objectError = mock(ObjectError.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_ASSET.name())));
        when(objectError.getDefaultMessage()).thenReturn("Invalid name.");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(objectError));
        when(assetRepository.findById(eq(uuid))).thenReturn(Optional.of(asset));
        when(editRequest.getName()).thenReturn("");
        when(editRequest.getDescription()).thenReturn("A bucket of carp.");

        assetResource.updateAsset(
                uuid.toString(),
                editRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = ValidationException.class)
    public void testUpdateAssetBadDescription() {
        UUID uuid = UUID.randomUUID();
        AssetEditRequest editRequest = mock(AssetEditRequest.class);
        Asset asset = mock(Asset.class);
        ObjectError objectError = mock(ObjectError.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_ASSET.name())));
        when(objectError.getDefaultMessage()).thenReturn("Invalid description.");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(objectError));
        when(assetRepository.findById(eq(uuid))).thenReturn(Optional.of(asset));
        when(editRequest.getName()).thenReturn("Carp");
        when(editRequest.getDescription()).thenReturn("");

        assetResource.updateAsset(
                uuid.toString(),
                editRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = ValidationException.class)
    public void testUpdateAssetMissingEvent() {
        UUID uuid = UUID.randomUUID();
        AssetEditRequest editRequest = new AssetEditRequest();
        Asset asset = mock(Asset.class);
        ObjectError objectError = mock(ObjectError.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_ASSET.name())));
        when(objectError.getDefaultMessage()).thenReturn("Invalid description.");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(objectError));
        when(assetRepository.findById(eq(uuid))).thenReturn(Optional.of(asset));

        editRequest.setName("Carp");
        editRequest.setDescription("A bucket of carp.");
        editRequest.setEventId(null);

        assetResource.updateAsset(
                uuid.toString(),
                editRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test
    public void testDeleteAsset() {
        UUID uuid = UUID.randomUUID();
        Asset asset = mock(Asset.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.DELETE_ASSET.name())));
        when(assetRepository.findById(eq(uuid))).thenReturn(Optional.of(asset));

        Asset result = assetResource.deleteAsset(
                uuid.toString(),
                principal,
                request
        );

        assertEquals(asset, result);
        verify(assetRepository).findById(eq(uuid));
        verify(assetRepository).delete(eq(asset));
        verify(auditLogService).extractRemoteIp(eq(request));
        verify(auditLogService).log(any(), any(), anyString());
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testDeleteAssetNoPermission() {
        UUID uuid = UUID.randomUUID();

        assetResource.deleteAsset(
                uuid.toString(),
                principal,
                request
        );
    }

    @Test(expected = NullPointerException.class)
    public void testDeleteAssetNotFound() {
        UUID uuid = UUID.randomUUID();

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.DELETE_ASSET.name())));

        assetResource.deleteAsset(
                uuid.toString(),
                principal,
                request
        );
    }
}
