package com.yb.rh.services

import com.google.gson.GsonBuilder
import com.yb.rh.entities.Cars
import com.yb.rh.entities.CarsDTO
import com.yb.rh.entities.UsersCars
import com.yb.rh.repositorties.CarsRepository
import com.yb.rh.repositorties.UsersCarsRepository
import com.yb.rh.repositorties.UsersRepository
import com.yb.rh.services.ilcarapi.IlCarJson
import mu.KotlinLogging
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestParam
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.util.concurrent.TimeUnit


@Service
class CarsService(
    private val carsRepository: CarsRepository,
    private val usersRepository: UsersRepository,
    private val usersCarsRepository: UsersCarsRepository,
) {
    private val logger = KotlinLogging.logger {}

    fun findAll(): List<Cars> = carsRepository.findAll().toList()

    fun findByPlateNumber(@RequestParam(name = "plateNumber") plateNumber: String): CarsDTO? {
        logger.info { "Try to find Car : $plateNumber" }
        return carsRepository.findByPlateNumber(plateNumber)?.toDto()
    }

    fun getCarInfo(plateNumber: String) {
        logger.info { "Try to get Car Info $plateNumber" }

        val url =
            "https://data.gov.il/api/3/action/datastore_search?resource_id=053cea08-09bc-40ec-8f7a-156f0677aff3&q=${plateNumber}"

        val request = Request.Builder().url(url).build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body!!.string()

                val carInfo = GsonBuilder().create().fromJson(body, IlCarJson::class.java)

                val carsDTO = carInfo.toCarDto()
            }

            override fun onFailure(call: Call, e: IOException) {
                logger.warn { "Failed to get Car Info" }
            }
        })
    }


    fun createOrUpdateCar(
        plateNumber: String,
        userId: Long?
    ): CarsDTO {
        logger.info { "Try to create or update Car : $plateNumber of user : $userId " }


        val currentCar = Cars.fromDto(CarsDTO.returnTest())

        carsRepository.save(currentCar)
        userId?.let { it ->
            usersRepository.findByUserId(it)?.let { currentUser ->
                usersCarsRepository.save(UsersCars(currentUser, currentCar))
            }
        }
        return currentCar.toDto()
    }
}

object ApiWorker {
    private var mClient: OkHttpClient? = null
    private var mGsonConverter: GsonConverterFactory? = null

    /**
     * Don't forget to remove Interceptors (or change Logging Level to NONE)
     * in production! Otherwise people will be able to see your request and response on Log Cat.
     */
    val client: OkHttpClient
        @Throws(NoSuchAlgorithmException::class, KeyManagementException::class)
        get() {
            if (mClient == null) {
                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY

                val httpBuilder = OkHttpClient.Builder()
                httpBuilder
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .addInterceptor(interceptor)  /// show all JSON in logCat
                mClient = httpBuilder.build()

            }
            return mClient!!
        }


    val gsonConverter: GsonConverterFactory
        get() {
            if (mGsonConverter == null) {
                mGsonConverter = GsonConverterFactory
                    .create(
                        GsonBuilder()
                            .setLenient()
                            .disableHtmlEscaping()
                            .create()
                    )
            }
            return mGsonConverter!!
        }
}

