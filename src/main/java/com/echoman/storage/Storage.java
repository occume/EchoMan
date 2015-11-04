package com.echoman.storage;

import java.sql.SQLException;
import java.util.List;

public interface Storage<T extends Storable> {

	public int save(T t) throws SQLException;
	
	public int[] batchSave(List<T> list) throws SQLException;
	
	public boolean exist(T t);
}
