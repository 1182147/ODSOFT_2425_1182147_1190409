package pt.psoft.g1.psoftg1.readermanagement.model;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import pt.psoft.g1.psoftg1.exceptions.ConflictException;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.readermanagement.services.UpdateReaderRequest;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReaderTest {

    private final int validReaderNumber = 123;
    private final String validBirthDate = "2010-01-01";
    private final String validPhoneNumber = "912345678";
    private final String validPhotoUri = "photo/uri";

    /******** Opaque *********/

    @Test
    void ensureReaderDetailsIsCreated_WhenReceivingValidParameters() {
        // arrange
        Reader doubleReader = mock(Reader.class);
        List<Genre> validInterestList = new ArrayList<>();
        try (
            MockedConstruction<ReaderNumber> ignored = mockConstruction(ReaderNumber.class);
            MockedConstruction<PhoneNumber> ignored1 = mockConstruction(PhoneNumber.class);
            MockedConstruction<BirthDate> ignored2 = mockConstruction(BirthDate.class)
        ) {
            // act
            ReaderDetails readerDetails = new ReaderDetails(
                validReaderNumber,
                doubleReader,
                validBirthDate,
                validPhoneNumber,
                true,
                false,
                false,
                validPhotoUri,
                validInterestList
            );
            // assert
            assertNotNull(readerDetails);
            assertEquals(doubleReader, readerDetails.getReader());
            assertTrue(readerDetails.isGdprConsent());
            assertFalse(readerDetails.isMarketingConsent());
            assertFalse(readerDetails.isThirdPartySharingConsent());
            assertEquals(validInterestList, readerDetails.getInterestList());
            assertNotNull(readerDetails.getPhoto());
            assertEquals(validPhotoUri, readerDetails.getPhoto().getPhotoFile());
        }
    }

    @Test
    void ensureReaderDetailsIsCreated_WhenReceivingNullPhotoUri() {
        // arrange
        Reader doubleReader = mock(Reader.class);
        List<Genre> validInterestList = new ArrayList<>();
        try (
            MockedConstruction<ReaderNumber> ignored = mockConstruction(ReaderNumber.class);
            MockedConstruction<PhoneNumber> ignored1 = mockConstruction(PhoneNumber.class);
            MockedConstruction<BirthDate> ignored2 = mockConstruction(BirthDate.class)
        ) {
            // act
            ReaderDetails readerDetails = new ReaderDetails(
                validReaderNumber,
                doubleReader,
                validBirthDate,
                validPhoneNumber,
                true,
                false,
                false,
                null,
                validInterestList
            );
            // assert
            assertNotNull(readerDetails);
            assertEquals(doubleReader, readerDetails.getReader());
            assertTrue(readerDetails.isGdprConsent());
            assertFalse(readerDetails.isMarketingConsent());
            assertFalse(readerDetails.isThirdPartySharingConsent());
            assertEquals(validInterestList, readerDetails.getInterestList());
            assertNull(readerDetails.getPhoto());
        }
    }

    @Test
    void ensureReaderDetailsIsCreated_WhenReceivingNullInterestList() {
        // arrange
        Reader doubleReader = mock(Reader.class);
        try (
            MockedConstruction<ReaderNumber> ignored = mockConstruction(ReaderNumber.class);
            MockedConstruction<PhoneNumber> ignored1 = mockConstruction(PhoneNumber.class);
            MockedConstruction<BirthDate> ignored2 = mockConstruction(BirthDate.class)
        ) {
            // act
            ReaderDetails readerDetails = new ReaderDetails(
                validReaderNumber,
                doubleReader,
                validBirthDate,
                validPhoneNumber,
                true,
                false,
                false,
                null,
                null
            );
            // assert
            assertNotNull(readerDetails);
            assertEquals(doubleReader, readerDetails.getReader());
            assertTrue(readerDetails.isGdprConsent());
            assertFalse(readerDetails.isMarketingConsent());
            assertFalse(readerDetails.isThirdPartySharingConsent());
            assertNull(readerDetails.getInterestList());
            assertNull(readerDetails.getPhoto());
        }
    }

    @Test
    void ensureReaderDetailsThrowsError_WhenReceivingNullReader() {
        // arrange
        List<Genre> validInterestList = new ArrayList<>();
        try (
            MockedConstruction<ReaderNumber> ignored = mockConstruction(ReaderNumber.class);
            MockedConstruction<PhoneNumber> ignored1 = mockConstruction(PhoneNumber.class);
            MockedConstruction<BirthDate> ignored2 = mockConstruction(BirthDate.class)
        ) {
            // act + assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ReaderDetails(
                    validReaderNumber,
                    null,
                    validBirthDate,
                    validPhoneNumber,
                    true,
                    false,
                    false,
                    validPhotoUri,
                    validInterestList
                )
            );
            assertEquals(IllegalArgumentException.class, exception.getClass());
            assertEquals("Provided argument resolves to null object", exception.getMessage());
        }
    }

    @Test
    void ensureReaderDetailsThrowsError_WhenReceivingNullPhoneNumber() {
        // arrange
        Reader doubleReader = mock(Reader.class);
        List<Genre> validInterestList = new ArrayList<>();
        try (
            MockedConstruction<ReaderNumber> ignored = mockConstruction(ReaderNumber.class);
            MockedConstruction<PhoneNumber> ignored1 = mockConstruction(PhoneNumber.class);
            MockedConstruction<BirthDate> ignored2 = mockConstruction(BirthDate.class)
        ) {
            // act + assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ReaderDetails(
                    validReaderNumber,
                    doubleReader,
                    validBirthDate,
                    null,
                    true,
                    false,
                    false,
                    validPhotoUri,
                    validInterestList
                )
            );
            assertEquals(IllegalArgumentException.class, exception.getClass());
            assertEquals("Provided argument resolves to null object", exception.getMessage());
        }
    }

    @Test
    void ensureReaderDetailsThrowsError_WhenReceivingFalsyGdpr() {
        // arrange
        Reader doubleReader = mock(Reader.class);
        List<Genre> validInterestList = new ArrayList<>();
        try (
            MockedConstruction<ReaderNumber> ignored = mockConstruction(ReaderNumber.class);
            MockedConstruction<PhoneNumber> ignored1 = mockConstruction(PhoneNumber.class);
            MockedConstruction<BirthDate> ignored2 = mockConstruction(BirthDate.class)
        ) {
            // act + assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ReaderDetails(
                    validReaderNumber,
                    doubleReader,
                    validBirthDate,
                    validPhoneNumber,
                    false,
                    false,
                    false,
                    validPhotoUri,
                    validInterestList
                )
            );
            assertEquals(IllegalArgumentException.class, exception.getClass());
            assertEquals("Readers must agree with the GDPR rules", exception.getMessage());
        }
    }

    @Test
    void ensureReaderDetailsPhotoIsRemoved_WhenCalledWithValidVersion() {
        // arrange
        Reader doubleReader = mock(Reader.class);
        List<Genre> validInterestList = new ArrayList<>();
        try (
            MockedConstruction<ReaderNumber> ignored = mockConstruction(ReaderNumber.class);
            MockedConstruction<PhoneNumber> ignored1 = mockConstruction(PhoneNumber.class);
            MockedConstruction<BirthDate> ignored2 = mockConstruction(BirthDate.class)
        ) {
            long targetVersion = 1L;
            ReaderDetails readerDetails = new ReaderDetails(
                validReaderNumber,
                doubleReader,
                validBirthDate,
                validPhoneNumber,
                true,
                false,
                false,
                validPhotoUri,
                validInterestList
            );
            readerDetails.setVersion(targetVersion);
            assertNotNull(readerDetails.getPhoto());
            // act
            readerDetails.removePhoto(targetVersion);
            // assert
            assertNull(readerDetails.getPhoto());
        }
    }

    @Test
    void ensureReaderDetailsPhotoRemovalThrows_WhenCalledWithInvalidVersion() {
        // arrange
        Reader doubleReader = mock(Reader.class);
        List<Genre> validInterestList = new ArrayList<>();
        try (
            MockedConstruction<ReaderNumber> ignored = mockConstruction(ReaderNumber.class);
            MockedConstruction<PhoneNumber> ignored1 = mockConstruction(PhoneNumber.class);
            MockedConstruction<BirthDate> ignored2 = mockConstruction(BirthDate.class)
        ) {
            long targetVersion = 1L;
            long wrongVersion = 2L;
            ReaderDetails readerDetails = new ReaderDetails(
                validReaderNumber,
                doubleReader,
                validBirthDate,
                validPhoneNumber,
                true,
                false,
                false,
                validPhotoUri,
                validInterestList
            );
            readerDetails.setVersion(targetVersion);
            assertNotNull(readerDetails.getPhoto());
            // act + assert
            ConflictException exception = assertThrows(
                ConflictException.class,
                () -> readerDetails.removePhoto(wrongVersion)
            );
            assertEquals(ConflictException.class, exception.getClass());
            assertEquals("Provided version does not match latest version of this object", exception.getMessage());
        }
    }

    /******** Transparent *********/

    @Test
    void ensureApplyPatchHasExpectedBehavior_WhenNotPatchingObjectProperties() {
        // arrange
        try (
            MockedConstruction<BirthDate> mockedBirthDate = mockConstruction(BirthDate.class);
            MockedConstruction<PhoneNumber> mockedPhoneNumber = mockConstruction(PhoneNumber.class);
            MockedConstruction<ReaderNumber> mockedReaderNumber = mockConstruction(ReaderNumber.class)
        ) {
            int expectedReaderNumber = validReaderNumber;
            String expectedBirthDate = validBirthDate;
            String expectedPhoneNumber = validPhoneNumber;
            boolean expectedGdprConsent = true;
            boolean expectedMarketingConsent = false;
            boolean expectedThirdPartySharingConsent = false;
            List<Genre> expectedInterestList = new ArrayList<>();

            Reader doubleReader = mock(Reader.class);
            UpdateReaderRequest doubleRequest = mock(UpdateReaderRequest.class);

            ReaderDetails readerDetails =
                new ReaderDetails(
                    expectedReaderNumber,
                    doubleReader,
                    expectedBirthDate,
                    expectedPhoneNumber,
                    expectedGdprConsent,
                    true,
                    true,
                    null,
                    expectedInterestList
                );
            readerDetails.setVersion(1L);

            BirthDate mockBirthDate = mockedBirthDate.constructed().getFirst();
            when(mockBirthDate.toString()).thenReturn(expectedBirthDate);

            ReaderNumber mockReaderNumber = mockedReaderNumber.constructed().getFirst();
            when(mockReaderNumber.toString()).thenReturn(String.valueOf(expectedReaderNumber));

            PhoneNumber mockPhoneNumber = mockedPhoneNumber.constructed().getFirst();
            when(mockPhoneNumber.toString()).thenReturn(expectedPhoneNumber);

            // act
            readerDetails.applyPatch(1L, doubleRequest, null, null);

            // assert
            verify(doubleRequest).getBirthDate();
            verify(doubleRequest).getPhoneNumber();
            verify(doubleRequest).getMarketing();
            verify(doubleRequest).getThirdParty();
            verify(doubleRequest).getFullName();
            verify(doubleRequest).getUsername();
            verify(doubleRequest).getPassword();
            // assert object structure
            assertEquals(doubleReader, readerDetails.getReader());
            assertEquals(String.valueOf(expectedReaderNumber), readerDetails.getReaderNumber());
            assertEquals(expectedBirthDate, readerDetails.getBirthDate().toString());
            assertEquals(expectedPhoneNumber, readerDetails.getPhoneNumber());
            assertEquals(expectedGdprConsent, readerDetails.isGdprConsent());
            assertEquals(expectedMarketingConsent, readerDetails.isMarketingConsent());
            assertEquals(expectedThirdPartySharingConsent, readerDetails.isThirdPartySharingConsent());
            assertEquals(expectedInterestList, readerDetails.getInterestList());
        }
    }

    @Test
    void ensureApplyPatchHasExpectedBehavior_WhenPatchingObjectProperties() {
        // arrange
        try (
            MockedConstruction<BirthDate> mockedBirthDate = mockConstruction(BirthDate.class);
            MockedConstruction<PhoneNumber> mockedPhoneNumber = mockConstruction(PhoneNumber.class);
            MockedConstruction<ReaderNumber> mockedReaderNumber = mockConstruction(ReaderNumber.class)
        ) {
            List<Genre> validInterestList = new ArrayList<>();

            String expectedBirthDate = validBirthDate;
            String expectedPhoneNumber = validPhoneNumber;
            boolean expectedGdprConsent = true;
            boolean expectedMarketingConsent = false;
            boolean expectedThirdPartySharingConsent = false;
            String expectedPhotoUri = validPhotoUri;
            List<Genre> expectedInterestList = new ArrayList<>();

            String initialBirthDate = "1999-01-01";
            String initialPhoneNumber = "999999999";

            Reader expectedDoubleReader = mock(Reader.class);
            UpdateReaderRequest doubleRequest = mock(UpdateReaderRequest.class);

            ReaderDetails readerDetails =
                new ReaderDetails(
                    validReaderNumber,
                    expectedDoubleReader,
                    initialBirthDate,
                    initialPhoneNumber,
                    true,
                    false,
                    false,
                    null,
                    null
                );
            readerDetails.setVersion(1L);

            ReaderNumber mockReaderNumber = mockedReaderNumber.constructed().getFirst();
            when(mockReaderNumber.toString()).thenReturn(String.valueOf(validReaderNumber));

            when(doubleRequest.getBirthDate()).thenReturn(expectedBirthDate);
            when(doubleRequest.getPhoneNumber()).thenReturn(expectedPhoneNumber);
            when(doubleRequest.getMarketing()).thenReturn(expectedMarketingConsent);
            when(doubleRequest.getThirdParty()).thenReturn(expectedThirdPartySharingConsent);
            when(doubleRequest.getFullName()).thenReturn("Some Name");
            when(doubleRequest.getUsername()).thenReturn("Some Name");
            when(doubleRequest.getPassword()).thenReturn("Some Password");

            // act
            readerDetails.applyPatch(1L, doubleRequest, expectedPhotoUri, validInterestList);

            BirthDate mockBirthDate = mockedBirthDate.constructed().get(1);
            when(mockBirthDate.toString()).thenReturn(expectedBirthDate);

            PhoneNumber mockPhoneNumber = mockedPhoneNumber.constructed().get(1);
            when(mockPhoneNumber.toString()).thenReturn(expectedPhoneNumber);

            // assert
            verify(doubleRequest).getBirthDate();
            verify(doubleRequest).getPhoneNumber();
            verify(doubleRequest).getMarketing();
            verify(doubleRequest).getThirdParty();
            verify(doubleRequest).getFullName();
            verify(doubleRequest).getUsername();
            verify(doubleRequest).getPassword();
            // assert object structure
            assertEquals(expectedDoubleReader, readerDetails.getReader());
            assertEquals(String.valueOf(validReaderNumber), readerDetails.getReaderNumber());
            assertEquals(expectedBirthDate, readerDetails.getBirthDate().toString());
            assertEquals(expectedPhoneNumber, readerDetails.getPhoneNumber());
            assertEquals(expectedGdprConsent, readerDetails.isGdprConsent());
            assertEquals(expectedMarketingConsent, readerDetails.isMarketingConsent());
            assertEquals(expectedThirdPartySharingConsent, readerDetails.isThirdPartySharingConsent());
            assertEquals(expectedInterestList, readerDetails.getInterestList());
            assertNotNull(readerDetails.getPhoto());
            assertEquals(expectedPhotoUri, readerDetails.getPhoto().getPhotoFile());
        }
    }

    @Test
    void ensureApplyPatchThrows_WhenReceivingInvalidVersion() {
        // arrange
        try (
            MockedConstruction<BirthDate> mockedBirthDate = mockConstruction(BirthDate.class);
            MockedConstruction<PhoneNumber> mockedPhoneNumber = mockConstruction(PhoneNumber.class);
            MockedConstruction<ReaderNumber> mockedReaderNumber = mockConstruction(ReaderNumber.class)
        ) {
            int expectedReaderNumber = validReaderNumber;
            String expectedBirthDate = validBirthDate;
            String expectedPhoneNumber = validPhoneNumber;
            boolean expectedGdprConsent = true;
            boolean expectedMarketingConsent = true;
            boolean expectedThirdPartySharingConsent = true;
            List<Genre> expectedInterestList = new ArrayList<>();

            Reader doubleReader = mock(Reader.class);
            UpdateReaderRequest doubleRequest = mock(UpdateReaderRequest.class);

            long targetVersion = 1L;
            long wrongVersion = 2L;
            ReaderDetails readerDetails =
                new ReaderDetails(
                    expectedReaderNumber,
                    doubleReader,
                    expectedBirthDate,
                    expectedPhoneNumber,
                    expectedGdprConsent,
                    expectedMarketingConsent,
                    expectedThirdPartySharingConsent,
                    null,
                    expectedInterestList
                );
            readerDetails.setVersion(targetVersion);

            BirthDate mockBirthDate = mockedBirthDate.constructed().getFirst();
            when(mockBirthDate.toString()).thenReturn(expectedBirthDate);

            ReaderNumber mockReaderNumber = mockedReaderNumber.constructed().getFirst();
            when(mockReaderNumber.toString()).thenReturn(String.valueOf(expectedReaderNumber));

            PhoneNumber mockPhoneNumber = mockedPhoneNumber.constructed().getFirst();
            when(mockPhoneNumber.toString()).thenReturn(expectedPhoneNumber);

            // act
            ConflictException exception = assertThrows(
                ConflictException.class,
                () -> readerDetails.applyPatch(wrongVersion, doubleRequest, null, null)
            );
            assertEquals(ConflictException.class, exception.getClass());
            assertEquals("Provided version does not match latest version of this object", exception.getMessage());

            // assert
            verify(doubleRequest, never()).getBirthDate();
            verify(doubleRequest, never()).getPhoneNumber();
            verify(doubleRequest, never()).getMarketing();
            verify(doubleRequest, never()).getThirdParty();
            verify(doubleRequest, never()).getFullName();
            verify(doubleRequest, never()).getUsername();
            verify(doubleRequest, never()).getPassword();
            // assert object structure
            assertEquals(doubleReader, readerDetails.getReader());
            assertEquals(String.valueOf(expectedReaderNumber), readerDetails.getReaderNumber());
            assertEquals(expectedBirthDate, readerDetails.getBirthDate().toString());
            assertEquals(expectedPhoneNumber, readerDetails.getPhoneNumber());
            assertEquals(expectedGdprConsent, readerDetails.isGdprConsent());
            assertEquals(expectedMarketingConsent, readerDetails.isMarketingConsent());
            assertEquals(expectedThirdPartySharingConsent, readerDetails.isThirdPartySharingConsent());
            assertEquals(expectedInterestList, readerDetails.getInterestList());
        }
    }
}
