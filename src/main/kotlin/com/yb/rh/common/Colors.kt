package com.yb.rh.common

enum class Colors(private val value: Int, private val prettyName: String) {
    UNKNOWN(0, "Unknown"),
    PEARL_GREEN(1, "Pearl Green"),
    PEARL_BLACK(2, "Pearl Black"),
    PEARL_BLUE(3, "Pearl Blue"),
    METALLIC_YELLOW(4, "Metallic Yellow"),
    FROM_LANG_METALLIC(5, "From Lang Metallic"),
    METALLIC_DARK_GRAY(6, "Metallic Dark Gray"),
    AZURE_SILVER_METALLIC(7, "Azure Silver Metallic"),
    METALLIC_COPPER(8, "Metallic Copper"),
    METALLIC_OLIVE_GREEN(9, "Metallic Olive Green"),
    BLACK(10, "Black"),
    METALLIC_BLACK(11, "Metallic Black"),
    BLACK_EGGPLANT(12, "Black Eggplant"),
    METALLIC_OLIVE(13, "Metallic Olive"),
    METALLIC_REDDISH(14, "Metallic Reddish"),
    METALLIC_LIGHT_GRAY(15, "Metallic Light Gray"),
    STRONG_YELLOW(16, "Strong Yellow"),
    GLOWING_RED(17, "Glowing Red"),
    GRAY_MELANGE(18, "Gray Melange"),
    MATTE_MANDARIN(19, "Matte Mandarin"),
    GRAY(20, "Gray"),
    STEEL_GRAY(21, "Steel Gray"),
    DARK_GRAY(22, "Dark Gray"),
    LIGHT_GRAY(23, "Light Gray"),
    BRONZE_GRAY(24, "Bronze Gray"),
    SILVER(25, "Silver"),
    PLATINUM(26, "Platinum"),
    METALLIC_GRAY(27, "Metallic Gray"),
    METALLIC_GREENISH(28, "Metallic Greenish"),
    METALLIC_SILVER(29, "Metallic Silver"),
    BLUE(30, "Blue"),
    DARK_BLUE(31, "Dark Blue"),
    LIGHT_BLUE(32, "Light Blue"),
    AZURE(33, "Azure"),
    TURQUOISE(34, "Turquoise"),
    DARK_TURQUOISE(35, "Dark Turquoise"),
    METALLIC_BLUE(36, "Metallic Blue"),
    METALLIC_BLUE_GRAY(37, "Metallic Blue Gray"),
    METALLIC_CHARCOAL_BLUE(38, "Metallic Charcoal Blue"),
    METALLIC_LIGHT_BLUE(39, "Metallic Light Blue"),
    GREEN(40, "Green"),
    BRIGHT_GREEN(41, "Bright Green"),
    DARK_GREEN(42, "Dark Green"),
    GREENISH(43, "Greenish"),
    LIGHT_GREEN(44, "Light Green"),
    OLIVE_GREEN(45, "Olive Green"),
    METALLIC_GREEN(46, "Metallic Green"),
    METALLIC_BORDEAUX(47, "Metallic Bordeaux"),
    SILVER_GREEN(48, "Silver Green"),
    METALLIC_GOLD_GREEN(49, "Metallic Gold Green"),
    RED(50, "Red"),
    DARK_RED(51, "Dark Red"),
    WINE(52, "Wine"),
    PURPLE(53, "Purple"),
    BORDEAUX(54, "Bordeaux"),
    PINK(55, "Pink"),
    METALLIC_RED(56, "Metallic Red"),
    EGGPLANT(57, "Eggplant"),
    COPPER(58, "Copper"),
    ROSE_METALLIC(59, "Rose Metallic"),
    YELLOW(60, "Yellow"),
    LEMON_YELLOW(61, "Lemon Yellow"),
    SAHARA(62, "Sahara"),
    MUSTARD(63, "Mustard"),
    ORANGE(64, "Orange"),
    GOLD(65, "Gold"),
    BEIGE(66, "Beige"),
    DARK_BEIGE(67, "Dark Beige"),
    CREAM(68, "Cream"),
    METALLIC_GOLD(69, "Metallic Gold"),
    BROWN(70, "Brown"),
    LIGHT_BROWN(71, "Light Brown"),
    DARK_BROWN(72, "Dark Brown"),
    GOLDEN(73, "Golden"),
    AQUA_GREEN(74, "Aqua Green"),
    DARK_PURPLE(75, "Dark Purple"),
    GREENISH_SILVER(76, "Greenish Silver"),
    CLASSIC_RED(77, "Classic Red"),
    METALLIC_TURQUOISE(78, "Metallic Turquoise"),
    SEA_MONEY(79, "Sea Money"),
    WHITE_IVORY(80, "White Ivory"),
    IVORY(81, "Ivory"),
    OTHER(82, "Other"),
    MULTI_COLORED(83, "Multi-colored"),
    METALLIC_LIGHT_TURQUOISE(84, "Metallic Light Turquoise"),
    LIGHT_PURPLE(85, "Light Purple"),
    MILLENNIUM_SILVER(86, "Millennium Silver"),
    BEIGE_METALLIC(87, "Beige Metallic"),
    METALLIC_COFFEE(88, "Metallic Coffee"),
    INDIGO_MATTE(89, "Indigo Matte"),
    TONIC(90, "Tonic"),
    BLUE_CRYSTAL(91, "Blue Crystal"),
    BRONZE(92, "Bronze"),
    DARK_SILVER(93, "Dark Silver"),
    LIGHT_SILVER(94, "Light Silver"),
    TURQUOISE_GREEN(95, "Turquoise Green"),
    SEA_GREEN(96, "Sea Green"),
    METALLIC_BLUISH_SILVER(97, "Metallic Bluish Silver"),
    RED_BLACK(98, "Red Black"),
    DARK_SILVER_METALLIC(99, "Dark Silver Metallic"),
    WHITE(100, "White");

    override fun toString(): String {
        return prettyName
    }

    companion object {
        private val mapping: MutableMap<Int, Colors> = HashMap()

        fun valueOf(value: Int): Colors {
            return mapping[value] ?: throw RuntimeException("Invalid value:$value")
        }

        init {
            for (colors in Colors.values()) {
                mapping[colors.value] = colors
            }
        }
    }

    fun toInt(): Int {
        return value
    }
}

