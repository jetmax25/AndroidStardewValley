package com.pickledgames.stardewvalleyguide.repositories

import android.content.SharedPreferences
import com.pickledgames.stardewvalleyguide.database.FarmDao
import com.pickledgames.stardewvalleyguide.enums.FarmType
import com.pickledgames.stardewvalleyguide.models.Farm
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject

class FarmRepository(
        private val farmDao: FarmDao,
        private val sharedPreferences: SharedPreferences
) {

    private val farms: MutableList<Farm> = mutableListOf()
    private var selectedFarmSubject: PublishSubject<Farm> = PublishSubject.create()
    private var selectedFarmIndex: Int = 0
    private var selectedFarmId: Long = sharedPreferences.getLong(SELECTED_FARM_ID, -1)

    fun toggleSelectedFarm(direction: Int): Single<Farm> {
        return getFarms()
                .map { f ->
                    if (direction == LEFT) {
                        selectedFarmIndex = if (selectedFarmIndex - 1 < 0) f.size - 1 else selectedFarmIndex - 1
                    } else if (direction == RIGHT) {
                        selectedFarmIndex = if (selectedFarmIndex + 1 == f.size) 0 else selectedFarmIndex + 1
                    }

                    val farm = f[selectedFarmIndex]
                    selectedFarmSubject.onNext(farm)
                    sharedPreferences.edit().putLong(SELECTED_FARM_ID, farm.id).apply()
                    return@map farm
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

    fun getSelectedFarmChanges(): Observable<Farm> {
        return selectedFarmSubject
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
            if (selectedFarmId == farm.id || selectedFarmIndex == position) {
                selectedFarmIndex = 0
                selectedFarmId = farms[selectedFarmIndex].id
                sharedPreferences.edit().putLong(SELECTED_FARM_ID, farm.id).apply()
            }
            emitter.onComplete()
        }
    }

    fun addFarm(farm: Farm): Single<Farm> {
        return Single.create<Farm> { emitter ->
            val id = farmDao.insertFarm(farm)
            val farmWithId = Farm(farm.name, farm.farmType, farm.communityCenterItems, farm.fishes, farm.museumItems, id)
            farms.add(farmWithId)
            selectedFarmIndex = farms.size - 1
            selectedFarmId = id
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
                .doOnSuccess { f ->
                    for (i in 0 until f.size) {
                        val farm = f[i]
                        if (farm.id == selectedFarmId) {
                            selectedFarmIndex = i
                            break
                        }
                    }
                }
    }

    companion object {
        const val LEFT = 0
        const val RIGHT = 1
        val SELECTED_FARM_ID = "${FarmRepository::class.java.simpleName}_SELECTED_FARM_ID"
    }
}
