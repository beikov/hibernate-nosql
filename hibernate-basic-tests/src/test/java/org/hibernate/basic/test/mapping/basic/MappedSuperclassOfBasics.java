/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.basic;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.engine.jdbc.proxy.BlobProxy;
import org.hibernate.engine.jdbc.proxy.ClobProxy;

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
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.AssertionFailureBuilder.assertionFailure;
import static org.junit.jupiter.api.Assertions.assertEquals;

@MappedSuperclass
public abstract class MappedSuperclassOfBasics<ID> {

	protected static final UUID MIN_UUID_V4 = UUID.fromString( "00000000-0000-4000-8000-000000000000");
	protected static final UUID MAX_UUID_V4 = UUID.fromString( "ffffffff-ffff-ffff-ffff-ffffffffffff");
	protected static final OffsetDateTime LAST_EPOCH_DAY_2026 = OffsetDateTime.of( 2026, 12, 31, 0, 0, 0, 0, ZoneOffset.UTC );
	protected static final OffsetDateTime LAST_EPOCH_SECOND_2026 = OffsetDateTime.of( 2026, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC );
	protected static final long LAST_EPOCH_DAY_2026_MILLIS = LAST_EPOCH_DAY_2026.toInstant().toEpochMilli();
	protected static final long LAST_EPOCH_SECOND_2026_MILLIS = LAST_EPOCH_SECOND_2026.toInstant().toEpochMilli();

	private Byte theByte;
	private Short theShort;
	private Integer theInteger;
	private Long theLong;
	private Float theFloat;
	private Double theDouble;
	private Boolean theBoolean;
	@JdbcTypeCode(Types.INTEGER)
	private Boolean theNumericBoolean;
	@JdbcTypeCode(Types.CHAR)
	private Boolean theStringBoolean;
	private Character theCharacter;
	private String theString;

	@Column(precision = 19, scale = 2)
	private BigDecimal theBigDecimal;
	@Column(precision = 19)
	private BigInteger theBigInteger;

	private BasicEnum theEnum;
	@Enumerated(EnumType.STRING)
	private BasicEnum theStringEnum;
	@Enumerated(EnumType.ORDINAL)
	private BasicEnum theOrdinalEnum;

	private java.sql.Date theSqlDate;
	private Time theSqlTime;
	private Timestamp theSqlTimestamp;
	@Temporal(TemporalType.DATE)
	private Date theSqlDateAsDate;
	@Temporal(TemporalType.TIME)
	private Date theSqlTimeAsDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date theSqlTimestampAsDate;
	private LocalDate theLocalDate;
	private LocalTime theLocalTime;
	private LocalDateTime theLocalDateTime;
	private Instant theInstant;
	private OffsetTime theOffsetTime;
	private OffsetDateTime theOffsetDateTime;
	private ZonedDateTime theZonedDateTime;

	private Duration theDuration;
	private UUID theUuid;
	private InetAddress theInetAddress;

	private Clob theClob;
	private Blob theBlob;
	private byte[] theByteArray;

	public MappedSuperclassOfBasics() {
	}

	public abstract ID getId();

	public abstract void setId(ID id);

	public Byte getTheByte() {
		return theByte;
	}

	public void setTheByte(Byte theByte) {
		this.theByte = theByte;
	}

	public Short getTheShort() {
		return theShort;
	}

	public void setTheShort(Short theShort) {
		this.theShort = theShort;
	}

	public Integer getTheInteger() {
		return theInteger;
	}

	public void setTheInteger(Integer theInteger) {
		this.theInteger = theInteger;
	}

	public Long getTheLong() {
		return theLong;
	}

	public void setTheLong(Long theLong) {
		this.theLong = theLong;
	}

	public Float getTheFloat() {
		return theFloat;
	}

	public void setTheFloat(Float theFloat) {
		this.theFloat = theFloat;
	}

	public Double getTheDouble() {
		return theDouble;
	}

	public void setTheDouble(Double theDouble) {
		this.theDouble = theDouble;
	}

	public Boolean getTheBoolean() {
		return theBoolean;
	}

	public void setTheBoolean(Boolean theBoolean) {
		this.theBoolean = theBoolean;
	}

