package com.ivanolmo.kanbantaskmanager.repository;

import com.ivanolmo.kanbantaskmanager.entity.Board;
import com.ivanolmo.kanbantaskmanager.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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

  @BeforeEach
  public void setUp() {
    setupMockSecurityContext();
    user = createUser();
    board1 = createBoard("Test Board 1", user);
    createBoard("Test Board 2", user);
  }

  @Test
  public void testFindAllByUserId() {
    List<Board> boards = boardRepository.findAllByUserId(user.getId()).orElse(Collections.emptyList());
    assertEquals(2, boards.size(), "Found board size should equal persisted boards");
  }

  @Test
  public void testFindByIdAndUserId() {
    Optional<Board> foundBoard = boardRepository.findByIdAndUserId(board1.getId(), user.getId());
    assertTrue(foundBoard.isPresent(), "Board should be found");
    assertEquals(board1.getId(), foundBoard.get().getId(), "Found board id should match persisted" +
        " board id");
  }

  @Test
  public void testExistsByIdAndUserId() {
    boolean exists = boardRepository.existsByIdAndUserId(board1.getId(), user.getId());
    assertTrue(exists, "Board should exist");
  }

  @Test
  public void testDeleteByIdAndUserId() {
    boardRepository.deleteByIdAndUserId(board1.getId(), user.getId());
    entityManager.flush();  // Ensure delete operation is completed
    Optional<Board> foundBoard = boardRepository.findByIdAndUserId(board1.getId(), user.getId());
    assertFalse(foundBoard.isPresent(), "Board should be deleted");
  }

  @Test
  public void testFindByIdAndUserId_BoardNotFound() {
    Optional<Board> foundBoard = boardRepository.findByIdAndUserId("nonexistent-id", user.getId());
    assertFalse(foundBoard.isPresent(), "Board should not be found");
  }

  @Test
  public void testExistsByIdAndUserId_BoardNotFound() {
    boolean exists = boardRepository.existsByIdAndUserId("nonexistent-id", user.getId());
    assertFalse(exists, "Board should not exist");
  }

  @Test
  public void testDeleteByIdAndUserId_BoardNotFound() {
    boardRepository.deleteByIdAndUserId("nonexistent-id", user.getId());
    entityManager.flush();
    boolean exists = boardRepository.existsByIdAndUserId("nonexistent-id", user.getId());
    assertFalse(exists, "Board should not exist");
  }

  @Test
  public void testFindByNameAndUserId() {
    Optional<Board> foundBoard = boardRepository.findByNameAndUserId("Test Board 1", user.getId());

    assertTrue(foundBoard.isPresent(), "Board should be found");
    assertEquals(board1.getId(), foundBoard.get().getId(), "Found board ID should match expected");
  }

  private void setupMockSecurityContext() {
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Authentication authentication = Mockito.mock(Authentication.class);
    SecurityContextHolder.setContext(securityContext);
    Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
    Mockito.when(authentication.getName()).thenReturn("user@example.com");
  }

  private User createUser() {
    User user = User.builder().email("user@example.com").password("password").build();
    entityManager.persistAndFlush(user);
    return user;
  }

  private Board createBoard(String name, User user) {
    Board board = Board.builder().name(name).user(user).build();
    entityManager.persistAndFlush(board);
    return board;
  }
}
