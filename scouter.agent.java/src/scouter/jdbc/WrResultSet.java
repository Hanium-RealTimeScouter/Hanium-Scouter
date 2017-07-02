/*
 *  Copyright 2015 the original author or authors. 
 *  @https://github.com/scouter-project/scouter
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 */

package scouter.jdbc;

import java.sql.SQLException;
import java.util.Map;

import scouter.agent.trace.TraceSQL;

public class WrResultSet implements java.sql.ResultSet {
	java.sql.ResultSet inner;

	public WrResultSet(java.sql.ResultSet inner) {
		this.inner = inner;
	}

	final public java.lang.Object getObject(java.lang.String a0, Map<String, Class<?>> a1) throws java.sql.SQLException {
		return this.inner.getObject(a0, a1);
	}

	final public java.lang.Object getObject(int a0, Map<String, Class<?>> a1) throws java.sql.SQLException {
		return this.inner.getObject(a0, a1);
	}

	final public java.lang.Object getObject(int a0) throws java.sql.SQLException {
		return this.inner.getObject(a0);
	}

	final public java.lang.Object getObject(java.lang.String a0) throws java.sql.SQLException {
		return this.inner.getObject(a0);
	}

	final public boolean getBoolean(java.lang.String a0) throws java.sql.SQLException {
		return this.inner.getBoolean(a0);
	}

	final public boolean getBoolean(int a0) throws java.sql.SQLException {
		return this.inner.getBoolean(a0);
	}

	final public byte getByte(java.lang.String a0) throws java.sql.SQLException {
		return this.inner.getByte(a0);
	}

	final public byte getByte(int a0) throws java.sql.SQLException {
		return this.inner.getByte(a0);
	}

	final public short getShort(int a0) throws java.sql.SQLException {
		return this.inner.getShort(a0);
	}

	final public short getShort(java.lang.String a0) throws java.sql.SQLException {
		return this.inner.getShort(a0);
	}

	final public int getInt(java.lang.String a0) throws java.sql.SQLException {
		return this.inner.getInt(a0);
	}

	final public int getInt(int a0) throws java.sql.SQLException {
		return this.inner.getInt(a0);
	}

	final public long getLong(java.lang.String a0) throws java.sql.SQLException {
		return this.inner.getLong(a0);
	}

	final public long getLong(int a0) throws java.sql.SQLException {
		return this.inner.getLong(a0);
	}

	final public float getFloat(int a0) throws java.sql.SQLException {
		return this.inner.getFloat(a0);
	}

	final public float getFloat(java.lang.String a0) throws java.sql.SQLException {
		return this.inner.getFloat(a0);
	}

	final public double getDouble(java.lang.String a0) throws java.sql.SQLException {
		return this.inner.getDouble(a0);
	}

	final public double getDouble(int a0) throws java.sql.SQLException {
		return this.inner.getDouble(a0);
	}

	final public byte[] getBytes(int a0) throws java.sql.SQLException {
		return this.inner.getBytes(a0);
	}

	final public byte[] getBytes(java.lang.String a0) throws java.sql.SQLException {
		return this.inner.getBytes(a0);
	}

	final public java.sql.Array getArray(java.lang.String a0) throws java.sql.SQLException {
		return this.inner.getArray(a0);
	}

	final public java.sql.Array getArray(int a0) throws java.sql.SQLException {
		return this.inner.getArray(a0);
	}

	final public boolean next() throws java.sql.SQLException {
		return TraceSQL.rsnext(this.inner.next());
	}

	final public java.net.URL getURL(java.lang.String a0) throws java.sql.SQLException {
		return this.inner.getURL(a0);
	}

	final public java.net.URL getURL(int a0) throws java.sql.SQLException {
		return this.inner.getURL(a0);
	}

	final public void close() throws java.sql.SQLException {
		TraceSQL.rsclose(this);
		this.inner.close();
	}

