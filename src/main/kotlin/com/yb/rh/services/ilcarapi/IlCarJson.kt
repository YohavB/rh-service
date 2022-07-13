package com.yb.rh.services.ilcarapi

import com.squareup.moshi.JsonClass
import com.yb.rh.common.Brands
import com.yb.rh.common.Colors
import com.yb.rh.entities.CarsDTO
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class IlCarJson(
    val help: String,
    val result: Result,
    val success: Boolean
) {
    fun toCarDto(): CarsDTO {
        return CarsDTO(getPlateNumber(), getBrand(), getModel(), getColor(), getLicenseDateExpiration())
    }

    private fun getPlateNumber() = this.result.getPlateNumber()
    private fun getBrand() = this.result.getBrand()
    private fun getModel() = this.result.getModel()
    private fun getColor() = this.result.getColor()
    private fun getLicenseDateExpiration() = this.result.getLicenseDateExpiration()
}

@JsonClass(generateAdapter = true)
data class Result(
    val records: List<Record>
){
     fun getPlateNumber() = this.records[0].mispar_rechev.toString()
     fun getBrand() = this.records[0].getBrand()
     fun getModel() = this.records[0].kinuy_mishari
     fun getColor() = this.records[0].getColor()
     fun getLicenseDateExpiration(): LocalDateTime = LocalDateTime.parse(this.records[0].tokef_dt)
}

@JsonClass(generateAdapter = true)
data class Record(
    val _id: Int,
    val baalut: String,
    val degem_cd: Int,
    val degem_manoa: String,
    val degem_nm: String,
    val horaat_rishum: Int,
    val kinuy_mishari: String,
    val kvutzat_zihum: Int,
    val misgeret: String,
    val mispar_rechev: Int,
    val mivchan_acharon_dt: String,
    val moed_aliya_lakvish: String,
    val ramat_eivzur_betihuty: Any,
    val ramat_gimur: String,
    val rank: Double,
    val shnat_yitzur: Int,
    val sug_degem: String,
    val sug_delek_nm: String,
    val tokef_dt: String,
    val tozeret_cd: Int,
    val tozeret_nm: String,
    val tzeva_cd: Int,
    val tzeva_rechev: String,
    val zmig_ahori: String,
    val zmig_kidmi: String
) {
    fun getBrand(): Brands {
        return when (this.tozeret_cd) {
            19, 21, 268, 283, 551, 990 -> Brands.AUDI
            33, 76, 103, 104, 138, 188, 451, 968 -> Brands.OPEL
            1236, 1434 -> Brands.AIWAYS
            67, 101 -> Brands.IVECO
            92, 127, 973 -> Brands.ISUZU
            1331 -> Brands.LEVC
            8 -> Brands.LTI
            74 -> Brands.ALPHA_ROMEO
            318 -> Brands.MCC
            349 -> Brands.ASTON_MARTIN
            1381 -> Brands.HONGQI
            32, 143, 695, 722, 910, 1276, 1349 -> Brands.BMW
            70, 156, 189 -> Brands.BUICK
            983 -> Brands.BENTLEY
            201, 936 -> Brands.GMC
            234, 239, 1020, 1287 -> Brands.JEEP
            1029 -> Brands.GOUPIL
            1213, 1231 -> Brands.GAC
            1321 -> Brands.GEELY
            503 -> Brands.GREAT_WALL
            351, 544, 1394 -> Brands.DACIA
            504 -> Brands.DE_TOMASO
            259, 285 -> Brands.DODGE
            229 -> Brands.DONGFENG
            1384, 57 -> Brands.DS
            1169 -> Brands.DFSK
            288 -> Brands.DAEWOO
            281 -> Brands.DAIHATSU
            183, 347 -> Brands.HUMMER
            155, 312, 313, 341, 346, 445, 601, 640 -> Brands.HONDA
            385 -> Brands.AVTOVAZ
            226, 361, 386, 388, 571 -> Brands.VOLVO
            412, 187, 413, 430, 432, 488, 490, 527, 672, 686, 839, 863, 1242, 1286, 1341 -> Brands.TOYOTA
            450 -> Brands.TELCO
            1229, 1327, 1417 -> Brands.TESLA
            465, 1422 -> Brands.JAGUAR
            174, 253, 421, 481, 502, 845 -> Brands.HYUNDAI
            441 -> Brands.LAMBORGHINI
            1346 -> Brands.LYNK_AND_CO
            509 -> Brands.LINCOLN
            1245, 1246 -> Brands.LAND_ROVER
            121, 513 -> Brands.LANCIA
            219, 404 -> Brands.LEXUS
            531, 145 -> Brands.MAN
            443 -> Brands.MG
            844 -> Brands.MASERATI
            49, 139, 588, 1437 -> Brands.MAZDA
            338, 590, 603, 925 -> Brands.MITSUBISHI
            1152 -> Brands.MAXUS
            114, 136, 152, 194, 197, 593, 616, 806, 830, 870, 1130, 1207, 1334, 869 -> Brands.MERCEDES
            185, 624, 634, 637, 647, 653, 1106 -> Brands.NISSAN
            644, 710, 851 -> Brands.SAAB
            712 -> Brands.SSANGYONG
            542, 650 -> Brands.SUBARU
            299, 322, 380, 609, 683 -> Brands.SUZUKI
            11, 71, 423, 697, 778, 1356 -> Brands.SEAT
            279, 492, 496, 516, 811, 817, 862, 1365 -> Brands.CITROEN
            657, 920, 977 -> Brands.SMART
            1283 -> Brands.CENNTRO
            676, 704 -> Brands.SKODA
            1358 -> Brands.SKYWELL
            1294 -> Brands.SERES
            1414 -> Brands.POLESTAR
            82, 191, 301, 459, 715, 724, 727, 787, 792, 1435 -> Brands.VOLKSWAGEN
            725 -> Brands.PONTIAC
            79, 263, 728, 729, 730, 731, 734, 775, 776, 802, 1318, 1378 -> Brands.FORD
            735, 1044 -> Brands.PORSCHE
            736 -> Brands.PIAGGIO
            177, 402, 518, 674, 678, 737, 782, 794, 798, 833 -> Brands.FIAT
            237, 310, 335, 403, 568, 739, 743, 822, 858, 1049 -> Brands.PEUGEOT
            770 -> Brands.FERRARI
            452, 840 -> Brands.CADILLAC
            1364 -> Brands.KARMA
            305, 416, 885, 926 -> Brands.CHRYSLER
            941 -> Brands.ROVER
            63, 675, 711, 766, 771, 928, 940, 943 -> Brands.RENAULT
            751, 961, 962, 981 -> Brands.CHEVROLET
            else -> Brands.UNKNOWN
        }
    }

    fun getColor(): Colors {
        return Colors.valueOf(this.tzeva_cd)
    }
}