package service

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import repository.*
import java.util.concurrent.TimeUnit

class NumUsers (dbOps: UserDBOps) {
    var numUsers = dbOps.getIDs()
    fun getIDList (): MutableList<Int> {
        return numUsers
    }
    fun updateAdd (newID: Int) {
        numUsers.add(newID)
    }
    fun updateSubtract (oldID: Int) {
        numUsers.remove(oldID)
    }
}

class CacheOps (private var dbOps: UserDBOps, private var numUsers: NumUsers)
{
    private var cache: LoadingCache<Int, User> = Caffeine.newBuilder()
        .maximumSize(10000)
        .expireAfterWrite(2, TimeUnit.MINUTES)
        .build {
                key -> dbOps.getUserByID(key)
        }

    fun getAllUsersCache (): String {
        val output: MutableMap<Int, User> = mutableMapOf()

        for (counter in numUsers.getIDList()) {
            try {
                val u: User = cache.get(counter)
                output[counter] = u
            } catch (_: Exception) {
                continue
            }
        }
        return output.toString()
    }
    fun getUserByIDCache (id: Int): String {
        return cache.get(id).toString()
    }
    fun updateUserCache (id: Int, userInfo: User) {
        dbOps.updateUser(id, userInfo)
        cache.refresh(id)
    }
    fun deleteUserCache (id: Int) {
        dbOps.deleteUser(id)
        numUsers.updateSubtract(id)
        cache.invalidate(id)
    }
}


