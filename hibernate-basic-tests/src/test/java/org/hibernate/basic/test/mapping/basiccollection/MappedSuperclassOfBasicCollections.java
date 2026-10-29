/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.basiccollection;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.engine.jdbc.proxy.BlobProxy;
import org.hibernate.engine.jdbc.proxy.ClobProxy;
import org.junit.Assert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MappedSuperclass
public abstract class MappedSuperclassOfBasicCollections<ID> {

	protected static final UUID MIN_UUID_V4 = UUID.fromString( "00000000-0000-4000-8000-000000000000");
	protected static final UUID MAX_UUID_V4 = UUID.fromString( "ffffffff-ffff-ffff-ffff-ffffffffffff");
	protected static final OffsetDateTime LAST_EPOCH_DAY_2026 = OffsetDateTime.of( 2026, 12, 31, 0, 0, 0, 0, ZoneOffset.UTC );
	protected static final OffsetDateTime LAST_EPOCH_SECOND_2026 = OffsetDateTime.of( 2026, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC );
	protected static final long LAST_EPOCH_DAY_2026_MILLIS = LAST_EPOCH_DAY_2026.toInstant().toEpochMilli();
	protected static final long LAST_EPOCH_SECOND_2026_MILLIS = LAST_EPOCH_SECOND_2026.toInstant().toEpochMilli();

	@ElementCollection
	private List<Byte> theBytes = new ArrayList<>();
	@ElementCollection
	private List<Short> theShorts = new ArrayList<>();
	@ElementCollection
	private List<Integer> theIntegers = new ArrayList<>();
	@ElementCollection
	private List<Long> theLongs = new ArrayList<>();
	@ElementCollection
	private List<Float> theFloats = new ArrayList<>();
	@ElementCollection
	private List<Double> theDoubles = new ArrayList<>();
	@ElementCollection
	private List<Boolean> theBooleans = new ArrayList<>();
	@ElementCollection
	@JdbcTypeCode(Types.INTEGER)
	private List<Boolean> theNumericBooleans = new ArrayList<>();
	@ElementCollection
	@JdbcTypeCode(Types.CHAR)
	private List<Boolean> theStringBooleans = new ArrayList<>();
	@ElementCollection
	private List<Character> theCharacters = new ArrayList<>();
	@ElementCollection
	private List<String> theStrings = new ArrayList<>();

	@ElementCollection
	@Column(precision = 19, scale = 2)
	private List<BigDecimal> theBigDecimals = new ArrayList<>();
	@ElementCollection
	@Column(precision = 19)
	private List<BigInteger> theBigIntegers = new ArrayList<>();

	@ElementCollection
	private List<BasicEnum> theEnums = new ArrayList<>();
	@ElementCollection
	@Enumerated(EnumType.STRING)
	private List<BasicEnum> theStringEnums = new ArrayList<>();
	@ElementCollection
	@Enumerated(EnumType.ORDINAL)
	private List<BasicEnum> theOrdinalEnums = new ArrayList<>();

	@ElementCollection
	private List<java.sql.Date> theSqlDates = new ArrayList<>();
	@ElementCollection
	private List<Time> theSqlTimes = new ArrayList<>();
	@ElementCollection
	private List<Timestamp> theSqlTimestamps = new ArrayList<>();
	@ElementCollection
	@Temporal(TemporalType.DATE)
	private List<Date> theSqlDateAsDates = new ArrayList<>();
	@ElementCollection
	@Temporal(TemporalType.TIME)
	private List<Date> theSqlTimeAsDates = new ArrayList<>();
	@ElementCollection
	@Temporal(TemporalType.TIMESTAMP)
	private List<Date> theSqlTimestampAsDates = new ArrayList<>();
	@ElementCollection
	private List<LocalDate> theLocalDates = new ArrayList<>();
	@ElementCollection
	private List<LocalTime> theLocalTimes = new ArrayList<>();
	@ElementCollection
	private List<LocalDateTime> theLocalDateTimes = new ArrayList<>();
	@ElementCollection
	private List<Instant> theInstants = new ArrayList<>();
	@ElementCollection
	private List<OffsetTime> theOffsetTimes = new ArrayList<>();
	@ElementCollection
	private List<OffsetDateTime> theOffsetDateTimes = new ArrayList<>();
	@ElementCollection
	private List<ZonedDateTime> theZonedDateTimes = new ArrayList<>();

