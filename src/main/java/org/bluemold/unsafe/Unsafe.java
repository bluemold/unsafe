package org.bluemold.unsafe;

import java.lang.reflect.Field;

/**
 * Unsafe<br/>
 * <p/>
 * [Description]
 */
public final class Unsafe
{
	public static final String unsafeErrorMsg = "Exception during call to get unsafe offset";

	private static final sun.misc.Unsafe _UNSAFE = getSunUnsafe();

	private static sun.misc.Unsafe getSunUnsafe() {
		sun.misc.Unsafe unsafe;
		try {
		   Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
		   field.setAccessible(true);
		   unsafe = (sun.misc.Unsafe) field.get(null);
		} catch (Exception e) {
		   throw new AssertionError(e);
		}
		return unsafe;
	}

	private static final boolean cs8supported;

	private static final boolean _64Bit = System.getProperty( "java.vm.name" ).toLowerCase().contains( "64" );

	static {
		boolean _cs8supported;
		try {
			Class klass = Class.forName( "java.util.concurrent.atomic.AtomicLong");
			Field field = klass.getDeclaredField( "VM_SUPPORTS_LONG_CAS" );
			field.setAccessible( true );
			_cs8supported = field.getBoolean( null );
		}
		catch ( Exception e )
		{
			throw new AssertionError( e );
		}
		cs8supported = _cs8supported;
	}

	public static boolean is64Bit()
	{
		return _64Bit;
	}

	public static boolean isCs8supported()
	{
		return cs8supported;
	}

	/**
	 * Returns -1 if a is "naturally" less than b,
	 * 1 if b is "naturally" less than a
	 * and 0 if they are equal
	 * @param a first reference
	 * @param b second reference
	 * @return comparison
	 */
	public static int naturalReferenceCompare( Object a, Object b ) {
		int ret = 0;
		if ( a != b ) {
			if ( is64Bit() ) {
				long refA = convertReferenceToLong( a );
				long refB = convertReferenceToLong( b );
				if ( refA == refB )
					throw new RuntimeException( "WTF" );
				ret = refA < refB ? -1 : 1;
			} else {
				int refA = convertReferenceToInt( a );
				int refB = convertReferenceToInt( b );
				if ( refA == refB )
					throw new RuntimeException( "WTF" );
				ret = refA < refB ? -1 : 1;
			}
		}
		return ret;
	}

	private static class UnsafeReferenceContainer {

		private static long referenceIndex = getIndex();

		private static long getIndex() {
			long index = 0;
			try {
				index = _UNSAFE.objectFieldOffset( UnsafeReferenceContainer.class.getDeclaredField( "reference" ) );
			}
			catch ( NoSuchFieldException e )
			{
				e.printStackTrace();
			}
			return index;
		}

		private Object reference;

		public UnsafeReferenceContainer( Object reference )
		{
			this.reference = reference;
		}
		public long getAsLong() {
			return _UNSAFE.getLong( this, referenceIndex );
		}
		public int getAsInt() {
			return _UNSAFE.getInt( this, referenceIndex );
		}
	}

	public static int getInt( Object target, long index ) {
		return _UNSAFE.getInt( target, index );
	}
	public static long getLong( Object target, long index ) {
		return _UNSAFE.getLong( target, index );
	}
	public static long convertReferenceToLong( Object target ) {
		return new UnsafeReferenceContainer( target ).getAsLong();
	}

	public static int convertReferenceToInt( Object target ) {
		return new UnsafeReferenceContainer( target ).getAsInt();
	}

	public static long objectDeclaredFieldOffset( Class clazz, String name ) {
		try {
			return _UNSAFE.objectFieldOffset( clazz.getDeclaredField( name ) );
		} catch ( Throwable t ) {
			throw new RuntimeException( unsafeErrorMsg, t );
		}
	}

	public static long objectFieldOffset( Field field ) {
		return _UNSAFE.objectFieldOffset( field );
	}

	public static boolean compareAndSwapObject( Object o, long fieldIndex, Object expect, Object update ) {
		return _UNSAFE.compareAndSwapObject( o, fieldIndex, expect, update );
	}

	public static int getIntVolatile( Object o, long fieldIndex ) {
		return _UNSAFE.getIntVolatile( o, fieldIndex );
	}

	public static void putIntVolatile( Object o, long fieldIndex, int o2 ) {
		_UNSAFE.putIntVolatile( o, fieldIndex, o2 );
	}

	public static int getLongVolatile( Object o, long fieldIndex ) {
		return _UNSAFE.getIntVolatile( o, fieldIndex );
	}

	public static void putLongVolatile( Object o, long fieldIndex, int o2 ) {
		_UNSAFE.putIntVolatile( o, fieldIndex, o2 );
	}

	public static Object getObjectVolatile( Object o, long fieldIndex ) {
		return _UNSAFE.getObjectVolatile( o, fieldIndex );
	}

	public static void putObjectVolatile( Object o, long fieldIndex, Object o2 ) {
		_UNSAFE.putObjectVolatile( o, fieldIndex, o2 );
	}

	public static boolean compareAndSwapInt( Object o, long fieldIndex, int expect, int update ) {
		return _UNSAFE.compareAndSwapInt( o, fieldIndex, expect, update );
	}

	public static boolean compareAndSwapLong( Object o, long fieldIndex, long expect, long update ) {
		return _UNSAFE.compareAndSwapLong( o, fieldIndex, expect, update );
	}

	public static int arrayBaseOffset( Class clazz ) {
		return _UNSAFE.arrayBaseOffset( clazz );
	}

	public static int arrayIndexScale( Class clazz ) {
		return _UNSAFE.arrayIndexScale( clazz );
	}
}