	public Boolean getTheNumericBoolean() {
		return theNumericBoolean;
	}

	public void setTheNumericBoolean(Boolean theNumericBoolean) {
		this.theNumericBoolean = theNumericBoolean;
	}

	public Boolean getTheStringBoolean() {
		return theStringBoolean;
	}

	public void setTheStringBoolean(Boolean theStringBoolean) {
		this.theStringBoolean = theStringBoolean;
	}

	public Character getTheCharacter() {
		return theCharacter;
	}

	public void setTheCharacter(Character theCharacter) {
		this.theCharacter = theCharacter;
	}

	public String getTheString() {
		return theString;
	}

	public void setTheString(String theString) {
		this.theString = theString;
	}

	public BigDecimal getTheBigDecimal() {
		return theBigDecimal;
	}

	public void setTheBigDecimal(BigDecimal theBigDecimal) {
		this.theBigDecimal = theBigDecimal;
	}

	public BigInteger getTheBigInteger() {
		return theBigInteger;
	}

	public void setTheBigInteger(BigInteger theBigInteger) {
		this.theBigInteger = theBigInteger;
	}

	public BasicEnum getTheEnum() {
		return theEnum;
	}

	public void setTheEnum(BasicEnum theEnum) {
		this.theEnum = theEnum;
	}

	public BasicEnum getTheStringEnum() {
		return theStringEnum;
	}

	public void setTheStringEnum(BasicEnum theStringEnum) {
		this.theStringEnum = theStringEnum;
	}

	public BasicEnum getTheOrdinalEnum() {
		return theOrdinalEnum;
	}

	public void setTheOrdinalEnum(BasicEnum theOrdinalEnum) {
		this.theOrdinalEnum = theOrdinalEnum;
	}

	public java.sql.Date getTheSqlDate() {
		return theSqlDate;
	}

	public void setTheSqlDate(java.sql.Date theSqlDate) {
		this.theSqlDate = theSqlDate;
	}

	public Time getTheSqlTime() {
		return theSqlTime;
	}

	public void setTheSqlTime(Time theSqlTime) {
		this.theSqlTime = theSqlTime;
	}

	public Timestamp getTheSqlTimestamp() {
		return theSqlTimestamp;
	}

	public void setTheSqlTimestamp(Timestamp theSqlTimestamp) {
		this.theSqlTimestamp = theSqlTimestamp;
	}

	public Date getTheSqlDateAsDate() {
		return theSqlDateAsDate;
	}

	public void setTheSqlDateAsDate(Date theSqlDateAsDate) {
		this.theSqlDateAsDate = theSqlDateAsDate;
	}

	public Date getTheSqlTimeAsDate() {
		return theSqlTimeAsDate;
	}

	public void setTheSqlTimeAsDate(Date theSqlTimeAsDate) {
		this.theSqlTimeAsDate = theSqlTimeAsDate;
	}

	public Date getTheSqlTimestampAsDate() {
		return theSqlTimestampAsDate;
	}

	public void setTheSqlTimestampAsDate(Date theSqlTimestampAsDate) {
		this.theSqlTimestampAsDate = theSqlTimestampAsDate;
	}

	public LocalDate getTheLocalDate() {
		return theLocalDate;
	}

	public void setTheLocalDate(LocalDate theLocalDate) {
		this.theLocalDate = theLocalDate;
	}

	public LocalTime getTheLocalTime() {
		return theLocalTime;
	}

	public void setTheLocalTime(LocalTime theLocalTime) {
		this.theLocalTime = theLocalTime;
	}

	public LocalDateTime getTheLocalDateTime() {
		return theLocalDateTime;
	}

	public void setTheLocalDateTime(LocalDateTime theLocalDateTime) {
		this.theLocalDateTime = theLocalDateTime;
	}

	public Instant getTheInstant() {
		return theInstant;
	}

	public void setTheInstant(Instant theInstant) {
		this.theInstant = theInstant;
	}

	public OffsetTime getTheOffsetTime() {
		return theOffsetTime;
	}

	public void setTheOffsetTime(OffsetTime theOffsetTime) {
		this.theOffsetTime = theOffsetTime;
	}

	public OffsetDateTime getTheOffsetDateTime() {
		return theOffsetDateTime;
	}

