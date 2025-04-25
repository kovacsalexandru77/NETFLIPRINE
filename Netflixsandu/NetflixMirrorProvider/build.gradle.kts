// use an integer for version numbers
version = 19

cloudstream {
    //language = "en"
    // All of these properties are optional, you can safely remove them

    description = "Netflix&PrimeVideo Magyar &
     Multiple Languages"
    authors = listOf("Horis, megix", "keyiflerolsun")

    /**
     * Status int as the following:
     * 0: Down
     * 1: Ok
     * 2: Slow
     * 3: Beta only
     * */
    status = 1 // will be 3 if unspecified
    tvTypes = listOf(
        "Movie",
        "TvSeries"
    )

    iconUrl = "https://ia601901.us.archive.org/27/items/aaa_20250423/aaa.png"
}
