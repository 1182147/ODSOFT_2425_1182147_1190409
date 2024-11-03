package pt.psoft.g1.psoftg1.genremanagement.IT;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import pt.psoft.g1.psoftg1.bookmanagement.services.GenreBookCountDTO;
import pt.psoft.g1.psoftg1.exceptions.NotFoundException;
import pt.psoft.g1.psoftg1.genremanagement.api.GenreBookCountView;
import pt.psoft.g1.psoftg1.genremanagement.api.GenreController;
import pt.psoft.g1.psoftg1.genremanagement.api.GenreViewMapperImpl;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.repositories.GenreRepository;
import pt.psoft.g1.psoftg1.genremanagement.services.GenreService;
import pt.psoft.g1.psoftg1.genremanagement.services.GenreServiceImpl;
import pt.psoft.g1.psoftg1.shared.api.ListResponse;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("bootstrap")
public class GenreControllerIT {

    private MockMvc mockMvc;

    private GenreViewMapperImpl genreViewMapper;

    private GenreService genreService;

    private GenreController genreController;

    @Mock
    private GenreRepository genreRepository;

    private String genreString;

    private Genre genre;

    private List<GenreBookCountDTO> genreBookCountDTOList;

    @Autowired
    public GenreControllerIT(MockMvc mockMvc) {
      this.mockMvc = mockMvc;
    }

    @BeforeEach
    public void setUp() {
      MockitoAnnotations.openMocks(this);

      genreViewMapper = new GenreViewMapperImpl();
      genreService = new GenreServiceImpl(genreRepository);
      genreController = new GenreController(genreService, genreViewMapper);

      genreString = "Lord of the Rings";
      genre = new Genre(genreString);

      genreBookCountDTOList = new ArrayList<>();
      genreBookCountDTOList.add(new GenreBookCountDTO(genreString, 20));

      Pageable pageable = PageRequest.of(0, 5);
      PageImpl<GenreBookCountDTO> page = new PageImpl<>(genreBookCountDTOList, pageable, genreBookCountDTOList.size());

      when(genreRepository.findTop5GenreByBookCount(pageable)).thenReturn(page);
    }

    @Test
    void whenGettingTopWithContent_shouldReturnExpectedListSize() {
        // act
        ListResponse<GenreBookCountView> result = genreController.getTop();
        // assert
        Assertions.assertEquals(genreBookCountDTOList.size(), result.getItems().size());
        Assertions.assertEquals(result.getItems().getFirst().getGenreView().getGenre(), genreString);
    }

//    @Test
//    void whenGettingTopWithoutContent_shouldThrowNotFoundException() {
//        // arrange
//        Pageable badPageable = PageRequest.of(0, 1);
//        PageImpl<GenreBookCountDTO> badPage = new PageImpl<>(new ArrayList<GenreBookCountDTO>(), badPageable, genreBookCountDTOList.size());
//
//        when(genreRepository.findTop5GenreByBookCount(badPageable)).thenReturn(badPage);
//        // act
//        NotFoundException exception = Assertions.assertThrows(
//            NotFoundException.class,
//            () -> genreController.getTop()
//        );
//        Assertions.assertEquals(NotFoundException.class, exception.getClass());
//        Assertions.assertEquals(exception.getMessage(), "Genre not found");
//    }
}
