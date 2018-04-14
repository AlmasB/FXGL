/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.almasb.fxgl.ai.btree.utils;

import com.almasb.fxgl.ai.utils.random.*;
import com.almasb.fxgl.core.collection.ObjectMap;

import java.util.StringTokenizer;

/** @author davebaol */
public class DistributionAdapters {

	/** Thrown to indicate that the application has attempted to convert a string to one of the distribution types, but that the
	 * string does not have the appropriate format.
	 * 
	 * @author davebaol */
	@SuppressWarnings("serial")
	public static class DistributionFormatException extends RuntimeException {

		/** Constructs a <code>DistributionFormatException</code> with no detail message. */
		public DistributionFormatException () {
			super();
		}

		/** Constructs a <code>DistributionFormatException</code> with the specified detail message.
		 *
		 * @param s the detail message. */
		public DistributionFormatException (String s) {
			super(s);
		}

		/** Constructs a <code>DistributionFormatException</code> with the specified detail message and cause.
		 * <p>
		 * Note that the detail message associated with <code>cause</code> is <i>not</i> automatically incorporated in this
		 * exception's detail message.
		 *
		 * @param message the detail message (which is saved for later retrieval by the {@link Throwable#getMessage()} method).
		 * @param cause the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method). (A <tt>null</tt>
		 *           value is permitted, and indicates that the cause is nonexistent or unknown.) */
		public DistributionFormatException (String message, Throwable cause) {
			super(message, cause);
		}

		/** Constructs a <code>DistributionFormatException</code> with the specified cause and a detail message of
		 * <tt>(cause==null ? null : cause.toString())</tt> (which typically contains the class and detail message of <tt>cause</tt>
		 * ). This constructor is useful for exceptions that are little more than wrappers for other throwables.
		 *
		 * @param cause the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method). (A <tt>null</tt>
		 *           value is permitted, and indicates that the cause is nonexistent or unknown.) */
		public DistributionFormatException (Throwable cause) {
			super(cause);
		}

	}

	public abstract static class Adapter<D extends Distribution> {
		final String category;
		final Class<?> type;

		public Adapter (String category, Class<?> type) {
			this.category = category;
			this.type = type;
		}

		public abstract D toDistribution (String[] args);

		public abstract String[] toParameters (D distribution);

		public static double parseDouble (String v) {
			try {
				return Double.parseDouble(v);
			} catch (NumberFormatException nfe) {
				throw new DistributionFormatException("Not a double value: " + v, nfe);
			}
		}

		public static float parseFloat (String v) {
			try {
				return Float.parseFloat(v);
			} catch (NumberFormatException nfe) {
				throw new DistributionFormatException("Not a float value: " + v, nfe);
			}
		}

		public static int parseInteger (String v) {
			try {
				return Integer.parseInt(v);
			} catch (NumberFormatException nfe) {
				throw new DistributionFormatException("Not an int value: " + v, nfe);
			}
		}

		public static long parseLong (String v) {
			try {
				return Long.parseLong(v);
			} catch (NumberFormatException nfe) {
				throw new DistributionFormatException("Not a long value: " + v, nfe);
			}
		}

	}

	public abstract static class DoubleAdapter<D extends DoubleDistribution> extends Adapter<D> {
		public DoubleAdapter (String category) {
			super(category, DoubleDistribution.class);
		}
	}

	public abstract static class FloatAdapter<D extends FloatDistribution> extends Adapter<D> {
		public FloatAdapter (String category) {
			super(category, FloatDistribution.class);
		}
	}

	public abstract static class IntegerAdapter<D extends IntegerDistribution> extends Adapter<D> {
		public IntegerAdapter (String category) {
			super(category, IntegerDistribution.class);
		}
	}

	public abstract static class LongAdapter<D extends LongDistribution> extends Adapter<D> {
		public LongAdapter (String category) {
			super(category, LongDistribution.class);
		}
	}

