package com.ivanolmo.kanbantaskmanager.repository;

import com.ivanolmo.kanbantaskmanager.entity.Board;
import com.ivanolmo.kanbantaskmanager.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class BoardRepositoryTest {
  @Autowired
  private BoardRepository boardRepository;
  @Autowired
  private TestEntityManager entityManager;
  private User user;
  private Board board1;
  private Board board2;
  private Board boardForId;

  @BeforeEach
  public void setUp() {
    user = User
        .builder()
        .email("test@example.com")
        .password("password")
        .build();
    entityManager.persist(user);

    board1 = Board
        .builder()
        .name("New Board 1")
        .user(user)
        .build();

    board2 = Board
        .builder()
        .name("New Board 2")
        .user(user)
        .build();

    User userForBoardIdTest = User
        .builder()
        .email("test2@example.com")
        .password("password")
        .build();
    entityManager.persist(userForBoardIdTest);

    boardForId = Board
        .builder()
        .name("Board for ID")
        .user(userForBoardIdTest)
        .build();

    boardRepository.save(boardForId);
  }

  @Test
  public void testFindById() {
    assertNotNull(boardForId.getId(), "ID should not be null");
    Optional<Board> foundBoard = boardRepository.findById(boardForId.getId());
    assertTrue(foundBoard.isPresent(), "Board should be found");
    assertEquals(boardForId, foundBoard.get(), "Found board should match the saved board");
  }

  @Test
  public void testFindByUserId() {
    entityManager.persist(board1);
    entityManager.persist(board2);

    List<Board> boards = boardRepository.findByUserId(user.getId()).orElse(Collections.emptyList());
    assertEquals(2, boards.size(), "Found board size should equal persisted boards");
  }
}