	@ElementCollection
	private List<Duration> theDurations = new ArrayList<>();
	@ElementCollection
	private List<UUID> theUuids = new ArrayList<>();
	@ElementCollection
	private List<InetAddress> theInetAddresses = new ArrayList<>();

	@ElementCollection
	private List<Clob> theClobs = new ArrayList<>();
	@ElementCollection
	private List<Blob> theBlobs = new ArrayList<>();
//	private List<byte[]> theByteArrays = new ArrayList<>();

	public MappedSuperclassOfBasicCollections() {
	}

	public abstract ID getId();

	public abstract void setId(ID id);

	public List<Byte> getTheBytes() {
		return theBytes;
	}

	public void setTheBytes(List<Byte> theByte) {
		this.theBytes = theByte;
	}

	public List<Short> getTheShorts() {
		return theShorts;
	}

	public void setTheShorts(List<Short> theShort) {
		this.theShorts = theShort;
	}

	public List<Integer> getTheIntegers() {
		return theIntegers;
	}

	public void setTheIntegers(List<Integer> theInteger) {
		this.theIntegers = theInteger;
	}

	public List<Long> getTheLongs() {
		return theLongs;
	}

	public void setTheLongs(List<Long> theLong) {
		this.theLongs = theLong;
	}

	public List<Float> getTheFloats() {
		return theFloats;
	}

	public void setTheFloats(List<Float> theFloat) {
		this.theFloats = theFloat;
	}

	public List<Double> getTheDoubles() {
		return theDoubles;
	}

	public void setTheDoubles(List<Double> theDouble) {
		this.theDoubles = theDouble;
	}

	public List<Boolean> getTheBooleans() {
		return theBooleans;
	}

	public void setTheBooleans(List<Boolean> theBoolean) {
		this.theBooleans = theBoolean;
	}

	public List<Boolean> getTheNumericBooleans() {
		return theNumericBooleans;
	}

	public void setTheNumericBooleans(List<Boolean> theNumericBoolean) {
		this.theNumericBooleans = theNumericBoolean;
	}

	public List<Boolean> getTheStringBooleans() {
		return theStringBooleans;
	}

	public void setTheStringBooleans(List<Boolean> theStringBoolean) {
		this.theStringBooleans = theStringBoolean;
	}

	public List<Character> getTheCharacters() {
		return theCharacters;
	}

	public void setTheCharacters(List<Character> theCharacter) {
		this.theCharacters = theCharacter;
	}

	public List<String> getTheStrings() {
		return theStrings;
	}

	public void setTheStrings(List<String> theString) {
		this.theStrings = theString;
	}

	public List<BigDecimal> getTheBigDecimals() {
		return theBigDecimals;
	}

	public void setTheBigDecimals(List<BigDecimal> theBigDecimal) {
		this.theBigDecimals = theBigDecimal;
	}

	public List<BigInteger> getTheBigIntegers() {
		return theBigIntegers;
	}

	public void setTheBigIntegers(List<BigInteger> theBigInteger) {
		this.theBigIntegers = theBigInteger;
	}

	public List<BasicEnum> getTheEnums() {
		return theEnums;
	}

	public void setTheEnums(List<BasicEnum> theEnum) {
		this.theEnums = theEnum;
	}

	public List<BasicEnum> getTheStringEnums() {
		return theStringEnums;
	}

	public void setTheStringEnums(List<BasicEnum> theStringEnum) {
		this.theStringEnums = theStringEnum;
	}

