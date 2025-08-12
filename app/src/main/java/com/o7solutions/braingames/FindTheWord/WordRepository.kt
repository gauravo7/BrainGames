package com.example.zigzag

object WordRepository {
    val words: List<String> = listOf(
        "CAT", "DOG", "SUN", "CAR", "BUS",
        "PEN", "BEE", "COW", "FOX", "OWL",
        "BAT", "RAT", "HEN", "PIG", "ANT",
        "JAM", "MAP", "BAG", "CAP", "PAN",
        "MUG", "JAR", "BUN", "FUN", "RUN",
        "GUN", "HAT", "MAT", "NET", "JET",
        "LOG", "MOP", "NOD", "POT", "RUG",
        "SAP", "TAP", "VAN", "WEB", "YAK",
        "ZIP", "EGG", "ICE", "OIL", "INK",
        "BID", "DIP", "FAN", "GAP", "HOP",
        "ACE", "AGE", "AIR", "ARM", "ASH",
        "BAY", "BED", "BOY", "BUN", "CAB",
        "CAN", "CUP", "DAY", "DEN", "EAR",
        "EEL", "ELK", "EMU", "END", "EVE",
        "FIG", "FIN", "FLY", "FUN", "GEM",
        "GUM", "GUY", "HEN", "HIM", "HIP",
        "HUT", "INK", "JAW", "JOY", "KEY",
        "KID", "LAB", "LID", "LIP", "MAN",
        "MIX", "MUG", "NUN", "OAK", "PAD",
        "PAL", "PEN", "PIT", "RAM", "RAY",
        "ART", "BAG", "BAR", "BAY", "BED",
        "BID", "BIN", "BOB", "BOG", "BOW",
        "BOX", "BOY", "BUN", "BUS", "BUT", "BUY", "BYE", "CAB", "CAN", "CAP", "CAR", "CAT", "COW", "COY", "CUP", "CUT", "DAD", "DAM", "DAY", "DEN", "DEW", "DID", "DIG", "DIM", "DIN", "DIP", "DOE", "DOG", "DOT", "DRY", "DUB", "DUE", "DUG", "EAR", "EAT", "EEL", "EGG", "ELF", "ELK", "EMU", "END", "EVE", "EWE", "EYE", "FAD", "FAN", "FAR", "FAT", "FAX", "FED", "FEE", "FEW", "FIG", "FIN", "FIT", "FIX", "FLY", "FOE", "FOG", "FOR", "FOX", "FRY", "FUN", "FUR", "GAG", "GAP", "GAS", "GEM", "GET", "GIG", "GIN", "GOD", "GUM", "GUN", "GUT", "GUY", "GYM", "HAD", "HAM", "HAS", "HAT", "HAY", "HEM", "HEN", "HER", "HEY", "HIM", "HIP", "HIS", "HIT", "HOG", "HOP", "HOT", "HOW", "HUB", "HUG", "HUM", "HUT"
    )

    val words4: List<String> = listOf(
        "TREE", "FISH", "BIRD", "FROG", "LION",
        "BEAR", "WOLF", "DEER", "GOAT", "DUCK",
        "SWAN", "MULE", "TOAD", "CRAB", "MOLE",
        "SEAL", "LAMB", "COLT", "PONY", "MOTH",
        "BULL", "CALF", "FOAL", "KITT", "PUMA",
        "SHAD", "PIKE", "PERL", "DOVE", "HAWK",
        "COOT", "TERN", "LOON", "GULL", "ROOK",
        "JAYE", "CROW", "SWAY", "BASS", "CARP",
        "NEWT", "IBIS", "ORCA", "MINK", "ELAN",
        "BARN", "BATS", "BEEH", "BIRD", "BOAR",
        "BULL", "CALF", "COWS", "DEER", "DOGS",
        "DUCK", "EELS", "ELKS", "EMUS", "FOAL",
        "FROG", "GOAT", "GULL", "HARE", "HAWK",
        "HENS", "IBEX", "JAYS", "KITT", "LAMB",
        "LARK", "LION", "LOON", "MINK", "MOLE",
        "MOTH", "MULE", "NEWT", "ORCA", "OTTO",
        "PIKE", "PONY", "PUMA", "ROOK", "SEAL",
        "SHAD", "SWAN", "TERN", "TOAD", "WOLF",
        "ABLE", "ACID", "AGED", "ALSO", "AREA", "ARMY", "AWAY", "BABY", "BACK", "BALL", "BAND", "BANK", "BASE", "BATH", "BEAR", "BEAT", "BEEF", "BEEN", "BEER", "BELL", "BELT", "BEST", "BILL", "BIRD", "BLOW", "BLUE", "BOAT", "BODY", "BOMB", "BOND", "BONE", "BOOK", "BOOM", "BOOT", "BORN", "BOSS", "BOTH", "BOWL", "BULK", "BURN", "BUSH", "BUSY", "CALL", "CALM", "CAME", "CAMP", "CARD", "CARE", "CASE", "CASH", "CAST", "CELL", "CHAT", "CHIP", "CITY", "CLUB", "COAL", "COAT", "CODE", "COLD", "COME", "COOK", "COOL", "COPE", "COPY", "CORE", "COST", "CREW", "CROP", "DARK", "DATA", "DATE", "DAWN", "DAYS", "DEAD", "DEAL", "DEAN", "DEAR", "DEBT", "DEEP", "DENY", "DESK", "DIAL", "DICK", "DIET", "DISC", "DISK", "DOES", "DONE", "DOOR", "DOSE", "DOWN", "DRAW", "DREW", "DROP", "DRUG", "DUAL", "DUKE", "DUST", "DUTY"
    )

