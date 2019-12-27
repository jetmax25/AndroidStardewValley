package com.pickledgames.stardewvalleyguide.repositories

import android.content.Context
import com.pickledgames.stardewvalleyguide.R
import com.pickledgames.stardewvalleyguide.models.Artifact
import com.pickledgames.stardewvalleyguide.models.LostBook
import com.pickledgames.stardewvalleyguide.models.Mineral
import com.pickledgames.stardewvalleyguide.utils.RepositoryUtil
import io.reactivex.Single
import io.reactivex.functions.Function3
import org.json.JSONObject

class MuseumItemRepository(
        private val context: Context
) {

    private var museumItemsWrapper: MuseumItemsWrapper? = null

    fun getMuseumItemsWrapper(): Single<MuseumItemsWrapper> {
        return if (museumItemsWrapper != null) Single.just(museumItemsWrapper)
        else Single.zip(
                getArtifactsFromAssets(),
                getLostBooksFromAssets(),
                getMineralsFromAssets(),
                Function3<List<Artifact>, List<LostBook>, List<Mineral>, MuseumItemsWrapper> { a, lb, m -> MuseumItemsWrapper(a, lb, m) }
        )
                .doOnSuccess { museumItemsWrapper = it }
    }

    private fun getArtifactsFromAssets(): Single<List<Artifact>> {
        val inputStream = context.resources.openRawResource(R.raw.artifacts)
        val json = RepositoryUtil.inputStreamToString(inputStream)
        val artifacts: MutableList<Artifact> = mutableListOf()

        return Single.create {
            val artifactsJSONObject = JSONObject(json)
            for (name in artifactsJSONObject.keys()) {
                val artifactJSONObject = artifactsJSONObject.getJSONObject(name)
                val description = artifactJSONObject.getString("Description")
                val price = artifactJSONObject.getInt("Price")
                val locations = mutableListOf<String>()
                val locationsJSONArray = artifactJSONObject.getJSONArray("Locations")
                for (i in 0 until locationsJSONArray.length()) {
                    val location = locationsJSONArray.getString(i)
                    locations.add(location)
                }

                val artifact = Artifact(name, description, price, locations)
                artifacts.add(artifact)
            }

            it.onSuccess(artifacts.sortedBy { artifact -> artifact.name })
        }
    }

    private fun getLostBooksFromAssets(): Single<List<LostBook>> {
        val inputStream = context.resources.openRawResource(R.raw.lost_books)
        val json = RepositoryUtil.inputStreamToString(inputStream)
        val lostBooks: MutableList<LostBook> = mutableListOf()

        return Single.create {
            val lostBooksJSONObject = JSONObject(json)
            for (name in lostBooksJSONObject.keys()) {
                val description = lostBooksJSONObject.getString(name)
                val lostBook = LostBook(name, description)
                lostBooks.add(lostBook)
            }

            it.onSuccess(lostBooks.sortedBy { lostBook -> lostBook.name })
        }
    }

    private fun getMineralsFromAssets(): Single<List<Mineral>> {
        val inputStream = context.resources.openRawResource(R.raw.minerals)
        val json = RepositoryUtil.inputStreamToString(inputStream)
        val minerals: MutableList<Mineral> = mutableListOf()

        return Single.create {
            val mineralsJSONObject = JSONObject(json)
            for (name in mineralsJSONObject.keys()) {
                val mineralJSONObject = mineralsJSONObject.getJSONObject(name)
                val price = mineralJSONObject.getInt("Price")
                val description = mineralJSONObject.getString("Description")

                var minMineLevel = -1
                var maxMineLevel = -1

                if (mineralJSONObject.has("Mines")) {
                    val minesJSONObject = mineralJSONObject.getJSONObject("Mines")
                    minMineLevel = minesJSONObject.getInt("Min")
                    maxMineLevel = minesJSONObject.getInt("Max")
                }

                val locations = mutableListOf<String>()
                val locationsJSONArray = mineralJSONObject.getJSONArray("Locations")
                for (i in 0 until locationsJSONArray.length()) {
                    val location = locationsJSONArray.getString(i)
                    locations.add(location)
                }

                val mineral = Mineral(name, price, description, minMineLevel, maxMineLevel, locations)
                minerals.add(mineral)
            }

            it.onSuccess(minerals.sortedBy { mineral -> mineral.name })
        }
    }

    data class MuseumItemsWrapper(
            val artifacts: List<Artifact>,
            val lostBooks: List<LostBook>,
            val minerals: List<Mineral>
    )
}