	public List<BasicEnum> getTheOrdinalEnums() {
		return theOrdinalEnums;
	}

	public void setTheOrdinalEnums(List<BasicEnum> theOrdinalEnum) {
		this.theOrdinalEnums = theOrdinalEnum;
	}

	public List<java.sql.Date> getTheSqlDates() {
		return theSqlDates;
	}

	public void setTheSqlDates(List<java.sql.Date> theSqlDate) {
		this.theSqlDates = theSqlDate;
	}

	public List<Time> getTheSqlTimes() {
		return theSqlTimes;
	}

	public void setTheSqlTimes(List<Time> theSqlTime) {
		this.theSqlTimes = theSqlTime;
	}

	public List<Timestamp> getTheSqlTimestamps() {
		return theSqlTimestamps;
	}

	public void setTheSqlTimestamps(List<Timestamp> theSqlTimestamp) {
		this.theSqlTimestamps = theSqlTimestamp;
	}

	public List<Date> getTheSqlDateAsDates() {
		return theSqlDateAsDates;
	}

	public void setTheSqlDateAsDates(List<Date> theSqlDateAsDate) {
		this.theSqlDateAsDates = theSqlDateAsDate;
	}

	public List<Date> getTheSqlTimeAsDates() {
		return theSqlTimeAsDates;
	}

	public void setTheSqlTimeAsDates(List<Date> theSqlTimeAsDate) {
		this.theSqlTimeAsDates = theSqlTimeAsDate;
	}

	public List<Date> getTheSqlTimestampAsDates() {
		return theSqlTimestampAsDates;
	}

	public void setTheSqlTimestampAsDates(List<Date> theSqlTimestampAsDate) {
		this.theSqlTimestampAsDates = theSqlTimestampAsDate;
	}

	public List<LocalDate> getTheLocalDates() {
		return theLocalDates;
	}

	public void setTheLocalDates(List<LocalDate> theLocalDate) {
		this.theLocalDates = theLocalDate;
	}

	public List<LocalTime> getTheLocalTimes() {
		return theLocalTimes;
	}

	public void setTheLocalTimes(List<LocalTime> theLocalTime) {
		this.theLocalTimes = theLocalTime;
	}

	public List<LocalDateTime> getTheLocalDateTimes() {
		return theLocalDateTimes;
	}

	public void setTheLocalDateTimes(List<LocalDateTime> theLocalDateTime) {
		this.theLocalDateTimes = theLocalDateTime;
	}

	public List<Instant> getTheInstants() {
		return theInstants;
	}

	public void setTheInstants(List<Instant> theInstant) {
		this.theInstants = theInstant;
	}

	public List<OffsetTime> getTheOffsetTimes() {
		return theOffsetTimes;
	}

	public void setTheOffsetTimes(List<OffsetTime> theOffsetTime) {
		this.theOffsetTimes = theOffsetTime;
	}

	public List<OffsetDateTime> getTheOffsetDateTimes() {
		return theOffsetDateTimes;
	}

	public void setTheOffsetDateTimes(List<OffsetDateTime> theOffsetDateTime) {
		this.theOffsetDateTimes = theOffsetDateTime;
	}

	public List<ZonedDateTime> getTheZonedDateTimes() {
		return theZonedDateTimes;
	}

	public void setTheZonedDateTimes(List<ZonedDateTime> theZonedDateTime) {
		this.theZonedDateTimes = theZonedDateTime;
	}

	public List<Duration> getTheDurations() {
		return theDurations;
	}

	public void setTheDurations(List<Duration> theDuration) {
		this.theDurations = theDuration;
	}

	public List<UUID> getTheUuids() {
		return theUuids;
	}

	public void setTheUuids(List<UUID> theUuid) {
		this.theUuids = theUuid;
	}

	public List<InetAddress> getTheInetAddresses() {
		return theInetAddresses;
	}

