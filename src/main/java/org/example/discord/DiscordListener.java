package org.example.discord;

import com.bernardomg.tabletop.dice.history.RollHistory;
import com.bernardomg.tabletop.dice.interpreter.DiceInterpreter;
import com.bernardomg.tabletop.dice.interpreter.DiceRoller;
import com.bernardomg.tabletop.dice.parser.DefaultDiceParser;
import com.bernardomg.tabletop.dice.parser.DiceParser;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.example.model.exceptions.UserNotInVoiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.StringJoiner;

public class DiscordListener extends ListenerAdapter {

    private MusicHandler musicHandler;
    private static final Logger logger = LoggerFactory.getLogger(DiscordListener.class);
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
                if (null == url) {
                    event.reply("You need to send a url dummy!").queue();
                    break;
                }
                try {
                    musicHandler.playMusic(url, event, false);
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
            case "wildmagic":
                event.reply(wildMagic()).queue();
            case "jumpqueue":
                url = event.getOption("url", OptionMapping::getAsString);
                if (null == url) {
                    event.reply("You need to send a url dummy!").queue();
                    break;
                }
                try {
                    musicHandler.playMusic(url, event, true);
                } catch (UserNotInVoiceException e) {
                    event.reply("HEY " + event.getMember().getAsMention() + " YOU NEED TO JOIN VOICE TO DO THAT!").queue();
                    break;
                }
                break;
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

        if (event.getMember().getUser().getId().equals("71998253645697024") && total < 38) {
            damageString = doFireball(event);
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

    public String wildMagic() {

        Random random = new Random();
        int roll = random.nextInt(1, 101);

        switch (roll) {
            case 1, 2:
                return "01-02 \tRoll on this table at the start of each of your turns for the next minute, ignoring this result on subsequent rolls.";
            case 3, 4:
                return "03-04 \tFor the next minute, you can see any invisible creature if you have line of sight to it.";
            case 5, 6:
                return "05-06 \tA modron chosen and controlled by the DM appears in an unoccupied space within 5 feet of you, then disappears I minute later.";
            case 7, 8:
                return "07-08 \tYou cast Fireball as a 3rd-level spell centered on yourself.";
            case 9, 10:
                return "09-10 \tYou cast Magic Missile as a 5th-level spell.";
            case 11, 12:
                return "11-12 \tRoll a d10. Your height changes by a number of inches equal to the roll. If the roll is odd, you shrink. If the roll is even, you grow.";
            case 13, 14:
                return "13-14 \tYou cast Confusion centered on yourself.";
            case 15, 16:
                return "15-16 \tFor the next minute, you regain 5 hit points at the start of each of your turns.";
            case 17, 18:
                return "17-18 \tYou grow a long beard made of feathers that remains until you sneeze, at which point the feathers explode out from your face.";
            case 19, 20:
                return "19-20 \tYou cast Grease centered on yourself.";
            case 21, 22:
                return "21-22 \tCreatures have disadvantage on saving throws against the next spell you cast in the next minute that involves a saving throw.";
            case 23, 24:
                return "23-24 \tYour skin turns a vibrant shade of blue. A Remove Curse spell can end this effect.";
            case 25, 26:
                return "25-26 \tAn eye appears on your forehead for the next minute. During that time, you have advantage on Wisdom (Perception) checks that rely on sight.";
            case 27, 28:
                return "27-28 \tFor the next minute, all your spells with a casting time of 1 action have a casting time of 1 bonus action.";
            case 29, 30:
                return "29-30 \tYou teleport up to 60 feet to an unoccupied space of your choice that you can see.";
            case 31, 32:
                return "31-32 \tYou are transported to the Astral Plane until the end of your next turn, after which time you return to the space you previously occupied or the nearest unoccupied space if that space is occupied.";
            case 33, 34:
                return "33-34 \tMaximize the damage of the next damaging spell you cast within the next minute.";
            case 35, 36:
                return "35-36 \tRoll a d10. Your age changes by a number of years equal to the roll. If the roll is odd, you get younger (minimum 1 year old). If the roll is even, you get older.";
            case 37, 38:
                return "37-38 \t1d6 flumphs controlled by the DM appear in unoccupied spaces within 60 feet of you and are frightened of you. They vanish after 1 minute.";
            case 39, 40:
                return "39-40 \tYou regain 2d10 hit points.";
            case 41, 42:
                return "41-42 \tYou turn into a potted plant until the start of your next turn. While a plant, you are incapacitated and have vulnerability to all damage. If you drop to 0 hit points, your pot breaks, and your form reverts.";
            case 43, 44:
                return "43-44 \tFor the next minute, you can teleport up to 20 feet as a bonus action on each of your turns.";
            case 45, 46:
                return "45-46 \tYou cast Levitate on yourself.";
            case 47, 48:
                return "47-48 \tA unicorn controlled by the DM appears in a space within 5 feet of you, then disappears 1 minute later.";
            case 49, 50:
                return "49-50 \tYou can't speak for the next minute. Whenever you try, pink bubbles float out of your mouth.";
            case 51, 52:
                return "51-52 \tA spectral shield hovers near you for the next minute, granting you a +2 bonus to AC and immunity to Magic Missile.";
            case 53, 54:
                return "53-54 \tYou are immune to being intoxicated by alcohol for the next 5d6 days.";
            case 55, 56:
                return "55-56 \tYour hair falls out but grows back within 24 hours.";
            case 57, 58:
                return "57-58 \tFor the next minute, any flammable object you touch that isn't being worn or carried by another creature bursts into flame.";
            case 59, 60:
                return "59-60 \tYou regain your lowest-level expended spell slot.";
            case 61, 62:
                return "61-62 \tFor the next minute, you must shout when you speak.";
            case 63, 64:
                return "63-64 \tYou cast Fog Cloud centered on yourself.";
            case 65, 66:
                return "65-66 \tUp to three creatures you choose within 30 feet of you take 4d10 lightning damage.";
            case 67, 68:
                return "67-68 \tYou are frightened by the nearest creature until the end of your next turn.";
            case 69, 70:
                return "69-70 \tEach creature within 30 feet of you becomes invisible for the next minute. The invisibility ends on a creature when it attacks or casts a spell.";
            case 71, 72:
                return "71-72 \tYou gain resistance to all damage for the next minute.";
            case 73, 74:
                return "73-74 \tA random creature within 60 feet of you becomes poisoned for 1d4 hours.";
            case 75, 76:
                return "75-76 \tYou glow with bright light in a 30-foot radius for the next minute. Any creature that ends its turn within 5 feet of you is blinded until the end of its next turn.";
            case 77, 78:
                return "77-78 \tYou cast Polymorph on yourself. If you fail the saving throw, you turn into a sheep for the spell's duration.";
            case 79, 80:
                return "79-80 \tIllusory butterflies and flower petals flutter in the air within 10 feet of you for the next minute.";
            case 81, 82:
                return "81-82 \tYou can take one additional action immediately.";
            case 83, 84:
                return "83-84 \tEach creature within 30 feet of you takes 1d10 necrotic damage. You regain hit points equal to the sum of the necrotic damage dealt.";
            case 85, 86:
                return "85-86 \tYou cast Mirror Image.";
            case 87, 88:
                return "87-88 \tYou cast Fly on a random creature within 60 feet of you.";
            case 89, 90:
                return "89-90 \tYou become invisible for the next minute. During that time, other creatures can't hear you. The invisibility ends if you attack or cast a spell.";
            case 91, 92:
                return "91-92 \tIf you die within the next minute, you immediately come back to life as if by the Reincarnate spell.";
            case 93, 94:
                return "93-94 \tYour size increases by one size category for the next minute.";
            case 95, 96:
                return "95-96 \tYou and all creatures within 30 feet of you gain vulnerability to piercing damage for the next minute.";
            case 97, 98:
                return "97-98 \tYou are surrounded by faint, ethereal music for the next minute.";
            case 99, 100:
                return "99-00 \tYou regain all expended sorcery points.";
            default:
                return "something fucked happened :" + roll;
        }

    }
}
