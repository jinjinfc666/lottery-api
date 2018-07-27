package com.jll.common.utils;

public class PagenationUtil {

	public static Integer computeTotalPages(Integer totalRecord, Integer size) {
		Integer zero = Integer.valueOf(0);
		if (totalRecord == null || size == null)
			return Integer.valueOf(0);
		if (size.compareTo(zero) == 0) {
			return Integer.valueOf(0);
		} else {
			double totalPages = Math.floor(totalRecord.intValue() / size.intValue());
			totalPages = totalRecord.intValue() % size.intValue() != 0 ? totalPages + 1.0D : totalPages;
			return Integer.valueOf((int) totalPages);
		}
	}

	public PagenationUtil() {
	}
	
	
}
