package org.example.features;

import com.bernardomg.tabletop.dice.history.RollHistory;
import com.bernardomg.tabletop.dice.interpreter.DiceInterpreter;
import com.bernardomg.tabletop.dice.interpreter.DiceRoller;
import com.bernardomg.tabletop.dice.parser.DefaultDiceParser;
import com.bernardomg.tabletop.dice.parser.DiceParser;
import lombok.Data;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Random;
import java.util.StringJoiner;

import static org.example.features.WildMagic.WILD_MAGIC;

@Data
public class JenRoller {
  public static final Random RANDOM = new Random();
  public static final String BAD_DICE_ROLL_STATEMENT = "Heeeeeeey, keep it simple >:(";

  public String rollWildMagic() {
    int roll = RANDOM.nextInt(0, WILD_MAGIC.table.size());
    return WILD_MAGIC.table.get(roll);
  }

  public String rollDice(String diceCommand) {
    final DiceParser parser;
    final RollHistory rolls;
    final DiceInterpreter<RollHistory> roller;

    diceCommand = diceCommand.replaceAll("\\s+", "");

    parser = new DefaultDiceParser();
    roller = new DiceRoller();

    try {
      rolls = parser.parse(diceCommand, roller);
    } catch (Exception e) {
      return BAD_DICE_ROLL_STATEMENT;
    }
    return "**Rolling! : "
        + diceCommand
        + "**\n"
        + rolls.toString()
        + "\nTotal : "
        + rolls.getTotalRoll();
  }

  public String doFireball(SlashCommandInteractionEvent event) {
    StringJoiner stringJoiner = new StringJoiner(", ");
    int total = 0;

    for (int i = 0; i < 8; i++) {
      int d6 = RANDOM.nextInt(1, 7);
      total += d6;
      stringJoiner.add("" + d6);
    }

    String damageString =
            stringJoiner + "\n**Total** : " + total + " fire damage! :fire:";
    if (total < 18) {
      damageString = damageString + "\n*Fucking weak ass fireball*";
    }

    if (total >= 36) {
      damageString = damageString + "\n**NOW THAT'S A FIREBALL! J'EN IS PLEASED!**";
    }

    if (event.getMember().getUser().getId().equals("71998253645697024") && total > 38) {
      damageString = doFireball(event);
    }

    return damageString;
  }
}
