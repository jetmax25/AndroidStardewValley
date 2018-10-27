package com.pickledgames.stardewvalleyguide.repositories

import com.pickledgames.stardewvalleyguide.database.FarmDao
import com.pickledgames.stardewvalleyguide.enums.FarmType
import com.pickledgames.stardewvalleyguide.models.Farm
import io.reactivex.Completable
import io.reactivex.Single

class FarmRepository(
        private val farmDao: FarmDao
) {

    private var farms: MutableList<Farm> = mutableListOf()
    private var selectedFarmIndex: Int = 0

    fun toggleSelectedFarm(direction: Int): Single<Farm> {
        return getFarms()
                .map { f ->
                    if (direction == LEFT) {
                        selectedFarmIndex = if (selectedFarmIndex - 1 < 0) f.size - 1 else selectedFarmIndex - 1
                    } else if (direction == RIGHT) {
                        selectedFarmIndex = if (selectedFarmIndex + 1 == f.size) 0 else selectedFarmIndex + 1
                    }
                    f[selectedFarmIndex]
                }
    }

    fun updateSelectedFarm(farm: Farm): Single<Int> {
        return Single.create<Int> { emitter ->
            val updated = farmDao.updateFarm(farm)
            farms[selectedFarmIndex] = farm
            emitter.onSuccess(updated)
        }
    }

    fun getSelectedFarm(): Single<Farm> {
        return getFarms()
                .map { f -> f[selectedFarmIndex] }
    }

    fun updateFarm(farm: Farm, position: Int): Single<Int> {
        return Single.create<Int> { emitter ->
            val updated = farmDao.updateFarm(farm)
            farms[position] = farm
            emitter.onSuccess(updated)
        }
    }

    fun deleteFarm(farm: Farm, position: Int): Completable {
        return Completable.create { emitter ->
            farmDao.deleteFarm(farm)
            farms.removeAt(position)
            emitter.onComplete()
        }
    }

    fun addFarm(farm: Farm): Single<Farm> {
        return Single.create<Farm> { emitter ->
            val id = farmDao.insertFarm(farm)
            val farmWithId = Farm(farm.name, farm.farmType, farm.communityCenterItems, farm.fishes, id)
            farms.add(farmWithId)
            emitter.onSuccess(farmWithId)
        }
    }

    fun getFarms(): Single<List<Farm>> {
        if (farms.isNotEmpty()) return Single.just(farms)
        return farmDao.getAllFarms()
                .flatMap { f ->
                    farms.addAll(f)

                    if (farms.isEmpty()) {
                        return@flatMap addFarm(Farm("Unnamed", FarmType.Standard))
                                .map { farmWithId ->
                                    return@map farms + listOf(farmWithId)
                                }
                    }

                    return@flatMap Single.just(farms)
                }
    }

    companion object {
        const val LEFT = 0
        const val RIGHT = 1
    }
}
