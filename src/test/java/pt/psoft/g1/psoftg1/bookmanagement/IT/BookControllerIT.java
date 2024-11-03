package pt.psoft.g1.psoftg1.bookmanagement.IT;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.repositories.AuthorRepository;
import pt.psoft.g1.psoftg1.bookmanagement.api.BookController;
import pt.psoft.g1.psoftg1.bookmanagement.api.BookCountView;
import pt.psoft.g1.psoftg1.bookmanagement.api.BookView;
import pt.psoft.g1.psoftg1.bookmanagement.api.BookViewMapperImpl;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookRepository;
import pt.psoft.g1.psoftg1.bookmanagement.services.*;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.repositories.GenreRepository;
import pt.psoft.g1.psoftg1.lendingmanagement.services.LendingService;
import pt.psoft.g1.psoftg1.readermanagement.repositories.ReaderRepository;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderService;
import pt.psoft.g1.psoftg1.shared.repositories.PhotoRepository;
import pt.psoft.g1.psoftg1.shared.services.ConcurrencyService;
import pt.psoft.g1.psoftg1.shared.services.FileStorageService;
import pt.psoft.g1.psoftg1.usermanagement.services.UserService;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("bootstrap")
public class BookControllerIT {

    private BookController bookController;
    private BookService bookService;

    @Mock
    private LendingService lendingService;
    @Mock
    private ConcurrencyService concurrencyService;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private UserService userService;
    @Mock
    private ReaderService readerService;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private GenreRepository genreRepository;
    @Mock
    private AuthorRepository authorRepository;
    @Mock
    private PhotoRepository photoRepository;
    @Mock
    private ReaderRepository readerRepository;

    private Book book;
    private Genre genre;
    private List<Author> authors;
    private String isbn = "9780531359464";

    @BeforeEach
    public void setUp() {
        BookViewMapperImpl bookViewMapper = new BookViewMapperImpl();
        bookService = new BookServiceImpl(bookRepository, genreRepository, authorRepository, photoRepository, readerRepository);
        bookController = new BookController(bookService, lendingService, concurrencyService, fileStorageService, userService, readerService, bookViewMapper);

        Author author = spy((new Author("João Alberto", "O João Alberto nasceu em Chaves e foi pedreiro a maior parte da sua vida.", null)));
        authors = List.of(author);

        genre = new Genre("Fantasia");
        book = spy(new Book(isbn, "Encantos de contar 1", "Descrição 1", genre, authors, "photo.jpeg"));
        Book book2 = new Book("9782206710204", "Encantos de contar 2", "Descrição 2", genre, authors, "photo.jpeg");
        Book book3 = new Book("9784074890866", "Encantos de contar 3", "Descrição 3", genre, authors, "photo.jpeg");
        Book book4 = new Book("9782449497900", "Encantos de contar 4", "Descrição 4", genre, authors, "photo.jpeg");
        Book book5 = new Book("9782490380596", "Encantos de contar 5", "Descrição 5", genre, authors, "photo.jpeg");

        List<BookCountDTO> bookCountDTOs = new ArrayList<>();

        bookCountDTOs.add(new BookCountDTO(book, 1));
        bookCountDTOs.add(new BookCountDTO(book2, 2));
        bookCountDTOs.add(new BookCountDTO(book3, 3));
        bookCountDTOs.add(new BookCountDTO(book4, 4));
        bookCountDTOs.add(new BookCountDTO(book5, 5));

        Pageable pageable = PageRequest.of(0, 5);
        PageImpl<BookCountDTO> page = new PageImpl<>(bookCountDTOs, pageable, bookCountDTOs.size());

        when(bookRepository.findTop5BooksLent(any(LocalDate.class), any(Pageable.class))).thenReturn(page);
        when(author.getAuthorNumber()).thenReturn(1L);

        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(book));
        when(book.getVersion()).thenReturn(1L);
    }

    @Test
    void ensureItFindsBookByISBN() {
        BookView book = bookController.findByIsbn(isbn).getBody();
        assertNotNull(book);
    }

    @Test
    void ensureItReturnsTop5Books() {
        List<BookCountView> books = bookController.getTop5BooksLent().getItems();
        assertNotNull(books);
        assertEquals(5, books.size());
    }
}