	private static final ObjectMap<Class<?>, Adapter<?>> ADAPTERS = new ObjectMap<Class<?>, Adapter<?>>();
	static {
		//
		// Constant distributions
		//

		ADAPTERS.put(ConstantDoubleDistribution.class, new DoubleAdapter<ConstantDoubleDistribution>("constant") {

			@Override
			public ConstantDoubleDistribution toDistribution (String[] args) {
				if (args.length != 1) throw invalidNumberOfArgumentsException(args.length, 1);
				return new ConstantDoubleDistribution(parseDouble(args[0]));
			}

			@Override
			public String[] toParameters (ConstantDoubleDistribution distribution) {
				return new String[] {Double.toString(distribution.getValue())};
			}
		});

		ADAPTERS.put(ConstantFloatDistribution.class, new FloatAdapter<ConstantFloatDistribution>("constant") {

			@Override
			public ConstantFloatDistribution toDistribution (String[] args) {
				if (args.length != 1) throw invalidNumberOfArgumentsException(args.length, 1);
				return new ConstantFloatDistribution(parseFloat(args[0]));
			}

			@Override
			public String[] toParameters (ConstantFloatDistribution distribution) {
				return new String[] {Float.toString(distribution.getValue())};
			}
		});

		ADAPTERS.put(ConstantIntegerDistribution.class, new IntegerAdapter<ConstantIntegerDistribution>("constant") {

			@Override
			public ConstantIntegerDistribution toDistribution (String[] args) {
				if (args.length != 1) throw invalidNumberOfArgumentsException(args.length, 1);
				return new ConstantIntegerDistribution(parseInteger(args[0]));
			}

			@Override
			public String[] toParameters (ConstantIntegerDistribution distribution) {
				return new String[] {Integer.toString(distribution.getValue())};
			}
		});

		ADAPTERS.put(ConstantLongDistribution.class, new LongAdapter<ConstantLongDistribution>("constant") {

			@Override
			public ConstantLongDistribution toDistribution (String[] args) {
				if (args.length != 1) throw invalidNumberOfArgumentsException(args.length, 1);
				return new ConstantLongDistribution(parseLong(args[0]));
			}

			@Override
			public String[] toParameters (ConstantLongDistribution distribution) {
				return new String[] {Long.toString(distribution.getValue())};
			}
		});

		//
		// Gaussian distributions
		//

		ADAPTERS.put(GaussianDoubleDistribution.class, new DoubleAdapter<GaussianDoubleDistribution>("gaussian") {

			@Override
			public GaussianDoubleDistribution toDistribution (String[] args) {
				if (args.length != 2) throw invalidNumberOfArgumentsException(args.length, 2);
				return new GaussianDoubleDistribution(parseDouble(args[0]), parseDouble(args[1]));
			}

			@Override
			public String[] toParameters (GaussianDoubleDistribution distribution) {
				return new String[] {Double.toString(distribution.getMean()), Double.toString(distribution.getStandardDeviation())};
			}
		});

		ADAPTERS.put(GaussianFloatDistribution.class, new FloatAdapter<GaussianFloatDistribution>("gaussian") {

			@Override
			public GaussianFloatDistribution toDistribution (String[] args) {
				if (args.length != 2) throw invalidNumberOfArgumentsException(args.length, 2);
				return new GaussianFloatDistribution(parseFloat(args[0]), parseFloat(args[1]));
			}

			@Override
			public String[] toParameters (GaussianFloatDistribution distribution) {
				return new String[] {Float.toString(distribution.getMean()), Float.toString(distribution.getStandardDeviation())};
			}
		});

		//
		// Triangular distributions
		//

		ADAPTERS.put(TriangularDoubleDistribution.class, new DoubleAdapter<TriangularDoubleDistribution>("triangular") {

			@Override
			public TriangularDoubleDistribution toDistribution (String[] args) {
				switch (args.length) {
				case 1:
					return new TriangularDoubleDistribution(parseDouble(args[0]));
				case 2:
					return new TriangularDoubleDistribution(parseDouble(args[0]), parseDouble(args[1]));
				case 3:
					return new TriangularDoubleDistribution(parseDouble(args[0]), parseDouble(args[1]), parseDouble(args[2]));
				default:
					throw invalidNumberOfArgumentsException(args.length, 1, 2, 3);
				}
			}

			@Override
			public String[] toParameters (TriangularDoubleDistribution distribution) {
				return new String[] {Double.toString(distribution.getLow()), Double.toString(distribution.getHigh()),
					Double.toString(distribution.getMode())};
			}
		});

		ADAPTERS.put(TriangularFloatDistribution.class, new FloatAdapter<TriangularFloatDistribution>("triangular") {

			@Override
			public TriangularFloatDistribution toDistribution (String[] args) {
				switch (args.length) {
				case 1:
					return new TriangularFloatDistribution(parseFloat(args[0]));
				case 2:
					return new TriangularFloatDistribution(parseFloat(args[0]), parseFloat(args[1]));
				case 3:
					return new TriangularFloatDistribution(parseFloat(args[0]), parseFloat(args[1]), parseFloat(args[2]));
				default:
					throw invalidNumberOfArgumentsException(args.length, 1, 2, 3);
				}
			}

			@Override
			public String[] toParameters (TriangularFloatDistribution distribution) {
				return new String[] {Float.toString(distribution.getLow()), Float.toString(distribution.getHigh()),
					Float.toString(distribution.getMode())};
			}
		});

		ADAPTERS.put(TriangularIntegerDistribution.class, new IntegerAdapter<TriangularIntegerDistribution>("triangular") {

			@Override
			public TriangularIntegerDistribution toDistribution (String[] args) {
				switch (args.length) {
				case 1:
					return new TriangularIntegerDistribution(parseInteger(args[0]));
				case 2:
					return new TriangularIntegerDistribution(parseInteger(args[0]), parseInteger(args[1]));
				case 3:
					return new TriangularIntegerDistribution(parseInteger(args[0]), parseInteger(args[1]), Float.valueOf(args[2]));
				default:
					throw invalidNumberOfArgumentsException(args.length, 1, 2, 3);
				}
			}

			@Override
			public String[] toParameters (TriangularIntegerDistribution distribution) {
				return new String[] {Integer.toString(distribution.getLow()), Integer.toString(distribution.getHigh()),
					Float.toString(distribution.getMode())};
			}
		});

		ADAPTERS.put(TriangularLongDistribution.class, new LongAdapter<TriangularLongDistribution>("triangular") {

			@Override
			public TriangularLongDistribution toDistribution (String[] args) {
				switch (args.length) {
				case 1:
					return new TriangularLongDistribution(parseLong(args[0]));
				case 2:
					return new TriangularLongDistribution(parseLong(args[0]), parseLong(args[1]));
				case 3:
					return new TriangularLongDistribution(parseLong(args[0]), parseLong(args[1]), parseDouble(args[2]));
				default:
					throw invalidNumberOfArgumentsException(args.length, 1, 2, 3);
				}
			}

			@Override
			public String[] toParameters (TriangularLongDistribution distribution) {
				return new String[] {Long.toString(distribution.getLow()), Long.toString(distribution.getHigh()),
					Double.toString(distribution.getMode())};
			}
		});

		//
		// Uniform distributions
		//

		ADAPTERS.put(UniformDoubleDistribution.class, new DoubleAdapter<UniformDoubleDistribution>("uniform") {

			@Override
			public UniformDoubleDistribution toDistribution (String[] args) {
				switch (args.length) {
				case 1:
					return new UniformDoubleDistribution(parseDouble(args[0]));
				case 2:
					return new UniformDoubleDistribution(parseDouble(args[0]), parseDouble(args[1]));
				default:
					throw invalidNumberOfArgumentsException(args.length, 1, 2);
				}
			}

			@Override
			public String[] toParameters (UniformDoubleDistribution distribution) {
				return new String[] {Double.toString(distribution.getLow()), Double.toString(distribution.getHigh())};
			}
		});

		ADAPTERS.put(UniformFloatDistribution.class, new FloatAdapter<UniformFloatDistribution>("uniform") {

			@Override
			public UniformFloatDistribution toDistribution (String[] args) {
				switch (args.length) {
				case 1:
					return new UniformFloatDistribution(parseFloat(args[0]));
				case 2:
					return new UniformFloatDistribution(parseFloat(args[0]), parseFloat(args[1]));
				default:
					throw invalidNumberOfArgumentsException(args.length, 1, 2);
				}
			}

			@Override
			public String[] toParameters (UniformFloatDistribution distribution) {
				return new String[] {Float.toString(distribution.getLow()), Float.toString(distribution.getHigh())};
			}
		});

		ADAPTERS.put(UniformIntegerDistribution.class, new IntegerAdapter<UniformIntegerDistribution>("uniform") {

			@Override
			public UniformIntegerDistribution toDistribution (String[] args) {
				switch (args.length) {
				case 1:
					return new UniformIntegerDistribution(parseInteger(args[0]));
				case 2:
					return new UniformIntegerDistribution(parseInteger(args[0]), parseInteger(args[1]));
				default:
					throw invalidNumberOfArgumentsException(args.length, 1, 2);
				}
			}

			@Override
			public String[] toParameters (UniformIntegerDistribution distribution) {
				return new String[] {Integer.toString(distribution.getLow()), Integer.toString(distribution.getHigh())};
			}
		});

		ADAPTERS.put(UniformLongDistribution.class, new LongAdapter<UniformLongDistribution>("uniform") {

			@Override
			public UniformLongDistribution toDistribution (String[] args) {
				switch (args.length) {
				case 1:
					return new UniformLongDistribution(parseLong(args[0]));
				case 2:
					return new UniformLongDistribution(parseLong(args[0]), parseLong(args[1]));
				default:
					throw invalidNumberOfArgumentsException(args.length, 1, 2);
				}
			}

			@Override
			public String[] toParameters (UniformLongDistribution distribution) {
				return new String[] {Long.toString(distribution.getLow()), Long.toString(distribution.getHigh())};
			}
		});
	}