    val words5: List<String> = listOf(
        "APPLE", "BANJO", "BERRY", "BISON", "BLADE",
        "BLEND", "BREAD", "BRICK", "BRUSH", "CABLE",
        "CAMEL", "CANAL", "CANDY", "CHAIR", "CHALK",
        "CHEST", "CHESS", "CLOWN", "CLOUD", "COAST",
        "CRANE", "CRASH", "CROWN", "DAISY", "DANCE",
        "DEMON", "DIVER", "DREAM", "DRILL", "EAGLE",
        "EARTH", "ELBOW", "ENEMY", "FABLE", "FAITH",
        "FENCE", "FIELD", "FLAME", "FLOOD", "FLOUR",
        "FROST", "FRUIT", "GHOST", "GIANT", "GLASS",
        "GLOVE", "GRAPE", "GRASS", "GREEN", "GUARD",
        "GUEST", "HONEY", "HORSE", "HOUSE", "HUMAN",
        "IVORY", "JELLY", "JUDGE", "JUICE", "KNIFE",
        "LADLE", "LAYER", "LEMON", "LIGHT", "LIVER",
        "MAGIC", "MAPLE", "MARCH", "MASON", "MELON",
        "METER", "MOUSE", "MUSIC", "NERVE", "NINJA",
        "NURSE", "OCEAN", "OLIVE", "ONION", "OPERA",
        "ORBIT", "ORGAN", "OTTER", "PAINT", "PANDA",
        "PANEL", "PEACH", "PEARL", "PILOT", "PLANE",
        "PLANT", "PLATE", "PLAZA", "PRIZE", "QUEEN",
        "QUIET", "RADIO", "RAVEN", "RIVER", "ROBIN",
        "ABIDE", "ABOUT", "ABOVE", "ABUSE", "ACTOR",
        "ACUTE", "ADMIT", "ADOPT", "ADULT", "AFTER",
         "AGAIN", "AGENT", "AGREE", "AHEAD", "ALARM",
          "ALBUM", "ALERT", "ALIEN", "ALIGN", "ALIVE",
           "ALLOW", "ALONE", "ALONG", "ALTER", "AMONG",
            "ANGER", "ANGLE", "ANGRY", "APART", "APPLE",
             "APPLY", "ARENA", "ARGUE", "ARISE", "ARRAY",
              "ARROW", "ASIDE", "ASSET", "AUDIO", "AUDIT",
               "AVOID", "AWARD", "AWARE", "BADGE", "BADLY",
                "BAKER", "BASIC", "BASIS", "BEACH", "BEGIN",
                 "BEING", "BELLY", "BENCH", "BILLY", "BIRTH",
                  "BLACK", "BLAME", "BLANK", "BLAST", "BLESS",
                   "BLIND", "BLOCK", "BLOOD", "BOARD", "BOOST",
                    "BOOTH", "BOUND", "BRAIN", "BRAND", "BRAVE", "BREAD", "BREAK", "BRICK", "BRIDE", "BRIEF", "BRING", "BROAD", "BROKE", "BROWN", "BRUSH", "BUILD", "BUILT", "BUYER", "CABIN", "CABLE", "CALIF", "CARRY", "CATCH", "CAUSE", "CHAIN", "CHAIR", "CHART", "CHASE", "CHEAP", "CHECK", "CHEEK", "CHEST", "CHIEF", "CHILD", "CHINA", "CHOIR", "CHOOSE", "CHOP"
    )

    fun getRandomWord3(): String = words.random()
    fun getRandomWord4(): String = words4.random()
    fun getRandomWord5(): String = words5.random()

    fun getRandomWords3(count: Int): List<String> = words.shuffled().take(count)
    fun getRandomWords4(count: Int): List<String> = words4.shuffled().take(count)
    fun getRandomWords5(count: Int): List<String> = words5.shuffled().take(count)
}