	public void setTheInetAddresses(List<InetAddress> theInetAddress) {
		this.theInetAddresses = theInetAddress;
	}

	public List<Clob> getTheClobs() {
		return theClobs;
	}

	public void setTheClobs(List<Clob> theClob) {
		this.theClobs = theClob;
	}

	public List<Blob> getTheBlobs() {
		return theBlobs;
	}

	public void setTheBlobs(List<Blob> theBlob) {
		this.theBlobs = theBlob;
	}

//	public List<byte[]> getTheByteArrays() {
//		return theByteArrays;
//	}
//
//	public void setTheByteArrays(List<byte[]> theByteArray) {
//		this.theByteArrays = theByteArray;
//	}

	public void assertEqual(MappedSuperclassOfBasicCollections<ID> that, EqualityCheck check) {
		if ( check == EqualityCheck.NUMERICS ) {
			if ( getTheBigDecimals() == null || that.getTheBigDecimals() == null ) {
				assertEquals( getTheBigDecimals(), that.getTheBigDecimals() );
			}
			else {
				assertEquals( getTheBigDecimals().size(), that.getTheBigDecimals().size() );
				for ( int i = 0; i < getTheBigDecimals().size(); i++ ) {
					assertEquals(
							getTheBigDecimals().get( i ).stripTrailingZeros(),
							that.getTheBigDecimals().get( i ).stripTrailingZeros()
					);
				}
			}
			assertEquals( getTheBigIntegers(), that.getTheBigIntegers() );
		}
		else {
			assertEquals( getId(), that.getId() );
			assertEquals( getTheBytes(), that.getTheBytes() );
			assertEquals( getTheShorts(), that.getTheShorts() );
			assertEquals( getTheIntegers(), that.getTheIntegers() );
			assertEquals( getTheLongs(), that.getTheLongs() );
			assertEquals( getTheFloats(), that.getTheFloats() );
			assertEquals( getTheDoubles(), that.getTheDoubles() );
			assertEquals( getTheBooleans(), that.getTheBooleans() );
			assertEquals( getTheNumericBooleans(), that.getTheNumericBooleans() );
			assertEquals( getTheStringBooleans(), that.getTheStringBooleans() );
			assertEquals( getTheCharacters(), that.getTheCharacters() );
			assertEquals( getTheStrings(), that.getTheStrings() );
			assertEquals( getTheEnums(), that.getTheEnums() );
			assertEquals( getTheStringEnums(), that.getTheStringEnums() );
			assertEquals( getTheOrdinalEnums(), that.getTheOrdinalEnums() );
			assertEquals( getTheSqlDates(), that.getTheSqlDates() );
			assertEquals( getTheSqlTimes(), that.getTheSqlTimes() );
			assertEquals( getTheSqlTimestamps(), that.getTheSqlTimestamps() );
			assertEquals( getTheSqlDateAsDates(), that.getTheSqlDateAsDates() );
			assertEquals( getTheSqlTimeAsDates(), that.getTheSqlTimeAsDates() );
			assertEquals( getTheSqlTimestampAsDates(), that.getTheSqlTimestampAsDates() );
			assertEquals( getTheLocalDates(), that.getTheLocalDates() );
			assertEquals( getTheLocalTimes(), that.getTheLocalTimes() );
			assertEquals( getTheLocalDateTimes(), that.getTheLocalDateTimes() );
			assertEquals( getTheInstants(), that.getTheInstants() );
			assertEquals( getTheOffsetTimes(), that.getTheOffsetTimes() );
			assertEquals( getTheOffsetDateTimes(), that.getTheOffsetDateTimes() );
			assertEquals( getTheZonedDateTimes(), that.getTheZonedDateTimes() );
			assertEquals( getTheDurations(), that.getTheDurations() );
			assertEquals( getTheUuids(), that.getTheUuids() );
			assertEquals( getTheInetAddresses(), that.getTheInetAddresses() );
			try {
				if ( getTheClobs() == null || that.getTheClobs() == null ) {
					assertEquals( getTheClobs(), that.getTheClobs() );
				}
				else {
					assertEquals( getTheClobs().size(), that.getTheClobs().size() );
					for ( int i = 0; i < getTheClobs().size(); i++ ) {
						assertEquals(
								getTheClobs().get( i ).getSubString( 1, (int) getTheClobs().get( i ).length() ),
								that.getTheClobs().get( i )
										.getSubString( 1, (int) that.getTheClobs().get( i ).length() )
						);
					}
				}
				if ( getTheBlobs() == null || that.getTheBlobs() == null ) {
					assertEquals( getTheBlobs(), that.getTheBlobs() );
				}
				else {
					assertEquals( getTheBlobs().size(), that.getTheBlobs().size() );
					for ( int i = 0; i < getTheBlobs().size(); i++ ) {
						Assert.assertArrayEquals(
								getTheBlobs().get( i ).getBytes( 1, (int) getTheBlobs().get( i ).length() ),
								that.getTheBlobs().get( i ).getBytes( 1, (int) that.getTheBlobs().get( i ).length() )
						);
					}
				}
			}
			catch (SQLException e) {
				throw new RuntimeException( e );
			}
//			if (getTheByteArrays() == null || that.getTheByteArrays() == null) {
//				assertEquals( getTheByteArrays(), that.getTheByteArrays() );
//			}
//			else {
//				assertEquals( getTheByteArrays().size(), that.getTheByteArrays().size() );
//				for ( int i = 0; i < getTheByteArrays().size(); i++ ) {
//					assertArrayEquals( getTheByteArrays().get( i ), that.getTheByteArrays().get( i ) );
//				}
//			}
		}
	}