	public void setTheOffsetDateTime(OffsetDateTime theOffsetDateTime) {
		this.theOffsetDateTime = theOffsetDateTime;
	}

	public ZonedDateTime getTheZonedDateTime() {
		return theZonedDateTime;
	}

	public void setTheZonedDateTime(ZonedDateTime theZonedDateTime) {
		this.theZonedDateTime = theZonedDateTime;
	}

	public Duration getTheDuration() {
		return theDuration;
	}

	public void setTheDuration(Duration theDuration) {
		this.theDuration = theDuration;
	}

	public UUID getTheUuid() {
		return theUuid;
	}

	public void setTheUuid(UUID theUuid) {
		this.theUuid = theUuid;
	}

	public InetAddress getTheInetAddress() {
		return theInetAddress;
	}

	public void setTheInetAddress(InetAddress theInetAddress) {
		this.theInetAddress = theInetAddress;
	}

	public Clob getTheClob() {
		return theClob;
	}

	public void setTheClob(Clob theClob) {
		this.theClob = theClob;
	}

	public Blob getTheBlob() {
		return theBlob;
	}

	public void setTheBlob(Blob theBlob) {
		this.theBlob = theBlob;
	}

	public byte[] getTheByteArray() {
		return theByteArray;
	}

	public void setTheByteArray(byte[] theByteArray) {
		this.theByteArray = theByteArray;
	}

	public void assertEqual(MappedSuperclassOfBasics<ID> that, EqualityCheck check) {
		assertEquals( getId(), that.getId() );
		if ( check == EqualityCheck.NUMERICS ) {
			if ( getTheBigDecimal() == null || that.getTheBigDecimal() == null ) {
				assertEquals( getTheBigDecimal(), that.getTheBigDecimal() );
			}
			else {
				assertEquals( getTheBigDecimal().stripTrailingZeros(), that.getTheBigDecimal().stripTrailingZeros() );
			}
			assertEquals( getTheBigInteger(), that.getTheBigInteger() );
		}
		else {
			assertEquals( getTheByte(), that.getTheByte() );
			assertEquals( getTheShort(), that.getTheShort() );
			assertEquals( getTheInteger(), that.getTheInteger() );
			assertEquals( getTheLong(), that.getTheLong() );
			assertEquals( getTheFloat(), that.getTheFloat() );
			assertEquals( getTheDouble(), that.getTheDouble() );
			assertEquals( getTheBoolean(), that.getTheBoolean() );
			assertEquals( getTheNumericBoolean(), that.getTheNumericBoolean() );
			assertEquals( getTheStringBoolean(), that.getTheStringBoolean() );
			assertEquals( getTheCharacter(), that.getTheCharacter() );
			assertEquals( getTheString(), that.getTheString() );
			assertEquals( getTheEnum(), that.getTheEnum() );
			assertEquals( getTheStringEnum(), that.getTheStringEnum() );
			assertEquals( getTheOrdinalEnum(), that.getTheOrdinalEnum() );
			assertEquals( getTheSqlDate(), that.getTheSqlDate() );
			assertEquals( getTheSqlTime(), that.getTheSqlTime() );
			assertEquals( getTheSqlTimestamp(), that.getTheSqlTimestamp() );
			assertEquals( getTheSqlDateAsDate(), that.getTheSqlDateAsDate() );
			assertEquals( getTheSqlTimeAsDate(), that.getTheSqlTimeAsDate() );
			assertEquals( getTheSqlTimestampAsDate(), that.getTheSqlTimestampAsDate() );
			assertEquals( getTheLocalDate(), that.getTheLocalDate() );
			assertEquals( getTheLocalTime(), that.getTheLocalTime() );
			assertEquals( getTheLocalDateTime(), that.getTheLocalDateTime() );
			assertEquals( getTheInstant(), that.getTheInstant() );
			if ( getTheOffsetTime() == null || that.getTheOffsetTime() == null ) {
				assertEquals( getTheOffsetTime(), that.getTheOffsetTime() );
			}
			else if ( !getTheOffsetTime().isEqual( that.getTheOffsetTime() ) ) {
				throw assertionFailure().expected( getTheOffsetTime() )
						.actual( that.getTheOffsetTime() )
						.build();
			}
			assertEquals( getTheOffsetDateTime(), that.getTheOffsetDateTime() );
			assertEquals( getTheZonedDateTime(), that.getTheZonedDateTime() );
			assertEquals( getTheDuration(), that.getTheDuration() );
			assertEquals( getTheUuid(), that.getTheUuid() );
			assertEquals( getTheInetAddress(), that.getTheInetAddress() );
			try {
				if ( getTheClob() == null || that.getTheClob() == null ) {
					assertEquals( getTheClob(), that.getTheClob() );
				}
				else {
					assertEquals(
							getTheClob().getSubString( 1, (int) getTheClob().length() ),
							that.getTheClob().getSubString( 1, (int) that.getTheClob().length() )
					);
				}
				if ( getTheBlob() == null || that.getTheBlob() == null ) {
					assertEquals( getTheBlob(), that.getTheBlob() );
				}
				else {
					assertArrayEquals(
							getTheBlob().getBytes( 1, (int) getTheBlob().length() ),
							that.getTheBlob().getBytes( 1, (int) that.getTheBlob().length() )
					);
				}
			}
			catch (SQLException e) {
				throw new RuntimeException( e );
			}
			assertArrayEquals( getTheByteArray(), that.getTheByteArray() );
		}
	}

