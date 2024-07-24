package com.titanium.seata.holder;

import com.titanium.seata.entity.UndoLog;
import com.titanium.seata.enums.TccStatusEnum;
import io.seata.core.constants.ClientTableColumnsName;
import io.seata.rm.datasource.ConnectionProxy;
import io.seata.rm.datasource.DataSourceProxy;
import io.seata.rm.datasource.undo.mysql.MySQLUndoLogManager;
import lombok.extern.slf4j.Slf4j;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Slf4j
public class MysqlTccResourceHolder extends MySQLUndoLogManager implements TccResourceHolder {
    /**
     * branch_id, xid, context, rollback_info, log_status, log_created, log_modified
     */
    private static final String INSERT_UNDO_LOG_SQL = "INSERT INTO " + UNDO_LOG_TABLE_NAME +
            " (" + ClientTableColumnsName.UNDO_LOG_BRANCH_XID + ", " + ClientTableColumnsName.UNDO_LOG_XID + ", "
            + ClientTableColumnsName.UNDO_LOG_CONTEXT + ", " + ClientTableColumnsName.UNDO_LOG_ROLLBACK_INFO + ", "
            + ClientTableColumnsName.UNDO_LOG_LOG_STATUS + ", " + ClientTableColumnsName.UNDO_LOG_LOG_CREATED + ", "
            + ClientTableColumnsName.UNDO_LOG_LOG_MODIFIED + ")"
            + " VALUES (?, ?, ?, ?, ?, now(6), now(6))";

    private static final String DELETE_UNDO_LOG_BY_CREATE_SQL = "DELETE FROM " + UNDO_LOG_TABLE_NAME +
            " WHERE " + ClientTableColumnsName.UNDO_LOG_LOG_CREATED + " <= ? LIMIT ?";

    private static final String UPDATE_UNDO_LOG_STATUS_SQL = "UPDATE " + UNDO_LOG_TABLE_NAME
            + " SET " + ClientTableColumnsName.UNDO_LOG_LOG_STATUS + "=? WHERE "
            + ClientTableColumnsName.UNDO_LOG_BRANCH_XID + "=?" +
            " AND " + ClientTableColumnsName.UNDO_LOG_XID + "=?" +
            " AND " + ClientTableColumnsName.UNDO_LOG_LOG_STATUS + "=?";

    private DataSourceProxy dataSourceProxy;

    public MysqlTccResourceHolder(DataSourceProxy dataSourceProxy) {
        this.dataSourceProxy = dataSourceProxy;
    }

