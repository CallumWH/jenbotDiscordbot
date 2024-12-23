package org.example.features;

import org.junit.jupiter.api.Test;

import static org.example.features.JenRoller.*;
import static org.junit.jupiter.api.Assertions.*;

class JenRollerTest {
  JenRoller jenRoller = new JenRoller();

  @Test
  void rollWildMagic() {
    String wildMagic = jenRoller.rollWildMagic();
    assertNotNull(wildMagic);
  }

  @Test
  void rollDice() {
    String rollDice = jenRoller.rollDice("2d6 + 4");
    System.out.println(rollDice);
    assertNotEquals(BAD_DICE_ROLL_STATEMENT, rollDice);
  }
}
