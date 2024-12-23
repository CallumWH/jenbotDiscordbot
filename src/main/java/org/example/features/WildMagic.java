package org.example.features;

import java.util.List;

public enum WildMagic {
  WILD_MAGIC(
      List.of(
          "01-02 \tRoll on this table at the start of each of your turns for the next minute, ignoring this result on subsequent rolls.",
          "03-04 \tFor the next minute, you can see any invisible creature if you have line of sight to it.",
          "05-06 \tA modron chosen and controlled by the DM appears in an unoccupied space within 5 feet of you, then disappears I minute later.",
          "07-08 \tYou cast Fireball as a 3rd-level spell centered on yourself.",
          "09-10 \tYou cast Magic Missile as a 5th-level spell.",
          "11-12 \tRoll a d10. Your height changes by a number of inches equal to the roll. If the roll is odd, you shrink. If the roll is even, you grow.",
          "13-14 \tYou cast Confusion centered on yourself.",
          "15-16 \tFor the next minute, you regain 5 hit points at the start of each of your turns.",
          "17-18 \tYou grow a long beard made of feathers that remains until you sneeze, at which point the feathers explode out from your face.",
          "19-20 \tYou cast Grease centered on yourself.",
          "21-22 \tCreatures have disadvantage on saving throws against the next spell you cast in the next minute that involves a saving throw.",
          "23-24 \tYour skin turns a vibrant shade of blue. A Remove Curse spell can end this effect.",
          "25-26 \tAn eye appears on your forehead for the next minute. During that time, you have advantage on Wisdom (Perception) checks that rely on sight.",
          "27-28 \tFor the next minute, all your spells with a casting time of 1 action have a casting time of 1 bonus action.",
          "29-30 \tYou teleport up to 60 feet to an unoccupied space of your choice that you can see.",
          "31-32 \tYou are transported to the Astral Plane until the end of your next turn, after which time you return to the space you previously occupied or the nearest unoccupied space if that space is occupied.",
          "33-34 \tMaximize the damage of the next damaging spell you cast within the next minute.",
          "35-36 \tRoll a d10. Your age changes by a number of years equal to the roll. If the roll is odd, you get younger (minimum 1 year old). If the roll is even, you get older.",
          "37-38 \t1d6 flumphs controlled by the DM appear in unoccupied spaces within 60 feet of you and are frightened of you. They vanish after 1 minute.",
          "39-40 \tYou regain 2d10 hit points.",
          "41-42 \tYou turn into a potted plant until the start of your next turn. While a plant, you are incapacitated and have vulnerability to all damage. If you drop to 0 hit points, your pot breaks, and your form reverts.",
          "43-44 \tFor the next minute, you can teleport up to 20 feet as a bonus action on each of your turns.",
          "45-46 \tYou cast Levitate on yourself.",
          "47-48 \tA unicorn controlled by the DM appears in a space within 5 feet of you, then disappears 1 minute later.",
          "49-50 \tYou can't speak for the next minute. Whenever you try, pink bubbles float out of your mouth.",
          "51-52 \tA spectral shield hovers near you for the next minute, granting you a +2 bonus to AC and immunity to Magic Missile.",
          "53-54 \tYou are immune to being intoxicated by alcohol for the next 5d6 days.",
          "55-56 \tYour hair falls out but grows back within 24 hours.",
          "57-58 \tFor the next minute, any flammable object you touch that isn't being worn or carried by another creature bursts into flame.",
          "59-60 \tYou regain your lowest-level expended spell slot.",
          "61-62 \tFor the next minute, you must shout when you speak.",
          "63-64 \tYou cast Fog Cloud centered on yourself.",
          "65-66 \tUp to three creatures you choose within 30 feet of you take 4d10 lightning damage.",
          "67-68 \tYou are frightened by the nearest creature until the end of your next turn.",
          "69-70 \tEach creature within 30 feet of you becomes invisible for the next minute. The invisibility ends on a creature when it attacks or casts a spell.",
          "71-72 \tYou gain resistance to all damage for the next minute.",
          "73-74 \tA random creature within 60 feet of you becomes poisoned for 1d4 hours.",
          "75-76 \tYou glow with bright light in a 30-foot radius for the next minute. Any creature that ends its turn within 5 feet of you is blinded until the end of its next turn.",
          "77-78 \tYou cast Polymorph on yourself. If you fail the saving throw, you turn into a sheep for the spell's duration.",
          "79-80 \tIllusory butterflies and flower petals flutter in the air within 10 feet of you for the next minute.",
          "81-82 \tYou can take one additional action immediately.",
          "83-84 \tEach creature within 30 feet of you takes 1d10 necrotic damage. You regain hit points equal to the sum of the necrotic damage dealt.",
          "85-86 \tYou cast Mirror Image.",
          "87-88 \tYou cast Fly on a random creature within 60 feet of you.",
          "89-90 \tYou become invisible for the next minute. During that time, other creatures can't hear you. The invisibility ends if you attack or cast a spell.",
          "91-92 \tIf you die within the next minute, you immediately come back to life as if by the Reincarnate spell.",
          "93-94 \tYour size increases by one size category for the next minute.",
          "95-96 \tYou and all creatures within 30 feet of you gain vulnerability to piercing damage for the next minute.",
          "97-98 \tYou are surrounded by faint, ethereal music for the next minute.",
          "99-00 \tYou regain all expended sorcery points."));

  final List<String> table;

  WildMagic(List<String> table) {
    this.table = table;
  }
}
