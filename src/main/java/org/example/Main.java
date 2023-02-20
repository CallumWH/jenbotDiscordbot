package org.example;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.example.discord.DiscordListener;

import static org.example.discord.DiscordListener.BOT_TOKEN;


public class Main {
    public static void main(String[] args) {
        // We don't need any intents for this bot. Slash commands work without any intents!
        JDA jda = JDABuilder.createDefault(BOT_TOKEN)
                .addEventListeners(new DiscordListener())
                .setActivity(Activity.playing("with Veldris"))
                .build();

        // Sets the global command list to the provided commands (removing all others)
        jda.updateCommands().addCommands(
                Commands.slash("castfireball", "drop the sun on some poor fucker"),
                Commands.slash("drow", "Summon the drow!"),
                Commands.slash("play", "play music").addOption(OptionType.STRING, "url", "youtube url"),
                Commands.slash("stop", "shut Jen up"),
                Commands.slash("queue", "get the current music queue"),
                Commands.slash("skip", "skip current track"),
                Commands.slash("repeat", "enable repeat mode")
        ).queue();
    }

}