	protected void setMinimumValues() {
		setTheBytes( List.of( Byte.MIN_VALUE ) );
		setTheShorts( List.of( Short.MIN_VALUE ) );
		setTheIntegers( List.of( Integer.MIN_VALUE ) );
		setTheLongs( List.of( Long.MIN_VALUE ) );
		setTheFloats( List.of( Float.MIN_VALUE ) );
		setTheDoubles( List.of( Double.MIN_VALUE ) );
		setTheBooleans( List.of( false ) );
		setTheNumericBooleans( List.of( false ) );
		setTheStringBooleans( List.of( false ) );
		setTheCharacters( List.of( Character.MIN_VALUE ) );
		setTheStrings( List.of( "" ) );
		setTheBigDecimals( List.of( BigDecimal.ZERO ) );
		setTheBigIntegers( List.of( BigInteger.ZERO ) );
		setTheEnums( List.of( BasicEnum.ONE ) );
		setTheStringEnums( List.of( BasicEnum.ONE ) );
		setTheOrdinalEnums( List.of( BasicEnum.ONE ) );
		setTheSqlDates( List.of( new java.sql.Date( 0 ) ) );
		setTheSqlTimes( List.of( new Time( 0 ) ) );
		setTheSqlTimestamps( List.of( new Timestamp( 0 ) ) );
		setTheSqlDateAsDates( List.of( new java.sql.Date( 0 ) ) );
		setTheSqlTimeAsDates( List.of( new java.sql.Time( 0 ) ) );
		setTheSqlTimestampAsDates( List.of( new java.sql.Timestamp( 0 ) ) );
		setTheLocalDates( List.of( LocalDate.EPOCH ) );
		setTheLocalTimes( List.of( LocalTime.MIN ) );
		setTheLocalDateTimes( List.of( LocalDate.EPOCH.atTime( LocalTime.MIN ) ) );
		setTheInstants( List.of( Instant.EPOCH ) );
		setTheOffsetTimes( List.of( OffsetTime.MIN ) );
		setTheOffsetDateTimes( List.of( Instant.EPOCH.atOffset( ZoneOffset.UTC ) ) );
		setTheZonedDateTimes( List.of( Instant.EPOCH.atZone( ZoneOffset.UTC ) ) );
		setTheDurations( List.of( Duration.ZERO ) );
		setTheUuids( List.of( MIN_UUID_V4 ) );
		try {
			setTheInetAddresses( List.of( InetAddress.getLocalHost() ) );
		}
		catch (UnknownHostException e) {
			throw new RuntimeException( e );
		}
		setTheClobs( List.of( ClobProxy.generateProxy( "" ) ) );
		setTheBlobs( List.of( BlobProxy.generateProxy( new byte[0] ) ) );
//		setTheByteArrays( List.of( new byte[0] ) );
	}

