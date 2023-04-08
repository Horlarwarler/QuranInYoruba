package com.sadaqaworks.yorubaquran.util

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import java.net.ConnectException


suspend inline fun <reified T> makeNetworkRequest(url:String, client: HttpClient) : Pair<T?, String?>{

    try {
        val response = client.get(urlString = url)

        val dataReceived = response.body<T>()
        return  Pair(dataReceived,null)

    }
    catch (error: ClientRequestException) {
        return Pair(null, "Invalid Request")

    }
    catch (error: ServerResponseException) {
        return Pair(null, "Invalid Response")
    }
    catch (error: ConnectException) {
        return Pair(null, "No internet Connection")
    }
    catch (otherError: Exception) {
        return Pair(null, otherError.message)

    }
}