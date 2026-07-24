/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.milvus.jdbc.internal;

import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import jakarta.annotation.Nullable;
import org.hibernate.internal.build.AllowReflection;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public abstract class AbstractResultSet<T extends Statement> implements ResultSet {

	private static final ZoneId UTC = ZoneId.of("UTC");
	protected final T statement;

	private int fetchDirection;
	private int fetchSize;
	private int resultSetType;

	private boolean closed = false;
	protected boolean wasNull = false;
	protected int position = -1;

	public AbstractResultSet(T statement) {
		this.statement = statement;
	}

	protected abstract int resultSize();

	@Override
	public boolean next() throws SQLException {
		wasNull = false;
		if (position + 1 < resultSize()) {
			position++;
			return true;
		}
		position = resultSize();
		return false;
	}

	@Override
	public void close() throws SQLException {
		closed = true;
	}

	protected void checkClosed() throws SQLException {
		if (closed) {
			throw new SQLException("ResultSet is closed");
		}
	}

	protected void checkIndex(int index, int size) throws SQLException {
		checkClosed();
		if ( index <= 0 || index > size ) {
			throw new SQLException( "Column index out of bounds: " + index );
		}
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		checkClosed();
		return null;
	}

	@Override
	public void clearWarnings() throws SQLException {
		checkClosed();
	}

	@Override
	public String getCursorName() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public boolean isClosed() throws SQLException {
		return closed;
	}

	@Override
	public boolean isBeforeFirst() throws SQLException {
		checkClosed();
		return position == -1;
	}

	@Override
	public boolean isAfterLast() throws SQLException {
		checkClosed();
		return position == resultSize();
	}

	@Override
	public boolean isFirst() throws SQLException {
		checkClosed();
		return position == 0;
	}

	@Override
	public boolean isLast() throws SQLException {
		checkClosed();
		return position == resultSize() - 1;
	}

	@Override
	public void beforeFirst() throws SQLException {
		checkClosed();
		position = -1;
		wasNull = false;
	}

	@Override
	public void afterLast() throws SQLException {
		checkClosed();
		position = resultSize();
		wasNull = false;
	}

	@Override
	public boolean first() throws SQLException {
		checkClosed();
		position = 0;
		wasNull = false;
		return resultSize() != 0;
	}

	@Override
	public boolean last() throws SQLException {
		checkClosed();
		position = resultSize() - 1;
		wasNull = false;
		return resultSize() != 0;
	}

	@Override
	public int getRow() throws SQLException {
		checkClosed();
		return position + 1;
	}

	@Override
	public boolean absolute(int row) throws SQLException {
		checkClosed();
		position = row - 1;
		wasNull = false;
		return position >= 0 && resultSize() != 0
			&& position < resultSize();
	}

	@Override
	public boolean relative(int rows) throws SQLException {
		checkClosed();
		return absolute( position + rows );
	}

	@Override
	public boolean previous() throws SQLException {
		checkClosed();
		wasNull = false;
		if (position - 1 >= 0) {
			position--;
			return true;
		}
		position = -1;
		return false;
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		checkClosed();
		this.fetchDirection = direction;
	}

	@Override
	public int getFetchDirection() throws SQLException {
		checkClosed();
		return fetchDirection;
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		checkClosed();
		this.fetchSize = rows;
	}

	@Override
	public int getFetchSize() throws SQLException {
		checkClosed();
		return fetchSize;
	}

	@Override
	public int getType() throws SQLException {
		checkClosed();
		return resultSetType;
	}

	@Override
	public int getConcurrency() throws SQLException {
		checkClosed();
		return ResultSet.CONCUR_READ_ONLY;
	}

	@Override
	public int getHoldability() throws SQLException {
		checkClosed();
		return ResultSet.HOLD_CURSORS_OVER_COMMIT;
	}

	@Override
	public Statement getStatement() throws SQLException {
		checkClosed();
		return statement;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new SQLException();
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	// ------------- Read APIs ----------------

	@Override
	public boolean wasNull() throws SQLException {
		checkClosed();
		return wasNull;
	}

	protected abstract Object getValue(int columnIndex) throws SQLException;

	protected abstract Object getValue(String columnLabel) throws SQLException;

	protected abstract int getColumnIndex(String columnLabel) throws SQLException;

	private String getString(Object value) throws SQLException {
		if ( value == null || value instanceof JsonNull ) {
			wasNull = true;
			return null;
		}
		else {
			return stringValue( value );
		}
	}

	private String stringValue(Object value) throws SQLException {
		if ( value instanceof String string ) {
			return string;
		}
		else if ( value instanceof JsonPrimitive jsonPrimitive ) {
			if ( !jsonPrimitive.isString() ) {
				throw new SQLException( "Can convert json primitive value to string: " + jsonPrimitive );
			}
			return jsonPrimitive.getAsString();
		}
		else {
			throw new SQLException( "Unsupported type: " + value.getClass().getName() );
		}
	}

	private boolean getBoolean(Object value) throws SQLException {
		if ( value == null || value instanceof JsonNull ) {
			wasNull = true;
			return false;
		}
		else if ( value instanceof Boolean booleanValue ) {
			return booleanValue;
		}
		else if ( value instanceof JsonPrimitive jsonPrimitive ) {
			if ( !jsonPrimitive.isBoolean() ) {
				throw new SQLException( "Can convert json primitive value to boolean: " + jsonPrimitive );
			}
			return jsonPrimitive.getAsBoolean();
		}
		else {
			throw new SQLException( "Unsupported type: " + value.getClass().getName() );
		}
	}

	private byte getByte(Object value) throws SQLException {
		if ( value == null || value instanceof JsonNull ) {
			wasNull = true;
			return 0;
		}
		else if ( value instanceof Byte byteValue ) {
			return byteValue;
		}
		else if ( value instanceof Number number ) {
			return byteValueExact( number );
		}
		else if ( value instanceof JsonPrimitive jsonPrimitive ) {
			if ( !jsonPrimitive.isNumber() ) {
				throw new SQLException( "Can convert json primitive value to number: " + jsonPrimitive );
			}
			return byteValueExact( jsonPrimitive.getAsNumber() );
		}
		else {
			throw new SQLException( "Unsupported type: " + value.getClass().getName() );
		}
	}

	private static byte byteValueExact(Number number) throws SQLException {
		try {
			if ( number instanceof BigDecimal bigDecimal ) {
				return bigDecimal.byteValueExact();
			}
			else if ( number instanceof BigInteger bigInteger ) {
				return bigInteger.byteValueExact();
			}
			else {
				final long longValue = number.longValue();
				if ( longValue < Byte.MIN_VALUE || longValue > Byte.MAX_VALUE ) {
					throw new ArithmeticException( "Number value out of byte range" );
				}
				return (byte) longValue;
			}
		}
		catch ( ArithmeticException e ) {
			throw new SQLException( e.getMessage() );
		}
	}

	private short getShort(Object value) throws SQLException {
		if ( value == null || value instanceof JsonNull ) {
			wasNull = true;
			return 0;
		}
		else if ( value instanceof Short shortValue ) {
			return shortValue;
		}
		else if ( value instanceof Number number ) {
			return shortValueExact( number );
		}
		else if ( value instanceof JsonPrimitive jsonPrimitive ) {
			if ( !jsonPrimitive.isNumber() ) {
				throw new SQLException( "Can convert json primitive value to number: " + jsonPrimitive );
			}
			return shortValueExact( jsonPrimitive.getAsNumber() );
		}
		else {
			throw new SQLException( "Unsupported type: " + value.getClass().getName() );
		}
	}

	private static short shortValueExact(Number number) throws SQLException {
		try {
			if ( number instanceof BigDecimal bigDecimal ) {
				return bigDecimal.shortValueExact();
			}
			else if ( number instanceof BigInteger bigInteger ) {
				return bigInteger.shortValueExact();
			}
			else {
				final long longValue = number.longValue();
				if ( longValue < Short.MIN_VALUE || longValue > Short.MAX_VALUE ) {
					throw new ArithmeticException( "Number value out of short range" );
				}
				return (short) longValue;
			}
		}
		catch ( ArithmeticException e ) {
			throw new SQLException( e.getMessage() );
		}
	}

	private int getInt(Object value) throws SQLException {
		if ( value == null || value instanceof JsonNull ) {
			wasNull = true;
			return 0;
		}
		else if ( value instanceof Integer integerValue ) {
			return integerValue;
		}
		else if ( value instanceof Number number ) {
			return intValueExact( number );
		}
		else if ( value instanceof JsonPrimitive jsonPrimitive ) {
			if ( !jsonPrimitive.isNumber() ) {
				throw new SQLException( "Can convert json primitive value to number: " + jsonPrimitive );
			}
			return intValueExact( jsonPrimitive.getAsNumber() );
		}
		else {
			throw new SQLException( "Unsupported type: " + value.getClass().getName() );
		}
	}

	private static int intValueExact(Number number) throws SQLException {
		try {
			if ( number instanceof BigDecimal bigDecimal ) {
				return bigDecimal.intValueExact();
			}
			else if ( number instanceof BigInteger bigInteger ) {
				return bigInteger.intValueExact();
			}
			else {
				final long longValue = number.longValue();
				if ( longValue < Integer.MIN_VALUE || longValue > Integer.MAX_VALUE ) {
					throw new ArithmeticException( "Number value out of int range" );
				}
				return (int) longValue;
			}
		}
		catch ( ArithmeticException e ) {
			throw new SQLException( e.getMessage() );
		}
	}

	private long getLong(Object value) throws SQLException {
		if ( value == null || value instanceof JsonNull ) {
			wasNull = true;
			return 0;
		}
		else if ( value instanceof Long longValue ) {
			return longValue;
		}
		else if ( value instanceof Number number ) {
			return longValueExact( number );
		}
		else if ( value instanceof JsonPrimitive jsonPrimitive ) {
			if ( !jsonPrimitive.isNumber() ) {
				throw new SQLException( "Can convert json primitive value to number: " + jsonPrimitive );
			}
			return longValueExact( jsonPrimitive.getAsNumber() );
		}
		else {
			throw new SQLException( "Unsupported type: " + value.getClass().getName() );
		}
	}

	private static long longValueExact(Number number) throws SQLException {
		try {
			if ( number instanceof BigDecimal bigDecimal ) {
				return bigDecimal.longValueExact();
			}
			else if ( number instanceof BigInteger bigInteger ) {
				return bigInteger.longValueExact();
			}
			else {
				return number.longValue();
			}
		}
		catch ( ArithmeticException e ) {
			throw new SQLException( e.getMessage() );
		}
	}

	private float getFloat(Object value) throws SQLException {
		if ( value == null || value instanceof JsonNull ) {
			wasNull = true;
			return 0;
		}
		else if ( value instanceof Float floatValue ) {
			return floatValue;
		}
		else if ( value instanceof Number number ) {
			return number.floatValue();
		}
		else if ( value instanceof JsonPrimitive jsonPrimitive ) {
			if ( !jsonPrimitive.isNumber() ) {
				throw new SQLException( "Can convert json primitive value to number: " + jsonPrimitive );
			}
			return jsonPrimitive.getAsNumber().floatValue();
		}
		else {
			throw new SQLException( "Unsupported type: " + value.getClass().getName() );
		}
	}

	private double getDouble(Object value) throws SQLException {
		if ( value == null || value instanceof JsonNull ) {
			wasNull = true;
			return 0;
		}
		else if ( value instanceof Double doubleValue ) {
			return doubleValue;
		}
		else if ( value instanceof Number number ) {
			return number.doubleValue();
		}
		else if ( value instanceof JsonPrimitive jsonPrimitive ) {
			if ( !jsonPrimitive.isNumber() ) {
				throw new SQLException( "Can convert json primitive value to number: " + jsonPrimitive );
			}
			return jsonPrimitive.getAsNumber().doubleValue();
		}
		else {
			throw new SQLException( "Unsupported type: " + value.getClass().getName() );
		}
	}

	private BigDecimal getBigDecimal(Object value, int scale) throws SQLException {
		if ( value == null || value instanceof JsonNull ) {
			wasNull = true;
			return null;
		}
		else {
			return getBigDecimal( value ).setScale( scale, RoundingMode.DOWN );
		}
	}

	private byte[] getBytes(Object value) throws SQLException {
		if ( value == null || value instanceof JsonNull ) {
			wasNull = true;
			return null;
		}
		else if ( value instanceof byte[] bytes ) {
			return bytes;
		}
		else {
			throw new SQLException( "Unsupported type: " + value.getClass().getName() );
		}
	}

	private Date getDate(Object value) throws SQLException {
		if ( value == null || value instanceof JsonNull ) {
			wasNull = true;
			return null;
		}
		else if ( value instanceof Date date ) {
			return date;
		}
		else {
			return getObject( value, Date.class );
		}
	}

	private Time getTime(Object value) throws SQLException {
		if ( value == null || value instanceof JsonNull ) {
			wasNull = true;
			return null;
		}
		else if ( value instanceof Time time ) {
			return time;
		}
		else {
			return getObject( value, Time.class );
		}
	}

	private Timestamp getTimestamp(Object value) throws SQLException {
		if ( value == null || value instanceof JsonNull ) {
			wasNull = true;
			return null;
		}
		else if ( value instanceof Timestamp timestamp ) {
			return timestamp;
		}
		else {
			return getObject( value, Timestamp.class );
		}
	}

	private InputStream getAsciiStream(Object value) throws SQLException {
		if ( value == null || value instanceof JsonNull ) {
			wasNull = true;
			return null;
		}
		else if ( value instanceof InputStream inputStream ) {
			return inputStream;
		}
		else {
			return new ByteArrayInputStream( stringValue( value ).getBytes( StandardCharsets.US_ASCII) );
		}
	}

	private InputStream getUnicodeStream(Object value) throws SQLException {
		if ( value == null || value instanceof JsonNull ) {
			wasNull = true;
			return null;
		}
		else if ( value instanceof InputStream inputStream ) {
			return inputStream;
		}
		else {
			return new ByteArrayInputStream( stringValue( value ).getBytes( StandardCharsets.UTF_8) );
		}
	}

	private InputStream getBinaryStream(Object value) throws SQLException {
		if ( value == null || value instanceof JsonNull ) {
			wasNull = true;
			return null;
		}
		else if ( value instanceof InputStream inputStream ) {
			return inputStream;
		}
		else if ( value instanceof byte[] bytes ) {
			return new ByteArrayInputStream( bytes );
		}
		else {
			throw new SQLException( "Unsupported type: " + value.getClass().getName() );
		}
	}

	private Object getObject(Object value) throws SQLException {
		return value;
	}

	private Reader getCharacterStream(Object value) throws SQLException {
		if ( value == null || value instanceof JsonNull ) {
			wasNull = true;
			return null;
		}
		else if ( value instanceof Reader reader ) {
			return reader;
		}
		else {
			return new StringReader( stringValue( value ) );
		}
	}

	private BigDecimal getBigDecimal(Object value) throws SQLException {
		if ( value == null || value instanceof JsonNull ) {
			wasNull = true;
			return null;
		}
		else if ( value instanceof BigDecimal bigDecimal ) {
			return bigDecimal;
		}
		else if ( value instanceof Double doubleValue ) {
			return BigDecimal.valueOf( doubleValue );
		}
		else if ( value instanceof Float floatValue ) {
			return BigDecimal.valueOf( floatValue );
		}
		else if ( value instanceof Long longValue ) {
			return BigDecimal.valueOf( longValue );
		}
		else if ( value instanceof Integer integerValue ) {
			return BigDecimal.valueOf( integerValue );
		}
		else if ( value instanceof Short shortValue ) {
			return BigDecimal.valueOf( shortValue );
		}
		else if ( value instanceof Byte byteValue ) {
			return BigDecimal.valueOf( byteValue );
		}
		else if ( value instanceof Number ) {
			return new BigDecimal( value.toString() );
		}
		else {
			throw new SQLException( "Unsupported type: " + value.getClass().getName() );
		}
	}

	private Object getObject(Object value, Map<String, Class<?>> map) throws SQLException {
		if ( value == null ) {
			wasNull = true;
			return null;
		}
		else {
			return value;
		}
	}

	private Blob getBlob(Object value) throws SQLException {
		if ( value == null || value instanceof JsonNull ) {
			wasNull = true;
			return null;
		}
		else if ( value instanceof Blob blob ) {
			return blob;
		}
		else if ( value instanceof byte[] bytes ) {
			return new ByteArrayBlob( bytes );
		}
		else {
			throw new SQLException( "Unsupported type: " + value.getClass().getName() );
		}
	}

	private Clob getClob(Object value) throws SQLException {
		if ( value == null || value instanceof JsonNull ) {
			wasNull = true;
			return null;
		}
		else if ( value instanceof Clob clob ) {
			return clob;
		}
		else {
			return new StringClob( stringValue( value ) );
		}
	}

	private Array getArray(Object value, int columnIndex) throws SQLException {
		if ( value == null || value instanceof JsonNull ) {
			wasNull = true;
			return null;
		}
		else if ( value.getClass().isArray() ) {
			final String baseTypeName = determineBaseTypeName( value );
			return new MilvusArray (
					baseTypeName == null
							? getBaseTypeName( getMetaData().getColumnTypeName( columnIndex ) )
							: baseTypeName,
					value
			);
		}
		else {
			throw new SQLException( "Unsupported type: " + value.getClass().getName() );
		}
	}

	private String determineBaseTypeName(Object value) {
		if ( value instanceof Object[] array ) {
			for ( Object element : array ) {
				if ( element != null ) {
					if ( element instanceof String ) {
						return "varchar";
					}
					else {
						throw new UnsupportedOperationException( "Unsupported array element type: " + element.getClass().getName() );
					}
				}
			}
			return null;
		}
		else if ( value instanceof boolean[] ) {
			return "boolean";
		}
		else if ( value instanceof byte[] ) {
			return "tinyint";
		}
		else if ( value instanceof short[] ) {
			return "smallint";
		}
		else if ( value instanceof int[] ) {
			return "integer";
		}
		else if ( value instanceof long[] ) {
			return "bigint";
		}
		else if ( value instanceof float[] ) {
			return "float";
		}
		else if ( value instanceof double[] ) {
			return "double precision";
		}
		else {
			return null;
		}
	}

	private String getBaseTypeName(String columnTypeName) {
		if ( columnTypeName == null || !columnTypeName.endsWith( "[]" ) ) {
			throw new IllegalArgumentException( "Array type name must end with '[]', but found: " + columnTypeName );
		}
		return columnTypeName.substring( 0, columnTypeName.length() - 2 );
	}

	private Date getDate(Object value, Calendar cal) throws SQLException {
		if ( value == null || value instanceof JsonNull ) {
			wasNull = true;
			return null;
		}
		else if ( value instanceof Date date ) {
			return date;
		}
		else {
			return getObject( value, Date.class, cal );
		}
	}

	private Time getTime(Object value, Calendar cal) throws SQLException {
		if ( value == null || value instanceof JsonNull ) {
			wasNull = true;
			return null;
		}
		else if ( value instanceof Time time ) {
			return time;
		}
		else {
			return getObject( value, Time.class, cal );
		}
	}

	private Timestamp getTimestamp(Object value, Calendar cal) throws SQLException {
		if ( value == null || value instanceof JsonNull ) {
			wasNull = true;
			return null;
		}
		else if ( value instanceof Timestamp timestamp ) {
			return timestamp;
		}
		else {
			return getObject( value, Timestamp.class, cal );
		}
	}

	private URL getURL(Object value) throws SQLException {
		if ( value == null || value instanceof JsonNull ) {
			wasNull = true;
			return null;
		}
		else if ( value instanceof URL url ) {
			return url;
		}
		else {
			try {
				return URI.create( stringValue( value ) ).toURL();
			}
			catch (MalformedURLException e) {
				throw new SQLException( "Invalid URL", e );
			}
		}
	}

	private RowId getRowId(Object value) throws SQLException {
		if ( value == null || value instanceof JsonNull ) {
			wasNull = true;
			return null;
		}
		else if ( value instanceof RowId rowId ) {
			return rowId;
		}
		// todo (milvus): emulate?
		else {
			throw new SQLException( "Unsupported type: " + value.getClass().getName() );
		}
	}

	private NClob getNClob(Object value) throws SQLException {
		if ( value == null || value instanceof JsonNull ) {
			wasNull = true;
			return null;
		}
		else if ( value instanceof NClob nClob ) {
			return nClob;
		}
		else {
			return new StringClob( stringValue( value ) );
		}
	}

	private <T> T getObject(Object value, Class<T> type) throws SQLException {
		return getObject( value, type, null );
	}

	// TODO Christian make sure reflection is correctly registered for the relevant types.
	//   Typically this is done by exposing the array types in here:
	//   https://github.com/hibernate/hibernate-nosql/blob/018b8eeda3627e114ec25bd48407ccb9c47564ce/hibernate-graalvm/src/main/java/org/hibernate/graalvm/internal/StaticClassLists.java#L42
	@AllowReflection
	private <T> T getObject(Object value, Class<T> type, @Nullable Calendar cal) throws SQLException {
		if ( value == null ) {
			wasNull = true;
			return null;
		}
		else if ( type.isInstance( value  ) ) {
			return type.cast( value );
		}
		else if ( type == byte[].class && value instanceof ByteBuffer byteBuffer ) {
			return (T) byteBuffer.array();
		}
		else if ( type.isArray() && value instanceof List<?> list ) {
			final Object array = java.lang.reflect.Array.newInstance( type.componentType(), list.size() );
			for ( int i = 0; i < list.size(); i++ ) {
				java.lang.reflect.Array.set( array, i, list.get( i ) );
			}
			return type.cast( array );
		}
		else {
			if ( value instanceof String string ) {
				if ( type == Date.class ) {
					final ZonedDateTime zonedDateTime = parseTimestamp( string, cal );
					//noinspection unchecked
					return (T) Date.valueOf( zonedDateTime.toLocalDate() );
				}
				else if ( type == Time.class ) {
					final ZonedDateTime zonedDateTime = parseTimestamp( string, cal );
					//noinspection unchecked
					return (T) Time.valueOf( zonedDateTime.toLocalTime() );
				}
				else if ( type == Timestamp.class ) {
					final ZonedDateTime zonedDateTime = parseTimestamp( string, cal );
					//noinspection unchecked
					return (T) Timestamp.valueOf( zonedDateTime.toLocalDateTime() );
				}
				else if ( type == Calendar.class ) {
					final ZonedDateTime zonedDateTime = parseTimestamp( string, cal );
					final Timestamp timestamp = Timestamp.valueOf( zonedDateTime.toLocalDateTime() );
					final Calendar calendar = Calendar.getInstance();
					calendar.setTime( timestamp );
					//noinspection unchecked
					return (T) calendar;
				}
				else if ( type == LocalDate.class ) {
					final ZonedDateTime zonedDateTime = parseTimestamp( string, cal );
					//noinspection unchecked
					return (T) zonedDateTime.toLocalDate();
				}
				else if ( type == LocalTime.class ) {
					final ZonedDateTime zonedDateTime = parseTimestamp( string, cal );
					//noinspection unchecked
					return (T) zonedDateTime.toLocalTime();
				}
				else if ( type == LocalDateTime.class ) {
					final ZonedDateTime zonedDateTime = parseTimestamp( string, cal );
					//noinspection unchecked
					return (T) zonedDateTime.toLocalDateTime();
				}
				else if ( type == Instant.class ) {
					final ZonedDateTime zonedDateTime = parseTimestamp( string, cal );
					//noinspection unchecked
					return (T) zonedDateTime.toInstant();
				}
				else if ( type == OffsetDateTime.class ) {
					final ZonedDateTime zonedDateTime = parseTimestamp( string, cal );
					//noinspection unchecked
					return (T) zonedDateTime.toOffsetDateTime();
				}
			}
			throw new SQLException( "Unsupported type: " + value.getClass().getName() );
		}
	}

	private static ZonedDateTime parseTimestamp(String timestamp, @Nullable Calendar calendar) {
		ZoneId zoneId = calendar == null || calendar.getTimeZone() == null
				? UTC
				: calendar.getTimeZone().toZoneId();
		return Instant.parse( timestamp ).atZone( zoneId );
	}

	// -------------- Index-based Read APIs

	@Override
	public String getString(int columnIndex) throws SQLException {
		return getString( getValue( columnIndex ) );
	}

	@Override
	public boolean getBoolean(int columnIndex) throws SQLException {
		return getBoolean( getValue( columnIndex ) );
	}

	@Override
	public byte getByte(int columnIndex) throws SQLException {
		return getByte( getValue( columnIndex ) );
	}

	@Override
	public short getShort(int columnIndex) throws SQLException {
		return getShort( getValue( columnIndex ) );
	}

	@Override
	public int getInt(int columnIndex) throws SQLException {
		return getInt( getValue( columnIndex ) );
	}

	@Override
	public long getLong(int columnIndex) throws SQLException {
		return getLong( getValue( columnIndex ) );
	}

	@Override
	public float getFloat(int columnIndex) throws SQLException {
		return getFloat( getValue( columnIndex ) );
	}

	@Override
	public double getDouble(int columnIndex) throws SQLException {
		return getDouble( getValue( columnIndex ) );
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		return getBigDecimal( getValue( columnIndex ), scale );
	}

	@Override
	public byte[] getBytes(int columnIndex) throws SQLException {
		return getBytes( getValue( columnIndex ) );
	}

	@Override
	public Date getDate(int columnIndex) throws SQLException {
		return getDate( getValue( columnIndex ) );
	}

	@Override
	public Time getTime(int columnIndex) throws SQLException {
		return getTime( getValue( columnIndex ) );
	}

	@Override
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		return getTimestamp( getValue( columnIndex ) );
	}

	@Override
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		return getAsciiStream( getValue( columnIndex ) );
	}

	@Override
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		return getUnicodeStream( getValue( columnIndex ) );
	}

	@Override
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		return getBinaryStream( getValue( columnIndex ) );
	}

	@Override
	public Object getObject(int columnIndex) throws SQLException {
		return getValue( columnIndex );
	}

	@Override
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		return getCharacterStream( getValue( columnIndex ) );
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		return getBigDecimal( getValue( columnIndex ) );
	}

	@Override
	public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
		return getObject( getValue( columnIndex ), map );
	}

	@Override
	public Ref getRef(int columnIndex) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public Blob getBlob(int columnIndex) throws SQLException {
		return getBlob( getValue( columnIndex ) );
	}

	@Override
	public Clob getClob(int columnIndex) throws SQLException {
		return getClob( getValue( columnIndex ) );
	}

	@Override
	public Array getArray(int columnIndex) throws SQLException {
		return getArray( getValue( columnIndex ), columnIndex );
	}

	@Override
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		return getDate( getValue( columnIndex ), cal );
	}

	@Override
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		return getTime( getValue( columnIndex ), cal );
	}

	@Override
	public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		return getTimestamp( getValue( columnIndex ), cal );
	}

	@Override
	public URL getURL(int columnIndex) throws SQLException {
		return getURL( getValue( columnIndex ) );
	}

	@Override
	public RowId getRowId(int columnIndex) throws SQLException {
		return getRowId( getValue( columnIndex ) );
	}

	@Override
	public NClob getNClob(int columnIndex) throws SQLException {
		return getNClob( getValue( columnIndex ) );
	}

	@Override
	public SQLXML getSQLXML(int columnIndex) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public String getNString(int columnIndex) throws SQLException {
		return getString( columnIndex );
	}

	@Override
	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		return getCharacterStream( getValue( columnIndex ) );
	}

	@Override
	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
		return getObject( getValue( columnIndex ), type );
	}

	// ------ String-based API -------

	@Override
	public String getString(String columnLabel) throws SQLException {
		return getString( getValue( columnLabel ) );
	}

	@Override
	public boolean getBoolean(String columnLabel) throws SQLException {
		return getBoolean( getValue( columnLabel ) );
	}

	@Override
	public byte getByte(String columnLabel) throws SQLException {
		return getByte( getValue( columnLabel ) );
	}

	@Override
	public short getShort(String columnLabel) throws SQLException {
		return getShort( getValue( columnLabel ) );
	}

	@Override
	public int getInt(String columnLabel) throws SQLException {
		return getInt( getValue( columnLabel ) );
	}

	@Override
	public long getLong(String columnLabel) throws SQLException {
		return getLong( getValue( columnLabel ) );
	}

	@Override
	public float getFloat(String columnLabel) throws SQLException {
		return getFloat( getValue( columnLabel ) );
	}

	@Override
	public double getDouble(String columnLabel) throws SQLException {
		return getDouble( getValue( columnLabel ) );
	}

	@Override
	public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
		return getBigDecimal( getValue( columnLabel ), scale );
	}

	@Override
	public byte[] getBytes(String columnLabel) throws SQLException {
		return getBytes( getValue( columnLabel ) );
	}

	@Override
	public Date getDate(String columnLabel) throws SQLException {
		return getDate( getValue( columnLabel ) );
	}

	@Override
	public Time getTime(String columnLabel) throws SQLException {
		return getTime( getValue( columnLabel ) );
	}

	@Override
	public Timestamp getTimestamp(String columnLabel) throws SQLException {
		return getTimestamp( getValue( columnLabel ) );
	}

	@Override
	public InputStream getAsciiStream(String columnLabel) throws SQLException {
		return getAsciiStream( getValue( columnLabel ) );
	}

	@Override
	public InputStream getUnicodeStream(String columnLabel) throws SQLException {
		return getUnicodeStream( getValue( columnLabel ) );
	}

	@Override
	public InputStream getBinaryStream(String columnLabel) throws SQLException {
		return getBinaryStream( getValue( columnLabel ) );
	}

	@Override
	public Object getObject(String columnLabel) throws SQLException {
		return getValue( columnLabel );
	}

	@Override
	public Reader getCharacterStream(String columnLabel) throws SQLException {
		return getCharacterStream( getValue( columnLabel ) );
	}

	@Override
	public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
		return getBigDecimal( getValue( columnLabel ) );
	}

	@Override
	public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
		return getObject( getValue( columnLabel ), map );
	}

	@Override
	public Ref getRef(String columnLabel) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public Blob getBlob(String columnLabel) throws SQLException {
		return getBlob( getValue( columnLabel ) );
	}

	@Override
	public Clob getClob(String columnLabel) throws SQLException {
		return getClob( getValue( columnLabel ) );
	}

	@Override
	public Array getArray(String columnLabel) throws SQLException {
		return getArray( getValue( columnLabel ), getColumnIndex( columnLabel ) );
	}

	@Override
	public Date getDate(String columnLabel, Calendar cal) throws SQLException {
		return getDate( getValue( columnLabel ), cal );
	}

	@Override
	public Time getTime(String columnLabel, Calendar cal) throws SQLException {
		return getTime( getValue( columnLabel ), cal );
	}

	@Override
	public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
		return getTimestamp( getValue( columnLabel ), cal );
	}

	@Override
	public URL getURL(String columnLabel) throws SQLException {
		return getURL( getValue( columnLabel ) );
	}

	@Override
	public RowId getRowId(String columnLabel) throws SQLException {
		return getRowId( getValue( columnLabel ) );
	}

	@Override
	public NClob getNClob(String columnLabel) throws SQLException {
		return getNClob( getValue( columnLabel ) );
	}

	@Override
	public SQLXML getSQLXML(String columnLabel) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public String getNString(String columnLabel) throws SQLException {
		return getString( columnLabel );
	}

	@Override
	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		return getCharacterStream( getValue( columnLabel ) );
	}

	@Override
	public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
		return getObject( getValue( columnLabel ), type );
	}

	// -------------- Update APIs ---------------

	@Override
	public boolean rowUpdated() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public boolean rowInserted() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public boolean rowDeleted() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateNull(int columnIndex) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateByte(int columnIndex, byte x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateShort(int columnIndex, short x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateInt(int columnIndex, int x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateLong(int columnIndex, long x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateFloat(int columnIndex, float x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateDouble(int columnIndex, double x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateString(int columnIndex, String x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateDate(int columnIndex, Date x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateTime(int columnIndex, Time x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateObject(int columnIndex, Object x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateNull(String columnLabel) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateBoolean(String columnLabel, boolean x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateByte(String columnLabel, byte x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateShort(String columnLabel, short x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateInt(String columnLabel, int x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateLong(String columnLabel, long x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateFloat(String columnLabel, float x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateDouble(String columnLabel, double x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateString(String columnLabel, String x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateBytes(String columnLabel, byte[] x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateDate(String columnLabel, Date x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateTime(String columnLabel, Time x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateObject(String columnLabel, Object x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void insertRow() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateRow() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void deleteRow() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void refreshRow() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void cancelRowUpdates() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void moveToInsertRow() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void moveToCurrentRow() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateRef(int columnIndex, Ref x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateRef(String columnLabel, Ref x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateBlob(String columnLabel, Blob x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateClob(int columnIndex, Clob x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateClob(String columnLabel, Clob x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateArray(int columnIndex, Array x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateArray(String columnLabel, Array x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateRowId(int columnIndex, RowId x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateRowId(String columnLabel, RowId x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateNString(int columnIndex, String nString) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateNString(String columnLabel, String nString) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateClob(int columnIndex, Reader reader) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateClob(String columnLabel, Reader reader) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateNClob(int columnIndex, Reader reader) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void updateNClob(String columnLabel, Reader reader) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}
}
