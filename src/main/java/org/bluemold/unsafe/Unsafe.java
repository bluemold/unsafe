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

	public static final sun.misc.Unsafe us = getSunUnsafe();

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

	public static sun.misc.Unsafe get() { return us; }

	private static class UnsafeReferenceContainer {

		private static long referenceIndex = getIndex();

		private static long getIndex() {
			long index = 0;
			try {
				index = us.objectFieldOffset( UnsafeReferenceContainer.class.getDeclaredField( "reference" ) );
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
			return us.getLong( this, referenceIndex );
		}
		public int getAsInt() {
			return us.getInt( this, referenceIndex );
		}
	}

	public static int getInt( Object target, long index ) {
		return us.getInt( target, index );
	}
	public static long getLong( Object target, long index ) {
		return us.getLong( target, index );
	}
	public static long convertReferenceToLong( Object target ) {
		return new UnsafeReferenceContainer( target ).getAsLong();
	}

	public static int convertReferenceToInt( Object target ) {
		return new UnsafeReferenceContainer( target ).getAsInt();
	}

	public static long objectDeclaredFieldOffset( Class clazz, String name ) {
		try {
			return us.objectFieldOffset( clazz.getDeclaredField( name ) );
		} catch ( Throwable t ) {
			throw new RuntimeException( unsafeErrorMsg, t );
		}
	}

	public static long objectFieldOffset( Field field ) {
		return us.objectFieldOffset( field );
	}

	public static boolean compareAndSwapObject( Object o, long fieldIndex, Object expect, Object update ) {
		return us.compareAndSwapObject( o, fieldIndex, expect, update );
	}

	public static int getIntVolatile( Object o, long fieldIndex ) {
		return us.getIntVolatile( o, fieldIndex );
	}

	public static void putIntVolatile( Object o, long fieldIndex, int o2 ) {
		us.putIntVolatile(o, fieldIndex, o2);
	}

	public static int getLongVolatile( Object o, long fieldIndex ) {
		return us.getIntVolatile( o, fieldIndex );
	}

	public static void putLongVolatile( Object o, long fieldIndex, int o2 ) {
		us.putIntVolatile(o, fieldIndex, o2);
	}

	public static Object getObject( Object o, long fieldIndex ) {
		return us.getObject( o, fieldIndex );
	}

	public static Object getObjectVolatile( Object o, long fieldIndex ) {
		return us.getObjectVolatile( o, fieldIndex );
	}

	public static void putObjectVolatile( Object o, long fieldIndex, Object o2 ) {
		us.putObjectVolatile(o, fieldIndex, o2);
	}

	public static boolean compareAndSwapInt( Object o, long fieldIndex, int expect, int update ) {
		return us.compareAndSwapInt( o, fieldIndex, expect, update );
	}

	public static boolean compareAndSwapLong( Object o, long fieldIndex, long expect, long update ) {
		return us.compareAndSwapLong( o, fieldIndex, expect, update );
	}

	public static int arrayBaseOffset( Class clazz ) {
		return us.arrayBaseOffset( clazz );
	}

	public static int arrayIndexScale( Class clazz ) {
		return us.arrayIndexScale( clazz );
	}
}
