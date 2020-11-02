package util;

public class Array {
	public static boolean matches(String[] regexs, String s) {
		for(String regex : regexs) {
			if(s.matches(regex)) return true;
		}
		return false;
	}

	public static boolean contains(Object[] array, Object target) {
		for(Object element : array) {
			if(element.equals(target)) return true;
		}
		return false;
	}
	
	public static boolean contains(Iterable<? extends Object> array, Object target) {
		for(Object element : array) {
			if(element.equals(target)) return true;
		}
		return false;
	}
	
	public static int containsCount(Iterable<? extends Object> array, Object target) {
		int ret = 0;
		for(Object element : array) {
			if(element.equals(target)) ret++;
		}
		return ret;
	}
}
