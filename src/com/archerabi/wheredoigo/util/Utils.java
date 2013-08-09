/**
 * 
 */
package com.archerabi.wheredoigo.util;

/**
 * @author gautamichitteti
 * 
 */
public class Utils {

	public static enum DISTANCE_UNIT {
		METERS, YARDS, MILES, KILOMETERS
	}

	public static double covertDistance(DISTANCE_UNIT fromUnit, DISTANCE_UNIT toUnit, double value) {
		switch (fromUnit) {
		case KILOMETERS:
			switch (toUnit) {
			case METERS:
				return value * 1000;
			case MILES:
				return value / 1.60934;
			case YARDS:
				return value * 1093.61;
			default:
				return value;
			}
		case METERS:
			switch (toUnit) {
			case KILOMETERS:
				return value / 1000;
			case MILES:
				return value / 1609.34;
			case YARDS:
				return value * 1.09361;
			default:
				return value;
			}
		case MILES:
			switch (toUnit) {
			case METERS:
				return value * 1609.34;
			case KILOMETERS:
				return value * 1000;
			case YARDS:
				return value * 1760;
			default:
				return value;
			}
		case YARDS:
			switch (toUnit) {
			case METERS:
				return value / 1.09361;
			case MILES:
				return value / 1760;
			case KILOMETERS:
				return value / 1093.61;
			default:
				return value;
			}
		}
		return value;
	}
}
