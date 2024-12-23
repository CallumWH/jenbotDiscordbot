package org.example.discord;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.example.features.JenRoller;
import org.example.model.exceptions.UserNotInVoiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscordListener extends ListenerAdapter {

  public static final String BOT_TOKEN = "BOT TOKEN HERE";
  private final MusicHandler musicHandler;
  private static final Logger logger = LoggerFactory.getLogger(DiscordListener.class);
  private JenRoller jenRoller = new JenRoller();

  public DiscordListener() {
    musicHandler = new MusicHandler();
  }

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    // make sure we handle the right command
    switch (event.getName()) {
      case "ping":
        long time = System.currentTimeMillis();
        event
            .reply("Pong!")
            .setEphemeral(true) // reply or acknowledge
            .flatMap(
                v ->
                    event
                        .getHook()
                        .editOriginalFormat(
                            "Pong: %d ms", System.currentTimeMillis() - time) // then edit original
                )
            .queue(); // Queue both reply and edit
        break;
      case "castfireball":
        event
            .reply(String.format("GLORIOUS 8d6 FIRE DAMAGE!: %s", jenRoller.doFireball(event)))
            .setEphemeral(false)
            .queue();
        break;
      case "drow":
        event.reply("<@72012326802300928> ILLIDAAAAAAAAAAAN!!!!").queue();
        break;
      case "play":
        String url = event.getOption("url", OptionMapping::getAsString);
        if (null == url) {
          event.reply("You need to send a url dummy!").queue();
          break;
        }
        try {
          musicHandler.playMusic(url, event, false);
        } catch (UserNotInVoiceException e) {
          event
              .reply(
                  "HEY " + event.getMember().getAsMention() + " YOU NEED TO JOIN VOICE TO DO THAT!")
              .queue();
          break;
        }
        break;
      case "stop":
        try {
          musicHandler.stopMusic(event);
        } catch (UserNotInVoiceException e) {
          event
              .reply(
                  "HEY " + event.getMember().getAsMention() + " YOU NEED TO JOIN VOICE TO DO THAT!")
              .queue();
          break;
        }
        event
            .reply(event.getMember().getAsMention() + " has stopped play and cleared the queue")
            .queue();
        break;
      case "queue":
        try {
          event
              .reply(musicHandler.getMusicQueue(event.getOption("page", OptionMapping::getAsInt)))
              .queue();
        } catch (NullPointerException | ArithmeticException e) {
          event.reply(musicHandler.getMusicQueue(1)).queue();
        }

        break;
      case "skip":
        try {
          musicHandler.skipTrack(event);
        } catch (UserNotInVoiceException e) {
          event
              .reply(
                  "HEY " + event.getMember().getAsMention() + " YOU NEED TO JOIN VOICE TO DO THAT!")
              .queue();
          break;
        }
        event.reply(event.getMember().getAsMention() + " has skipped the current track").queue();
        break;
      case "repeat":
        event
            .reply("**REPEAT** " + (musicHandler.toggleRepeat() ? "**ENABLED**" : "**DISABLED**"))
            .queue();
        break;
      case "roll":
        event
            .reply(jenRoller.rollDice(event.getOption("dice", OptionMapping::getAsString)))
            .queue();
        break;
      case "leave":
        try {
          musicHandler.stopMusic(event);
          musicHandler.leaveVoice();
          event.reply("bye!").queue();
          break;
        } catch (UserNotInVoiceException e) {
          event
              .reply(
                  "HEY " + event.getMember().getAsMention() + " YOU NEED TO JOIN VOICE TO DO THAT!")
              .queue();
          break;
        }
      case "join":
        event
            .reply("Joined : " + musicHandler.joinVoice(event))
            .setEphemeral(true)
            .queue(null, e -> System.out.println(e.getLocalizedMessage()));
        break;
      case "shuffle":
        musicHandler.shuffle();
        event.reply("***DOES THE SHUFFLE***").setEphemeral(false).queue();
      case "wildmagic":
        event.reply(jenRoller.rollWildMagic()).queue();
      case "jumpqueue":
        url = event.getOption("url", OptionMapping::getAsString);
        if (null == url) {
          event.reply("You need to send a url dummy!").queue();
          break;
        }
        try {
          musicHandler.playMusic(url, event, true);
        } catch (UserNotInVoiceException e) {
          event
              .reply(
                  "HEY " + event.getMember().getAsMention() + " YOU NEED TO JOIN VOICE TO DO THAT!")
              .queue();
          break;
        }
        break;
    }
  }

  @Override
  public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
    musicHandler.checkChannelPopulation(event);
  }
}