	protected void setSimpleValues() {
		setTheByte( (byte) 1 );
		setTheShort( (short) 1 );
		setTheInteger( 1 );
		setTheLong( 1L );
		setTheFloat( 1F );
		setTheDouble( 1D );
		setTheBoolean( false );
		setTheNumericBoolean( false );
		setTheStringBoolean( false );
		setTheCharacter( 'A' );
		setTheString( "abc" );
		setTheBigDecimal( BigDecimal.ONE );
		setTheBigInteger( BigInteger.ONE );
		setTheEnum(BasicEnum.ONE);
		setTheStringEnum(BasicEnum.ONE);
		setTheOrdinalEnum(BasicEnum.ONE);
		setTheSqlDate( new java.sql.Date( 0 ) );
		setTheSqlTime( new java.sql.Time( 0 ) );
		setTheSqlTimestamp( new java.sql.Timestamp( 0 ) );
		setTheSqlDateAsDate( new java.sql.Date( 0 ) );
		setTheSqlTimeAsDate( new java.sql.Time( 0 ) );
		setTheSqlTimestampAsDate( new java.sql.Timestamp( 0 ) );
		setTheLocalDate( LocalDate.EPOCH );
		setTheLocalTime( LocalTime.MIN );
		setTheLocalDateTime( LocalDate.EPOCH.atTime( LocalTime.MIN ) );
		setTheInstant( Instant.EPOCH );
		setTheOffsetTime( OffsetTime.MIN );
		setTheOffsetDateTime( Instant.EPOCH.atOffset( ZoneOffset.UTC ) );
		setTheZonedDateTime( Instant.EPOCH.atZone( ZoneOffset.UTC ) );
		setTheDuration( Duration.ofSeconds( 1 ) );
		setTheUuid( MIN_UUID_V4 );
		try {
			setTheInetAddress( InetAddress.getLocalHost() );
		}
		catch (UnknownHostException e) {
			throw new RuntimeException( e );
		}
		setTheClob( ClobProxy.generateProxy( "abc" ) );
		setTheBlob( BlobProxy.generateProxy( new byte[] { 1 } ) );
		setTheByteArray( new byte[] { 1 } );
	}