	final public int getType() throws java.sql.SQLException {
		return this.inner.getType();
	}

	final public boolean previous() throws java.sql.SQLException {
		return this.inner.previous();
	}

	final public java.sql.Ref getRef(java.lang.String a0) throws java.sql.SQLException {
		return this.inner.getRef(a0);
	}

	final public java.sql.Ref getRef(int a0) throws java.sql.SQLException {
		return this.inner.getRef(a0);
	}

	final public java.lang.String getString(java.lang.String a0) throws java.sql.SQLException {
		return this.inner.getString(a0);
	}

	final public java.lang.String getString(int a0) throws java.sql.SQLException {
		return this.inner.getString(a0);
	}

	final public java.sql.Time getTime(java.lang.String a0, java.util.Calendar a1) throws java.sql.SQLException {
		return this.inner.getTime(a0, a1);
	}

	final public java.sql.Time getTime(int a0, java.util.Calendar a1) throws java.sql.SQLException {
		return this.inner.getTime(a0, a1);
	}

	final public java.sql.Time getTime(java.lang.String a0) throws java.sql.SQLException {
		return this.inner.getTime(a0);
	}

	final public java.sql.Time getTime(int a0) throws java.sql.SQLException {
		return this.inner.getTime(a0);
	}

	final public java.sql.Date getDate(int a0, java.util.Calendar a1) throws java.sql.SQLException {
		return this.inner.getDate(a0, a1);
	}

	final public java.sql.Date getDate(java.lang.String a0) throws java.sql.SQLException {
		return this.inner.getDate(a0);
	}

	final public java.sql.Date getDate(java.lang.String a0, java.util.Calendar a1) throws java.sql.SQLException {
		return this.inner.getDate(a0, a1);
	}

	final public java.sql.Date getDate(int a0) throws java.sql.SQLException {
		return this.inner.getDate(a0);
	}

	final public boolean first() throws java.sql.SQLException {
		return this.inner.first();
	}

	final public boolean last() throws java.sql.SQLException {
		return this.inner.last();
	}

	final public boolean wasNull() throws java.sql.SQLException {
		return this.inner.wasNull();
	}

	final public java.math.BigDecimal getBigDecimal(int a0, int a1) throws java.sql.SQLException {
		return this.inner.getBigDecimal(a0, a1);
	}

	final public java.math.BigDecimal getBigDecimal(java.lang.String a0, int a1) throws java.sql.SQLException {
		return this.inner.getBigDecimal(a0, a1);
	}

	final public java.math.BigDecimal getBigDecimal(java.lang.String a0) throws java.sql.SQLException {
		return this.inner.getBigDecimal(a0);
	}

	final public java.math.BigDecimal getBigDecimal(int a0) throws java.sql.SQLException {
		return this.inner.getBigDecimal(a0);
	}

	final public java.sql.Timestamp getTimestamp(int a0, java.util.Calendar a1) throws java.sql.SQLException {
		return this.inner.getTimestamp(a0, a1);
	}

	final public java.sql.Timestamp getTimestamp(java.lang.String a0, java.util.Calendar a1)
			throws java.sql.SQLException {
		return this.inner.getTimestamp(a0, a1);
	}

	final public java.sql.Timestamp getTimestamp(java.lang.String a0) throws java.sql.SQLException {
		return this.inner.getTimestamp(a0);
	}

	final public java.sql.Timestamp getTimestamp(int a0) throws java.sql.SQLException {
		return this.inner.getTimestamp(a0);
	}

	final public java.io.InputStream getAsciiStream(java.lang.String a0) throws java.sql.SQLException {
		return this.inner.getAsciiStream(a0);
	}

	final public java.io.InputStream getAsciiStream(int a0) throws java.sql.SQLException {
		return this.inner.getAsciiStream(a0);
	}

	final public java.io.InputStream getUnicodeStream(java.lang.String a0) throws java.sql.SQLException {
		return this.inner.getUnicodeStream(a0);
	}

