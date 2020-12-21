package integration.todo.create;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class BuddyTest {
  @Test
  void lookMomICanMockStaticMethods() {
    assertThat(Buddy.name()).isEqualTo("John");

    try (MockedStatic<Buddy> theMock = Mockito.mockStatic(Buddy.class)) {
      theMock.when(Buddy::name).thenReturn("Rafael");
      assertThat(Buddy.name()).isEqualTo("Rafael");
    }

    assertThat(Buddy.name()).isEqualTo("John");
  }

}