package pt.psoft.g1.psoftg1.bookmanagement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.bookmanagement.services.UpdateBookRequest;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookTest {
    private final String validIsbn = "9782826012092";
    private final String validTitle = "Encantos de contar";
    private final String validDescription = "Descrição";
    private final String validPhotoURI = "photo.jpeg";
    private final Author validAuthor1 = new Author("João Alberto", "O João Alberto nasceu em Chaves e foi pedreiro a maior parte da sua vida.", null);
    private final Author validAuthor2 = new Author("Maria José", "A Maria José nasceu em Viseu e só come laranjas às segundas feiras.", null);
    private final Genre validGenre = new Genre("Fantasia");
    private ArrayList<Author> authors = new ArrayList<>();

    @BeforeEach
    void setUp() {
        authors.clear();
    }

    @Test
    void ensureBookNotNull() {
        try (MockedConstruction<Title> mockTitle = mockConstruction(Title.class);
             MockedConstruction<Isbn> mockIsbn = mockConstruction(Isbn.class);
             MockedConstruction<Genre> mockGenre = mockConstruction(Genre.class);
             MockedConstruction<Description> mockDescription = mockConstruction(Description.class)) {
            Genre doubleGenre = new Genre("Fantasia");
            authors.add(validAuthor1);
            authors.add(validAuthor2);

            Book book = new Book(validIsbn, validTitle, validDescription, doubleGenre, authors, validPhotoURI);

            assertNotNull(book);
        }
    }

    @Test
    void ensureNullBookThrowsException() {
        try (MockedConstruction<Title> mockTitle = mockConstruction(Title.class);
             MockedConstruction<Isbn> mockIsbn = mockConstruction(Isbn.class);
             MockedConstruction<Genre> mockGenre = mockConstruction(Genre.class);
             MockedConstruction<Description> mockDescription = mockConstruction(Description.class)) {

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Book(null, null, null, null, null, null));

            assertEquals(IllegalArgumentException.class, exception.getClass());
        }
    }

    @Test
    void ensureNullGenreThrowsException() {
        try (MockedConstruction<Title> mockTitle = mockConstruction(Title.class);
             MockedConstruction<Isbn> mockIsbn = mockConstruction(Isbn.class);
             MockedConstruction<Genre> mockGenre = mockConstruction(Genre.class);
             MockedConstruction<Description> mockDescription = mockConstruction(Description.class)) {
            authors.add(validAuthor1);
            authors.add(validAuthor2);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Book(validIsbn, validTitle, validDescription, null, authors, validPhotoURI));

            assertEquals(IllegalArgumentException.class, exception.getClass());
            assertEquals("Genre cannot be null", exception.getMessage());
        }
    }

    @Test
    void ensureNullAuthorsThrowsException() {
        try (MockedConstruction<Title> mockTitle = mockConstruction(Title.class);
             MockedConstruction<Isbn> mockIsbn = mockConstruction(Isbn.class);
             MockedConstruction<Genre> mockGenre = mockConstruction(Genre.class);
             MockedConstruction<Description> mockDescription = mockConstruction(Description.class)) {
            Genre doubleGenre = new Genre("Fantasia");

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Book(validIsbn, validTitle, validDescription, doubleGenre, null, validPhotoURI));

            assertEquals(IllegalArgumentException.class, exception.getClass());
            assertEquals("Author list is null", exception.getMessage());
        }
    }

    @Test
    void ensureEmptyAuthorsListThrowsException() {
        try (MockedConstruction<Title> mockTitle = mockConstruction(Title.class);
             MockedConstruction<Isbn> mockIsbn = mockConstruction(Isbn.class);
             MockedConstruction<Genre> mockGenre = mockConstruction(Genre.class);
             MockedConstruction<Description> mockDescription = mockConstruction(Description.class)) {
            Genre doubleGenre = new Genre("Fantasia");

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Book(validIsbn, validTitle, validDescription, doubleGenre, authors, validPhotoURI));

            assertEquals(IllegalArgumentException.class, exception.getClass());
            assertEquals("Author list is empty", exception.getMessage());
        }
    }

    @Test
    void ensureApplyPatchHasExpectedBehavior_WhenNotChangingValidParameters() {
        try (MockedConstruction<Title> mockedTitle = mockConstruction(Title.class);
             MockedConstruction<Isbn> mockedIsbn = mockConstruction(Isbn.class);
             MockedConstruction<Description> mockedDescription = mockConstruction(Description.class)) {

            String expectedIsbn = "9782826012092";
            String expectedTitle = "Encantos de contar";
            String expectedDescription = "Descrição";
            Genre doubleGenre = mock(Genre.class);
            Author expectedAuthor = new Author("Pedro", "Pedro escritor português", null);
            List<Author> expectedAuthorList = new ArrayList<>();
            expectedAuthorList.add(expectedAuthor);
            String expectedPhotoUri = "photo.jpeg";

            UpdateBookRequest doubleRequest = mock(UpdateBookRequest.class);
            Book book =
                    new Book(
                            expectedIsbn,
                            expectedTitle,
                            expectedDescription,
                            doubleGenre,
                            expectedAuthorList,
                            expectedPhotoUri
                    );
            book.setVersion(1L);

            Isbn mockIsbn = mockedIsbn.constructed().getFirst();
            when(mockIsbn.toString()).thenReturn(expectedIsbn);

            Title mockTitle = mockedTitle.constructed().getFirst();
            when(mockTitle.toString()).thenReturn(expectedTitle);

            when(doubleGenre.toString()).thenReturn("Fantasia");

            Description mockDescription = mockedDescription.constructed().getFirst();
            when(mockDescription.toString()).thenReturn(expectedDescription);

            when(doubleRequest.getAuthorObjList()).thenReturn(null);

            book.applyPatch(1L, doubleRequest);

            verify(doubleRequest).getTitle();
            verify(doubleRequest).getDescription();
            verify(doubleRequest).getGenreObj();
            verify(doubleRequest).getAuthorObjList();
            verify(doubleRequest).getPhotoURI();

            assertEquals(doubleGenre, book.getGenre());
            assertEquals(expectedIsbn, book.getIsbn());
            assertEquals(expectedTitle, book.getTitle().toString());
            assertEquals(expectedAuthorList, book.getAuthors());
            assertEquals(expectedDescription, book.getDescription());
            assertEquals(expectedPhotoUri, book.getPhoto().getPhotoFile());
        }
    }

    @Test
    void ensureApplyPatchHasExpectedBehavior_WhenChangingValidParameters() {
        try (MockedConstruction<Title> mockedTitle = mockConstruction(Title.class);
             MockedConstruction<Isbn> mockedIsbn = mockConstruction(Isbn.class);
             MockedConstruction<Description> mockedDescription = mockConstruction(Description.class)) {

            String expectedIsbn = "9782826012092";
            String expectedTitle = "Encantos de contar";
            String expectedDescription = "Descrição";
            Genre doubleGenre = mock(Genre.class);
            Author expectedAuthor = new Author("Pedro", "Pedro escritor português", null);
            List<Author> expectedAuthorList = new ArrayList<>();
            expectedAuthorList.add(expectedAuthor);
            String expectedPhotoUri = "null";

            UpdateBookRequest doubleRequest = mock(UpdateBookRequest.class);
            Book book =
                    new Book(
                            null,
                            null,
                            "",
                            doubleGenre,
                            expectedAuthorList,
                            "null"
                    );
            book.setVersion(1L);

            Isbn mockIsbn = mockedIsbn.constructed().getFirst();
            when(mockIsbn.toString()).thenReturn(expectedIsbn);

            Title mockTitle = mockedTitle.constructed().getFirst();
            when(mockTitle.toString()).thenReturn(expectedTitle);

            when(doubleGenre.toString()).thenReturn("Fantasia");

            Description mockDescription = mockedDescription.constructed().getFirst();
            when(mockDescription.toString()).thenReturn(expectedDescription);

            when(doubleRequest.getAuthorObjList()).thenReturn(null);

            book.applyPatch(1L, doubleRequest);

            verify(doubleRequest).getTitle();
            verify(doubleRequest).getDescription();
            verify(doubleRequest).getGenreObj();
            verify(doubleRequest).getAuthorObjList();
            verify(doubleRequest).getPhotoURI();

            assertEquals(doubleGenre, book.getGenre());
            assertEquals(expectedIsbn, book.getIsbn());
            assertEquals(expectedTitle, book.getTitle().toString());
            assertEquals(expectedAuthorList, book.getAuthors());
            assertEquals(expectedDescription, book.getDescription());
            assertEquals(expectedPhotoUri, book.getPhoto().getPhotoFile());
        }
    }
}