	final public java.io.InputStream getUnicodeStream(int a0) throws java.sql.SQLException {
		return this.inner.getUnicodeStream(a0);
	}

	final public java.io.InputStream getBinaryStream(java.lang.String a0) throws java.sql.SQLException {
		return this.inner.getBinaryStream(a0);
	}

	final public java.io.InputStream getBinaryStream(int a0) throws java.sql.SQLException {
		return this.inner.getBinaryStream(a0);
	}

	final public java.sql.SQLWarning getWarnings() throws java.sql.SQLException {
		return this.inner.getWarnings();
	}

	final public void clearWarnings() throws java.sql.SQLException {
		this.inner.clearWarnings();
	}

	final public java.lang.String getCursorName() throws java.sql.SQLException {
		return this.inner.getCursorName();
	}

	final public java.sql.ResultSetMetaData getMetaData() throws java.sql.SQLException {
		return this.inner.getMetaData();
	}

	final public int findColumn(java.lang.String a0) throws java.sql.SQLException {
		return this.inner.findColumn(a0);
	}

	final public java.io.Reader getCharacterStream(int a0) throws java.sql.SQLException {
		return this.inner.getCharacterStream(a0);
	}

	final public java.io.Reader getCharacterStream(java.lang.String a0) throws java.sql.SQLException {
		return this.inner.getCharacterStream(a0);
	}

	final public boolean isBeforeFirst() throws java.sql.SQLException {
		return this.inner.isBeforeFirst();
	}

	final public boolean isAfterLast() throws java.sql.SQLException {
		return this.inner.isAfterLast();
	}

	final public boolean isFirst() throws java.sql.SQLException {
		return this.inner.isFirst();
	}

	final public boolean isLast() throws java.sql.SQLException {
		return this.inner.isLast();
	}

	final public void beforeFirst() throws java.sql.SQLException {
		this.inner.beforeFirst();
	}

	final public void afterLast() throws java.sql.SQLException {
		this.inner.afterLast();
	}

	final public int getRow() throws java.sql.SQLException {
		return this.inner.getRow();
	}

	final public boolean absolute(int a0) throws java.sql.SQLException {
		return this.inner.absolute(a0);
	}

	final public boolean relative(int a0) throws java.sql.SQLException {
		return this.inner.relative(a0);
	}

	final public void setFetchDirection(int a0) throws java.sql.SQLException {
		this.inner.setFetchDirection(a0);
	}

	final public int getFetchDirection() throws java.sql.SQLException {
		return this.inner.getFetchDirection();
	}

	final public void setFetchSize(int a0) throws java.sql.SQLException {
		this.inner.setFetchSize(a0);
	}

	final public int getFetchSize() throws java.sql.SQLException {
		return this.inner.getFetchSize();
	}

	final public int getConcurrency() throws java.sql.SQLException {
		return this.inner.getConcurrency();
	}

	final public boolean rowUpdated() throws java.sql.SQLException {
		return this.inner.rowUpdated();
	}

	final public boolean rowInserted() throws java.sql.SQLException {
		return this.inner.rowInserted();
	}

	final public boolean rowDeleted() throws java.sql.SQLException {
		return this.inner.rowDeleted();
	}

	final public void updateNull(java.lang.String a0) throws java.sql.SQLException {
		this.inner.updateNull(a0);
	}

	final public void updateNull(int a0) throws java.sql.SQLException {
		this.inner.updateNull(a0);
	}

	final public void updateBoolean(java.lang.String a0, boolean a1) throws java.sql.SQLException {
		this.inner.updateBoolean(a0, a1);
	}

	final public void updateBoolean(int a0, boolean a1) throws java.sql.SQLException {
		this.inner.updateBoolean(a0, a1);
	}

	final public void updateByte(int a0, byte a1) throws java.sql.SQLException {
		this.inner.updateByte(a0, a1);
	}

