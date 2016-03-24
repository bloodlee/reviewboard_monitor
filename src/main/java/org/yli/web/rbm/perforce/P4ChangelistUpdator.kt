package org.yli.web.rbm.perforce

import com.google.common.collect.Sets
import com.perforce.p4java.core.file.IFileSpec
import com.perforce.p4java.exception.P4JavaException
import com.perforce.p4java.option.server.ChangelistOptions
import com.perforce.p4java.server.IOptionsServer
import com.perforce.p4java.server.ServerFactory
import org.apache.logging.log4j.LogManager
import org.yli.web.rbm.db.RbDb
import java.sql.*
import java.util.*

/**
 * Created by yli on 3/6/2016.
 */
class P4ClUtil(val p4UserName: String, val p4Passwd: String) {

    private val LOGGER = LogManager.getLogger();

    private var P4_SERVER: String? = null;

    private val p4Server: IOptionsServer;

    var status = "Updater is stopped."

    init {
        if (System.getProperty("p4server") == null) {
            P4_SERVER = "localhost:1666"
        } else {
            P4_SERVER = System.getProperty("p4server")
        }

        p4Server = ServerFactory.getOptionsServer("p4java://${P4_SERVER}", null)
    }

    private fun connectToP4() {
        p4Server.connect()
        p4Server.userName = p4UserName
        p4Server.login(p4Passwd, true)
    }

    /**
     * Get the latest changelist ID in database.
     */
    fun getLatestClIdInDb(): Int {
        var conn: Connection? = null
        var st: Statement? = null

        try {
            conn = RbDb.getConnection()

            st = conn.createStatement()

            var rs: ResultSet = st.executeQuery("SELECT max(id) FROM p4_cl")

            if (rs.next()) {
                return rs.getInt(1)
            }

        } catch (e : SQLException) {
            LOGGER.debug(e.message)
        } finally {
            if (conn != null) {
                conn.close()
            }

            if (st != null) {
                st.close()
            }
        }

        return -1
    }

    /**
     * Get the last changelist id from perforce
     */
    fun getLastClIdFromP4(): Int {
        connectToP4()

        val changelists =
            p4Server.getChangelists(
                    1, // get the last one.
                    ArrayList<IFileSpec>(), // file specs
                    null, // client name,
                    null, // username,
                    true, // include integrated
                    true, // submitted only
                    false, // pending only
                    false); // long desc

        return changelists.get(0).id
    }

    fun updateNewClToP4() {
        val lastClIdInP4 = getLastClIdFromP4()
        val latestClIdInDb = getLatestClIdInDb()

        val diffCount = lastClIdInP4 - latestClIdInDb

        status = "Updater is going to start."
        if (diffCount > 0) {
            insertNewRecords(diffCount)
            updateClInformation(latestClIdInDb, lastClIdInP4)
        }
        status = "Updater is stopped"
    }

    private fun updateClInformation(latestClId: Int, lastClId: Int) {
        val startCl = latestClId + 1

        var options = ChangelistOptions();
        options.setOriginalChangelist(true);

        connectToP4()

        var conn: Connection? = null
        var st: PreparedStatement? = null


        try {
            conn = RbDb.getConnection()
            st = conn.prepareStatement("UPDATE p4_cl SET pending_cl_id = ? WHERE id = ?")

            var batchSize = 0

            val renumberedCls = Sets.newHashSet<Int>()

            var index = startCl
            while (index <= lastClId) {
                if (renumberedCls.contains(index)) {
                    continue
                }

                try {
                    val changelist = p4Server.getChangelist(index, options);

                    if (index != changelist.getId()) {
                        renumberedCls.add(changelist.getId());
                    }

                    st.setInt(1, index);
                    st.setInt(2, changelist.getId());

                    status = "Insert changelist ${changelist.id} to database"

                    st.addBatch();
                    ++batchSize;

                    if (batchSize == 1000) {
                        st.executeBatch();
                        Thread.sleep(100);
                        batchSize = 0;
                    }
                } catch (e: P4JavaException) {
                    // System.out.println("CL " + index + " doesn't exist! Skip it");
                }
            }

            if (batchSize > 0) {
                st.executeBatch();
                Thread.sleep(100);
                batchSize = 0;
            }

        } finally {
            if (conn != null) {
                conn.close()
            }

            if (st != null) {
                st.close()
            }
        }
    }

    private fun insertNewRecords(clCount: Int) {
        var conn: Connection? = null
        var st: PreparedStatement? = null

        connectToP4()

        try {
            conn = RbDb.getConnection()

            st = conn.prepareStatement("" +
                    "INSERT INTO p4_cl (id, username, description, client_id, date) " +
                    "VALUES (?, ?, ?, ?, ?)");

            var changelists =
                p4Server.getChangelists(
                        clCount, // max most recent
                        ArrayList<IFileSpec>(), // file specs
                        null, // client name,
                        null, // username,
                        true, // include integrated
                        true, // submitted only
                        false, // pending only
                        false); // long desc

            LOGGER.debug("changelist count is ${changelists.size}");

            var batchSize: Int = 1;
            for (changelist in changelists) {
                st.setInt(1, changelist.id);
                st.setString(2, changelist.username);
                st.setString(3, changelist.description);
                st.setString(4, changelist.clientId);
                st.setDate(5, java.sql.Date(changelist.date.time));

                status = "Update pending changelist (${changelist.id}) information to database."

                st.addBatch();

                if (batchSize == 1000) {
                    st.executeBatch();
                    Thread.sleep(100);
                    batchSize = 0
                }

                ++batchSize;
            }

            if (batchSize > 0) {
                st.executeBatch();
                Thread.sleep(100);
            }

        } finally {
            if (conn != null) {
                conn.close()
            }

            if (st != null) {
                st.close()
            }
        }

    }
}
