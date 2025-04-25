class MoviesDriveProvider : MainAPI() {
    override var mainUrl = "https://napifilm.hu"
    override var name = "NapiFilm"
    override val hasMainPage = true
    override var lang = "hu"
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)

    override val mainPage = listOf(
        MainPageData("Friss feltöltések", "$mainUrl/", "div.card")
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val doc = app.get(request.data).document
        val movies = doc.select("div.card").mapNotNull {
            val title = it.select(".card-title a")?.text() ?: return@mapNotNull null
            val href = it.select("a.filename.link")?.attr("href") ?: return@mapNotNull null
            val poster = it.select("img")?.attr("src") ?: ""
            newMovieSearchResponse(title, href, TvType.Movie) {
                this.posterUrl = poster
            }
        }
        return newHomePageResponse(request.name, movies)
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val res = app.get("$mainUrl/?s=$query").document
        return res.select("div.card").mapNotNull {
            val title = it.select(".card-title a")?.text() ?: return@mapNotNull null
            val href = it.select("a.filename.link")?.attr("href") ?: return@mapNotNull null
            val poster = it.select("img")?.attr("src") ?: ""
            newMovieSearchResponse(title, href, TvType.Movie) {
                this.posterUrl = poster
            }
        }
    }

    override suspend fun load(url: String): LoadResponse {
        val doc = app.get(url).document
        val title = doc.select("h1")?.text() ?: ""
        val poster = doc.select("div.poster img")?.attr("src")
        val description = doc.select(".post-content p")?.text() ?: ""
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
