package com.ds.da.sensorsurvey

class NetworkStream (val url: String) {
    private val callAPI = CallAPI()

    fun sendData(string: String){
        callAPI.execute(url, string)
    }
}