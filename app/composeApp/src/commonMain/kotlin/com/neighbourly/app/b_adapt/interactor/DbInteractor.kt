package com.neighbourly.app.b_adapt.interactor

import com.neighbourly.app.NeighbourlyDB
import com.neighbourly.app.adevice.db.Households
import com.neighbourly.app.adevice.db.Items
import com.neighbourly.app.adevice.db.Users
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
    override suspend fun clear() {
        return withContext(Dispatchers.IO) {
            db.itemsQueries.clear()
            db.householdsQueries.clear()
            db.usersQueries.clear()
        }
    }

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
                it.id?.let { itemId ->
                    db.itemsQueries.addOrUpdateItem(
                        id = itemId.toLong(),
                        type = it.type.name,
                        name = it.name,
                        description = it.description,
                        url = it.url,
                        targetuserid = it.targetUserId?.toLong(),
                        accent = if (it.accent) 1 else 0,
                        images = Json.encodeToString(it.images),
                        files = Json.encodeToString(it.files),
                        startts = it.startTs.toLong(),
                        endts = it.endTs.toLong(),
                        lastmodifiedts = it.lastModifiedTs?.toLong() ?: 0,
                        neighbourhoodid = it.neighbourhoodId?.toLong(),
                        householdid = it.householdId?.toLong(),
                        userid = it.userId?.toLong()
                    )
                }
            }
        }
    }

    override suspend fun stripItems(validIds: List<Int>) {
        withContext(Dispatchers.IO) {
            db.itemsQueries.strip(validIds.map { it.toLong() })
        }
    }

    override suspend fun deleteItem(itemId: Int) {
        withContext(Dispatchers.IO) {
            db.itemsQueries.delete(itemId.toLong())
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

    override suspend fun stripUsers(validIds: List<Int>) {
        withContext(Dispatchers.IO) {
            db.usersQueries.strip(validIds.map { it.toLong() })
        }
    }

    override suspend fun getUser(userId: Int): User {
        return withContext(Dispatchers.IO) {
            db.usersQueries.getUser(userId.toLong()).executeAsOne().toUser()
        }
    }

    override suspend fun getUsers(ids: List<Int>?): List<User> {
        return withContext(Dispatchers.IO) {
            if (ids.isNullOrEmpty()) {
                db.usersQueries.getUsers().executeAsList().map { it.toUser() }
            } else {
                db.usersQueries.getUsersByIds(ids.map { it.toLong() }).executeAsList()
                    .map { it.toUser() }
            }
        }
    }

    override suspend fun getHousehold(householdId: Int): Household {
        return withContext(Dispatchers.IO) {
            db.householdsQueries.getHousehold(householdId.toLong()).executeAsOne().toHousehold()
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

    override suspend fun stripHouseholds(validIds: List<Int>) {
        withContext(Dispatchers.IO) {
            db.householdsQueries.strip(validIds.map { it.toLong() })
        }
    }

    override suspend fun getItem(itemId: Int): Item {
        return withContext(Dispatchers.IO) {
            db.itemsQueries.getItem(itemId.toLong()).executeAsOne().toItem()
        }
    }

    override suspend fun getItemIds(): List<Int> =
        db.itemsQueries.getItemIds().executeAsList().map { it.toInt() }


    override suspend fun filterItems(
        type: ItemType?,
        householdId: Int?,
        ids: List<Int>?
    ): List<Item> {
        return withContext(Dispatchers.IO) {
            val query =
                if (!ids.isNullOrEmpty()) {
                    db.itemsQueries.getItemsByIds(ids.map { it.toLong() })
                } else if (type != null && householdId != null)
                    db.itemsQueries.filterItemsByTypeAndHousehold(
                        type.name,
                        householdId.toLong()
                    )
                else if (type != null)
                    db.itemsQueries.filterItemsByType(type.name)
                else if (householdId != null)
                    db.itemsQueries.filterItemsByHousehold(householdId.toLong())
                else
                    db.itemsQueries.getItems()

            query.executeAsList().map {
                it.toItem()
            }
        }
    }

    override suspend fun filterHouseholds(ids: List<Int>?): List<Household> {
        return withContext(Dispatchers.IO) {
            (ids?.let {
                db.householdsQueries.filterHouseholdsByIds(ids.map { it.toLong() })
            } ?: db.householdsQueries.getHouseholds())
                .executeAsList().map {
                    it.toHousehold()
                }
        }
    }
}

private fun Households.toHousehold(): Household = Household(
    householdid = id.toInt(),
    name = name,
    headid = headid.toInt(),
    about = about,
    imageurl = image,
    location = latitude?.let { lat ->
        longitude?.let { lng ->
            Pair(
                lat.toFloat(),
                lng.toFloat(),
            )
        }
    },
    address = address,
    lastModifiedTs = lastmodifiedts.toInt(),
)

private fun Users.toUser(): User =
    User(
        id = id.toInt(),
        username = username,
        fullname = fullname,
        about = about,
        email = email,
        phone = phone,
        imageurl = image,
        householdid = householdid?.toInt(),
        lastModifiedTs = lastmodifiedts.toInt()
    )

private fun Items.toItem(): Item =
    Item(
        id = id.toInt(),
        type = ItemType.getByName(type),
        name = name,
        description = description,
        url = url,
        targetUserId = targetuserid?.toInt(),
        accent = accent > 0,
        images = images?.let { Json.decodeFromString(it) } ?: emptyList(),
        files = files?.let { Json.decodeFromString(it) } ?: emptyList(),
        startTs = startts.toInt(),
        endTs = endts.toInt(),
        lastModifiedTs = lastmodifiedts.toInt(),
        neighbourhoodId = neighbourhoodid?.toInt(),
        householdId = householdid?.toInt(),
        userId = userid?.toInt(),
    )
