package com.example.knockitbranchapp.Model

class RiderModel(
    var riderId: String,
    var name : String,
    var email: String,
    var profile: String,
    var totalEarning: Long,
    var number: String,
    var city: String,
    var state: String,
    var country: String,
    var pincode: String,
    var address: String,
    var latitude: Float,
    var longitude: Float,
    var drivingLicence: String,
    var drivingLicenceImage_1: String,
    var drivingLicenceImage_2: String,
    var driverAccount: String,
    var bankAccountNumber: String,
    var bankName: String,
    var bankHolderName: String,
    var bankIFSCCode: String,
    var connectWithStore: String,
) {

    constructor() : this(
        "",
        "",
        "",
        "",
        1,
        "",
        "",
        "",
        "",
        "",
        "",
        1F,
        1F,
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        ""
    )
}