    /**
     * 将资源标记为已占用
     * @param xid
     * @return
     */
    @Override
    public Boolean prepare(String xid, Long branchId) {
        try {
            Boolean hasUndoLog = hasUndoLog(xid, branchId);
            if (hasUndoLog == false) {
                insertUndoLog(xid, branchId, null, null, TccStatusEnum.TRYING.getCode());
            }
            return true;
        } catch (Exception e) {
            log.error("mysql resource holder prepare failed,cause insert undo log error", e);
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 将资源标记为已提交
     * @param xid
     * @return
     */
    @Override
    public Boolean commit(String xid, Long branchId) {
        try {
            boolean commited = updateUndoLog(xid, branchId, TccStatusEnum.CONFIRM, TccStatusEnum.TRYING);
            if (commited) {
                deleteUndoLog(xid, branchId, dataSourceProxy.getConnection());
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("mysql resource holder prepare failed,cause insert undo log error", e);
            return false;
        }
    }

    /**
     * 将资源标记为已回滚
     * 如果需要判断当前是否为空回滚，可以调用{@linkplain #hasUndoLog(String, long)} 判断
     * @param xid
     * @return
     */
    @Override
    public Boolean cancel(String xid, Long branchId) {
        try {
            boolean canceled = updateUndoLog(xid, branchId, TccStatusEnum.CANCEL, TccStatusEnum.TRYING);
            if (canceled) {
                deleteUndoLog(xid, branchId, dataSourceProxy.getConnection());
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("mysql resource holder prepare failed,cause insert undo log error", e);
            return false;
        }
    }

    /**
     * 获取当前资源状态
     * @param xid
     */
    @Override
    public Integer getStatus(String xid, Long branchId) {
        try {
            UndoLog undoLog = findUndoLog(xid, branchId);
            if (undoLog == null) {
                return TccStatusEnum.INIT.getCode();
            }
            return Optional.ofNullable(undoLog.getLogStatus()).orElse(TccStatusEnum.INIT.getCode());
        } catch (Exception e) {
            log.error("mysql resource holder get status failed,cause find undo log error", e);
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 删除资源
     * @param xid
     * @return
     */
    @Override
    public Boolean delete(String xid, Long branchId) {
        try {
            ConnectionProxy connection = dataSourceProxy.getConnection();
            deleteUndoLog(xid, branchId, connection);
            return true;
        } catch (Exception e) {
            log.error("mysql resource holder delete failed,cause delete undo log error", e);
            log.error(e.getMessage(), e);
            return false;
        }
    }

    private void insertUndoLog(String xid, long branchId, String rollbackCtx, byte[] undoLogContent,
                               Integer state) throws SQLException {
        try (PreparedStatement pst = dataSourceProxy.getConnection().prepareStatement(INSERT_UNDO_LOG_SQL)) {
            pst.setLong(1, branchId);
            pst.setString(2, xid);
            pst.setString(3, rollbackCtx);
            pst.setBytes(4, undoLogContent);
            pst.setInt(5, state);
            pst.executeUpdate();
        } catch (Exception e) {
            if (!(e instanceof SQLException)) {
                e = new SQLException(e);
            }
            throw (SQLException) e;
        }
    }

    private Boolean updateUndoLog(String xid, long branchId, TccStatusEnum newStatus, TccStatusEnum oldStatus) throws SQLException {
        try (PreparedStatement pst = dataSourceProxy.getConnection().prepareStatement(UPDATE_UNDO_LOG_STATUS_SQL)) {
            pst.setInt(1, newStatus.getCode());
            pst.setLong(2, branchId);
            pst.setString(3, xid);
            pst.setInt(4, oldStatus.getCode());
            int effectRows = pst.executeUpdate();
            return effectRows > 0;
        } catch (Exception e) {
            if (!(e instanceof SQLException)) {
                e = new SQLException(e);
            }
            throw (SQLException) e;
        }
    }

    /**
     * 查询回滚日志是否存在
     * @param xid
     * @param branchId
     * @return
     * @throws SQLException
     */
    private boolean hasUndoLog(String xid, long branchId) throws SQLException {
        // Find UNDO LOG
        PreparedStatement selectPST = dataSourceProxy.getConnection().prepareStatement(buildSelectUndoSql());
        selectPST.setLong(1, branchId);
        selectPST.setString(2, xid);
        ResultSet rs = selectPST.executeQuery();

        boolean exists = false;
        while (rs.next()) {
            exists = true;
        }
        return exists;
    }


    /**
     * 查询回滚日志是否存在
     * @param xid
     * @param branchId
     * @return
     * @throws SQLException
     */
    private UndoLog findUndoLog(String xid, long branchId) throws SQLException {
        // Find UNDO LOG
        PreparedStatement selectPST = dataSourceProxy.getConnection().prepareStatement(buildSelectUndoSql());
        selectPST.setLong(1, branchId);
        selectPST.setString(2, xid);
        ResultSet rs = selectPST.executeQuery();

        UndoLog undoLog = null;
        while (rs.next()) {
            if (undoLog == null) {
                undoLog = new UndoLog();
            }
            undoLog.setBranchId(rs.getLong("branch_id"));
            undoLog.setXid(rs.getString("xid"));
            undoLog.setContext(rs.getString("context"));
            undoLog.setRollbackInfo(new String(rs.getBytes("rollback_info")));
            undoLog.setLogStatus(rs.getInt("log_status"));
            undoLog.setLogCreated(rs.getTimestamp("log_created").toLocalDateTime());
            undoLog.setLogModified(rs.getTimestamp("log_modified").toLocalDateTime());
            break;
        }
        return undoLog;
    }
}
