package com.megix

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import com.lagradost.cloudstream3.LoadResponse.Companion.addImdbUrl

class TopmoviesProvider : MoviesmodProvider() { // all providers must be an instance of MainAPI
    override var mainUrl = "https://napifilm.hu"
    override var name = "Movies"
    override val hasMainPage = true
    override var lang = "hu"
    override val hasDownloadSupport = true
    override val supportedTypes = setOf(
        TvType.Movie,
        TvType.TvSeries
    )

    override val mainPage = mainPageOf(
        "$mainUrl/?p=" to "Home",
        "$mainUrl/sorozatok" to "Latest Web Series",
        "$mainUrl/?p=1" to "Movies",
    )
}