	protected void setMaximumValues() {
		setTheBytes( List.of( Byte.MAX_VALUE ) );
		setTheShorts( List.of( Short.MAX_VALUE ) );
		setTheIntegers( List.of( Integer.MAX_VALUE ) );
		setTheLongs( List.of( Long.MAX_VALUE ) );
		setTheFloats( List.of( Float.MAX_VALUE ) );
		setTheDoubles( List.of( Double.MAX_VALUE ) );
		setTheBooleans( List.of( true ) );
		setTheNumericBooleans( List.of( true ) );
		setTheStringBooleans( List.of( true ) );
		setTheCharacters( List.of( Character.MAX_VALUE ) );
		setTheStrings( List.of( "test" ) );
		setTheBigDecimals( List.of( new BigDecimal( "99999999999999999.99" ) ) );
		setTheBigIntegers( List.of( new BigInteger( "9999999999999999999" ) ) );
		setTheEnums( List.of( BasicEnum.THREE ) );
		setTheStringEnums( List.of( BasicEnum.THREE ) );
		setTheOrdinalEnums( List.of( BasicEnum.THREE ) );
		setTheSqlDates( List.of( new java.sql.Date( LAST_EPOCH_DAY_2026_MILLIS ) ) );
		setTheSqlTimes( List.of( new Time( 86399000L ) ) );
		setTheSqlTimestamps( List.of( new Timestamp( LAST_EPOCH_SECOND_2026_MILLIS ) ) );
		setTheSqlDateAsDates( List.of( new java.sql.Date( LAST_EPOCH_DAY_2026_MILLIS ) ) );
		setTheSqlTimeAsDates( List.of( new java.sql.Time( 86399000L ) ) );
		setTheSqlTimestampAsDates( List.of( new java.sql.Timestamp( LAST_EPOCH_SECOND_2026_MILLIS ) ) );
		setTheLocalDates( List.of( LAST_EPOCH_DAY_2026.toLocalDate() ) );
		setTheLocalTimes( List.of( LocalTime.MAX.withNano( 0 ) ) );
		setTheLocalDateTimes( List.of( LAST_EPOCH_DAY_2026.toLocalDateTime() ) );
		setTheInstants( List.of( LAST_EPOCH_DAY_2026.toInstant() ) );
		setTheOffsetTimes( List.of( OffsetTime.MAX.withNano( 0 ) ) );
		setTheOffsetDateTimes( List.of( LAST_EPOCH_DAY_2026 ) );
		setTheZonedDateTimes( List.of( LAST_EPOCH_DAY_2026.toZonedDateTime() ) );
		setTheDurations( List.of( Duration.ofSeconds( 999_999_999L ) ) );
		setTheUuids( List.of( MAX_UUID_V4 ) );
		try {
			setTheInetAddresses( List.of( InetAddress.getLocalHost() ) );
		}
		catch (UnknownHostException e) {
			throw new RuntimeException( e );
		}
		setTheClobs( List.of( ClobProxy.generateProxy( "test" ) ) );
		setTheBlobs( List.of( BlobProxy.generateProxy( new byte[] {1} ) ) );
//		setTheByteArrays( List.of( new byte[] {1} ) );
	}

	public enum EqualityCheck {
		NUMERICS,
		OTHER
	}
}
