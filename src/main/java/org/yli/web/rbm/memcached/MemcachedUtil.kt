package org.yli.web.rbm.memcached

import net.spy.memcached.AddrUtil
import net.spy.memcached.MemcachedClient
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.joda.time.DateTime
import org.joda.time.Duration

/**
 * Created by yli on 2/16/2016.
 */
class MemcachedUtil(val host: String, val port: String = "11211") {

    companion object Statics {
        private val LOGGER: Logger = LogManager.getLogger(MemcachedUtil::class)
    }

    private val client: MemcachedClient

    init {
        client = MemcachedClient(AddrUtil.getAddresses("$host:$port"))
    }

    /**
     * set the cache
     *
     * @param key the key
     * @param value the value
     *
     * @return true or false, which indicates if the value is set correctly.
     */
    fun set(key: String, value: String) : Boolean {
        val now = DateTime.now()
        val tomorrow = now.plusDays(1)

        val secondsToMidnight = DateTime(tomorrow.year, tomorrow.monthOfYear, tomorrow.dayOfMonth, 0, 0, 0)
        val seconds = Math.min(Duration(now, secondsToMidnight).toStandardSeconds().seconds, 50)

        // LOGGER.debug("to midnight $seconds")

        val result = client.set(key, seconds, value)
        return result.get()
    }

    /**
     * get the cached value by key
     *
     * @param key the key
     * @return the value. Null for nothing found.
     */
    fun get(key: String) :  String? {
        val value = client.get(key)
        if (value is String) {
            return value
        } else {
            return null
        }
    }

}

fun main(args: Array<String>) {
    var util = MemcachedUtil(host = "localhost")

    System.out.println(util.get("abc"))

    util.set("abc", "123")

    System.out.println(util.get("abc"))
}