	final public void updateByte(java.lang.String a0, byte a1) throws java.sql.SQLException {
		this.inner.updateByte(a0, a1);
	}

	final public void updateShort(java.lang.String a0, short a1) throws java.sql.SQLException {
		this.inner.updateShort(a0, a1);
	}

	final public void updateShort(int a0, short a1) throws java.sql.SQLException {
		this.inner.updateShort(a0, a1);
	}

	final public void updateInt(int a0, int a1) throws java.sql.SQLException {
		this.inner.updateInt(a0, a1);
	}

	final public void updateInt(java.lang.String a0, int a1) throws java.sql.SQLException {
		this.inner.updateInt(a0, a1);
	}

	final public void updateLong(int a0, long a1) throws java.sql.SQLException {
		this.inner.updateLong(a0, a1);
	}

	final public void updateLong(java.lang.String a0, long a1) throws java.sql.SQLException {
		this.inner.updateLong(a0, a1);
	}

	final public void updateFloat(int a0, float a1) throws java.sql.SQLException {
		this.inner.updateFloat(a0, a1);
	}

	final public void updateFloat(java.lang.String a0, float a1) throws java.sql.SQLException {
		this.inner.updateFloat(a0, a1);
	}

	final public void updateDouble(java.lang.String a0, double a1) throws java.sql.SQLException {
		this.inner.updateDouble(a0, a1);
	}

	final public void updateDouble(int a0, double a1) throws java.sql.SQLException {
		this.inner.updateDouble(a0, a1);
	}

	final public void updateBigDecimal(java.lang.String a0, java.math.BigDecimal a1) throws java.sql.SQLException {
		this.inner.updateBigDecimal(a0, a1);
	}

	final public void updateBigDecimal(int a0, java.math.BigDecimal a1) throws java.sql.SQLException {
		this.inner.updateBigDecimal(a0, a1);
	}

	final public void updateString(java.lang.String a0, java.lang.String a1) throws java.sql.SQLException {
		this.inner.updateString(a0, a1);
	}

	final public void updateString(int a0, java.lang.String a1) throws java.sql.SQLException {
		this.inner.updateString(a0, a1);
	}

	final public void updateBytes(java.lang.String a0, byte[] a1) throws java.sql.SQLException {
		this.inner.updateBytes(a0, a1);
	}

	final public void updateBytes(int a0, byte[] a1) throws java.sql.SQLException {
		this.inner.updateBytes(a0, a1);
	}

	final public void updateDate(java.lang.String a0, java.sql.Date a1) throws java.sql.SQLException {
		this.inner.updateDate(a0, a1);
	}

	final public void updateDate(int a0, java.sql.Date a1) throws java.sql.SQLException {
		this.inner.updateDate(a0, a1);
	}

	final public void updateTime(int a0, java.sql.Time a1) throws java.sql.SQLException {
		this.inner.updateTime(a0, a1);
	}

	final public void updateTime(java.lang.String a0, java.sql.Time a1) throws java.sql.SQLException {
		this.inner.updateTime(a0, a1);
	}

	final public void updateTimestamp(int a0, java.sql.Timestamp a1) throws java.sql.SQLException {
		this.inner.updateTimestamp(a0, a1);
	}

	final public void updateTimestamp(java.lang.String a0, java.sql.Timestamp a1) throws java.sql.SQLException {
		this.inner.updateTimestamp(a0, a1);
	}

	final public void updateAsciiStream(int a0, java.io.InputStream a1) throws java.sql.SQLException {
		this.inner.updateAsciiStream(a0, a1);
	}

	final public void updateAsciiStream(int a0, java.io.InputStream a1, long a2) throws java.sql.SQLException {
		this.inner.updateAsciiStream(a0, a1, a2);
	}

	final public void updateAsciiStream(java.lang.String a0, java.io.InputStream a1, long a2)
			throws java.sql.SQLException {
		this.inner.updateAsciiStream(a0, a1, a2);
	}