	protected void setMinimumValues() {
		setTheByte( Byte.MIN_VALUE );
		setTheShort( Short.MIN_VALUE );
		setTheInteger( Integer.MIN_VALUE );
		setTheLong( Long.MIN_VALUE );
		setTheFloat( Float.MIN_VALUE );
		setTheDouble( Double.MIN_VALUE );
		setTheBoolean( false );
		setTheNumericBoolean( false );
		setTheStringBoolean( false );
		setTheCharacter( Character.MIN_VALUE );
		setTheString( "" );
		setTheBigDecimal( BigDecimal.ZERO );
		setTheBigInteger( BigInteger.ZERO );
		setTheEnum(BasicEnum.ONE);
		setTheStringEnum(BasicEnum.ONE);
		setTheOrdinalEnum(BasicEnum.ONE);
		setTheSqlDate( new java.sql.Date( 0 ) );
		setTheSqlTime( new java.sql.Time( 0 ) );
		setTheSqlTimestamp( new java.sql.Timestamp( 0 ) );
		setTheSqlDateAsDate( new java.sql.Date( 0 ) );
		setTheSqlTimeAsDate( new java.sql.Time( 0 ) );
		setTheSqlTimestampAsDate( new java.sql.Timestamp( 0 ) );
		setTheLocalDate( LocalDate.EPOCH );
		setTheLocalTime( LocalTime.MIN );
		setTheLocalDateTime( LocalDate.EPOCH.atTime( LocalTime.MIN ) );
		setTheInstant( Instant.EPOCH );
		setTheOffsetTime( OffsetTime.MIN );
		setTheOffsetDateTime( Instant.EPOCH.atOffset( ZoneOffset.UTC ) );
		setTheZonedDateTime( Instant.EPOCH.atZone( ZoneOffset.UTC ) );
		setTheDuration( Duration.ZERO );
		setTheUuid( MIN_UUID_V4 );
		try {
			setTheInetAddress( InetAddress.getLocalHost() );
		}
		catch (UnknownHostException e) {
			throw new RuntimeException( e );
		}
		setTheClob( ClobProxy.generateProxy( "" ) );
		setTheBlob( BlobProxy.generateProxy( new byte[0] ) );
		setTheByteArray( new byte[0] );
	}

	protected void setMaximumValues() {
		setTheByte( Byte.MAX_VALUE );
		setTheShort( Short.MAX_VALUE );
		setTheInteger( Integer.MAX_VALUE );
		setTheLong( Long.MAX_VALUE );
		setTheFloat( Float.MAX_VALUE );
		setTheDouble( Double.MAX_VALUE );
		setTheBoolean( true );
		setTheNumericBoolean( true );
		setTheStringBoolean( true );
		setTheCharacter( Character.MAX_VALUE );
		setTheString( "test" );
		setTheBigDecimal( new BigDecimal( "99999999999999999.99" ) );
		setTheBigInteger( new BigInteger( "9999999999999999999" ) );
		setTheEnum(BasicEnum.THREE);
		setTheStringEnum(BasicEnum.THREE);
		setTheOrdinalEnum(BasicEnum.THREE);
		setTheSqlDate( new java.sql.Date( LAST_EPOCH_DAY_2026_MILLIS ) );
		setTheSqlTime( new java.sql.Time( 86399000L ) );
		setTheSqlTimestamp( new java.sql.Timestamp( LAST_EPOCH_SECOND_2026_MILLIS ) );
		setTheSqlDateAsDate( new java.sql.Date( LAST_EPOCH_DAY_2026_MILLIS ) );
		setTheSqlTimeAsDate( new java.sql.Time( 86399000L ) );
		setTheSqlTimestampAsDate( new java.sql.Timestamp( LAST_EPOCH_SECOND_2026_MILLIS ) );
		setTheLocalDate( LAST_EPOCH_DAY_2026.toLocalDate() );
		setTheLocalTime( LocalTime.MAX.withNano( 0 ) );
		setTheLocalDateTime( LAST_EPOCH_DAY_2026.toLocalDateTime() );
		setTheInstant( LAST_EPOCH_DAY_2026.toInstant() );
		setTheOffsetTime( OffsetTime.MAX.withNano( 0 ) );
		setTheOffsetDateTime( LAST_EPOCH_DAY_2026 );
		setTheZonedDateTime( LAST_EPOCH_DAY_2026.toZonedDateTime() );
		setTheDuration( Duration.ofSeconds( 999_999_999L ) );
		setTheUuid( MAX_UUID_V4 );
		try {
			setTheInetAddress( InetAddress.getLocalHost() );
		}
		catch (UnknownHostException e) {
			throw new RuntimeException( e );
		}
		setTheClob( ClobProxy.generateProxy( "test" ) );
		setTheBlob( BlobProxy.generateProxy( new byte[]{ 1 } ) );
		setTheByteArray( new byte[] { 1 } );
	}

	public enum EqualityCheck {
		NUMERICS,
		OTHER
	}
}
