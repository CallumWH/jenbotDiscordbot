package org.example.discord;

import com.bernardomg.tabletop.dice.history.RollHistory;
import com.bernardomg.tabletop.dice.interpreter.DiceInterpreter;
import com.bernardomg.tabletop.dice.interpreter.DiceRoller;
import com.bernardomg.tabletop.dice.parser.DefaultDiceParser;
import com.bernardomg.tabletop.dice.parser.DiceParser;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.example.model.exceptions.UserNotInVoiceException;

import java.util.Random;
import java.util.StringJoiner;

import static org.example.discord.MusicHandler.BOT_ID;

public class DiscordListener extends ListenerAdapter {

    public static final String BOT_TOKEN = "MTA3NzAwNTcxNzk5NjgzODkxMg.GQVngh.2aI2UeKg0xLt_6zTIAiSqz4vIGytZh1XJcAYMo";
    private MusicHandler musicHandler;

    public DiscordListener() {
        musicHandler = new MusicHandler();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        // make sure we handle the right command
        switch (event.getName()) {
            case "ping":
                long time = System.currentTimeMillis();
                event.reply("Pong!").setEphemeral(true) // reply or acknowledge
                        .flatMap(v ->
                                event.getHook().editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time) // then edit original
                        ).queue(); // Queue both reply and edit
                break;
            case "castfireball":
                event.reply(String.format("GLORIOUS 8d6 FIRE DAMAGE!: %s", doFireball(event))).setEphemeral(false).queue();
                break;
            case "drow":
                event.reply("<@72012326802300928> ILLIDAAAAAAAAAAAN!!!!").queue();
                break;
            case "play":
                String url = event.getOption("url", OptionMapping::getAsString);
                if(null == url) {
                    event.reply("You need to send a url dummy!").queue();
                    break;
                }
                try {
                    musicHandler.playMusic(url, event);
                } catch (UserNotInVoiceException e) {
                    event.reply("HEY " + event.getMember().getAsMention() + " YOU NEED TO JOIN VOICE TO DO THAT!").queue();
                    break;
                }
                break;
            case "stop":
                try {
                    musicHandler.stopMusic(event);
                } catch (UserNotInVoiceException e) {
                    event.reply("HEY " + event.getMember().getAsMention() + " YOU NEED TO JOIN VOICE TO DO THAT!").queue();
                    break;
                }
                event.reply(event.getMember().getAsMention() + " has stopped play and cleared the queue").queue();
                break;
            case "queue":

                try {
                    event.reply(musicHandler.getMusicQueue(event.getOption("page", OptionMapping::getAsInt))).queue();
                } catch (NullPointerException | ArithmeticException e) {
                    event.reply(musicHandler.getMusicQueue(1)).queue();
                }

                break;
            case "skip":
                try {
                    musicHandler.skipTrack(event);
                } catch (UserNotInVoiceException e) {
                    event.reply("HEY " + event.getMember().getAsMention() + " YOU NEED TO JOIN VOICE TO DO THAT!").queue();
                    break;
                }
                event.reply(event.getMember().getAsMention() + " has skipped the current track").queue();
                break;
            case "repeat":
                event.reply("**REPEAT** " + (musicHandler.toggleRepeat() ? "**ENABLED**" : "**DISABLED**")).queue();
                break;
            case "roll":
                event.reply(diceRoller(event.getOption("dice", OptionMapping::getAsString))).queue();
                break;
            case "leave":
                try {
                    musicHandler.stopMusic(event);
                    musicHandler.leaveVoice();
                    event.reply("bye!").queue();
                    break;
                } catch (UserNotInVoiceException e) {
                    event.reply("HEY " + event.getMember().getAsMention() + " YOU NEED TO JOIN VOICE TO DO THAT!").queue();
                    break;
                }
            case "join":
                event.reply("Joined : " + musicHandler.joinVoice(event)).setEphemeral(true).queue(null, e -> System.out.println(e.getLocalizedMessage()));
                break;
            case "shuffle":
                musicHandler.shuffle();
                event.reply("***DOES THE SHUFFLE***").setEphemeral(false).queue();
        }
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        musicHandler.checkChannelPopulation(event);
    }

    private String doFireball(SlashCommandInteractionEvent event) {
        Random random = new Random();
        StringJoiner stringJoiner = new StringJoiner(", ");
        int total = 0;

//        if (event.getMember().getUser().getId().equals("71998253645697024")) {
//            return "6, 6, 6, 6, 6, 6, 6, 6\n**Total** : 48 fire damage! :fire:\n**NOW THAT'S A FIREBALL! J'EN IS PLEASED!**";
//        }
        for (int i = 0; i < 8; i++) {
            int d6 = random.nextInt(1, 7);
            total += d6;
            stringJoiner.add(new String("" + d6));
        }

        String damageString = new String(stringJoiner.toString() + "\n**Total** : " + total + " fire damage! :fire:");
        if (total < 18) {
            damageString = damageString + "\n*Fucking weak ass fireball*";
        }

        if (total >= 36) {
            damageString = damageString + "\n**NOW THAT'S A FIREBALL! J'EN IS PLEASED!**";
        }

        return damageString;
    }

    public String diceRoller(String diceCommand) {
        final DiceParser parser;
        final RollHistory rolls;
        final DiceInterpreter<RollHistory> roller;

        diceCommand = diceCommand.replaceAll("\\s+", "");

        parser = new DefaultDiceParser();
        roller = new DiceRoller();

        try {
            rolls = parser.parse(diceCommand, roller);
        } catch (Exception e) {
            return "Heeeeeeey, keep it simple >:(";
        }


        return new String("**Rolling! : " + diceCommand + "**\n" + rolls.toString() + "\nTotal : " + rolls.getTotalRoll());
    }
}
