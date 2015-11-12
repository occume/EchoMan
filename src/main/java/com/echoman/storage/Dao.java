package com.echoman.storage;

import java.sql.SQLException;
import java.util.List;

public interface Dao<T> {

	int save(Storable bean) throws SQLException;

	int[] batchSave(List<Storable> list) throws SQLException;

	boolean exist(Storable bean);

}