	ObjectMap<Class<?>, Adapter<?>> map;
	ObjectMap<Class<?>, ObjectMap<String, Adapter<?>>> typeMap;

	public DistributionAdapters () {
		this.map = new ObjectMap<Class<?>, Adapter<?>>();
		this.typeMap = new ObjectMap<Class<?>, ObjectMap<String, Adapter<?>>>();
		for (ObjectMap.Entry<Class<?>, Adapter<?>> e : ADAPTERS.entries())
			add(e.key, e.value);
	}

	public final void add (Class<?> clazz, Adapter<?> adapter) {
		map.put(clazz, adapter);

		ObjectMap<String, Adapter<?>> m = typeMap.get(adapter.type);
		if (m == null) {
			m = new ObjectMap<String, Adapter<?>>();
			typeMap.put(adapter.type, m);
		}
		m.put(adapter.category, adapter);
	}

	@SuppressWarnings("unchecked")
	public <T extends Distribution> T toDistribution (String value, Class<T> clazz) {
		StringTokenizer st = new StringTokenizer(value, ", \t\f");
		if (!st.hasMoreTokens()) throw new DistributionFormatException("Missing ditribution type");
		String type = st.nextToken();
		ObjectMap<String, Adapter<?>> categories = typeMap.get(clazz);
		Adapter<T> converter = (Adapter<T>)categories.get(type);
		if (converter == null)
			throw new DistributionFormatException("Cannot create a '" + clazz.getSimpleName() + "' of type '" + type + "'");
		String[] args = new String[st.countTokens()];
		for (int i = 0; i < args.length; i++)
			args[i] = st.nextToken();
		return converter.toDistribution(args);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public String toString (Distribution distribution) {
		Adapter adapter = map.get(distribution.getClass());
		String args[] = adapter.toParameters(distribution);
		String out = adapter.category;
		for (String a : args)
			out += "," + a;
		return out;
	}

	private static DistributionFormatException invalidNumberOfArgumentsException (int found, int... expected) {
		String message = "Found " + found + " arguments in triangular distribution; expected ";
		if (expected.length < 2)
			message += expected.length;
		else {
			String sep = "";
			int i = 0;
			while (i < expected.length - 1) {
				message += sep + expected[i++];
				sep = ", ";
			}
			message += " or " + expected[i];
		}
		return new DistributionFormatException(message);
	}
}
