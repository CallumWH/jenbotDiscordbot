import org.example.discord.DiscordListener;
import org.junit.jupiter.api.Test;

public class MainTest {

    @Test
    public void runProgram() {
        DiscordListener discordListener = new DiscordListener();
        System.out.println(discordListener.diceRoller("asdasd8d6 + 5 + 1d20"));
    }
}
