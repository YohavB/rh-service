package com.yb.rh.enum

import com.fasterxml.jackson.annotation.JsonValue

enum class Brands(private val value: Int, private val prettyName: String) {
    AIWAYS(0, "Aiways"),
    ALPHA_ROMEO(2, "Alpha Romeo"),
    ASTON_MARTIN(3, "Aston Martin"),
    AUDI(4, "Audi"),
    AVTOVAZ(5, "AvtoVAZ"),
    BENTLEY(6, "Bentley"),
    BMW(7, "BMW"),
    BUICK(9, "Buick"),
    CADILLAC(10, "Cadillac"),
    CENNTRO(11, "Cenntro"),
    CHEVROLET(12, "Chevrolet"),
    CHRYSLER(13, "Chrysler"),
    CITROEN(14, "Citroen"),
    DACIA(15, "Dacia"),
    DAEWOO(16, "Daewoo"),
    DAIHATSU(17, "Daihatsu"),
    DE_TOMASO(18, "De Tomaso"),
    DFSK(19, "DFSK"),
    DODGE(20, "Dodge"),
    DONGFENG(21, "Dongfeng"),
    DS(22, "DS"),
    FERRARI(24, "Ferrari"),
    FIAT(25, "Fiat"),
    FORD(26, "Ford"),
    GAC(27, "GAC"),
    GEELY(28, "Geely"),
    GMC(29, "GMC"),
    GOUPIL(30, "Goupil"),
    GREAT_WALL(31, "Great Wall"),
    HONDA(32, "Honda"),
    HONGQI(33, "Hongqi"),
    HUMMER(34, "Hummer"),
    HYUNDAI(35, "Hyundai"),
    ISUZU(36, "Isuzu"),
    IVECO(37, "Iveco"),
    JAGUAR(38, "Jaguar"),
    JEEP(39, "Jeep"),
    KARMA(40, "Karma"),
    KIA(41, "Kia"),
    LAMBORGHINI(42, "Lamborghini"),
    LANCIA(43, "Lancia"),
    LAND_ROVER(44, "Land Rover"),
    LEVC(45, "LEVC"),
    LEXUS(46, "Lexus"),
    LINCOLN(47, "Lincoln"),
    LTI(48, "LTI"),
    LYNK_AND_CO(49, "Lynk & Co"),
    MAN(50, "Man"),
    MASERATI(52, "Maserati"),
    MAXUS(53, "Maxus"),
    MAZDA(54, "Mazda"),
    MCC(55, "MCC"),
    MERCEDES(56, "Mercedes"),
    MG(57, "MG"),
    MITSUBISHI(58, "Mitsubishi"),
    NISSAN(59, "Nissan"),
    OPEL(60, "Opel"),
    PEUGEOT(61, "Peugeot"),
    PIAGGIO(62, "Piaggio"),
    POLESTAR(63, "Polestar"),
    PONTIAC(64, "Pontiac"),
    PORSCHE(65, "Porsche"),
    RENAULT(66, "Renault"),
    ROVER(67, "Rover"),
    SAAB(68, "Saab"),
    SEAT(69, "Seat"),
    SERES(70, "Seres"),
    SKODA(71, "Skoda"),
    SKYWELL(72, "Skywell"),
    SMART(73, "Smart"),
    SSANGYONG(74, "Ssangyong"),
    SUBARU(75, "Subaru"),
    SUZUKI(76, "Suzuki"),
    TELCO(77, "TELCO"),
    TESLA(78, "Tesla"),
    TOYOTA(79, "Toyota"),
    VOLKSWAGEN(81, "Volkswagen"),
    VOLVO(82, "Volvo"),
    UNKNOWN(999, "Unknown");


    @JsonValue
    fun getValue(): Int = value

    companion object {
        private val mapping: MutableMap<Int, Brands> = HashMap()

        fun valueOf(value: Int): Brands {
            return mapping[value] ?: throw RuntimeException("Invalid value:$value")
        }

        init {
            for (brands in Brands.entries) {
                mapping[brands.value] = brands
            }
        }
    }
}