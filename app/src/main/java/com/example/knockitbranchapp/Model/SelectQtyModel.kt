package com.example.knockitbranchapp.Model

class SelectQtyModel(var id: String,var price: String, var cuttedPrice: String, var timeStamp: Long, var availableQty: Int, var qty: String, var productId: String) {

    constructor(): this("","","",1,1,"","")
}