	final public void updateAsciiStream(int a0, java.io.InputStream a1, int a2) throws java.sql.SQLException {
		this.inner.updateAsciiStream(a0, a1, a2);
	}

	final public void updateAsciiStream(java.lang.String a0, java.io.InputStream a1, int a2)
			throws java.sql.SQLException {
		this.inner.updateAsciiStream(a0, a1, a2);
	}

	final public void updateAsciiStream(java.lang.String a0, java.io.InputStream a1) throws java.sql.SQLException {
		this.inner.updateAsciiStream(a0, a1);
	}

	final public void updateBinaryStream(java.lang.String a0, java.io.InputStream a1, long a2)
			throws java.sql.SQLException {
		this.inner.updateBinaryStream(a0, a1, a2);
	}

	final public void updateBinaryStream(java.lang.String a0, java.io.InputStream a1) throws java.sql.SQLException {
		this.inner.updateBinaryStream(a0, a1);
	}

	final public void updateBinaryStream(int a0, java.io.InputStream a1, int a2) throws java.sql.SQLException {
		this.inner.updateBinaryStream(a0, a1, a2);
	}

	final public void updateBinaryStream(java.lang.String a0, java.io.InputStream a1, int a2)
			throws java.sql.SQLException {
		this.inner.updateBinaryStream(a0, a1, a2);
	}

	final public void updateBinaryStream(int a0, java.io.InputStream a1, long a2) throws java.sql.SQLException {
		this.inner.updateBinaryStream(a0, a1, a2);
	}

	final public void updateBinaryStream(int a0, java.io.InputStream a1) throws java.sql.SQLException {
		this.inner.updateBinaryStream(a0, a1);
	}

	final public void updateCharacterStream(int a0, java.io.Reader a1, long a2) throws java.sql.SQLException {
		this.inner.updateCharacterStream(a0, a1, a2);
	}

	final public void updateCharacterStream(java.lang.String a0, java.io.Reader a1) throws java.sql.SQLException {
		this.inner.updateCharacterStream(a0, a1);
	}

	final public void updateCharacterStream(java.lang.String a0, java.io.Reader a1, int a2)
			throws java.sql.SQLException {
		this.inner.updateCharacterStream(a0, a1, a2);
	}

	final public void updateCharacterStream(java.lang.String a0, java.io.Reader a1, long a2)
			throws java.sql.SQLException {
		this.inner.updateCharacterStream(a0, a1, a2);
	}

	final public void updateCharacterStream(int a0, java.io.Reader a1) throws java.sql.SQLException {
		this.inner.updateCharacterStream(a0, a1);
	}

	final public void updateCharacterStream(int a0, java.io.Reader a1, int a2) throws java.sql.SQLException {
		this.inner.updateCharacterStream(a0, a1, a2);
	}

	final public void updateObject(int a0, java.lang.Object a1, int a2) throws java.sql.SQLException {
		this.inner.updateObject(a0, a1, a2);
	}

	final public void updateObject(int a0, java.lang.Object a1) throws java.sql.SQLException {
		this.inner.updateObject(a0, a1);
	}

	final public void updateObject(java.lang.String a0, java.lang.Object a1, int a2) throws java.sql.SQLException {
		this.inner.updateObject(a0, a1, a2);
	}

	final public void updateObject(java.lang.String a0, java.lang.Object a1) throws java.sql.SQLException {
		this.inner.updateObject(a0, a1);
	}

	final public void insertRow() throws java.sql.SQLException {
		this.inner.insertRow();
	}

	final public void updateRow() throws java.sql.SQLException {
		this.inner.updateRow();
	}

	final public void deleteRow() throws java.sql.SQLException {
		this.inner.deleteRow();
	}

	final public void refreshRow() throws java.sql.SQLException {
		this.inner.refreshRow();
	}

