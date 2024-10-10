package com.neighbourly.app.b_adapt.interactor

import com.neighbourly.app.NeighbourlyDB
import com.neighbourly.app.d_entity.data.Household
import com.neighbourly.app.d_entity.data.Item
import com.neighbourly.app.d_entity.data.ItemType
import com.neighbourly.app.d_entity.data.User
import com.neighbourly.app.d_entity.interf.Db
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.math.max

class DbInteractor(val db: NeighbourlyDB) : Db {
    override suspend fun getLastModifTs(): Int {
        return withContext(Dispatchers.IO) {
            max(
                db.itemsQueries.getLastModified().executeAsOne().lastModifTs?.toInt() ?: 0,
                max(
                    db.usersQueries.getLastModified().executeAsOne().lastModifTs?.toInt() ?: 0,
                    db.householdsQueries.getLastModified().executeAsOne().lastModifTs?.toInt() ?: 0
                )
            )
        }
    }

    override suspend fun storeItems(items: List<Item>) {
        withContext(Dispatchers.IO) {
            items.forEach {
                db.itemsQueries.addOrUpdateItem(
                    id = it.id.toLong(),
                    type = it.type.name,
                    name = it.name,
                    description = it.description,
                    url = it.url,
                    targetuserid = it.targetUserId?.toLong(),
                    images = Json.encodeToString(it.images),
                    files = Json.encodeToString(it.files),
                    startts = it.startTs?.toLong(),
                    endts = it.endTs?.toLong(),
                    lastmodifiedts = it.lastModifiedTs.toLong(),
                    neighbourhoodid = it.neighbourhoodId?.toLong(),
                    householdid = it.householdId?.toLong(),
                    userid = it.userId?.toLong()
                )
            }
        }
    }

    override suspend fun storeUsers(users: List<User>) {
        withContext(Dispatchers.IO) {
            users.forEach {
                db.usersQueries.addOrUpdateUser(
                    id = it.id.toLong(),
                    username = it.username,
                    fullname = it.fullname,
                    about = it.about,
                    email = it.email,
                    phone = it.phone,
                    image = it.imageurl,
                    householdid = it.householdid?.toLong(),
                    lastmodifiedts = it.lastModifiedTs.toLong(),
                )
            }
        }
    }

    override suspend fun storeHouseholds(households: List<Household>) {
        withContext(Dispatchers.IO) {
            households.forEach {
                db.householdsQueries.addOrUpdateHousehold(
                    id = it.householdid.toLong(),
                    name = it.name,
                    about = it.about,
                    image = it.imageurl,
                    address = it.address,
                    headid = it.headid.toLong(),
                    latitude = it.location?.first?.toDouble(),
                    longitude = it.location?.second?.toDouble(),
                    lastmodifiedts = it.lastModifiedTs.toLong(),
                )
            }
        }
    }

    override suspend fun filterItems(type: ItemType): List<Item> {
        return withContext(Dispatchers.IO) {
            db.itemsQueries.filterItems(type.name).executeAsList().map {
                Item(
                    id = it.id.toInt(),
                    type = ItemType.getByName(it.type),
                    name = it.name,
                    description = it.description,
                    url = it.url,
                    targetUserId = it.targetuserid?.toInt(),
                    images = it.images?.let { Json.decodeFromString(it) } ?: emptyMap(),
                    files = it.files?.let { Json.decodeFromString(it) } ?: emptyMap(),
                    startTs = it.startts?.toInt(),
                    endTs = it.endts?.toInt(),
                    lastModifiedTs = it.lastmodifiedts.toInt(),
                    neighbourhoodId = it.neighbourhoodid?.toInt(),
                    householdId = it.householdid?.toInt(),
                    userId = it.userid?.toInt(),
                )
            }
        }
    }

    override suspend fun filterHouseholds(): List<Household> {
        return withContext(Dispatchers.IO) {
            db.householdsQueries.filterHouseholds().executeAsList().map {
                Household(
                    householdid = it.id.toInt(),
                    name = it.name,
                    headid = it.headid.toInt(),
                    about = it.about,
                    imageurl = it.image,
                    location = it.latitude?.let { lat ->
                        it.longitude?.let { lng ->
                            Pair(
                                lat.toFloat(),
                                lng.toFloat(),
                            )
                        }
                    },
                    address = it.address,
                    lastModifiedTs = it.lastmodifiedts.toInt(),
                )
            }
        }
    }
}