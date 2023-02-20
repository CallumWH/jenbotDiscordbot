package org.example.discord;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.Random;
import java.util.StringJoiner;

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
                musicHandler.playMusic(event.getOption("url", OptionMapping::getAsString), event);
                break;
            case "stop":
                musicHandler.stopMusic();
                event.reply(event.getMember().getAsMention() + " has stopped play and cleared the queue").queue();
                break;
            case "queue":
                event.reply(musicHandler.getMusicQueue()).queue();
                break;
            case "skip":
                musicHandler.skipTrack(event);
                event.reply(event.getMember().getAsMention() + " has skipped the current track").queue();
                break;
            case "repeat":
                event.reply("**REPEAT** " + (musicHandler.toggleRepeat() ? "**ENABLED**" : "**DISABLED**")).queue();
                break;
        }
    }

    private String doFireball(SlashCommandInteractionEvent event) {
        Random random = new Random();
        StringJoiner stringJoiner = new StringJoiner(", ");
        int total = 0;

//        if(event.getMember().getUser().getId().equals("71998253645697024")) {
//            return "GLORIOUS 8d6 FIRE DAMAGE!: 6, 6, 6, 6, 6, 6\n**Total** : 36 fire damage! :fire:\n**NOW THAT'S A FIREBALL! J'EN IS PLEASED!**";
//        }
        for (int i = 0; i < 6; i++) {
            int d6 = random.nextInt(1, 7);
            total += d6;
            stringJoiner.add(new String("" + d6));
        }

        String damageString = new String(stringJoiner.toString() + "\n**Total** : " + total + " fire damage! :fire:");
        if (total < 18) {
            damageString = damageString + "\n*Fucking weak ass fireball*";
        }

        if (total == 36) {
            damageString = damageString + "\n**NOW THAT'S A FIREBALL! J'EN IS PLEASED!**";
        }

        return damageString;
    }


}