	final public void cancelRowUpdates() throws java.sql.SQLException {
		this.inner.cancelRowUpdates();
	}

	final public void moveToInsertRow() throws java.sql.SQLException {
		this.inner.moveToInsertRow();
	}

	final public void moveToCurrentRow() throws java.sql.SQLException {
		this.inner.moveToCurrentRow();
	}

	final public java.sql.Statement getStatement() throws java.sql.SQLException {
		return this.inner.getStatement();
	}

	final public java.sql.Blob getBlob(int a0) throws java.sql.SQLException {
		return this.inner.getBlob(a0);
	}

	final public java.sql.Blob getBlob(java.lang.String a0) throws java.sql.SQLException {
		return this.inner.getBlob(a0);
	}

	final public java.sql.Clob getClob(int a0) throws java.sql.SQLException {
		return this.inner.getClob(a0);
	}

	final public java.sql.Clob getClob(java.lang.String a0) throws java.sql.SQLException {
		return this.inner.getClob(a0);
	}

	final public void updateRef(java.lang.String a0, java.sql.Ref a1) throws java.sql.SQLException {
		this.inner.updateRef(a0, a1);
	}

	final public void updateRef(int a0, java.sql.Ref a1) throws java.sql.SQLException {
		this.inner.updateRef(a0, a1);
	}

	final public void updateBlob(java.lang.String a0, java.io.InputStream a1, long a2) throws java.sql.SQLException {
		this.inner.updateBlob(a0, a1, a2);
	}

	final public void updateBlob(int a0, java.io.InputStream a1, long a2) throws java.sql.SQLException {
		this.inner.updateBlob(a0, a1, a2);
	}

	final public void updateBlob(java.lang.String a0, java.sql.Blob a1) throws java.sql.SQLException {
		this.inner.updateBlob(a0, a1);
	}

	final public void updateBlob(int a0, java.io.InputStream a1) throws java.sql.SQLException {
		this.inner.updateBlob(a0, a1);
	}

	final public void updateBlob(int a0, java.sql.Blob a1) throws java.sql.SQLException {
		this.inner.updateBlob(a0, a1);
	}

	final public void updateBlob(java.lang.String a0, java.io.InputStream a1) throws java.sql.SQLException {
		this.inner.updateBlob(a0, a1);
	}

	final public void updateClob(int a0, java.io.Reader a1) throws java.sql.SQLException {
		this.inner.updateClob(a0, a1);
	}

	final public void updateClob(java.lang.String a0, java.sql.Clob a1) throws java.sql.SQLException {
		this.inner.updateClob(a0, a1);
	}

	final public void updateClob(java.lang.String a0, java.io.Reader a1) throws java.sql.SQLException {
		this.inner.updateClob(a0, a1);
	}

	final public void updateClob(int a0, java.io.Reader a1, long a2) throws java.sql.SQLException {
		this.inner.updateClob(a0, a1, a2);
	}

	final public void updateClob(java.lang.String a0, java.io.Reader a1, long a2) throws java.sql.SQLException {
		this.inner.updateClob(a0, a1, a2);
	}

	final public void updateClob(int a0, java.sql.Clob a1) throws java.sql.SQLException {
		this.inner.updateClob(a0, a1);
	}

	final public void updateArray(java.lang.String a0, java.sql.Array a1) throws java.sql.SQLException {
		this.inner.updateArray(a0, a1);
	}

	final public void updateArray(int a0, java.sql.Array a1) throws java.sql.SQLException {
		this.inner.updateArray(a0, a1);
	}

	final public java.sql.RowId getRowId(int a0) throws java.sql.SQLException {
		return this.inner.getRowId(a0);
	}

	final public java.sql.RowId getRowId(java.lang.String a0) throws java.sql.SQLException {
		return this.inner.getRowId(a0);
	}

	final public void updateRowId(int a0, java.sql.RowId a1) throws java.sql.SQLException {
		this.inner.updateRowId(a0, a1);
	}

