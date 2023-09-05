package com.example.knockitbranchapp.Model

class ProductModel(
    var productImage: String,
    var productTitle: String,
    var productPrice: Int,
    var productCuttedPrice: Int,
    var productRating: String,
    var productTotalRating: String,
    var productCategory: String,
    var productSubCategory: String,
    var productBrandName: String,
    var qty: String,
    var availableQty: Int,
    var city_1: String,
    var city_2: String,
    var city_3: String,
    var city_4: String,
    var city_5: String,
    var id: String,
    var storeId: String,
    var productVerification: String,
    var productDescription: String
) {

    constructor() : this(
        "",
        "",
        1,
        1,
        "",
        "",
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
        "",
        "",
        ""
    )

    companion object {
        var priceLowToHigh: java.util.Comparator<ProductModel?> =
            Comparator<ProductModel?> { p1, p2 ->
                p1?.productPrice?.let {
                    p2?.productPrice?.compareTo(
                        it
                    )
                }!!
            }
        var priceHighToLow: java.util.Comparator<ProductModel?> =
            Comparator<ProductModel?> { p1, p2 ->
                p2?.productPrice?.let {
                    p1?.productPrice?.compareTo(
                        it
                    )
                }!!
            }

        var rattingHighToLow: java.util.Comparator<ProductModel?> =
            Comparator<ProductModel?> { p1, p2 ->
                p1?.productRating?.let {
                    p2?.productRating?.compareTo(
                        it
                    )
                }!!
            }
        var rattingLowTOHigh: java.util.Comparator<ProductModel?> =
            Comparator<ProductModel?> { p1, p2 ->
                p2?.productRating?.let {
                    p1?.productRating?.compareTo(
                        it
                    )
                }!!
            }
    }
}