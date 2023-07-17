package org.example;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.example.discord.DiscordListener;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

import static org.example.discord.DiscordListener.BOT_TOKEN;


public class Main {
    public static void main(String[] args) {
        // We don't need any intents for this bot. Slash commands work without any intents!
        JDA jda = JDABuilder.createDefault(BOT_TOKEN)
                .addEventListeners(new DiscordListener())
                .setActivity(Activity.playing("Where the hell is J'arven?"))
                .build();

        // Sets the global command list to the provided commands (removing all others)
        jda.updateCommands().addCommands(
                Commands.slash("castfireball", "drop the sun on some poor fucker"),
                Commands.slash("drow", "Summon the drow!"),
                Commands.slash("play", "play music").addOption(OptionType.STRING, "url", "youtube url"),
                Commands.slash("stop", "shut Jen up"),
                Commands.slash("queue", "get the current music queue").addOption(OptionType.INTEGER, "page", "queue page number"),
                Commands.slash("skip", "skip current track"),
                Commands.slash("repeat", "enable repeat mode"),
                Commands.slash("roll", "ROLL THE BONES AND DECIDE THY FATE").addOption(OptionType.STRING, "dice", "Dice to roll, supports #d# + #. modifier expressions (8d6!, 8d6kh2, 8d6d2 etc) not supported"),
                Commands.slash("leave", "BEGONE CAT"),
                Commands.slash("join", "Summon the cat"),
                Commands.slash("shuffle", "do the shuffle, do be do do do do...")
        ).queue();

        Collection<Integer> numbers = Arrays.asList(1, 2, 1, 3);
        for (int number : findUniqueNumbers(numbers))
            System.out.println(number);
    }

    public static Collection<Integer> findUniqueNumbers(Collection<Integer> numbers){
        return numbers.stream().distinct().collect(Collectors.toCollection(LinkedList::new));
    }

}