	final public void updateRowId(java.lang.String a0, java.sql.RowId a1) throws java.sql.SQLException {
		this.inner.updateRowId(a0, a1);
	}

	final public int getHoldability() throws java.sql.SQLException {
		return this.inner.getHoldability();
	}

	final public boolean isClosed() throws java.sql.SQLException {
		return this.inner.isClosed();
	}

	final public void updateNString(int a0, java.lang.String a1) throws java.sql.SQLException {
		this.inner.updateNString(a0, a1);
	}

	final public void updateNString(java.lang.String a0, java.lang.String a1) throws java.sql.SQLException {
		this.inner.updateNString(a0, a1);
	}

	final public void updateNClob(java.lang.String a0, java.io.Reader a1, long a2) throws java.sql.SQLException {
		this.inner.updateNClob(a0, a1, a2);
	}

	final public void updateNClob(java.lang.String a0, java.sql.NClob a1) throws java.sql.SQLException {
		this.inner.updateNClob(a0, a1);
	}

	final public void updateNClob(java.lang.String a0, java.io.Reader a1) throws java.sql.SQLException {
		this.inner.updateNClob(a0, a1);
	}

	final public void updateNClob(int a0, java.io.Reader a1) throws java.sql.SQLException {
		this.inner.updateNClob(a0, a1);
	}

	final public void updateNClob(int a0, java.io.Reader a1, long a2) throws java.sql.SQLException {
		this.inner.updateNClob(a0, a1, a2);
	}

	final public void updateNClob(int a0, java.sql.NClob a1) throws java.sql.SQLException {
		this.inner.updateNClob(a0, a1);
	}

	final public java.sql.NClob getNClob(int a0) throws java.sql.SQLException {
		return this.inner.getNClob(a0);
	}

	final public java.sql.NClob getNClob(java.lang.String a0) throws java.sql.SQLException {
		return this.inner.getNClob(a0);
	}

	final public java.sql.SQLXML getSQLXML(int a0) throws java.sql.SQLException {
		return this.inner.getSQLXML(a0);
	}

	final public java.sql.SQLXML getSQLXML(java.lang.String a0) throws java.sql.SQLException {
		return this.inner.getSQLXML(a0);
	}

	final public void updateSQLXML(java.lang.String a0, java.sql.SQLXML a1) throws java.sql.SQLException {
		this.inner.updateSQLXML(a0, a1);
	}

	final public void updateSQLXML(int a0, java.sql.SQLXML a1) throws java.sql.SQLException {
		this.inner.updateSQLXML(a0, a1);
	}

	final public java.lang.String getNString(int a0) throws java.sql.SQLException {
		return this.inner.getNString(a0);
	}

	final public java.lang.String getNString(java.lang.String a0) throws java.sql.SQLException {
		return this.inner.getNString(a0);
	}

	final public java.io.Reader getNCharacterStream(int a0) throws java.sql.SQLException {
		return this.inner.getNCharacterStream(a0);
	}

	final public java.io.Reader getNCharacterStream(java.lang.String a0) throws java.sql.SQLException {
		return this.inner.getNCharacterStream(a0);
	}

	final public void updateNCharacterStream(int a0, java.io.Reader a1, long a2) throws java.sql.SQLException {
		this.inner.updateNCharacterStream(a0, a1, a2);
	}

	final public void updateNCharacterStream(int a0, java.io.Reader a1) throws java.sql.SQLException {
		this.inner.updateNCharacterStream(a0, a1);
	}

	final public void updateNCharacterStream(java.lang.String a0, java.io.Reader a1, long a2)
			throws java.sql.SQLException {
		this.inner.updateNCharacterStream(a0, a1, a2);
	}

	final public void updateNCharacterStream(java.lang.String a0, java.io.Reader a1) throws java.sql.SQLException {
		this.inner.updateNCharacterStream(a0, a1);
	}

	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

}