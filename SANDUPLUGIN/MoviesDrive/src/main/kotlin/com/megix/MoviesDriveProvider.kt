package com.megix

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.Jsoup

class MoviesDriveProvider : MainAPI() {
    override var mainUrl = "https://napifilm.hu"
    override var name = "NapiFilm"
    override val hasMainPage = true
    override var lang = "hu"
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)

    override val mainPage = listOf(
        MainPageData("Friss feltöltések", "$mainUrl/", "div.moviefilm")
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val doc = app.get(request.data).document
        val movies = doc.select("div.moviefilm").mapNotNull {
            val title = it.selectFirst("h3")?.text() ?: return@mapNotNull null
            val href = it.selectFirst("a")?.attr("href") ?: return@mapNotNull null
            val poster = it.selectFirst("img")?.attr("src") ?: ""
            newMovieSearchResponse(title, href, TvType.Movie) {
                this.posterUrl = poster
            }
        }
        return newHomePageResponse(request.name, movies)
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val res = app.get("$mainUrl/?s=$query").document
        return res.select("div.moviefilm").mapNotNull {
            val title = it.selectFirst("h3")?.text() ?: return@mapNotNull null
            val href = it.selectFirst("a")?.attr("href") ?: return@mapNotNull null
            val poster = it.selectFirst("img")?.attr("src") ?: ""
            newMovieSearchResponse(title, href, TvType.Movie) {
                this.posterUrl = poster
            }
        }
    }

    override suspend fun load(url: String): LoadResponse {
        val doc = app.get(url).document
        val title = doc.selectFirst("h1")?.text() ?: ""
        val poster = doc.selectFirst("div.poster img")?.attr("src")
        val description = doc.selectFirst(".post-content p")?.text() ?: ""
        val year = Regex("(19|20)\\d{2}").find(description)?.value?.toIntOrNull()

        val links = doc.select("iframe").mapNotNull {
            it.attr("src")
        }

        return newMovieLoadResponse(title, url, TvType.Movie, url) {
            this.posterUrl = poster
            this.year = year
            this.plot = description
            this.addedEpisodes = links.mapIndexed { index, link ->
                Episode(link, "Link ${index + 1}")
            }
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        callback(
            ExtractorLink(
                source = name,
                name = "NapiFilm Link",
                url = data,
                referer = mainUrl,
                quality = Qualities.Unknown.value,
                isM3u8 = data.endsWith(".m3u8")
            )
        )
    }
}
